# Merging the Java test suite into Kotlin

**Date:** 2026-08-10 · **Status:** implemented

## Context

This repo backs a conference talk on advanced Kotlin testing. Each exercise exists
as a pair: a deliberately inferior "before" test and an advanced "after" test. The
original talk targeted Java developers, so the "before" side was written in Java.
The re-run targets Kotlin developers, so the Java side has to become Kotlin —
without losing the inferior approach that makes each pair land.

## Decisions

| Decision | Rationale |
|---|---|
| **No test builders anywhere.** All 15 classes in `src/test/java/.../data/builders/` deleted, never ported. | The builder-pain exercises (E03, E07, E08) are being replaced later by Arrow deep-copy exercises, so the builder narrative is retired here rather than carried forward in Kotlin. |
| Inferior test data = raw positional constructors, temporary variables, hand-copied fields, inline `repository.save(...)` in dependency order. | Keeps a real "before" smell without a builder layer. E07 in particular becomes field-by-field manual copying — the ideal setup for the Arrow replacement. |
| Naming: `Exx_XxxTest.kt` = inferior · `Exx_XxxSuperchargedTest.kt` = advanced. Sibling files, same package. | Matches the pre-existing convention, keeps `recordings/` filenames aligned, and reads well side by side on stage. |
| Java-only pain → nearest naive-**Kotlin** smell, helper kept rather than deleted. | `Function<T,R>`-forced `return null`, static-import overload sets, and `toDto` overload-by-type don't reproduce in Kotlin. The pyramid, the manual `TestTransaction` juggling, and the ambiguous overload set do. |
| Ported inferior helpers become Kotlin `object`s (`object E06_MockMvcTestUtils`, `object TestDtoConversions`, `object E02_TransactionTestUtils`, `object E02_EntityLifecycleTestUtils`). | Mirrors the Java static-utility shape (itself part of the smell) and avoids top-level redeclaration clashes with the advanced extension-function versions in the same package. |
| Compilation must be clean; tests that fail today keep failing. | The pre-existing failures are shared-fixture pollution (every test uses `ada@example.com` against one H2 instance) plus intentionally wrong assertions. Not this change's scope. |

### Structural collision and the one rename

Java `E06_MockMvcTestUtils` and Kotlin `E06_MockMvcTestUtils.kt` shared package
`com.conference.website.utils`, coexisting only because the Java one was a class and
the Kotlin one compiled to `…UtilsKt`. Once both are Kotlin, the file name and the
top-level `val objectMapper` both collide.

Resolved by renaming the advanced file to `E06_MockMvcSuperchargedTestUtils.kt`
(package unchanged, both consumers wildcard-import the package → **zero import
edits**) and giving the plain name to the ported inferior `object`.

**Forced deviation:** Java's `withNewTransaction(Runnable)` + `withNewTransaction(Supplier<T>)`
overload pair cannot be reproduced — in Kotlin `(() -> Unit)` and `<T> (() -> T)` are
resolution-ambiguous. Only the generic form survives; the actual E02 smell is untouched.

## What changed

**Ported (Java deleted, Kotlin added):** E02 `TalkControllerIT` + `TalkControllerAdvancedIT`
(integration), E03/E04/E07/E08/E09/E10 service tests, E05/E06 tag-controller tests,
`TestDtoConversions`, `E02_TransactionTestUtils`, `E02_EntityLifecycleTestUtils`,
`E06_MockMvcTestUtils`.

**Deleted outright:** 15 builders including `TalkGraphPersistence`; `src/main/java`,
`src/test/java`, `src/integrationTest/java`.

**Reduced:** E08 went from three stages to two (raw constructors; ObjectMother) —
the builder stage had no replacement. That dropped test was already failing on
`NullPointer … parameter primarySpeaker`.

**Weakened:** E09. Its Java lesson was "Kotlin `@JvmOverloads` defaults are visible
from Java," which has no Kotlin analogue. Reframed as positional-vs-named arguments.
This is the thinnest surviving pair.

**Build (`pom.xml`):** dropped the three `<sourceDir>` java entries, the whole
`maven-compiler-plugin` block (`default-compile`/`default-testCompile` disabling plus
the `java-compile`/`java-test-compile` rebinding), and `build-helper`'s
`add-integration-test-source`.

## Trap worth recording

Surefire/Failsafe `<includes>` match **compiled class names**, not source files. The
pre-existing `**/*Test.java` patterns were what discovered the *Kotlin* tests; the
`**/*Test.kt` patterns had always matched nothing. Removing the `.java` patterns
silently disabled all test discovery — `./mvnw test` exited **0** with zero tests run.
Now `**/*Test.class` / `**/*Tests.class` / `**/*IT.class`.

## Verification

- `./mvnw -o clean test-compile` → BUILD SUCCESS.
- `./mvnw -o clean test` → 28 tests, 5 failures, 8 errors vs. a baseline of 29/5/9.
  The single delta is E08's dropped builder stage; every other failure is identical
  in class, line, and cause.
- `./mvnw verify` (Docker/Testcontainers PostgreSQL 16) → all 3 ITs green, including
  both E02 ports.

## Out of scope

Arrow deep-copy exercises; the Maven/Gradle contradiction (`build.gradle.kts` still
says "Java-first", targets JVM 25 vs Maven's 21, and holds the only
ktlint/detekt/kover/konsist config); removing the now-pointless `@JvmRecord` /
`@JvmOverloads` from main DTOs; the shared-fixture `ada@example.com` collisions;
episode renumbering (would desync both `recordings/` directories).
