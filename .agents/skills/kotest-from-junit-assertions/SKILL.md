---
name: kotest-from-junit-assertions
description: Use this skill when converting Kotlin tests from JUnit assertions like assertEquals, assertNotNull, assertNull, assertTrue, assertFalse, and assertThrows to Kotest matcher DSL in this repository, especially E10-style transformations. Apply it for Kotlin AFTER tests. Do not use this skill for AssertJ chain conversions.
---

## Goal

Convert JUnit-style assertions in Kotlin tests into Kotest matcher DSL so tests read like the E10 supercharged style.

This repository keeps Java tests as intentional BEFORE examples. Never modernize Java tests.

## Default Procedure

1. Identify JUnit assertions in Kotlin test files (`assertEquals`, `assertNotNull`, `assertNull`, `assertTrue`, `assertFalse`, `assertThrows`).
2. Apply the default rewrite mappings from `references/rewrite-map.md`.
3. Prefer matcher assertions on the actual value (`actual shouldBe expected`) and collection/string matchers over boolean assertions.
4. If multiple related checks target one result object, group with `assertSoftly { ... }`.
5. Replace exception assertions with `shouldThrow<T> { ... }` and then assert on message/details with Kotest string matchers.
6. Remove unused JUnit assertion imports and add required Kotest imports.

## Boundaries

- Use this skill for JUnit assertion migration only.
- Do not rewrite AssertJ chains here (handled by the AssertJ-to-Kotest skill).
- Keep test behavior and intent identical; only modernize assertion style.

## Gotchas

- Preserve order semantics:
  - `containsExactly` behavior maps to order-sensitive checks (`shouldContainInOrder`).
  - Set-like checks should remain order-insensitive (`shouldContainAllInAnyOrder`).
- Prefer specific matchers over boolean wrappers:
  - Replace `assertTrue("x" in msg)` with string/collection matchers.
- Null-safe exception message checks:
  - `shouldThrow<...> { ... }.message.shouldContain(...)` is acceptable in this repo style.
- Size checks should use matcher DSL (`shouldHaveSize`) instead of `assertEquals(expected, list.size)`.

## Validation Loop

After rewrites:

1. Confirm no JUnit assertion functions remain in the edited Kotlin file.
2. Run focused tests for the edited class.
3. If imports or matcher semantics fail, fix and rerun.
4. Stop only when tests pass and assertion intent is unchanged.

Suggested command pattern:

```bash
./gradlew test --tests "com.conference.website.service.*E10*"
```

## Output Style

When reporting completion, include:

- which file(s) were converted,
- which assertion families were replaced,
- any matcher-semantics decisions (order-sensitive vs order-insensitive).
