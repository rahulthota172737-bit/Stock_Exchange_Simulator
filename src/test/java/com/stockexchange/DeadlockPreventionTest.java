package com.stockexchange;

import com.stockexchange.concurrency.AccountLock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DeadlockPreventionTest {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("======================================");
        System.out.println("       DEADLOCK PREVENTION TEST");
        System.out.println("======================================");

        AccountLock accountA = new AccountLock();
        AccountLock accountB = new AccountLock();

        CountDownLatch startSignal = new CountDownLatch(1);

        Thread thread1 = new Thread(() -> {

            try {

                startSignal.await();

                transfer("Account-A", accountA, "Account-B", accountB);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        });

        Thread thread2 = new Thread(() -> {

            try {

                startSignal.await();

                transfer("Account-B", accountB, "Account-A", accountA);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        });

        thread1.start();
        thread2.start();

        System.out.println("Starting two opposing transfers...");

        startSignal.countDown();

        thread1.join(5_000);
        thread2.join(5_000);

        if (thread1.isAlive() || thread2.isAlive()) {

            System.out.println();
            System.out.println("FAIL: Possible deadlock detected.");

            thread1.interrupt();
            thread2.interrupt();

        } else {

            System.out.println();
            System.out.println("PASS: No deadlock detected.");
        }

        System.out.println("======================================");
    }

    private static void transfer(String accountId1, AccountLock accountLock1, String accountId2, AccountLock accountLock2) {

        AccountLock firstLock;
        AccountLock secondLock;

        /*
         * Always acquire locks in a deterministic order.
         */

        if (accountId1.compareTo(accountId2) < 0) {

            firstLock = accountLock1;
            secondLock = accountLock2;

        } else {

            firstLock = accountLock2;
            secondLock = accountLock1;
        }

        firstLock.lock();

        try {

            System.out.println(Thread.currentThread().getName() + " acquired first lock");

            secondLock.lock();

            try {

                System.out.println(Thread.currentThread().getName() + " acquired second lock");

                System.out.println(Thread.currentThread().getName() + " performing transfer");

            } finally {

                secondLock.unlock();
            }

        } finally {

            firstLock.unlock();
        }
    }
}