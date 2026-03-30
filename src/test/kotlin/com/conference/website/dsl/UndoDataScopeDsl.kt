package com.conference.website.dsl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.test.context.transaction.TestTransaction
import java.util.concurrent.atomic.AtomicReference

/**
 * Test DSL to ensure that created entities are removed after a test.
 * this is the replacement of the nested: doWith... approach
 */

class TestDataScope2 : AutoCloseable {
    private val finalizers = mutableListOf<() -> Unit>()

    fun <T : Any> T.registerCleanup(cleanup: (T) -> Unit): T =
        this.apply { finalizers.add({ cleanup(this) }) }

    fun <T : Any> JpaRepository<T, *>.persistWithUndo(entities: List<T>): List<T> =
        saveAll(entities).apply { registerCleanup({deleteAll(entities)}) }

    override fun close() = finalizers.reversed().forEach { it.invoke() }

}

inline fun testDataScope2(block: TestDataScope2.() -> Unit) =
    TestDataScope2().use(block)

//fun TestDataScope2.persistAndPostCleanup


class TestDataScope : AutoCloseable {
    typealias CleanupFunction = () -> Unit

    private val finalizers: AtomicReference<List<CleanupFunction>> = AtomicReference(emptyList())

    fun <T : Any> T.registerCleanup(cleanup: (T) -> Unit): T =
        this.apply { finalizers.updateAndGet { it + { cleanup(this) } } }

    fun <T : Any> JpaRepository<T, *>.persistWithUndo(vararg entities: T): List<T> =
        persistWithUndo(entities.toList())

    fun <T : Any> JpaRepository<T, *>.persistWithUndo(entities: List<T>): List<T> =
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
inline fun testDataScope(block: TestDataScope.() -> Unit) =
    TestDataScope().use(block)

/**
 * Commits the current transaction and starts a new one. Should only be used in a TestDataScope as the test data can
 * no longer be removed by a transaction rollback. This is enforced through the TestDataScope receiver, even though
 * the TestDataScope is not used.
 */
fun <T> TestDataScope.withNewTransaction(block: () -> T): T {
    TestTransaction.flagForCommit()
    TestTransaction.end()
    TestTransaction.start()
    TestTransaction.flagForCommit()
    return block()
}

