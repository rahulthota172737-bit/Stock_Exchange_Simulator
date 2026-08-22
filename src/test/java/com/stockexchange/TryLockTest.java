package com.stockexchange;

import com.stockexchange.concurrency.AccountLock;

import java.util.concurrent.TimeUnit;

public class TryLockTest {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("======================================");
        System.out.println("          TRY-LOCK TEST");
        System.out.println("======================================");

        AccountLock accountLock = new AccountLock();

        Thread holderThread = new Thread(() -> {

            accountLock.lock();

            try {

                System.out.println("Holder thread acquired the lock.");

                System.out.println("Holder thread is working...");

                try {

                    Thread.sleep(3_000);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    System.out.println("Holder thread interrupted.");
                }

            } finally {

                accountLock.unlock();

                System.out.println("Holder thread released the lock.");
            }

        });

        Thread waitingThread = new Thread(() -> {

            try {

                Thread.sleep(500);

                System.out.println("Waiting thread attempting tryLock...");

                boolean acquired = accountLock.getLock().tryLock(1, TimeUnit.SECONDS);

                if (acquired) {

                    try {

                        System.out.println("Waiting thread acquired the lock.");

                    } finally {

                        accountLock.unlock();
                    }

                } else {

                    System.out.println("Waiting thread timed out.");
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.out.println("Waiting thread interrupted.");
            }

        });

        holderThread.start();
        waitingThread.start();

        holderThread.join();
        waitingThread.join();

        System.out.println();
        System.out.println("PASS: tryLock behavior tested.");

        System.out.println("======================================");
    }
}
