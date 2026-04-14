# kotlin-for-testing

Spring I/O demo repo for "Java tests before / Kotlin tests after".

## Non-negotiable

- Keep the contrast: Java tests are intentionally verbose "before" examples; Kotlin tests are the concise "after" examples.
- Do not modernize/refactor Java tests to look like Kotlin tests.

## Build and test commands

- `./gradlew test` runs unit-style tests (`src/test/java` + `src/test/kotlin`).
- `./gradlew integrationTest` runs integration tests from `src/integrationTest/**`.
- `./gradlew check` includes `integrationTest` (it is wired as a dependency).
- `./mvnw test` runs unit-style tests; `./mvnw verify` runs failsafe integration tests.
- Single test (Gradle): `./gradlew test --tests 'com.conference.website.service.E10_TagServiceSuperchargedTest'`.
- Single integration test (Gradle): `./gradlew integrationTest --tests 'com.conference.website.api.E02_TalkControllerSuperchargedIT'`.

## Integration-test gotchas

- Integration DB uses Testcontainers JDBC (`jdbc:tc:postgresql:16-alpine:///conference` in `src/integrationTest/resources/application-it.yaml`), so Docker must be running.
- Integration tests use `@ActiveProfiles("it")`; keep that profile when adding new integration tests.

## Repo structure that matters

- App entrypoint: `src/main/java/com/conference/website/ConferenceWebsiteApplication.java`.
- Main app code is Java under `src/main/java/com/conference/website/**`.
- Kotlin DTOs are currently under `src/main/kotlin/kom/conference/website/dto/**` (`kom`, not `com`); Java services call both Java DTOs and these Kotlin DTOs.
- Tests are split by language and layer:
  - Java before-tests: `src/test/java/**`, `src/integrationTest/java/**`
  - Kotlin after-tests/helpers: `src/test/kotlin/**`, `src/integrationTest/kotlin/**`

## Talk file naming

- Subject numbering is encoded in file names as `E0N_*` across tests/helpers.
- Use this prefix first when locating files for a talk section.
