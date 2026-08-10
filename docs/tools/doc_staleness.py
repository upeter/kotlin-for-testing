#!/usr/bin/env python3
"""Documentation staleness, direct impact, and coverage for Kotlin sources.

The CI-safe commands deliberately use only Git, documentation frontmatter, and
the checked-out Kotlin source tree. Blast-radius analysis belongs to the live
codebase-memory MCP graph; see .claude/skills/generate-docs/SKILL.md.
"""
import argparse
import json
import re
import subprocess
from pathlib import Path

SOURCE_ROOTS = ("src/main/kotlin", "src/test/kotlin", "src/integrationTest/kotlin")
PRODUCTION_ROOT = "src/main/kotlin"
DECLARATION = re.compile(r"\b(?:class|interface|object)\s+([A-Za-z_]\w*)")
PACKAGE = re.compile(r"^\s*package\s+([\w.]+)", re.MULTILINE)


def run(root, *args, check=True):
    result = subprocess.run(args, cwd=root, text=True, capture_output=True)
    if check and result.returncode:
        raise RuntimeError(result.stderr.strip() or " ".join(args))
    return result


def git(root, *args):
    return run(root, "git", *args).stdout


def repo_root(cwd):
    return Path(git(cwd, "rev-parse", "--show-toplevel").strip())


def rev_exists(root, rev):
    return run(root, "git", "rev-parse", "--verify", "--quiet", rev, check=False).returncode == 0


def changed_files(root, base, head):
    return {line for line in git(root, "diff", "--name-only", base, head).splitlines() if line}


def mini_yaml(block):
    """Enough YAML for this repository's frontmatter, without a dependency."""
    root, stack, pending = {}, [(-1, {})], []
    root = stack[0][1]
    for raw in block.splitlines():
        if not raw.strip() or raw.lstrip().startswith("#"):
            continue
        indent = len(raw) - len(raw.lstrip())
        value = raw.strip()
        while stack and stack[-1][0] >= indent:
            stack.pop()
        while pending and pending[-1][0] >= indent:
            pending.pop()
        container = stack[-1][1]
        if value.startswith("- "):
            if not pending:
                continue
            _, parent, key = pending[-1]
            items = parent.setdefault(key, [])
            if not isinstance(items, list):
                items = parent[key] = []
            items.append(value[2:].strip().strip("'\""))
            continue
        key, _, rest = value.partition(":")
        key, rest = key.strip(), rest.strip().strip("'\"")
        if rest:
            container[key] = rest
        else:
            child = {}
            container[key] = child
            pending.append((indent, container, key))
            stack.append((indent, child))
    return root


def frontmatter(path):
    match = re.match(r"^---\n(.*?)\n---\n", path.read_text(encoding="utf-8"), re.DOTALL)
    if not match:
        return {}
    try:
        import yaml
        return yaml.safe_load(match.group(1)) or {}
    except Exception:
        return mini_yaml(match.group(1))


def document_fqns(metadata):
    documented = metadata.get("documents") or {}
    if isinstance(documented, list):
        return [str(item) for item in documented]
    if isinstance(documented, dict):
        return [str(item) for values in documented.values() if isinstance(values, list) for item in values]
    return []


class Doc:
    def __init__(self, path, root):
        self.rel = str(path.relative_to(root))
        self.metadata = frontmatter(path)
        self.fqns = document_fqns(self.metadata)
        self.reviewed_at = self.metadata.get("reviewed_at_commit")
        self.last_reviewed = self.metadata.get("last_reviewed")


def load_docs(root, docs_dir):
    return [
        Doc(path, root) for path in sorted(docs_dir.glob("*.md"))
        if path.name not in ("TEMPLATE.md", "README.md") and frontmatter(path).get("documents")
    ]


def kotlin_files(root, source_roots=SOURCE_ROOTS):
    for source_root in source_roots:
        directory = root / source_root
        if directory.exists():
            yield from directory.rglob("*.kt")


def declared_fqns(path):
    source = path.read_text(encoding="utf-8", errors="ignore")
    package = PACKAGE.search(source)
    if not package:
        return []
    return [f"{package.group(1)}.{name}" for name in DECLARATION.findall(source)]


def resolve_anchor(root, fqn):
    package_path = fqn.replace(".", "/")
    for source_root in SOURCE_ROOTS:
        direct = root / source_root / f"{package_path}.kt"
        if direct.exists():
            return str(direct.relative_to(root))
    simple = fqn.rsplit(".", 1)[-1]
    for path in kotlin_files(root):
        if path.stem == simple or fqn in declared_fqns(path):
            return str(path.relative_to(root))
    return None


def anchor_files(root, doc):
    return {fqn: resolve_anchor(root, fqn) for fqn in doc.fqns}


def cmd_check(root, docs, head):
    stale = 0
    for doc in docs:
        reasons = []
        if not doc.reviewed_at:
            reasons.append("no reviewed_at_commit in frontmatter")
        elif not rev_exists(root, doc.reviewed_at):
            reasons.append(f"reviewed_at_commit {doc.reviewed_at} not in history")
        anchors = anchor_files(root, doc)
        for fqn, path in anchors.items():
            if path is None:
                reasons.append(f"BROKEN anchor {fqn}")
        if doc.reviewed_at and rev_exists(root, doc.reviewed_at):
            files = {path for path in anchors.values() if path}
            for path in sorted(changed_files(root, doc.reviewed_at, head) & files):
                reasons.append(f"changed: {path}")
        if reasons:
            stale += 1
        print(f"{'STALE' if reasons else 'ok   '}  {doc.rel}  "
              f"(reviewed {doc.last_reviewed or '?'} @ {doc.reviewed_at or '?'})")
        for reason in reasons:
            print(f"         - {reason}")
    print(f"\n{stale} stale / {len(docs)} docs")
    return 1 if stale else 0


LAYERS = (
    (lambda fqn: fqn.endswith("Application"), None, False),
    (lambda fqn: ".api." in fqn and fqn.endswith("Controller"), "surface (controller)", True),
    (lambda fqn: ".api." in fqn, "cross-cutting (api)", False),
    (lambda fqn: ".service." in fqn and fqn.endswith("Service"), "service", False),
    (lambda fqn: ".service." in fqn and fqn.endswith("Exception"), "cross-cutting (errors)", False),
    (lambda fqn: ".domain." in fqn, "domain", False),
    (lambda fqn: ".dto." in fqn, "dto", False),
    (lambda fqn: ".repository." in fqn, "repository", False),
    (lambda fqn: ".integration." in fqn, "integration", False),
)
LAYER_ORDER = ("surface (controller)", "service", "domain", "dto", "repository",
               "integration", "cross-cutting (api)", "cross-cutting (errors)")


def classify(fqn):
    for matches, layer, surface in LAYERS:
        if matches(fqn):
            return layer, surface
    return "other", False


def cmd_coverage(root, docs, fail_on, as_json):
    documented = {fqn for doc in docs for fqn in doc.fqns}
    rows = []
    for path in kotlin_files(root, (PRODUCTION_ROOT,)):
        for fqn in declared_fqns(path):
            layer, surface = classify(fqn)
            if layer:
                rows.append({"fqn": fqn, "layer": layer,
                             "file": str(path.relative_to(root)),
                             "is_surface": surface, "covered": fqn in documented})
    rows.sort(key=lambda row: row["fqn"])
    if as_json:
        print(json.dumps({"covered": sum(row["covered"] for row in rows), "total": len(rows),
                          "uncovered": [{key: value for key, value in row.items() if key != "covered"}
                                        for row in rows if not row["covered"]]}, indent=2))
    else:
        print("DOCUMENTATION COVERAGE\n")
        for layer in LAYER_ORDER:
            items = [row for row in rows if row["layer"] == layer]
            if not items:
                continue
            print(f"{layer:<28} {sum(row['covered'] for row in items)}/{len(items)} covered")
            for row in items:
                print(f"    [{'x' if row['covered'] else ' '}] {row['fqn']}")
            print()
        surfaces = [row for row in rows if row["is_surface"]]
        missing_surfaces = [row for row in surfaces if not row["covered"]]
        print(f"{sum(row['covered'] for row in rows)}/{len(rows)} documentable classes covered   "
              f"({len(surfaces) - len(missing_surfaces)}/{len(surfaces)} surfaces)")
    missing = [row for row in rows if not row["covered"]]
    return int((fail_on == "any" and missing) or
               (fail_on == "surfaces" and any(row["is_surface"] for row in missing)))


def cmd_impact(root, docs, base, head):
    changed = changed_files(root, base, head)
    impacted = {}
    for doc in docs:
        for fqn, path in anchor_files(root, doc).items():
            if path in changed:
                impacted.setdefault(doc.rel, set()).add(fqn)
    print(f"DIRECT IMPACT: {base}..{head}")
    print(f"changed files: {len(changed)}   impacted docs: {len(impacted)}\n")
    for rel in sorted(impacted):
        print(f"  {rel}")
        print(f"      via: {', '.join(sorted(fqn.rsplit('.', 1)[-1] for fqn in impacted[rel]))}")
    return 0


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--docs", default="docs", help="documentation directory (default: docs)")
    commands = parser.add_subparsers(dest="command", required=True)
    check = commands.add_parser("check")
    check.add_argument("--head", default="HEAD")
    coverage = commands.add_parser("coverage")
    coverage.add_argument("--json", action="store_true")
    coverage.add_argument("--fail-on", choices=("surfaces", "any", "none"), default="surfaces")
    impact = commands.add_parser("impact")
    impact.add_argument("base")
    impact.add_argument("head", nargs="?", default="HEAD")
    args = parser.parse_args()
    root = repo_root(Path.cwd())
    docs_dir = Path(args.docs) if Path(args.docs).is_absolute() else root / args.docs
    docs = load_docs(root, docs_dir)
    if args.command == "check":
        return cmd_check(root, docs, args.head)
    if args.command == "coverage":
        return cmd_coverage(root, docs, args.fail_on, args.json)
    return cmd_impact(root, docs, args.base, args.head)


if __name__ == "__main__":
    raise SystemExit(main())
