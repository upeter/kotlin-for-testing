# Unit / slice test rules (`src/test`)

This source set is run by **Surefire**; name classes `*Test` / `*Tests`. Use H2
and mocks here — never require external infrastructure (that's for
`src/integrationTest`). The `Exx_` prefix marks teaching exercises; only continue
a numbering series when extending that exercise.

Surefire/Failsafe `<includes>` in `pom.xml` match **compiled class names**
(`**/*Test.class`), not source files. A `**/*Test.kt` pattern matches nothing and
silently disables discovery — the build then passes with zero tests.

## Episode pairs: inferior vs supercharged (read before "fixing" a test)
Every `Exx_` exercise exists as a **pair**, and the whole repo is Kotlin:

| File | Role |
|---|---|
| `Exx_XxxTest.kt` | the **deliberately inferior** "before" version |
| `Exx_XxxSuperchargedTest.kt` | the **advanced** "after" version |

The inferior file's verbosity **is the teaching content**. In it, expect — and
preserve — hand-written positional constructor calls, temporary-variable chains,
manual field-by-field copying, `@Autowired lateinit var` field injection, string-based
AssertJ `extracting("name", …)`, `!!`, hand-written overloads, and manual
`TestTransaction` juggling. Some inferior tests also **fail on purpose**; a red test
here is not necessarily a bug.

Never refactor an `Exx_XxxTest.kt` toward the idiomatic style, and never delete one
because it duplicates its `Supercharged` twin. Improvements belong in the
`Supercharged` file. There are no test builders anywhere — that layer was removed on
purpose; the builder-pain exercises are being replaced by Arrow deep-copy ones.

## Kotlin test style
This repo is a Kotlin-testing testbed. Match the Kotlin style of the file/feature
you're extending:

- **ObjectMother** functions in `data`
  (`createTalkRequest(...)`, `createSpeakerDto(...)` — defaults + `@JvmOverloads`),
  **DSLs** in `dsl` (`talks { talk { … primarySpeaker { … } tags("a","b") } }`),
  **Kotest** matchers (`shouldBe`, `shouldContainAllInAnyOrder`, `shouldHaveSize`),
  **MockK / springmockk** (`every { … }`). Power-assert is enabled.

## Test data
In `*SuperchargedTest` files and all new tests, build fixtures through the
factories/DSLs above — do not hand-construct entities/DTOs inline. Add new defaults
to the shared factory rather than duplicating. In `Exx_XxxTest` files the inline
hand-construction is intentional (see above) — leave it alone.

## Wiring
- Service/persistence tests: `@SpringBootTest @Transactional` (auto rollback).
- Kotlin tests inject via the constructor and implement the `RepositorySupport`
  interface to get repositories + persistence helpers.
- Reuse shared helpers in `utils/` (MockMvc / RestTestClient / coroutine
  utilities) instead of re-deriving them.

## Scope
Keep these tests fast and self-contained (unit logic, service behavior with
rollback, controller slices). Anything needing a real database or the full HTTP
boundary belongs in `src/integrationTest` (`*IT`).
