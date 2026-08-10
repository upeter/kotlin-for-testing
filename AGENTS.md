# Shared agent instructions

This repository's canonical agent guidance lives in [`CLAUDE.md`](CLAUDE.md).
Read and follow it before making changes. It defines the documentation-first
workflow, code-exploration tools, build commands, and the scoped guidance below.

When working in a subdirectory that contains an `AGENTS.md` or `CLAUDE.md`, read
and follow that file as well; its rules refine the repository-wide instructions.

## Codebase Knowledge Graph

Prefer the `codebase-memory-mcp` graph for code discovery:

1. `search_graph` for symbols, classes, routes, and variables.
2. `trace_path` for callers and callees.
3. `get_code_snippet` for the exact source of a symbol.
4. `query_graph` for complex relationships.
5. `get_architecture` for a high-level overview.

Use text search only for string literals, error messages, configuration, other
non-code files, or when the graph does not provide enough information.
