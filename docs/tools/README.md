# docs/tools — documentation staleness, direct impact & coverage

doc_staleness.py keeps docs/*.md honest using only Git, frontmatter, and the
checked-out Kotlin source tree. It deliberately has no graph snapshot and does
not need MCP access, so it is safe and deterministic in CI. Python 3.8+ and Git
are the only requirements.

The script reads each page's frontmatter:

- documents: lists the symbols the page owns. These anchors drive check, impact,
  and coverage.
- references: is explanatory only; it never makes another page stale.
- reviewed_at_commit: is the Git baseline after which a change requires review.

## Commands

    # CI gate: fails when an owned Kotlin anchor cannot be resolved or its current
    # source file changed since the page's reviewed_at_commit.
    python docs/tools/doc_staleness.py check

    # Direct, file-level impact: which docs own anchors in files touched by a diff?
    python docs/tools/doc_staleness.py impact <base> [head]

    # Scan src/main/kotlin declarations and report documentation coverage.
    python docs/tools/doc_staleness.py coverage
    python docs/tools/doc_staleness.py coverage --json
    python docs/tools/doc_staleness.py coverage --fail-on any|surfaces|none

Anchor resolution first tries the conventional Kotlin package path, then finds a
matching Kotlin file or declaration. This supports files containing several
types, such as domain/Domain.kt and repository/Repositories.kt, without
maintaining a duplicated symbol index.

## Live blast radius

Blast radius is intentionally not an offline CLI command. It needs a fresh call
graph, so run it in an MCP-capable agent session:

1. Ensure the repository is indexed with index_repository; for team reuse,
   request persistence: true to write .codebase-memory/graph.db.zst.
2. Use trace_path with mode calls, the changed symbol, direction inbound, and a
   suitable depth to find callers.
3. Use query_graph when you need to combine paths or inspect several changed
   symbols. Map reached classes/services/controllers to pages that own them in
   documents:.

This keeps CI independent of the graph backend while making interactive impact
analysis use the current graph rather than a potentially stale JSON export.

## Suggested CI wiring

1. coverage --fail-on surfaces — each controller has an owning document.
2. check — each document is reviewed after its owned source changes.
3. impact <merge-base> HEAD — print directly affected documents on a PR.
