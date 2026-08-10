# CLAUDE.md — conference-website

A Kotlin Spring Boot conference-website domain with an extensive Kotlin test
suite — the repo doubles as a Kotlin-testing testbed. Stack: Spring Boot 4 /
Kotlin 2.3.20 on Java 21, Undertow, Spring MVC (+ Project Reactor
for Engagement), Spring Data JPA/Hibernate, H2 (runtime) / PostgreSQL via
Testcontainers (integration tests), Maven.

## Documentation is the source of truth
Feature behavior, contracts, business rules, and architecture are documented in
[`docs/`](docs/). **Before implementing or changing a feature, read its doc**;
after changing code, keep the doc in sync.

- Overview: [`docs/architecture.md`](docs/architecture.md)
- Features: [`talks`](docs/talks.md) · [`speakers`](docs/speakers.md) ·
  [`tags`](docs/tags.md) · [`engagement`](docs/engagement.md)
- Shared: [`docs/cross-cutting.md`](docs/cross-cutting.md) (error model and DTO
  conversion)

### Keeping docs honest (required after code changes)
Docs carry frontmatter anchoring them to the code they describe. Use the tooling
in [`docs/tools/`](docs/tools/README.md):

```bash
python docs/tools/doc_staleness.py check       # fail if a doc lags its code (CI gate)
python docs/tools/doc_staleness.py coverage    # code with no doc (surfaces first)
python docs/tools/doc_staleness.py impact <base> [head]   # docs directly touched by a diff
```

Workflow:
1. Changed a documented class → update its doc, refresh `last_reviewed` and
   `reviewed_at_commit`, and run `check`.
2. Added a new API surface → generate a doc so `coverage` stays green. Use the
   **`generate-docs`** skill (`.claude/skills/generate-docs/`), which drives the
   codebase-memory MCP graph and `docs/TEMPLATE.md`.
3. On a PR, impact lists directly affected docs. For transitive blast radius,
   use the live codebase-memory MCP graph (trace_path / query_graph).

## Exploring the code
Prefer the **codebase-memory MCP** graph over blind file search:
`search_graph` / `query_graph` (find symbols & routes), `trace_path` (call
chains, blast radius), `get_code_snippet` (exact source), `get_architecture`
(structure). Project name: `Users-rbulsing-Projects-kotlin-coding-harness`.

## Scoped rules
Directory-specific conventions live in nested CLAUDE.md files (read the one for
the area you're editing):
- Production code → [`src/main/CLAUDE.md`](src/main/CLAUDE.md)
- Unit / slice tests → [`src/test/CLAUDE.md`](src/test/CLAUDE.md)
- Integration tests → [`src/integrationTest/CLAUDE.md`](src/integrationTest/CLAUDE.md)

## Build & test
- Build: `./mvnw verify` (Surefire runs `*Test`/`*Tests`; Failsafe runs `*IT`
  under the `integrationtest` profile).
- Kotlin 2.3.20 targets Java 21 (`kotlin-maven-plugin`).
- Integration tests need Docker (Testcontainers PostgreSQL).


# Development Rules for com.conference.website

This file provides guidance to AI agents and AI-assisted development tools when working with this project. This includes Claude Code, Cursor IDE, GitHub Copilot, Windsurf, and any other AI coding assistants.

## General Coding Principles
- **Fail fast — never swallow errors.** Always propagate errors and exit with code 1 immediately. No silent fallbacks, no ignored exceptions.
- **Never assume any default values anywhere.** Check for required values explicitly and fail if something is missing. Default values mask underlying issues and make them hard to debug.
- **Never suppress checks with annotations.** Fix the underlying issue instead. No `@Suppress`, `@SuppressWarnings`, `//noinspection`, `NOSONAR`, or any other mechanism that silences a checker.
- Always be explicit about values, paths, and configurations
- If a value is not provided, throw an exception — never silently fall back to a default

## Git Commit Guidelines

**IMPORTANT:** When creating git commits in this repository:
- **NEVER include AI attribution in commit messages**
- **NEVER add "Generated with [AI tool name]" or similar phrases**
- **NEVER add "Co-Authored-By: [AI name]" or similar attribution**
- **NEVER run `git add -A` or `git add .` - always stage files explicitly**
- Keep commit messages professional and focused on the changes made
- Commit messages should describe what changed and why, without mentioning AI assistance
- **ALWAYS run `git push` after creating a commit to push changes to the remote repository**

## Kotlin Execution Rules
- Kotlin code must be built and executed **only** via `./gradlew`
    - Example: `./gradlew run`, `./gradlew test`, `./gradlew build`
    - **Never** use `kotlin`, `kotlinc`, or `java -jar` directly
- **Never** modify the Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/`)
- All dependencies must be managed through `build.gradle.kts`
- Use the Gradle wrapper (`./gradlew`) exclusively, never a system-installed `gradle`

## Justfile Conventions
- **Use `printf` for colored or formatted output** — never `echo` with ANSI escape sequences, as some terminals won't render colors with `echo`. Plain `echo ""` is acceptable only for blank-line spacing.
- **Add an empty `@echo ""` line before and after each target's command block** to visually separate output between targets.
- **The `help` target must be a dedicated recipe** with manually written `printf` lines that group related commands and order them by typical execution flow (setup → run → code quality → testing). Never use `just --list`.
- **The default target (`_default`) must call `just help`.**
- **Every target must end with a clear status message**: green (`\033[32m`) on success, red (`\033[31m`) on failure with `exit 1`.
- **Composite targets (e.g. `ci`) must fail fast**: use `set -e` or `&&` chaining.
- All Kotlin execution in the justfile uses `./gradlew`, never `kotlin` directly
- Use `just init` to set up the project
- Use `just run` to execute the main program
- Use `just destroy` to clean build artifacts
- Use `just ci` to run all validation checks (verbose)
- Use `just ci-quiet` to run all validation checks (silent, fail-fast)


These tools enforce the rules:
- **ktlint** — formatting and style (including wildcard imports)
- **detekt** — bug patterns, exception handling, and complexity (`just code-security`)
- **Strict compiler** — every compiler warning is a build error (`allWarningsAsErrors`)
- **Konsist** — architecture constraints (`just code-architecture`)
- **Kover** — code coverage threshold (80%)
- **trivy** — dependency vulnerability scanning

## Project Structure
- Main source code lives in `src/main/kotlin/com/conference/website/`
- Test code lives in `src/test/kotlin/com/conference/website/`
- Utility scripts go in `scripts/`
- **Input data is organized**: `data/input/`
- **Output data is organized**: `data/output/`
- Configuration files go in `config/` (detekt, semgrep, codespell)
- **Never create Kotlin files in the project root directory**
    - Wrong: `./Main.kt`, `./Helper.kt`
    - Correct: `./src/main/kotlin/com/conference/website/Helper.kt`

## Optimization
- **Skip processing if output already exists** - Don't reprocess unnecessarily
- Check if output file exists before starting expensive operations
- Track skipped items separately in summary reports
- Allow users to force reprocessing by deleting output files
