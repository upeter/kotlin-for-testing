package com.conference.website.dsl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.test.context.transaction.TestTransaction
import java.util.concurrent.atomic.AtomicReference

/**
 * Test DSL to ensure that created entities are removed after a test.
 * this is the replacement of the nested: doWith... approach
 */

class UndoDataScope : AutoCloseable {
    private val finalizers: AtomicReference<List<() -> Unit>> = AtomicReference(emptyList())

    fun <T : Any> T.undoWith(doLast: Boolean = false, finalize: (T) -> Unit): T =
        also { entity -> finalizers.updateAndGet {
            if (doLast) listOf { finalize(entity) } + it else  it + { finalize(entity) } }
        }

    fun <T : Any> JpaRepository<T, *>.persistWithPostUndo(vararg entities: T): List<T> = persistWithPostUndo(entities.toList())

    fun <T : Any> JpaRepository<T, *>.persistWithPostUndo(entities: List<T>): List<T> =
        if (entities.isEmpty()) emptyList() else saveAll(entities).also { addToFinalizer(it) }

    private fun <T : Any> JpaRepository<T, *>.addToFinalizer(entities: List<T>) =
        finalizers.updateAndGet { it + { deleteAll(entities) } }

    override fun close() {
        finalizers.get().reversed().fold(null as Throwable?) { exception, finalizer ->
            val finalizeException = runCatching { finalizer.invoke() }.exceptionOrNull()
            if (exception != null) exception.add(finalizeException) else finalizeException

        }?.let { throw it }
    }

    private fun Throwable?.add(other: Throwable?): Throwable? =
        this?.apply { other?.let { addSuppressed(it) } } ?: other
}

/**
 * Scope method
 */
inline fun undoDataScope(block: UndoDataScope.() -> Unit) =
    UndoDataScope().use(block)

/**
 * Commits the current transaction and starts a new one. Should only be used in a UndoDataScope as the test data can
 * no longer be removed by a transaction rollback. This is enforced through the UndoDataScope receiver, even though
 * the UndoDataScope is not used.
 */
fun <T> UndoDataScope.withNewTransaction(block: () -> T): T {
    TestTransaction.flagForCommit()
    TestTransaction.end()
    TestTransaction.start()
    TestTransaction.flagForCommit()
    return block()
}
