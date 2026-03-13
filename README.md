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
