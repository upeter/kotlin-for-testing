# Parameter classification checklist

Use this checklist when designing a Kotlin object-mother function signature.

## 1) Mandatory (no default)

Mark a parameter mandatory when at least one is true:

- The domain requires it (entity/request invalid without it).
- Existing tests always provide it and failures occur when omitted.
- Validation constraints make omission invalid (`@NotNull`, `@NotBlank`, etc.).

Signature rule:

```kotlin
fun createThing(required: RequiredType, ...)
```

## 2) Optional (nullable)

Mark a parameter optional when omission is a valid business/API state.

Signature rule:

```kotlin
fun createThing(optional: OptionalType? = null, ...)
```

## 3) Convenience default (non-null with default)

Use defaults for common values that are not mandatory but frequently needed.

Signature rule:

```kotlin
fun createThing(name: String = "Default Name", tags: List<String> = emptyList())
```

## 4) Java interop

If Java tests call the Kotlin factory, annotate with `@JvmOverloads`.

```kotlin
@JvmOverloads
fun createThing(required: Req, name: String = "x", note: String? = null): Thing
```

## 5) Determinism and readability

- Keep defaults stable and human-readable.
- Use named arguments at call sites to show intent.
- Prefer small, focused factories over one mega-factory.
