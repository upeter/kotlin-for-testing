---
name: kotlin-object-mother-default-args
description: Use this skill when creating Kotlin Object Mother factory functions for test data objects (requests, DTOs, entities). Create Kotlin methods with default arguments for common values, nullable types for optional properties, and required non-null parameters without defaults for mandatory properties.
---

## Goal

Create concise Kotlin factory functions for test data objects that are safe, readable, and easy to override per test.

## Default Procedure

1. Identify target objects used in tests (for example request types, DTOs, domain entities).
2. Determine constructor/request fields and classify each using `references/classification-checklist.md`:
   - mandatory: must be passed by caller,
   - optional: nullable by domain/API contract,
   - convenience default: non-null with sensible default.
3. Create Kotlin factory function(s) in an object-mother file (for example `ObjectMother.kt`).
4. Encode parameters with these rules:
   - mandatory -> non-null parameter without default,
   - optional -> nullable type (`Type?`) with `null` default only if absence is valid,
   - convenience values -> non-null parameter with deterministic default.
5. Keep function naming explicit and creation-oriented (`createSpeakerRequest`, `createTalkDto`, etc.).
6. When Java tests call these functions, add `@JvmOverloads` to preserve Java ergonomics.
7. Update Kotlin tests to use named arguments for focused overrides.

## Boundaries

- Use this skill for object-mother factory creation for test data objects.
- Do not build a full receiver DSL here (that belongs to a separate DSL skill).
- Preserve object semantics; only change test-data creation style.

## Gotchas

- Do not give defaults to truly mandatory properties (hides required dependencies).
- Avoid random defaults; use stable, deterministic values to keep tests reproducible.
- For collections, default to `emptyList()`/`emptySet()` unless domain requires otherwise.
- Prefer immutable inputs in factory signatures (`List<T>` not mutable collections) unless mutability is required.
- Keep default values realistic enough to pass validations in typical tests.
- If the source type is Java and nullability is ambiguous, infer optionality from validation annotations, API usage, and existing tests before deciding `Type` vs `Type?`.

## Common Rewrite Example

Before:

```kotlin
val request = CreateUserRequest(
    name = "Ada",
    email = "ada@example.com",
    company = "Analytical Engines",
)
```

After (Object Mother):

```kotlin
@JvmOverloads
fun createUserRequest(
    name: String = "Ada Lovelace",
    email: String = "ada@example.com",
    company: String? = "Analytical Engines",
): CreateUserRequest = CreateUserRequest(name, email, company)
```

Mandatory-field example:

```kotlin
@JvmOverloads
fun createOrderRequest(
    customer: CustomerDto,
    itemIds: List<Long> = emptyList(),
    note: String? = null,
): CreateOrderRequest = CreateOrderRequest(customer, itemIds, note)
```

In the second example, `customer` is required and intentionally has no default.

## Validation Loop

After introducing/updating object mothers:

1. Verify every mandatory property is still impossible to omit.
2. Verify optional fields are nullable only where domain/API allows omission.
3. Run tests touching migrated factories.
4. Fix signature/default issues and rerun until green.

Suggested command pattern:

```bash
./gradlew test --tests "*YourTestClassPattern*"
```

## Output Style

When reporting completion, include:

- which factory functions were added/updated,
- which parameters were marked mandatory vs optional vs defaulted,
- whether `@JvmOverloads` was added for Java interop.
