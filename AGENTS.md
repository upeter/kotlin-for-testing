# kotlin-for-testing

Demo application for the Spring I/O talk **"Supercharging Spring Boot tests with Kotlin DSL power"**.
Every Java test is the intentional **before** — verbose, clumsy, noisy.
Every Kotlin test is the **after** — concise, safe, readable.
Never improve or modernise the Java tests. That contrast is the point of the talk.

## Tech stack

- Java 21 (toolchain) + Kotlin 2.x
- Spring Boot 4.x — MVC, Data JPA, Validation, Actuator
- H2 in-memory database
- Kotest 6.x (`kotest-assertions-core-jvm`)
- SpringMockK (`springmockk`) — Kotlin-native mocking for Spring
- Jackson Kotlin module (`jackson-module-kotlin`)

## Build

```bash
./gradlew test          # unit-style tests only
./gradlew integrationTest
./mvnw test             # unit-style tests only
./mvnw verify           # runs integration tests via failsafe
./gradlew bootRun       # run the app
```

Power-assert is configured in `build.gradle.kts` for `kotlin.test.*` and `io.kotest.matchers.shouldBe`.

## Talk section → file map

| # | Feature | Java (before) | Kotlin (after) |
|---|---------|--------------|----------------|
| 1 | Kotest matchers + power-assert | `TalkControllerTest` (AssertJ + jsonPath) | `TalkServiceSuperchargedIT` (`shouldBe`, `shouldBeEqualUsingFields`) |
| 2 | Named/default args + data classes | `data/builders/` (11 builder classes) | `data/ObjectMother.kt` (factory fns with defaults) |
| 3 | Extension fns + reified generics | `TalkControllerTest` (verbose MockMvc) | `utils/TestUtils.kt` + `TalkControllerSuperchargedIT` (`jsonContent<T>`, `readBody<T>`) |
| 4 | Coroutines for async/concurrency | `TalkEngagementServiceTest` (StepVerifier) | `TalkEngagementServiceSuperchargedTest` (`runTest`, `awaitSingle`) |
| 5 | Function literal with receiver DSL | _(not yet implemented)_ | _(to be created — grand finale)_ |

## Key packages

```
src/main/java/com/conference/website/
  api/          Controllers + ApiExceptionHandler
  domain/       JPA entities (Talk, Speaker, Tag, Rating, ScheduleSlot)
  dto/          Java records (request/response DTOs)
  service/      TalkService, SpeakerService, TagService, ViewTrackingService, TalkEngagementService
  integration/  Fake external clients (MetricsClient, BuzzClient)
  repository/   Spring Data JPA interfaces

src/test/java/com/conference/website/
  api/          Java unit tests — WebMvcTest + Mockito (BEFORE)
  data/builders/ 11 Java builder classes for test data

src/integrationTest/java/com/conference/website/
  api/          Java integration tests — SpringBootTest (BEFORE)
  service/      Java integration tests — SpringBootTest (BEFORE)

src/integrationTest/kotlin/com/conference/website/
  api/          Kotlin integration tests (AFTER)

src/test/kotlin/com/conference/website/
  service/      Kotlin tests (AFTER)
  data/         ObjectMother.kt — factory functions with named/default args
  utils/        TestUtils.kt — extensions + reified generics
```

## Scoped rules

Detailed coding conventions live in `.claude/rules/` and load only when relevant:
- `kotlin-tests.md` — applies to `src/test/kotlin/**`
- `kotlin-tests.md` — applies to `src/integrationTest/kotlin/**`
- `java-tests.md`  — applies to `src/test/java/**`
- `java-tests.md`  — applies to `src/integrationTest/java/**`
