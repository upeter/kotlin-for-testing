---
name: java-test-to-kotlin-supercharged
description: Use this skill to convert a Java test into an equivalent Kotlin test. Reuse existing Kotlin test infrastructure by default, preserve behavior, and verify with focused test commands.
---

## Goal

Convert one Java test class into an equivalent Kotlin test that preserves behavior while improving Kotlin readability and test ergonomics.

Default mode: `reuse-existing`.

## Non-negotiable

- Preserve test behavior and assertion semantics exactly.
- Do not change production code unless required for compatibility and explicitly requested.
- In Kotlin tests, prefer constructor injection for Spring beans; do not introduce field injection via `lateinit var` unless constructor injection is not viable for that specific test setup.
- Convert Java camelCase test method names to Kotlin backticked sentence names, preserving intent (example: Java `shouldCreateTalkAndSpeakerCorrectly()` -> Kotlin `` `should create talk and speaker correctly`() ``).

## Inputs

- Source Java test class path.
- Target Kotlin test class path/name.
- Target scope: unit/web slice/integration.

If target path is not provided, infer it from existing project naming/location conventions.

## Procedure (Checklist)

- [ ] 1. Classify source test type (`@WebMvcTest`, service unit test, `@SpringBootTest` IT).
- [ ] 2. Create Kotlin target file in the matching test source set for the project.
- [ ] 3. Port setup and test flow first (Arrange/Act/Assert), without changing behavior.
- [ ] 4. Convert assertions to Kotest idioms with matching semantics.
- [ ] 5. Reuse existing infra where available (object mothers, utils, DSL, repository helpers).
- [ ] 6. Run focused verification command for the converted class.
- [ ] 7. Rename converted Kotlin test functions to backticked, human-readable sentence form.

## Reuse-existing rules

Use existing infrastructure first:

- object mothers/factories,
- HTTP/test client helpers,
- DSL/repository helpers,
- shared assertion/helpers already used by existing Kotlin tests,
- existing Kotlin extension functions/methods (for example, `toDto()` extensions) before calling static conversion helpers directly.

When both an extension and a static helper exist for the same behavior:

- prefer the extension form already used in nearby Kotlin tests,
- keep conversion semantics identical (nullability, ordering, default values),
- do not introduce new conversion helpers if an existing extension already matches the test intent.

When calling object-mother/factory methods that provide defaults:

- prefer the shortest valid call first (for example, `objectMotherMethod()`, but not `objectMotherMethod(arg1, arg2, arg3)` if all args have defaults),
  - if an argument is required for the test, provide only the strictly needed one(s). For the rely on defaults arguments.
- do not restate default values just because Java builders/constructors set them explicitly,
- override an argument only when it is assertion-relevant, setup-critical, or required for uniqueness/validation,
- if every provided argument matches function defaults and none are needed for intent, remove all of them.

Default-value shorthand policy:

- If a factory call can be zero-arg without changing the test's asserted behavior, use zero-arg.
- If only one or two fields matter, pass only those fields.
- Treat spelling out default-valued named args as a style violation unless there is a documented reason.

If required infra is missing, do not broad-build new shared infrastructure by default. Use the smallest local fallback needed to finish this conversion. If reusable infra is clearly warranted, recommend running one of:

- `kotlin-object-mother-default-args`
- `kotlin-test-extensions-boilerplate`
- `typesafe-test-data-dsl`

## Project gotchas

- Watch for package or namespace mismatches between Java and Kotlin models/DTOs.
- Preserve project-specific integration profile/environment annotations.
- If integration tests depend on containers/external services, ensure prerequisites are available before running them.
- Prefer focused test commands while iterating; avoid full-suite runs until final verification.
- Preserve existing naming/numbering conventions for converted test files.

## Conversion defaults by test type

### Service/unit tests

- Prefer concise Kotlin setup and named arguments.
- Keep object-mother calls minimal: include only non-default arguments needed by the test intent; prefer zero-arg calls when defaults are sufficient.
- For Spring-managed tests, inject collaborators via primary constructor parameters instead of `@Autowired lateinit var` fields.
- Use backticked test names written as real sentences (space-separated words) rather than camelCase.
- Keep mocking/stubbing behavior equivalent.
- Keep assertions explicit and readable (Kotest matchers).

### Web slice tests (`@WebMvcTest`)

- Reuse MockMvc Kotlin helpers when present (headers/json/response body parsing).
- Preserve request/response assertions and HTTP status semantics exactly.

### Integration tests (`@SpringBootTest`)

- Keep profile and transactional behavior aligned with Java test.
- Prefer constructor injection for Spring beans in the test class.
- Reuse transactional/cleanup helpers (`testDataScope`, `withNewTransaction`, repository support) when applicable.
- Preserve visibility/commit-boundary behavior in API-level assertions.

## Assertion migration guidance

- Preserve order sensitivity (`containsExactly` vs any-order checks).
- Preserve nullability/exception semantics.
- For collection field checks, map fields explicitly before asserting.
- Avoid redundant assertions: if full-object equality already proves the same fields, do not add extra field-by-field checks.
- Keep only assertions that add independent semantic coverage (for example, ordering guarantees, exception details, nullability distinctions, or values not covered by an existing equality assertion).
- Treat generated "readability" assertions that restate already-proven values as a style violation.

Use existing migration skills only when they directly match the assertion style being migrated:

- `kotest-from-assertj` for AssertJ chain patterns.
- `kotest-from-junit-assertions` for JUnit assertion functions.

## Validation loop

1. Run focused test command for the converted class:

```bash
./gradlew test --tests 'your.package.YourConvertedTest'
```

or integration:

```bash
./gradlew integrationTest --tests 'your.package.YourConvertedIT'
```

2. If failing, fix semantic mismatches (assertion type, ordering, nullability, transaction boundaries).
3. Re-run until green.

## Output format

Report completion with:

- source Java test and new Kotlin test paths,
- what existing infra was reused,
- any local fallback helpers added,
- semantics-sensitive decisions (ordering/exceptions/nullability),
- exact verification command used.
