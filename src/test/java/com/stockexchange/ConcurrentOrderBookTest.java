package com.stockexchange;

import com.stockexchange.concurrency.ConcurrencyGuard;
import com.stockexchange.concurrency.OrderBookLock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentOrderBookTest {

    private static final int THREAD_COUNT = 100;
    private static final int ORDERS_PER_THREAD = 100;

    private static final Map<Long, Long> orderBook = new ConcurrentHashMap<>();

    private static final AtomicInteger addedOrders = new AtomicInteger();

    private static final AtomicInteger removedOrders = new AtomicInteger();

    private static final AtomicInteger duplicateOrders = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        System.out.println("======================================");
        System.out.println("      CONCURRENT ORDER BOOK TEST");
        System.out.println("======================================");

        OrderBookLock orderBookLock = new OrderBookLock();

        CountDownLatch startSignal = new CountDownLatch(1);

        CountDownLatch completionSignal = new CountDownLatch(THREAD_COUNT);

        for (int threadNumber = 0; threadNumber < THREAD_COUNT; threadNumber++) {

            final int threadId = threadNumber;

            Thread thread = new Thread(() -> {

                try {

                    startSignal.await();

                    /*
                     * ADD ORDERS
                     */

                    for (int i = 0; i < ORDERS_PER_THREAD; i++) {

                        long orderId = ((long) threadId * 1000) + i;

                        orderBookLock.lock();

                        try {

                            if (orderBook.containsKey(orderId)) {

                                duplicateOrders.incrementAndGet();

                            } else {

                                orderBook.put(orderId, 100L);

                                addedOrders.incrementAndGet();

                                ConcurrencyGuard.verifyOrderQuantity(100);
                            }

                        } finally {

                            orderBookLock.unlock();
                        }
                    }

                    /*
                     * REMOVE HALF OF THE ORDERS
                     */

                    for (int i = 0; i < ORDERS_PER_THREAD / 2; i++) {

                        long orderId = ((long) threadId * 1000) + i;

                        orderBookLock.lock();

                        try {

                            if (orderBook.remove(orderId) != null) {

                                removedOrders.incrementAndGet();
                            }

                        } finally {

                            orderBookLock.unlock();
                        }
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                } finally {

                    completionSignal.countDown();
                }

            });

            thread.start();
        }

        System.out.println("Threads              : " + THREAD_COUNT);

        System.out.println("Orders per thread    : " + ORDERS_PER_THREAD);

        System.out.println("Total orders added   : " + (THREAD_COUNT * ORDERS_PER_THREAD));

        System.out.println();
        System.out.println("Starting concurrent order-book operations...");

        startSignal.countDown();

        completionSignal.await();

        System.out.println();
        System.out.println("======================================");
        System.out.println("             RESULTS");
        System.out.println("======================================");

        System.out.println("Orders added       : " + addedOrders.get());

        System.out.println("Orders removed     : " + removedOrders.get());

        System.out.println("Orders remaining   : " + orderBook.size());

        System.out.println("Duplicate orders   : " + duplicateOrders.get());

        verifyResults();
    }

    private static void verifyResults() {

        boolean passed = true;

        int expectedAdded = THREAD_COUNT * ORDERS_PER_THREAD;

        int expectedRemoved = THREAD_COUNT * (ORDERS_PER_THREAD / 2);

        int expectedRemaining = expectedAdded - expectedRemoved;

        if (addedOrders.get() != expectedAdded) {

            System.out.println("FAIL: Incorrect number of added orders.");

            passed = false;
        }

        if (removedOrders.get() != expectedRemoved) {

            System.out.println("FAIL: Incorrect number of removed orders.");

            passed = false;
        }

        if (orderBook.size() != expectedRemaining) {

            System.out.println("FAIL: Incorrect final order-book size.");

            passed = false;
        }

        if (duplicateOrders.get() != 0) {

            System.out.println("FAIL: Duplicate orders detected.");

            passed = false;
        }

        if (passed) {

            System.out.println();
            System.out.println("PASS: Order book remained consistent.");
        }

        System.out.println("======================================");
    }
}
