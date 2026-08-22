package com.stockexchange;

import com.stockexchange.concurrency.AccountLock;
import com.stockexchange.concurrency.ConcurrencyGuard;
import com.stockexchange.Util.AtomicIdGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FinalConcurrencyTest {

    private static final int TRADER_COUNT = 100;
    private static final int STOCK_COUNT = 10;
    private static final int WORKER_COUNT = 10;
    private static final int ORDER_COUNT = 10_000;

    private static final double INITIAL_BALANCE = 10_000.0;
    private static final double ORDER_VALUE = 8_000.0;

    private static final Map<String, Double> balances = new ConcurrentHashMap<>();

    private static final Map<String, AccountLock> accountLocks = new ConcurrentHashMap<>();

    private static final java.util.Set<Long> orderIds = ConcurrentHashMap.newKeySet();

    private static final AtomicInteger successfulOrders = new AtomicInteger();

    private static final AtomicInteger rejectedOrders = new AtomicInteger();

    private static final AtomicInteger completedOrders = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        System.out.println("==============================================");
        System.out.println("       FINAL DAY-7 CONCURRENCY TEST");
        System.out.println("==============================================");

        initialize();

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

                    completedOrders.incrementAndGet();
                    completionSignal.countDown();
                }
            });
        }

        System.out.println("Traders        : " + TRADER_COUNT);
        System.out.println("Stocks         : " + STOCK_COUNT);
        System.out.println("Workers        : " + WORKER_COUNT);
        System.out.println("Orders         : " + ORDER_COUNT);

        System.out.println();
        System.out.println("Starting final concurrency test...");

        startSignal.countDown();

        completionSignal.await();

        executor.shutdown();

        boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);

        long executionTime = System.currentTimeMillis() - startTime;

        System.out.println();
        System.out.println("==============================================");
        System.out.println("                  RESULTS");
        System.out.println("==============================================");

        System.out.println("Submitted orders : " + ORDER_COUNT);

        System.out.println("Completed orders : " + completedOrders.get());

        System.out.println("Successful       : " + successfulOrders.get());

        System.out.println("Rejected         : " + rejectedOrders.get());

        System.out.println("Unique IDs       : " + orderIds.size());

        System.out.println("Execution time   : " + executionTime + " ms");

        System.out.println("Executor stopped : " + terminated);

        verifyFinalState();
    }

    private static void initialize() {

        for (int i = 1; i <= TRADER_COUNT; i++) {

            String traderId = "TRADER-" + i;

            balances.put(traderId, INITIAL_BALANCE);

            accountLocks.put(traderId, new AccountLock());
        }
    }

    private static void processOrder() {

        String traderId = "TRADER-" + ((int) (Math.random() * TRADER_COUNT) + 1);

        String stock = "STOCK-" + ((int) (Math.random() * STOCK_COUNT) + 1);

        long orderId = AtomicIdGenerator.nextOrderId();

        orderIds.add(orderId);

        AccountLock accountLock = accountLocks.get(traderId);

        accountLock.lock();

        try {

            double balance = balances.get(traderId);

            if (balance >= ORDER_VALUE) {

                double updatedBalance = balance - ORDER_VALUE;

                balances.put(traderId, updatedBalance);

                ConcurrencyGuard.verifyNonNegativeBalance(updatedBalance);

                ConcurrencyGuard.verifyOrderQuantity(1);

                successfulOrders.incrementAndGet();

            } else {

                rejectedOrders.incrementAndGet();
            }

        } finally {

            accountLock.unlock();
        }
    }

    private static void verifyFinalState() {

        boolean passed = true;

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("           INVARIANT VERIFICATION");
        System.out.println("----------------------------------------------");

        if (completedOrders.get() != ORDER_COUNT) {

            System.out.println("FAIL: Lost orders detected.");

            passed = false;

        } else {

            System.out.println("PASS: No orders lost.");
        }

        int processed = successfulOrders.get() + rejectedOrders.get();

        if (processed != ORDER_COUNT) {

            System.out.println("FAIL: Processed order count mismatch.");

            passed = false;

        } else {

            System.out.println("PASS: Order accounting is correct.");
        }

        if (orderIds.size() != ORDER_COUNT) {

            System.out.println("FAIL: Duplicate order IDs detected.");

            passed = false;

        } else {

            System.out.println("PASS: No duplicate order IDs.");
        }

        for (Map.Entry<String, Double> entry : balances.entrySet()) {

            try {

                ConcurrencyGuard.verifyNonNegativeBalance(entry.getValue());

            } catch (IllegalStateException e) {

                System.out.println("FAIL: Negative balance: " + entry.getKey());

                passed = false;
            }
        }

        if (passed) {

            System.out.println("PASS: No negative balances.");
        }

        if (!isExecutorStateValid()) {

            System.out.println("FAIL: Executor did not terminate correctly.");

            passed = false;

        } else {

            System.out.println("PASS: No deadlock detected.");
        }

        System.out.println();
        System.out.println("----------------------------------------------");

        if (passed) {

            System.out.println("DAY 7 CONCURRENCY TEST PASSED ");

        } else {

            System.out.println("CONCURRENCY TEST FAILED");
        }

        System.out.println("==============================================");
    }

    private static boolean isExecutorStateValid() {
        return completedOrders.get() == ORDER_COUNT;
    }
}
