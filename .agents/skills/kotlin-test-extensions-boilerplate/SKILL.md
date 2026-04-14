---
name: kotlin-test-extensions-boilerplate
description: Use this skill when Kotlin test code contains duplicated setup or helper logic repeated at least 3 times. Create focused Kotlin extension functions (or small helper functions) to remove repetition while preserving test behavior and readability.
---

## Goal

Extract repeated Kotlin test plumbing into small extension/helper functions so tests stay focused on intent.

Trigger threshold: apply this skill when the same pattern appears 3+ times.

## Default Procedure

1. Scan Kotlin test files and identify duplicated code blocks repeated 3+ times.
2. Group duplicates by kind (data creation, assertion setup, fixture wiring, HTTP setup, response parsing, coroutine helpers, transactional helpers).
3. For each group, extract the smallest reusable unit:
   - extension function when behavior belongs to a type,
   - top-level helper when no receiver type is natural.
4. Keep defaults for common values, but allow explicit overrides.
5. Replace duplicated call sites with the new utility.
6. Remove dead imports/helpers and keep behavior-identical.

Use extraction and naming rules from `references/patterns.md`.

## Boundaries

- Apply this skill to test code only.
- Do not change endpoint behavior or assertion meaning.
- Keep extensions small and composable; avoid large helper methods that hide important assertions.
- Do not extract code that appears fewer than 3 times unless requested.

## Gotchas

- Prefer extension functions only when a receiver type makes the API clearer.
- Keep helpers narrow: one responsibility per function.
- Avoid over-abstraction: if extraction harms readability at call sites, keep inline.
- Preserve assertion semantics exactly (order-sensitive vs order-insensitive, nullability checks, exception checks).
- Name helpers by intent, not implementation details.

## Common Refactor Example

Before:

```kotlin
val speakerA = createSpeakerRequest(name = "Ada")
val speakerB = createSpeakerRequest(name = "Grace")
val speakerC = createSpeakerRequest(name = "Linus")

val savedA = speakerService.createSpeaker(speakerA)
val savedB = speakerService.createSpeaker(speakerB)
val savedC = speakerService.createSpeaker(speakerC)
```

After:

```kotlin
fun SpeakerService.createSpeakers(vararg requests: CreateSpeakerRequest): List<SpeakerDto> =
    requests.map(::createSpeaker)

val (savedA, savedB, savedC) = speakerService.createSpeakers(
    createSpeakerRequest(name = "Ada"),
    createSpeakerRequest(name = "Grace"),
    createSpeakerRequest(name = "Linus")
)
```

## Validation Loop

1. Confirm each extracted helper replaces code repeated 3+ times.
2. Confirm call sites are shorter and still explicit about test intent.
3. Run tests that use the extracted helpers.
4. If behavior or semantics changed, revert and re-extract more narrowly.
5. Stop only when tests pass and readability improves.

## Output Style

When reporting completion, include:

- utility file(s) added or updated,
- extension functions introduced or changed,
- representative test files refactored to use them.
