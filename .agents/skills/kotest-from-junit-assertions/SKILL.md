---
name: kotest-from-junit-assertions
description: Use this skill when converting Kotlin tests from JUnit assertions like assertEquals, assertNotNull, assertNull, assertTrue, assertFalse, and assertThrows to Kotest matcher DSL. Apply it to Kotlin test files and preserve behavior. Do not use this skill for AssertJ chain conversions.
---

## Goal

Convert JUnit-style assertions in Kotlin tests into Kotest matcher DSL while keeping test behavior identical.

## Default Procedure

1. Identify JUnit assertions in Kotlin test files (`assertEquals`, `assertNotNull`, `assertNull`, `assertTrue`, `assertFalse`, `assertThrows`).
2. Apply the default rewrite mappings from `references/rewrite-map.md`.
3. Prefer matcher assertions on the actual value (`actual shouldBe expected`) and collection/string matchers over boolean assertions.
4. If multiple related checks target one result object, validate them inside `instance.apply { ... }`.
5. If multiple related checks target one result object and should report together, wrap the `apply { ... }` block in `assertSoftly { ... }`.
6. Replace exception assertions with `shouldThrow<T> { ... }` and then assert on message/details with Kotest string matchers.
7. Remove unused JUnit assertion imports and add required Kotest imports.

## Boundaries

- Use this skill for JUnit assertion migration only.
- Do not rewrite AssertJ chains here (handled by the AssertJ-to-Kotest skill).
- Keep test behavior and intent identical; only modernize assertion style.
- If Java tests exist in the project, do not rewrite Java assertions with this skill.

## Gotchas

- Preserve order semantics:
  - `containsExactly` behavior maps to order-sensitive checks (`shouldContainInOrder`).
  - Set-like checks should remain order-insensitive (`shouldContainAllInAnyOrder`).
- Prefer specific matchers over boolean wrappers:
  - Replace `assertTrue("x" in msg)` with string/collection matchers.
- Null-safe exception message checks:
  - `shouldThrow<...> { ... }.message.shouldContain(...)` is acceptable when message assertions are required.
- Size checks should use matcher DSL (`shouldHaveSize`) instead of `assertEquals(expected, list.size)`.
- For one instance with several attribute checks, prefer:
  - `result.apply { field1 shouldBe ...; field2.shouldNotBeNull() }`

## Common Rewrite Example

Before:

```kotlin
assertEquals(1, users.size)
assertNotNull(users.first().id)
assertTrue("active" in status)
assertThrows<IllegalArgumentException> {
    service.create(invalidInput)
}
```

After:

```kotlin
users shouldHaveSize 1
users.first().id.shouldNotBeNull()
status shouldContain "active"
shouldThrow<IllegalArgumentException> {
    service.create(invalidInput)
}
```

## Validation Loop

After rewrites:

1. Confirm no JUnit assertion functions remain in the edited Kotlin file.
2. Run focused tests for the edited class.
3. If imports or matcher semantics fail, fix and rerun.
4. Stop only when tests pass and assertion intent is unchanged.

Suggested command pattern:

```bash
./gradlew test --tests "*YourTestClassPattern*"
```

## Output Style

When reporting completion, include:

- which file(s) were converted,
- which assertion families were replaced,
- any matcher-semantics decisions (order-sensitive vs order-insensitive).
