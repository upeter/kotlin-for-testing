---
name: kotlin-test-extensions-boilerplate
description: Use this skill when Kotlin HTTP/API tests contain repeated request setup, headers, JSON serialization, or response parsing code. Create focused Kotlin extension functions to remove duplicated test boilerplate for clients like MockMvc or RestTestClient while preserving test behavior.
---

## Goal

Extract repeated test plumbing into small Kotlin extension functions so tests stay focused on Arrange/Act/Assert intent.

## Default Procedure

1. Find duplicated setup in tests (headers, auth token, correlation id, JSON body setup, response decoding).
2. Create or update a shared Kotlin test utility file with extension functions.
3. Introduce defaults for frequent values (for example default token/correlation id), but keep overrides available.
4. Add typed body readers (`readBody<T>()`) for response deserialization.
5. Replace duplicated call sites with the new extensions.
6. Remove now-unused imports and keep tests behavior-identical.

Use naming and signatures from `references/patterns.md` as defaults.

## Boundaries

- Apply this skill to test code only.
- Do not change endpoint behavior or assertion meaning.
- Keep extensions small and composable; avoid large helper methods that hide important assertions.

## Gotchas

- Keep extension return types chainable to preserve fluent test calls.
- Always keep a way to override default headers for test-specific values.
- Ensure JSON serialization and deserialization use the same configured mapper.
- Use reified generics for typed response parsing to avoid repetitive type tokens.
- Do not hide HTTP status assertions inside generic helpers unless the project explicitly uses that pattern.

## Common Refactor Example

Before:

```kotlin
val response = mockMvc.perform(
    post("/api/items")
        .header("Authorization", "Bearer token")
        .header("X-Correlation-Id", "corr-123")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
)
    .andExpect(status().isCreated)
    .andReturn()
    .response
    .contentAsString

val created = objectMapper.readValue(response, object : TypeReference<ItemDto>() {})
```

After:

```kotlin
val created = mockMvc.perform(
    post("/api/items")
        .defaultHeaders()
        .jsonContent(request)
)
    .andExpect(status().isCreated)
    .readBody<ItemDto>()
```

## Validation Loop

1. Confirm duplicated setup is actually reduced across call sites.
2. Run tests that use the new extensions.
3. If serialization or typing fails, fix utility signatures and rerun.
4. Keep behavior identical and stop only when tests pass.

## Output Style

When reporting completion, include:

- utility file(s) added or updated,
- extension functions introduced or changed,
- representative test files refactored to use them.
