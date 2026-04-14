# Default extension patterns

Use these as starting patterns and adapt to project conventions.

## Shared mapper

```kotlin
val objectMapper: ObjectMapper = jacksonObjectMapper()
```

## MockMvc request helpers

```kotlin
fun MockHttpServletRequestBuilder.authorizationHeader(
    token: String = "default-token"
): MockHttpServletRequestBuilder =
    header("Authorization", "Bearer $token")

fun MockHttpServletRequestBuilder.correlationIdHeader(
    correlationId: String = "default-correlation-id"
): MockHttpServletRequestBuilder =
    header("X-Correlation-Id", correlationId)

fun MockHttpServletRequestBuilder.defaultHeaders(
    token: String = "default-token",
    correlationId: String = "default-correlation-id"
): MockHttpServletRequestBuilder =
    authorizationHeader(token).correlationIdHeader(correlationId)

fun <T> MockHttpServletRequestBuilder.jsonContent(obj: T): MockHttpServletRequestBuilder =
    contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(obj))
```

## MockMvc response helpers

```kotlin
inline fun <reified T : Any> MvcResult.readBody(): T =
    objectMapper.readValue<T>(response.contentAsByteArray)

inline fun <reified T : Any> ResultActions.readBody(): T =
    andReturn().readBody()
```

## RestTestClient helpers

```kotlin
inline fun <reified T : Any> RestTestClient.ResponseSpec.readBody(): T =
    returnResult(object : ParameterizedTypeReference<T>() {}).responseBody
        ?: error("Expected non-null response body")

fun <S : RestTestClient.RequestHeadersSpec<S>> S.defaultHeaders(
    token: String = "default-token",
    correlationId: String = "default-correlation-id"
): S =
    header("Authorization", "Bearer $token")
        .header("X-Correlation-Id", correlationId)

fun <T : Any> RestTestClient.RequestBodySpec.jsonContent(obj: T): RestTestClient.RequestHeadersSpec<*> =
    contentType(MediaType.APPLICATION_JSON).body(obj)
```

## Refactor checklist

- Keep helpers single-purpose and composable.
- Keep default values overridable.
- Prefer typed `readBody<T>()` over manual string parsing in tests.
- Keep status assertions in tests for readability.
