package com.conference.website.utils;

import org.springframework.test.context.transaction.TestTransaction;

import java.util.function.Supplier;

public final class TransactionTestUtils {

    private TransactionTestUtils() {
    }

    public static void withNewTransaction(Runnable callback) {
        withNewTransaction(() -> {
            callback.run();
            return null;
        });
    }

    public static <T> T withNewTransaction(Supplier<T> callback) {
        TestTransaction.flagForCommit();
        TestTransaction.end();
        try {
            return callback.get();
        }
        finally {
            TestTransaction.start();
        }
    }

    public static void doInCommittedTransaction(Runnable callback) {
        if (TestTransaction.isActive()) {
            withNewTransaction(callback);
            return;
        }
        callback.run();
    }
}
