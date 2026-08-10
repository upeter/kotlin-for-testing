---
feature: speakers
title: Speakers
last_reviewed: 2026-07-16
reviewed_at_commit: 1f2ddd1
owners:
  - rene.bulsing@xebia.com
documents:
  controllers:
    - com.conference.website.api.SpeakerController
  services:
    - com.conference.website.service.SpeakerService
  domain:
    - com.conference.website.domain.Speaker
  dtos:
    - com.conference.website.dto.SpeakerDto
    - com.conference.website.dto.CreateSpeakerRequest
references:
  - com.conference.website.repository.SpeakerRepository
  - com.conference.website.dto.DtoConversions
  - com.conference.website.service.BadRequestException
related_docs:
  - ./talks.md
  - ./cross-cutting.md
---

# Speakers

## 1. Introduction / Summary
A **Speaker** is a person who presents talks. Speakers are created independently
and then referenced by id when creating a [Talk](./talks.md) (as primary speaker
or co-speaker). This feature owns creating and listing speakers.

## 2. API / Surface Reference
Base path: `/api/speakers` (`SpeakerController`).

| Method | Path | Request | Response | Success | Errors |
|--------|------|---------|----------|---------|--------|
| `POST` | `/api/speakers` | `CreateSpeakerRequest` | `SpeakerDto` | 201 | 400 |
| `GET`  | `/api/speakers` | – | `SpeakerDto[]` | 200 | – |

`SpeakerService` also exposes `getSpeakerById` (returns nullable `SpeakerDto?`), but **no
GET-by-id endpoint is wired** — it is used internally / by tests.

## 3. Functional Description
Creating a speaker validates the request body and rejects a duplicate email
(case-insensitive) before persisting. Listing returns all speakers. Speakers have
no lifecycle beyond creation (no update/delete surface).

## 4. Entity Descriptions

### Speaker (`speakers` table)
- **Purpose:** a presenter.
- **Fields:** `id`, `name`, `email`, `company`, `bio`.
- **Referenced by:** `Talk.primarySpeaker` (required) and `Talk.coSpeakers`.

```mermaid
erDiagram
    SPEAKER ||--o{ TALK : "is primary speaker of"
    SPEAKER }o--o{ TALK : "co-speaks"
    SPEAKER {
        Long id PK
        String name
        String email "unique, case-insensitive"
        String company
        String bio
    }
```

## 5. Technical Lifecycle
```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant SC as SpeakerController
    participant SS as SpeakerService
    participant R as SpeakerRepository
    C->>SC: POST /api/speakers (CreateSpeakerRequest)
    SC->>SS: createSpeaker(request)
    SS->>R: findByEmailIgnoreCase(email)
    alt email already exists
        R-->>SS: existing speaker
        SS-->>C: 400 BadRequestException
    else new
        SS->>R: save(speaker)
        SS-->>SC: SpeakerDto
        SC-->>C: 201 Created + SpeakerDto
    end
```
A persisted speaker is effectively terminal — no state transitions.

## 6. Business Rules
1. **Email must be unique (case-insensitive).** `SpeakerService.createSpeaker`
   → 400 `BadRequestException` ("Speaker email already exists: …").
2. **Request-body validation.** `CreateSpeakerRequest` requires non-blank
   `name`, `company`, `bio` and a `@NotBlank @Email` `email` → 400 on violation.

## 7. Notable Exceptions & Edge Cases
- **Duplicate email → 400** (not 409) when the service's case-insensitive
  pre-check observes it. The entity also declares a database unique constraint;
  a concurrent constraint violation is not translated by `ApiExceptionHandler`.

## 8. Related Documentation
- [Talks](./talks.md) — consumes speakers by id.
- [Cross-cutting concerns](./cross-cutting.md) — error model, DTO conversion.
