# DSL Scope Template

Use this template before implementing or extending a DSL.

## Target tests

- Files:
- Scenarios:

## Include in DSL (explicit)

- Primary aggregate/root:
- Related entities required for assembly:

## Exclude from DSL (explicit)

- Domain types intentionally out of scope:
- Relationships intentionally out of scope:

## Required invariants

- Required fields per root/child type:
- Relationship constraints:

## Assembly operations needed

- Example: attach child entities
- Example: share one object across multiple parents
- Example: set ordered relationships

## Minimal acceptance checks

- DSL creates all objects needed by target tests
- DSL assembles required relationships
- Invalid/missing required values fail fast with clear error
- API surface remains focused to selected subset only
