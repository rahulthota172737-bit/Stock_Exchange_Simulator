package com.stockexchange;

import com.stockexchange.concurrency.AccountLock;

import java.util.concurrent.CountDownLatch;

public class RaceConditionTest {

    private static double balance = 10_000;

    public static void main(String[] args) throws InterruptedException {

        int threadCount = 100;
        double withdrawalAmount = 100;

        AccountLock accountLock = new AccountLock();

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch completionSignal = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {

            Thread thread = new Thread(() -> {

                try {

                    startSignal.await();

                    accountLock.lock();

                    try {

                        if (balance >= withdrawalAmount) {
                            balance -= withdrawalAmount;
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

        System.out.println("Starting 100 concurrent withdrawals...");

        startSignal.countDown();

        completionSignal.await();

        double expectedBalance = 10_000 - (threadCount * withdrawalAmount);

        System.out.println("Expected balance: ₹" + expectedBalance);
        System.out.println("Actual balance:   ₹" + balance);

        if (balance == expectedBalance) {
            System.out.println("PASS: No race condition detected.");
        } else {
            System.out.println("FAIL: Race condition detected.");
        }
    }
}
