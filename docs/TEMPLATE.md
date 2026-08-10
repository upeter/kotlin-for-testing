<!--
  Feature documentation template.

  Copy this file to docs/<feature-slug>.md and fill it in.

  The frontmatter is machine-readable and drives staleness detection:
  a CI check resolves every symbol under `documents:` to its defining
  file and runs `git log <reviewed_at_commit>..HEAD -- <those files>`.
  A non-empty result means the code changed after the last human review,
  so the doc is flagged STALE and `last_reviewed` / `reviewed_at_commit`
  must be refreshed once a human has re-verified the page.

  Rules of thumb:
  - Use fully-qualified names (package + class) so symbols resolve
    unambiguously and match the code graph.
  - `documents:`  -> this page is AUTHORITATIVE for these symbols.
                     A change here triggers review of THIS page.
  - `references:` -> mentioned here but OWNED by another page.
                     A change here does NOT flag this page (the owning
                     page catches it). Keep shared helpers here.
  - Every business rule and exception should link to the enforcing
    service method (and ideally the test) so the prose stays checkable.
-->
---
feature: <slug>                         # stable id; equals the filename
title: <Human Readable Feature Name>
last_reviewed: <YYYY-MM-DD>             # date a human last verified this page
reviewed_at_commit: <short-sha>        # baseline commit for staleness diffing
owners:
  - <email-or-team>
documents:                              # authoritative — changes trigger review
  controllers:
    - com.conference.website.api.<XxxController>
  services:
    - com.conference.website.service.<XxxService>
  domain:
    - com.conference.website.domain.<Entity>
  dtos:
    - com.conference.website.dto.<XxxDto>
    - com.conference.website.dto.<CreateXxxRequest>
references:                             # mentioned, owned elsewhere (no staleness)
  - com.conference.website.dto.DtoConversions
  - com.conference.website.service.NotFoundException
related_docs:
  - ./cross-cutting.md
---

# <Feature Name>

## 1. Introduction / Summary
One paragraph: what this feature is, who uses it, and why it exists.
A reader should be able to stop here and know whether this is the page
they need.

## 2. API / Surface Reference
The externally reachable surfaces this feature exposes. This section is
the contract; keep it in sync with the controller and DTOs listed in
frontmatter.

| Method | Path | Request body | Response body | Success | Errors |
|--------|------|--------------|---------------|---------|--------|
| `GET`  | `/api/...` | – | `XxxDto` | 200 | 404 |
| `POST` | `/api/...` | `CreateXxxRequest` | `XxxDto` | 201 | 400, 404 |

## 3. Functional Description
What the feature does from the user's / caller's point of view, in prose.
Cover the main flows and how the endpoints above combine into real usage.

## 4. Entity Descriptions
Each domain entity this feature owns: purpose, notable fields, relationships,
and invariants that must always hold.

### <Entity>
- **Purpose:** …
- **Key fields:** …
- **Relationships:** …
- **Invariants:** …

## 5. Technical Lifecycle
How the entities move through their states over time (created → … → terminal),
including where persistence happens and what triggers each transition. Add a
small state diagram only where a state machine actually exists.

## 6. Business Rules
Numbered, each traceable to the code that enforces it.

1. **<Rule>.** Enforced by `XxxService.method()` — tested by `Exx_XxxServiceTest`.
2. …

## 7. Notable Exceptions & Edge Cases
Failure modes, error responses, and non-obvious behavior. Link each to the
exception class or branch that produces it.

- **<Condition> → <HTTP status> (`ExceptionClass`).** …

## 8. Related Documentation
- [Cross-cutting concerns](./cross-cutting.md)
- <other feature docs this one interacts with>
