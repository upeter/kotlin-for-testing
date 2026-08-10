# Integration test rules (`src/integrationTest`)

A separate source set run by **Failsafe**; name classes `*IT` (also
`*SuperchargedIT` for the Kotlin variants). Activated via the Spring profile
`integrationtest` (Failsafe) — in-class use `@ActiveProfiles("it")`. Config:
`src/integrationTest/resources/application-it.yaml`.

## Real database (Testcontainers)
These tests run against **real PostgreSQL 16 via Testcontainers**
(`jdbc:tc:postgresql:16-alpine:///conference`) — **Docker must be available**. No
manual DB setup; the container is provisioned by the JDBC URL. Use this set (not
`src/test`) whenever behavior depends on the real database.

## HTTP boundary tests
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `@AutoConfigureRestTestClient`.
- Drive the API through `RestTestClient`, using the shared helpers:
  `defaultHeaders()` (auth + correlation id), `readBody<T>()`, `jsonContent(obj)`
  from `com.conference.website.utils`.
- Assert against real serialized responses (status + body), not service internals.

## Transactions & data visibility
When asserting committed-vs-uncommitted behavior across transactions, use the
provided scopes:
- `testDataScope { … }` to bound a scenario,
- `withNewTransaction { … }` to commit setup in its own transaction,
- `persistWithUndo()` for **guaranteed cleanup** of fixtures created outside the
  test's rollback.

Reuse the same DSL / ObjectMother test-data helpers as `src/test` (see
[`src/test/CLAUDE.md`](../test/CLAUDE.md)) — don't duplicate fixture logic.
