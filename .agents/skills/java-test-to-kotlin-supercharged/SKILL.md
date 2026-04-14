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

## Reuse-existing rules

Use existing infrastructure first:

- object mothers/factories,
- HTTP/test client helpers,
- DSL/repository helpers,
- shared assertion/helpers already used by existing Kotlin tests.

When calling object-mother/factory methods that provide defaults:

- prefer default arguments and pass only values required by the test scenario,
- do not restate default values just because Java builders set them explicitly,
- override an argument only when it is assertion-relevant, setup-critical, or required for uniqueness/validation.

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
- Keep object-mother calls minimal: include only non-default arguments needed by the test intent.
- Keep mocking/stubbing behavior equivalent.
- Keep assertions explicit and readable (Kotest matchers).

### Web slice tests (`@WebMvcTest`)

- Reuse MockMvc Kotlin helpers when present (headers/json/response body parsing).
- Preserve request/response assertions and HTTP status semantics exactly.

### Integration tests (`@SpringBootTest`)

- Keep profile and transactional behavior aligned with Java test.
- Reuse transactional/cleanup helpers (`testDataScope`, `withNewTransaction`, repository support) when applicable.
- Preserve visibility/commit-boundary behavior in API-level assertions.

## Assertion migration guidance

- Preserve order sensitivity (`containsExactly` vs any-order checks).
- Preserve nullability/exception semantics.
- For collection field checks, map fields explicitly before asserting.

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
