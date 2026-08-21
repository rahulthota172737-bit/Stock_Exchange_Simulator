package com.stockexchange.concurrency;

public class TradingExecutorDemo {

    public static void main(String[] args) {

        TradingExecutor executor = new TradingExecutor(3);

        for (int i = 1; i <= 10; i++) {

            int taskNumber = i;

            executor.submit(() -> {
                System.out.println(
                        Thread.currentThread().getName()
                                + " executing task "
                                + taskNumber
                );
            });
        }

        executor.shutdown();
        executor.awaitCompletion();

        System.out.println("All tasks completed.");
    }
}