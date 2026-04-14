# AssertJ -> Kotest rewrite map

Use these defaults unless the test requires different semantics.

## Scalar checks

- `assertThat(actual).isEqualTo(expected)` -> `actual shouldBe expected`
- `assertThat(actual).isNotEqualTo(expected)` -> `actual shouldNotBe expected`
- `assertThat(value).isNull()` -> `value shouldBe null`
- `assertThat(value).isNotNull()` -> `value.shouldNotBeNull()`
- `assertThat(flag).isTrue()` -> `flag shouldBe true`
- `assertThat(flag).isFalse()` -> `flag shouldBe false`

## Collection checks

- `assertThat(items).hasSize(n)` -> `items shouldHaveSize n`
- `containsExactly(...)` -> order-sensitive matcher (`shouldContainInOrder` / exact-order equivalent)
- `containsExactlyInAnyOrder(...)` -> order-insensitive matcher (`shouldContainAllInAnyOrder` / exact-any-order equivalent)
- `isEmpty()` -> `shouldBeEmpty()` or `shouldHaveSize 0`

## Extracting / projection checks

- `assertThat(items).extracting("name").containsExactlyInAnyOrder(...)`
  -> `items.map { it.name } shouldContainAllInAnyOrder(...)`
- For single-object extraction chains, prefer explicit field assertions with `assertSoftly`.

## Exception checks

- `assertThatThrownBy { call() }.isInstanceOf(X::class.java)`
  -> `shouldThrow<X> { call() }`
- `hasMessageContaining("text")`
  -> assert on exception message with Kotest string matcher.

## General guidance

- Convert one assertion intent at a time, then simplify.
- Keep failure diagnostics clear; avoid over-compressed matcher expressions.
- Keep migration scoped to Kotlin test files unless explicitly asked otherwise.
