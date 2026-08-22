package com.stockexchange;

import com.stockexchange.concurrency.AccountLock;
import com.stockexchange.concurrency.ConcurrencyGuard;
import com.stockexchange.Util.AtomicIdGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyStressTest {

    private static final int TRADER_COUNT = 100;
    private static final int STOCK_COUNT = 10;
    private static final int ORDER_COUNT = 10_000;
    private static final int WORKER_COUNT = 10;

    private static final double INITIAL_BALANCE = 100_000.0;
    private static final double ORDER_VALUE = 1_000.0;

    private static final ConcurrentHashMap<String, Double> balances = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, AccountLock> accountLocks = new ConcurrentHashMap<>();

    private static final Set<Long> generatedOrderIds = ConcurrentHashMap.newKeySet();

    private static final AtomicInteger successfulOrders = new AtomicInteger();

    private static final AtomicInteger rejectedOrders = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        System.out.println("======================================");
        System.out.println("     CONCURRENCY STRESS TEST");
        System.out.println("======================================");

        initializeTraders();

        ExecutorService executor = Executors.newFixedThreadPool(WORKER_COUNT);

        CountDownLatch startSignal = new CountDownLatch(1);

        CountDownLatch completionSignal = new CountDownLatch(ORDER_COUNT);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ORDER_COUNT; i++) {

            executor.submit(() -> {

                try {

                    startSignal.await();

                    processOrder();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                } finally {

                    completionSignal.countDown();
                }
            });
        }

        System.out.println();
        System.out.println("Traders       : " + TRADER_COUNT);
        System.out.println("Stocks        : " + STOCK_COUNT);
        System.out.println("Workers       : " + WORKER_COUNT);
        System.out.println("Orders        : " + ORDER_COUNT);

        System.out.println();
        System.out.println("Starting stress test...");

        startSignal.countDown();

        completionSignal.await();

        executor.shutdown();

        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {

            System.out.println("FAIL: Executor did not terminate.");

            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();

        printResults(endTime - startTime);
    }

    private static void initializeTraders() {

        for (int i = 1; i <= TRADER_COUNT; i++) {

            String traderId = "TRADER-" + i;

            balances.put(traderId, INITIAL_BALANCE);

            accountLocks.put(traderId, new AccountLock());
        }
    }

    private static void processOrder() {

        String traderId = "TRADER-" + ((int) (Math.random() * TRADER_COUNT) + 1);

        String stockSymbol = "STOCK-" + ((int) (Math.random() * STOCK_COUNT) + 1);

        long orderId = AtomicIdGenerator.nextOrderId();

        generatedOrderIds.add(orderId);

        AccountLock accountLock = accountLocks.get(traderId);

        accountLock.lock();

        try {

            double currentBalance = balances.get(traderId);

            if (currentBalance >= ORDER_VALUE) {

                double newBalance = currentBalance - ORDER_VALUE;

                balances.put(traderId, newBalance);

                ConcurrencyGuard.verifyNonNegativeBalance(newBalance);

                successfulOrders.incrementAndGet();

            } else {

                rejectedOrders.incrementAndGet();
            }

        } finally {

            accountLock.unlock();
        }
    }

    private static void printResults(long executionTime) {

        int totalProcessed = successfulOrders.get() + rejectedOrders.get();

        System.out.println();
        System.out.println("======================================");
        System.out.println("             RESULTS");
        System.out.println("======================================");

        System.out.println("Orders submitted : " + ORDER_COUNT);

        System.out.println("Orders processed : " + totalProcessed);

        System.out.println("Successful       : " + successfulOrders.get());

        System.out.println("Rejected         : " + rejectedOrders.get());

        System.out.println("Unique IDs       : " + generatedOrderIds.size());

        System.out.println("Execution time   : " + executionTime + " ms");

        System.out.println();

        verifyResults();

        System.out.println();
        System.out.println("======================================");
    }

    private static void verifyResults() {

        boolean passed = true;

        if (totalOrdersProcessed() != ORDER_COUNT) {

            System.out.println("FAIL: Some orders were lost.");

            passed = false;
        }

        if (generatedOrderIds.size() != ORDER_COUNT) {

            System.out.println("FAIL: Duplicate order IDs detected.");

            passed = false;
        }

        for (var entry : balances.entrySet()) {

            double balance = entry.getValue();

            if (balance < 0) {

                System.out.println("FAIL: Negative balance for " + entry.getKey());

                passed = false;
            }
        }

        if (passed) {

            System.out.println("PASS: All concurrency invariants satisfied.");
        }
    }

    private static int totalOrdersProcessed() {

        return successfulOrders.get() + rejectedOrders.get();
    }
}
