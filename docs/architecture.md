---
feature: architecture
title: Architecture Overview
last_reviewed: 2026-07-16
reviewed_at_commit: 1f2ddd1
owners:
  - rene.bulsing@xebia.com
documents:
  bootstrap:
    - com.conference.website.ConferenceWebsiteApplication
references:
  - com.conference.website.api.TalkController
  - com.conference.website.api.SpeakerController
  - com.conference.website.api.TagController
  - com.conference.website.api.EngagementController
  - com.conference.website.service.TalkService
  - com.conference.website.service.EngagementService
  - com.conference.website.dto.DtoConversions
  - com.conference.website.repository.TalkRepository
  - com.conference.website.integration.MetricsClient
related_docs:
  - ./talks.md
  - ./speakers.md
  - ./tags.md
  - ./engagement.md
  - ./cross-cutting.md
---

# Architecture Overview

> **Scope of this page.** This is a *structural* overview of the whole
> application — stack, layering, request flow, persistence, and build topology.
> It is anchored only to the composition root (`ConferenceWebsiteApplication`),
> so automated staleness `check` will not catch every structural drift. Re-review
> it when `doc_staleness.py coverage` reports a new surface, when packages/layers
> change, or when the build or runtime stack changes.

## 1. Summary
A **Kotlin Spring Boot conference-website domain**. It exposes a small REST API over four
features — [Talks](./talks.md), [Speakers](./speakers.md), [Tags](./tags.md),
[Engagement](./engagement.md) — backed by a relational store via JPA. Most of the
app is classic blocking Spring MVC; the Engagement feature is reactive
(Project Reactor). The repository's real purpose is a testbed for Kotlin testing
techniques, with Kotlin production code and Kotlin test/integration-test suites.

## 2. Technology Stack
| Concern | Choice |
|---|---|
| Language | Kotlin 2.3.20 (targeting Java 25) |
| Framework | Spring Boot 4.0.3 (`spring-boot-starter-parent`) |
| Web server | Embedded server from `spring-boot-starter-web` (no Undertow dependency declared) |
| Web model | Spring MVC (blocking) + Project Reactor `Mono` (Engagement) |
| Persistence | Spring Data JPA + Hibernate |
| Database | **Runtime:** H2 in-memory in PostgreSQL mode; **Integration tests:** PostgreSQL 16 via Testcontainers |
| Validation | Bean Validation (`starter-validation`, Jakarta constraints) |
| JSON | Jackson (+ `jackson-module-kotlin`) |
| Ops | Spring Boot Actuator |
| Nullness | JSpecify annotations |
| Build | Gradle (`build.gradle.kts`, `./gradlew`) with Kotlin JVM / Spring / JPA / power-assert plugins |
| Test | JUnit 5, Kotest assertions, MockK (`springmockk`), Reactor-test, kotlinx-coroutines-test |

## 3. Layered Architecture
```mermaid
flowchart TD
    subgraph client[Clients]
        HTTP[HTTP / JSON]
    end
    subgraph api[api - controllers + advice]
        TC[TalkController]:::s
        SC[SpeakerController]:::s
        GC[TagController]:::s
        EC[EngagementController - reactive]:::s
        AEH[ApiExceptionHandler]
    end
    subgraph svc[service - business rules]
        TS[TalkService]
        SS[SpeakerService]
        GS[TagService]
        ES[EngagementService - reactive]
    end
    subgraph dm[domain - JPA entities]
        E[Talk / Rating / ScheduleSlot / Speaker / Tag]
    end
    subgraph repo[repository - Spring Data JPA]
        R[TalkRepository / SpeakerRepository / TagRepository / RatingRepository]
    end
    subgraph integ[integration - metrics]
        MC[MetricsClient -> RedisMetricsClient -> RedisMetricsDatabase]
    end
    DX[dto.DtoConversions - entity->DTO]

    HTTP --> api
    TC --> TS
    SC --> SS
    GC --> GS
    EC --> ES
    TS --> R
    SS --> R
    GS --> R
    ES -->|existence check| R
    ES --> MC
    R --> DB[(H2 / PostgreSQL)]
    TS -.-> DX
    SS -.-> DX
    GS -.-> DX
    api -.throws.-> AEH
    classDef s fill:#2b6,stroke:#164,color:#fff;
```

Dependencies point strictly downward (`api → service → repository/domain`); the
graph confirms `service` (the `main` package core) has high fan-in and no
outward dependency on `api`. `DtoConversions` is a shared leaf every read path
funnels through.

## 4. Request Lifecycle

### Blocking path (Talks / Speakers / Tags)
```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant U as Undertow
    participant Ctl as Controller
    participant Svc as Service
    participant Repo as JPA Repository
    participant DB as H2 / Postgres
    C->>U: HTTP request (JSON)
    U->>Ctl: dispatch (@Valid body binding)
    Ctl->>Svc: call (@Transactional)
    Svc->>Repo: query / save
    Repo->>DB: SQL
    DB-->>Repo: rows
    Svc->>Svc: DtoConversions.toDto(entity)
    Svc-->>Ctl: DTO
    Ctl-->>C: JSON (or ProblemDetail on exception)
```

### Reactive path (Engagement)
```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant EC as EngagementController
    participant ES as EngagementService
    participant M as MetricsClient
    C->>EC: POST/GET .../engagement
    EC->>ES: Mono<EngagementCountDto>
    ES->>M: increment/read (composed, timeout 2s)
    M-->>ES: counts
    ES-->>EC: Mono
    EC-->>C: JSON (subscribed by the framework)
```
See [Engagement](./engagement.md) for the full reactive detail and the
in-memory metrics backend.

## 5. Module / Package Structure
```
com.conference.website
├── ConferenceWebsiteApplication   # @SpringBootApplication composition root
├── api            # REST controllers (surfaces) + ApiExceptionHandler
├── service        # business rules, @Transactional; NotFound/BadRequest exceptions
├── domain         # JPA entities: Talk, Rating, ScheduleSlot, Speaker, Tag, TalkLevel
├── dto            # Kotlin request/response data classes + mapping extensions
├── repository     # Spring Data JPA interfaces
└── integration    # metrics backend (MetricsClient/RedisMetricsClient/RedisMetricsDatabase),
                   # plus BuzzClient/FakeBuzzClient (present, not wired to endpoints)
```

## 6. Persistence & Data Model
- **Runtime:** H2 in-memory (`MODE=PostgreSQL`), `ddl-auto: update`,
  `open-in-view: false`, `spring.sql.init.mode: always`. State is **not durable**
  across restarts.
- **Integration tests:** PostgreSQL 16 via Testcontainers (JDBC URL
  `jdbc:tc:postgresql:16-alpine:///conference`), giving production-like behavior.
- Entity relationships (Talk aggregate with speakers/tags/ratings/slot) are
  detailed in [Talks §4](./talks.md). Engagement counters are **not** JPA
  entities — they live in the metrics backend keyed by `talkId`.

## 7. Cross-cutting Concerns
Error handling (RFC-7807 `ProblemDetail` via `ApiExceptionHandler`) and
entity→DTO conversion (`DtoConversions`) are shared by all features and
documented once in [cross-cutting](./cross-cutting.md).

## 8. Build & Test Topology
```mermaid
flowchart LR
    subgraph build[Gradle build]
        G[Gradle Kotlin + Spring plugins<br/>compile Kotlin for Java 25]
        C[check task]
    end
    subgraph tests[Test source sets]
        UT[src/test - unit + slice<br/>task: test]
        IT[src/integrationTest - full<br/>task: integrationTest, profile integrationtest]
    end
    G --> UT
    UT --> IT
    IT --> TCPG[(Testcontainers PostgreSQL)]
    IT --> C
```
- **Unit/slice tests** (`src/test`, Gradle `test`) run against H2 / mocks.
- **Integration tests** (`src/integrationTest`, Gradle `integrationTest`, profile
  `integrationtest`) run against real PostgreSQL.
- `check` depends on `integrationTest`, so the default verification pipeline includes both test layers.
- Kotlin test tooling: AssertJ, Kotest assertions, MockK, power-assert, Reactor-test.

## 9. Notable Characteristics & Constraints
- **No authentication/authorization** on the API surfaces.
- **In-memory everything by default:** H2 data and engagement counters reset on
  restart; the "Redis" metrics classes are an in-memory fake.
- **Kotlin DTO boundary:** request and response `data class`es in `dto/` are the
  HTTP contract; entities remain internal.
- **Mixed concurrency models:** blocking MVC everywhere except the reactive
  Engagement endpoints; reactive errors are not mapped by `ApiExceptionHandler`.
- **Undertow, not Tomcat** — relevant for server tuning and metrics.

## 10. Related Documentation
- Features: [Talks](./talks.md) · [Speakers](./speakers.md) · [Tags](./tags.md) · [Engagement](./engagement.md)
- [Cross-cutting concerns](./cross-cutting.md)
- Tooling: [docs/tools/README.md](./tools/README.md) — staleness, impact, blast-radius, coverage
