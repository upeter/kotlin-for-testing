---
name: kotlin-data-class-from-java-records
description: Use this skill when converting Java record DTOs or request/response models to Kotlin data classes that remain Java-friendly. Always add @JvmRecord, map nullability correctly, keep related domain models in the same Kotlin file, and add only safe defaults (prefer emptyList() or null, not generic String defaults).
---

## Goal

Convert Java records into Kotlin data classes with Java interoperability and clean Kotlin type semantics.

## Default Procedure

1. Identify a domain group of related Java records (for example, request and response DTOs for the same feature).
2. Create or update one Kotlin file for that domain group and place the related data classes together.
3. Convert each Java record to a Kotlin `data class` and add `@JvmRecord`.
4. Map Java types to Kotlin with correct nullability using `references/mapping-rules.md`.
5. Preserve validation annotations (use `@field:` targets where needed).
6. Add defaults only when they are semantically safe:
   - prefer `emptyList()` for collections,
   - prefer `null` for optional fields,
   - avoid arbitrary defaults for strings or required scalar values.
7. Keep names and field order stable unless there is a strong reason to change.

## Boundaries

- This skill is for Java `record` -> Kotlin `data class` conversion.
- Do not refactor unrelated business logic during conversion.
- Do not invent placeholder defaults like `""`, `"N/A"`, or `0` for required fields.

## Gotchas

- `@JvmRecord` constraints apply:
  - all primary-constructor properties must be `val`,
  - no extra backing state in the class body.
- Java boxed types (`Long`, `Integer`, `Double`, etc.) often indicate optionality in DTOs; map to nullable Kotlin when applicable.
- If Java validation annotations exist, ensure Kotlin keeps them effective with `@field:` for bean-validation processing.
- Defaults must not change API meaning. Use defaults only where the model already treats the field as optional.

## Common Conversion Example

Before (Java):

```java
public record CreateTalkRequest(
    @NotBlank String title,
    @NotBlank String abstractText,
    TalkLevel level,
    @Min(5) int durationMinutes,
    SpeakerDto primarySpeaker,
    List<SpeakerDto> coSpeakers,
    List<TagDto> tags
) {}
```

After (Kotlin):

```kotlin
@JvmRecord
data class CreateTalkRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val abstractText: String,
    val level: TalkLevel,
    @field:Min(5) val durationMinutes: Int,
    val primarySpeaker: SpeakerDto,
    val coSpeakers: List<SpeakerDto> = emptyList(),
    val tags: List<TagDto> = emptyList(),
)
```

## Validation Loop

After conversion:

1. Confirm every converted model has `@JvmRecord`.
2. Confirm related models are grouped in the intended domain Kotlin file.
3. Re-check nullability and defaults against the source record semantics.
4. Run compile/tests and fix any interop or validation issues.

Suggested command pattern:

```bash
./gradlew test --tests "*YourDtoOrServiceTests*"
```

## Output Style

When reporting completion, include:

- converted files and domain grouping choices,
- nullability/default decisions,
- any annotation migration details.
