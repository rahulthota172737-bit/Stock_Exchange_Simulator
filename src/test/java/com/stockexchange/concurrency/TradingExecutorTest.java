package com.stockexchange.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TradingExecutorTest {

    @Test
    void shouldExecuteRunnableTasks() {

        TradingExecutor executor = new TradingExecutor(3);

        try {

            Future<?> future = executor.submit(() -> System.out.println("Runnable executed by " + Thread.currentThread().getName()));

            assertNotNull(future);

            future.get();

        } catch (Exception e) {

            throw new RuntimeException(e);

        } finally {

            executor.shutdown();
            executor.awaitCompletion();
        }
    }

    @Test
    void shouldExecuteCallableTask() {

        TradingExecutor executor = new TradingExecutor(3);

        try {

            Future<Integer> future = executor.submit(() -> 100);

            assertEquals(100, future.get());

        } catch (Exception e) {

            throw new RuntimeException(e);

        } finally {

            executor.shutdown();
            executor.awaitCompletion();
        }
    }

    @Test
    void shouldExecuteMultipleTasks() {

        TradingExecutor executor = new TradingExecutor(3);

        try {

            for (int i = 0; i < 20; i++) {

                int taskNumber = i;

                executor.submit(() -> System.out.println("Task " + taskNumber + " executed by " + Thread.currentThread().getName()));
            }

        } finally {

            executor.shutdown();
            executor.awaitCompletion();
        }
    }
}