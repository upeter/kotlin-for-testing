# Unit / slice test rules (`src/test`)

This source set is run by **Surefire**; name classes `*Test` / `*Tests`. Use H2
and mocks here — never require external infrastructure (that's for
`src/integrationTest`). The `Exx_` prefix marks teaching exercises; only continue
a numbering series when extending that exercise.

## Kotlin test style
This repo is a Kotlin-testing testbed. Match the Kotlin style of the file/feature
you're extending:

- **ObjectMother** functions in `data`
  (`createTalkRequest(...)`, `createSpeakerDto(...)` — defaults + `@JvmOverloads`),
  **DSLs** in `dsl` (`talks { talk { … primarySpeaker { … } tags("a","b") } }`),
  **Kotest** matchers (`shouldBe`, `shouldContainAllInAnyOrder`, `shouldHaveSize`),
  **MockK / springmockk** (`every { … }`). Power-assert is enabled.

## Test data
Build fixtures through the factories/DSLs above — do not hand-construct
entities/DTOs inline. Add new defaults to the shared factory rather than
duplicating.

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
