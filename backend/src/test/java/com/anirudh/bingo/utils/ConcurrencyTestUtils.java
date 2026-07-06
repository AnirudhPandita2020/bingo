package com.anirudh.bingo.utils;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.fail;

public final class ConcurrencyTestUtils {

    private ConcurrencyTestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void await(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail(e);
        }
    }

}
