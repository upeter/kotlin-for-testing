---
name: kotest-from-assertj
description: Use this skill when converting AssertJ assertions in Kotlin tests to Kotest matcher DSL. Apply it to Kotlin test files, preserve assertion semantics, and keep the test intent unchanged. Do not use this skill for JUnit assertEquals/assertNotNull migrations.
---

## Goal

Convert AssertJ assertions in Kotlin tests into Kotest matcher DSL while preserving behavior and readability.

## Default Procedure

1. Identify AssertJ usage in Kotlin tests (`assertThat`, `assertThatThrownBy`, chained assertions, `extracting`).
2. Apply rewrite defaults from `references/rewrite-map.md`.
3. Keep semantics exact:
   - order-sensitive vs order-insensitive checks,
   - equality vs identity,
   - empty vs null.
4. Prefer direct, explicit Kotlin assertions on fields/properties instead of long chain conversions.
5. When validating several related properties, group with `assertSoftly { ... }`.
6. Remove AssertJ imports and add required Kotest imports.

## Boundaries

- Use this skill for AssertJ-to-Kotest migration only.
- Do not convert JUnit primitive assertions here (`assertEquals`, `assertNotNull`, `assertThrows`).
- Keep behavior identical; only change assertion style.
- If Java tests exist, do not migrate them with this skill unless explicitly requested.

## Gotchas

- Preserve collection order semantics:
  - `containsExactly(...)` is order-sensitive.
  - `containsExactlyInAnyOrder(...)` is order-insensitive.
- `extracting(...)` chains often become clearer as explicit property checks in Kotlin.
- For object-list extraction checks, map first, then assert:
  - `items.map { it.name }.shouldContainAllInAnyOrder(...)`
- `assertThatThrownBy { ... }` maps to `shouldThrow<ExceptionType> { ... }`.
- Keep nullability semantics exact (`isNull` vs `isNotNull`).

## Common Rewrite Example

Before:

```kotlin
assertThat(result.id).isNotNull()
assertThat(result.name).isEqualTo("Ada")
assertThat(tags).extracting { it.name }.containsExactlyInAnyOrder("kotlin", "testing")
assertThatThrownBy { service.create(invalidInput) }
    .isInstanceOf(IllegalArgumentException::class.java)
    .hasMessageContaining("invalid")
```

After:

```kotlin
result.id.shouldNotBeNull()
result.name shouldBe "Ada"
tags.map { it.name }.shouldContainAllInAnyOrder("kotlin", "testing")
shouldThrow<IllegalArgumentException> {
    service.create(invalidInput)
}.message.shouldContain("invalid")
```

## Validation Loop

After rewrites:

1. Confirm no AssertJ assertion calls remain in the edited Kotlin file.
2. Run focused tests for the changed test class.
3. If matcher imports or semantics fail, fix and rerun.
4. Finish only when tests pass and assertion intent is preserved.

Suggested command pattern:

```bash
./gradlew test --tests "*YourTestClassPattern*"
```

## Output Style

When reporting completion, include:

- which file(s) were converted,
- which AssertJ patterns were replaced,
- any semantics choices (especially ordering and extraction behavior).
