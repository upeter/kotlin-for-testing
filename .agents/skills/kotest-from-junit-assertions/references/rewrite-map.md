# JUnit -> Kotest rewrite map

Use these defaults unless a test requires different semantics.

## Scalar and null checks

- `assertEquals(expected, actual)` -> `actual shouldBe expected`
- `assertNotNull(value)` -> `value.shouldNotBeNull()`
- `assertNull(value)` -> `value.shouldBeNull()`
- `assertTrue(condition)` -> prefer a specific matcher for the value under test
- `assertFalse(condition)` -> prefer a specific matcher for the value under test

## Collection and string checks

- `assertEquals(n, list.size)` -> `list shouldHaveSize n`
- order-sensitive sequence -> `shouldContainInOrder(...)`
- order-insensitive membership -> `shouldContainAllInAnyOrder(...)`
- text contains checks -> `shouldContain(...)` or `shouldContainInOrder(...)`

## Exception checks

- `assertThrows(X.class, () -> call())` -> `shouldThrow<X> { call() }`
- message assertions should be performed on the thrown exception:

```kotlin
shouldThrow<IllegalArgumentException> {
    service.create(invalidInput)
}.message.shouldContain("invalid")
```

## General guidance

- Keep Java tests unchanged when this migration is scoped to Kotlin tests.
- Target Kotlin tests only.
- Favor readable assertions over dense chains.
