# kotlin-for-testing

Java-first Spring Boot conference website domain used for a talk about Kotlin for testing.

## Tech baseline

- Java 25 (toolchain)
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
