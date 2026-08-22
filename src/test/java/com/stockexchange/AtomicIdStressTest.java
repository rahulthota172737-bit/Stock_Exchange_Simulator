package com.stockexchange;

import com.stockexchange.Util.AtomicIdGenerator;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class AtomicIdStressTest {

    private static final int THREAD_COUNT = 100;
    private static final int IDS_PER_THREAD = 1_000;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("======================================");
        System.out.println("        ATOMIC ID STRESS TEST");
        System.out.println("======================================");

        Set<Long> generatedIds = ConcurrentHashMap.newKeySet();

        CountDownLatch startSignal = new CountDownLatch(1);

        CountDownLatch completionSignal = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {

            Thread thread = new Thread(() -> {

                try {

                    startSignal.await();

                    for (int j = 0; j < IDS_PER_THREAD; j++) {

                        long id = AtomicIdGenerator.nextOrderId();

                        generatedIds.add(id);
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                } finally {

                    completionSignal.countDown();
                }
            });

            thread.start();
        }

        int expectedIds = THREAD_COUNT * IDS_PER_THREAD;

        System.out.println("Threads       : " + THREAD_COUNT);

        System.out.println("IDs/thread    : " + IDS_PER_THREAD);

        System.out.println("Expected IDs  : " + expectedIds);

        System.out.println();
        System.out.println("Starting concurrent ID generation...");

        startSignal.countDown();

        completionSignal.await();

        int actualIds = generatedIds.size();

        System.out.println();
        System.out.println("======================================");
        System.out.println("             RESULTS");
        System.out.println("======================================");

        System.out.println("Expected IDs : " + expectedIds);

        System.out.println("Unique IDs   : " + actualIds);

        if (actualIds == expectedIds) {

            System.out.println();
            System.out.println("PASS: No duplicate IDs detected.");

        } else {

            System.out.println();
            System.out.println("FAIL: Duplicate IDs detected.");
        }

        System.out.println("======================================");
    }
}
