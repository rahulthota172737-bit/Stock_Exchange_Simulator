package com.stockexchange;

import com.stockexchange.concurrency.AccountLock;
import com.stockexchange.concurrency.ConcurrencyGuard;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class DoubleSpendRaceTest {

    private static final double INITIAL_BALANCE = 10_000;
    private static final double ORDER_VALUE = 8_000;
    private static final int THREAD_COUNT = 100;

    private static double balance = INITIAL_BALANCE;

    private static final AtomicInteger successfulOrders = new AtomicInteger();

    private static final AtomicInteger rejectedOrders = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        System.out.println("======================================");
        System.out.println("       DOUBLE-SPEND RACE TEST");
        System.out.println("======================================");

        AccountLock accountLock = new AccountLock();

        CountDownLatch startSignal = new CountDownLatch(1);

        CountDownLatch completionSignal = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {

            Thread thread = new Thread(() -> {

                try {

                    startSignal.await();

                    accountLock.lock();

                    try {

                        if (balance >= ORDER_VALUE) {

                            balance -= ORDER_VALUE;

                            successfulOrders.incrementAndGet();

                            ConcurrencyGuard.verifyNonNegativeBalance(balance);

                        } else {

                            rejectedOrders.incrementAndGet();
                        }

                    } finally {

                        accountLock.unlock();
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                } finally {

                    completionSignal.countDown();
                }
            });

            thread.start();
        }

        System.out.println("Initial balance : ₹" + INITIAL_BALANCE);

        System.out.println("Threads         : " + THREAD_COUNT);

        System.out.println("Each attempts   : ₹" + ORDER_VALUE);

        System.out.println();
        System.out.println("Starting simultaneous transactions...");

        startSignal.countDown();

        completionSignal.await();

        System.out.println();
        System.out.println("======================================");
        System.out.println("             RESULTS");
        System.out.println("======================================");

        System.out.println("Successful : " + successfulOrders.get());

        System.out.println("Rejected   : " + rejectedOrders.get());

        System.out.println("Final cash : ₹" + balance);

        verifyResults();
    }

    private static void verifyResults() {

        boolean passed = true;

        if (successfulOrders.get() != 1) {

            System.out.println("FAIL: More than one transaction succeeded.");

            passed = false;
        }

        if (rejectedOrders.get() != 99) {

            System.out.println("FAIL: Incorrect number of rejected transactions.");

            passed = false;
        }

        if (balance != 2_000) {

            System.out.println("FAIL: Incorrect final balance.");

            passed = false;
        }

        if (balance < 0) {

            System.out.println("FAIL: Negative balance detected.");

            passed = false;
        }

        if (passed) {

            System.out.println();
            System.out.println("PASS: Double-spending prevented.");
        }

        System.out.println("======================================");
    }
}