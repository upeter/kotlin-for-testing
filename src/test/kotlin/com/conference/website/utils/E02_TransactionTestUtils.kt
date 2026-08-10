package com.conference.website.utils

import org.springframework.test.context.transaction.TestTransaction

/**
 * The inferior transaction helper: a static-style holder that hand-rolls the
 * `TestTransaction` dance. Nothing stops a caller from invoking it outside a test
 * data scope — there is no receiver to constrain it.
 *
 * Compare with `dsl/E02_UndoDataScopeDsl.kt`, where `withNewTransaction` is an
 * extension on `TestDataScope` and therefore only callable where cleanup is
 * guaranteed.
 */
object E02_TransactionTestUtils {

    fun <T> withNewTransaction(callback: () -> T): T {
        TestTransaction.flagForCommit()
        TestTransaction.end()
        try {
            return callback()
        } finally {
            TestTransaction.start()
        }
    }

    fun doInCommittedTransaction(callback: () -> Unit) {
        if (TestTransaction.isActive()) {
            withNewTransaction(callback)
            return
        }
        callback()
    }
}
