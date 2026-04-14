# JUnit -> Kotest rewrite map (E10 style)

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
shouldThrow<BadRequestException> {
    tagService.createTags(CreateTagsRequest(listOf("Kotlin")))
}.message.shouldContainInOrder("Tag already exists", "kotlin")
```

## Repo-specific guidance

- Keep Java tests unchanged (BEFORE baseline).
- Target Kotlin supercharged tests only.
- Favor readable assertions over dense chains.
