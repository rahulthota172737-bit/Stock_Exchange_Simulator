package com.stockexchange.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TradingExecutor {

    private final ExecutorService executorService;

    public TradingExecutor(int threadCount) {

        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be greater than 0");
        }

        executorService = Executors.newFixedThreadPool(threadCount);
    }

    public Future<?> submit(Runnable task) {

        return executorService.submit(task);
    }

    public List<Future<?>> submitAll(List<? extends Runnable> tasks) {

        List<Future<?>> futures = new ArrayList<>();

        for (Runnable task : tasks) {

            futures.add(submit(task));
        }

        return futures;
    }

    public <T> Future<T> submit(Callable<T> task) {

        return executorService.submit(task);
    }

    public void shutdown() {

        executorService.shutdown();
    }

    public void awaitCompletion() {

        try {

            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {

                executorService.shutdownNow();
            }

        } catch (InterruptedException e) {

            executorService.shutdownNow();

            Thread.currentThread().interrupt();
        }
    }
}