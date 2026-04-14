# Extraction patterns for 3+ duplicates

Use this process and patterns for any duplicated Kotlin test code, not only HTTP tests.

## 1) Identify extraction candidates

Extract only when the same pattern appears at least 3 times.

Good candidates:

- repeated fixture creation blocks
- repeated service invocation sequences
- repeated assertion setup or projection code
- repeated serialization/parsing snippets
- repeated coroutine/transaction wrappers

Avoid extraction when:

- the block appears once or twice
- each occurrence differs meaningfully
- abstraction would hide important test intent

## 2) Choose function shape

- Use an extension function when a natural receiver exists.
- Use a top-level helper function for cross-cutting logic.
- Use `inline reified` generics for typed parsing/assertion helpers.
- Keep one responsibility per helper.

## 3) Generic examples

### Repeated service-call sequence

Before:

```kotlin
val a = speakerService.createSpeaker(createSpeakerRequest(name = "Ada"))
val b = speakerService.createSpeaker(createSpeakerRequest(name = "Grace"))
val c = speakerService.createSpeaker(createSpeakerRequest(name = "Linus"))
```

After:

```kotlin
fun SpeakerService.createSpeakers(vararg requests: CreateSpeakerRequest): List<SpeakerDto> =
    requests.map(::createSpeaker)

val (a, b, c) = speakerService.createSpeakers(
    createSpeakerRequest(name = "Ada"),
    createSpeakerRequest(name = "Grace"),
    createSpeakerRequest(name = "Linus")
)
```

### Repeated result projection assertion

Before:

```kotlin
resultA.map { it.name } shouldContainInOrder expectedNames
resultB.map { it.name } shouldContainInOrder expectedNames
resultC.map { it.name } shouldContainInOrder expectedNames
```

After:

```kotlin
fun Iterable<TagDto>.names(): List<String> = map { it.name }

resultA.names() shouldContainInOrder expectedNames
resultB.names() shouldContainInOrder expectedNames
resultC.names() shouldContainInOrder expectedNames
```

### Repeated HTTP setup (when present)

```kotlin
fun MockHttpServletRequestBuilder.defaultHeaders(
    token: String = "default-token",
    correlationId: String = "default-correlation-id"
): MockHttpServletRequestBuilder =
    header("Authorization", "Bearer $token")
        .header("X-Correlation-Id", correlationId)
```

## 4) Refactor checklist

- Confirm each helper removes 3+ duplicated occurrences.
- Confirm call-site readability improves.
- Keep behavior and assertion semantics unchanged.
- Keep defaults overridable.
- Keep helpers in test utility locations.
