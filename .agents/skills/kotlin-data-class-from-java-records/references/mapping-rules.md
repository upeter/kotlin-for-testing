# Java records -> Kotlin data classes mapping rules

Use these defaults unless the target project has stricter conventions.

## Type mapping

- Java primitive -> Kotlin non-null primitive
  - `int` -> `Int`
  - `long` -> `Long`
  - `double` -> `Double`
  - `boolean` -> `Boolean`
- Java boxed types -> Kotlin nullable when field is optional
  - `Integer` -> `Int?`
  - `Long` -> `Long?`
  - `Double` -> `Double?`
  - `Boolean` -> `Boolean?`
- `String` -> `String` unless explicitly optional, then `String?`
- `List<T>` -> `List<T>` or `List<T> = emptyList()` when optional

## Defaults

- Allowed safe defaults:
  - optional collections: `emptyList()`
  - optional nullable fields: `null`
- Avoid generic defaults for required values:
  - do not default required `String` to `""`
  - do not default required numbers to `0`

## Annotations and interop

- Add `@JvmRecord` to each converted Kotlin data class.
- Preserve validation annotations:
  - Java: `@NotBlank String name`
  - Kotlin: `@field:NotBlank val name: String`

## File organization

- Group related models in one Kotlin file by domain/feature.
- Keep conversion scope focused: DTO models only, no unrelated refactors.
