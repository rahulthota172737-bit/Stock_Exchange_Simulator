package com.stockexchange.Util;

import java.util.concurrent.atomic.AtomicLong;

public final class AtomicIdGenerator {

    private static final AtomicLong ORDER_ID = new AtomicLong(0);
    private static final AtomicLong TRADE_ID = new AtomicLong(0);
    private static final AtomicLong TRANSACTION_ID = new AtomicLong(0);

    private AtomicIdGenerator() {

    }

    public static long nextOrderId() {
        return ORDER_ID.incrementAndGet();
    }

    public static long nextTradeId() {
        return TRADE_ID.incrementAndGet();
    }

    public static long nextTransactionId() {
        return TRANSACTION_ID.incrementAndGet();
    }
}
