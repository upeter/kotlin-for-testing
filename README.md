# Supercharge your Spring Boot Tests with Kotlin DSL Power

Companion repository for the talk **"Supercharge your Spring Boot Tests with Kotlin DSL Power"** presented at [Spring I/O 2026](https://springio.net) in Barcelona.

A Java-first Spring Boot conference website domain where every Java test has a Kotlin "after" counterpart — showcasing how Kotlin DSLs, matchers, and language features can dramatically improve test readability and ergonomics.

## Presentation

[![Presentation](https://img.shields.io/badge/Google%20Slides-Open%20Presentation-FBBC04?style=for-the-badge&logo=googleslides&logoColor=white)](https://docs.google.com/presentation/d/e/2PACX-1vRsYk0egkPWb2puLUS-vlalwpWgUhml0y7aK5k4wbESbOlFHv8vjphFxPqgEfBoow/pub?start=false&loop=false&delayms=3000)

## Tech baseline

- Java 21 (toolchain)
- Spring Boot 4.x
- Spring MVC + Spring Data JPA + Validation + Actuator
- H2 in-memory database for local development

## Run

```bash
./gradlew bootRun
```

or with Maven:

```bash
./mvnw spring-boot:run
```

## Tests

```bash
./gradlew test          # unit-style tests only
./gradlew integrationTest
./mvnw test             # unit-style tests only
./mvnw verify           # runs integration tests via failsafe
```

Integration tests use PostgreSQL Testcontainers (`jdbc:tc:` datasource URL), so Docker must be available.

## Talk subject → test mapping

The talk content is organized by numbered subjects. In the codebase, the same number is used as a file prefix:

- Subject `N` maps to files named `E0N_...`
- Tests use `...Test` / `...IT` suffixes
- Supporting utilities for a subject (DSL/helpers/object mothers) may use the same `E0N_` prefix without `Test` in the filename

Use this index when navigating:

| # | Subject | Marker to search |
|---|---------|------------------|
| 1 | Kotlin Matcher DSLs | `E10_`           |
| 2 | Power Assert | `E09_`           |
| 3 | Test Data Builders | `E08_`           |
| 4 | Test Data Mutators | `E07_`           |
| 5 | Custom Test Extensions | `E06_`           |
| 6 | Serialization Utilities | `E05_`           |
| 7 | Coroutines | `E04_`           |
| 8 | Test Data Creation DSL | `E03_`           |
| 9 | Test Data Lifecycle DSL | `E02_`           |
| 10 | Kotlin Notebooks | `E01_`           |

## Claude Code Skills

This repository ships with a set of [Claude Code](https://docs.anthropic.com/en/docs/claude-code) skills (in `.agents/skills/`) that automate common conversion and refactoring tasks demonstrated in the talk:

| Skill | Description |
|-------|-------------|
| **java-test-to-kotlin-supercharged** | Convert a Java test class into an equivalent Kotlin test — reusing existing Kotlin test infrastructure, preserving behavior, and applying idiomatic Kotlin patterns. |
| **kotest-from-assertj** | Migrate AssertJ assertion chains in Kotlin tests to the Kotest matcher DSL while preserving assertion semantics. |
| **kotest-from-junit-assertions** | Migrate JUnit assertions (`assertEquals`, `assertNotNull`, `assertThrows`, …) in Kotlin tests to Kotest matchers. |
| **kotlin-data-class-from-java-records** | Convert Java record DTOs to Kotlin data classes with `@JvmRecord`, correct nullability, and Java interop. |
| **kotlin-object-mother-default-args** | Create Kotlin Object Mother factory functions with default arguments for test data objects. |
| **kotlin-test-extensions-boilerplate** | Extract duplicated test setup/helper logic (3+ occurrences) into focused Kotlin extension functions. |
| **typesafe-test-data-dsl** | Build a type-safe Kotlin DSL for constructing and assembling domain object graphs in tests. |

## Initial API surface

- `POST /api/speakers`
- `GET /api/speakers`
- `POST /api/tags`
- `GET /api/tags`
- `POST /api/talks`
- `GET /api/talks?level=BEGINNER&tag=java`
- `GET /api/talks/{talkId}`
- `POST /api/talks/{talkId}/ratings`
- `PUT /api/talks/{talkId}/schedule`
- `POST /api/talks/{talkId}/views`
- `POST /api/talks/{talkId}/views/simulate?events=1000`
- `GET /api/talks/{talkId}/views`
- `GET /api/talks/{talkId}/engagement`
