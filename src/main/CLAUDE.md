# Production code rules (`src/main`)

Read the relevant feature doc in [`docs/`](../../docs/) before changing behavior.
Conventions below are derived from the current code — follow them for consistency.

## Layering (strict, one-directional)
`api → service → repository/domain`. Dependencies point downward only.
- **Controllers (`api/`) are thin:** map the route, validate input, delegate to a
  service, return a DTO. No business logic, no data access, no HTTP status
  juggling.
- **Business rules live in services (`service/`)**, annotated `@Transactional`
  (`readOnly = true` for reads). Services return DTOs, never entities.
- **Repositories (`repository/`)** are Spring Data JPA interfaces with derived
  query methods (e.g. `findByEmailIgnoreCase`, `findAllByNameLowerIn`,
  `findDetailedById`).

## DTOs & mapping
- HTTP request/response types are Kotlin `data class`es (annotated `@JvmRecord`)
  in `dto/` — these are the wired contract.
- **Never expose JPA entities over HTTP.** Map entities → DTOs through the
  extension functions in `dto/DtoConversions.kt`. Add new mappings there (it also
  derives fields like `averageRating`/`totalRatings`).
- Validate inputs with Jakarta constraints on the request DTO (`@NotBlank`,
  `@NotNull`, `@Min`, `@Email`, …) plus `@Valid` on the controller parameter.

## Errors
- Throw `service.NotFoundException` (→ 404) or `service.BadRequestException`
  (→ 400) from services. **Do not build `ResponseEntity`/status codes in
  controllers.**
- `api/ApiExceptionHandler` (`@RestControllerAdvice`) renders every error as an
  RFC-7807 `ProblemDetail`. If you introduce a new error condition, ensure a
  handler covers it. See [`docs/cross-cutting.md`](../../docs/cross-cutting.md).

## Domain / persistence
- Entities (`domain/`): Kotlin `open` `@Entity` classes (opened by the Kotlin
  JPA compiler plugins) with a constructor for required fields; set `createdAt`
  in `@PrePersist`; use `@Version`
  for optimistic locking where concurrent edits matter (see `Talk`).
- `open-in-view: false` — lazy associations are **not** available outside the
  transaction. Load aggregates with an explicit fetch query (e.g.
  `TalkRepository.findDetailedById`) when you need the graph.
- Runtime DB is H2 in PostgreSQL mode (non-durable; resets on restart); integration
  tests use real PostgreSQL. Avoid vendor-specific SQL.

## Concurrency model
Blocking Spring MVC everywhere **except Engagement**, which is reactive (Project
Reactor `Mono` + `integration/MetricsClient`). Keep reactive code confined to that
feature; don't spread `Mono`/`Flux` into the blocking features without cause.

## Known behavioral quirks (current, documented — preserve or fix deliberately)
- `TagService.createTags` only rejects when **every** requested name already
  exists; partial-overlap batches can create duplicate tags.
- Engagement endpoints use plain `@RequestBody` (no `@Valid`).
- Speaker email uniqueness has a pre-check lookup and a database unique constraint;
  a concurrent constraint violation is not translated by the advice.

If you change any of these, update the corresponding doc and run
`python docs/tools/doc_staleness.py check`.
