---
name: typesafe-test-data-dsl
description: Use this skill when implementing a type-safe Kotlin test data DSL for a selected subset of domain objects. Apply when tests need readable object graph construction and assembly (not just object creation). Ask for or infer the exact domain subset and build only that subset.
---

## Goal

Create a type-safe Kotlin DSL for test data that supports both:

- constructing domain objects, and
- assembling object graphs/relationships.

The DSL should cover only a focused subset of domain classes required by the target tests, not the entire domain model.

## Scope First (Required)

Before coding, determine the exact DSL scope from tests or user input:

1. Identify the target test scenarios.
2. List candidate domain types used there.
3. Choose only the minimum subset needed.

Use `references/scoping-template.md` to define the explicit include/exclude list.

## Default Implementation Procedure

1. Create a DSL entry point for the chosen aggregate (for example, `talks { ... }` or `orders { ... }`).
2. Add typed builder classes with `@DslMarker` to prevent receiver confusion.
3. Model required fields as mandatory in `build()` with clear `requireNotNull` errors.
4. Provide ergonomic helpers for relationships/assembly (for example, attach tags, add co-speakers, link parent/child entities).
5. Prevent invalid nesting or out-of-scope usage with explicit error methods or restricted API surface.
6. Return immutable snapshots from root builders (`toList`, copied sets/maps) so tests do not mutate internal DSL state accidentally.
7. Keep defaults practical for tests, but avoid hiding mandatory domain constraints.

## Design Rules

- Keep DSL API small and scenario-driven.
- Favor readability over configurability.
- Use domain names in DSL functions (`talk`, `speaker`, `tag`) only for in-scope types.
- Do not generate a "DSL for everything".
- Prefer one clear way to express each relationship.

## Gotchas

- Over-scoping is the main failure mode; only build what current tests need.
- Builder-only patterns often construct objects but do not assemble relationships; include assembly operations explicitly.
- Missing `@DslMarker` can cause accidental cross-scope mutation.
- Ensure deterministic collection behavior when assertions rely on order.
- Fail fast with helpful messages when required pieces are missing.

## Validation Loop

1. Verify DSL usage in target tests reads clearly and reduces temporary variables.
2. Run tests that consume the DSL.
3. If a test requires unsupported structure, extend scope deliberately (update include/exclude list first).
4. Stop when DSL covers target scenarios without broadening into unrelated domain areas.

## Output Style

When reporting work:

- state which domain types are included,
- state which related types are explicitly excluded,
- summarize assembly features added,
- note any invariants enforced by `build()`.
