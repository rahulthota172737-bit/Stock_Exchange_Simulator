package com.stockexchange;

import com.stockexchange.Util.AtomicIdGenerator;

public class AtomicIdTest {

    public static void main(String[] args) {

        System.out.println(AtomicIdGenerator.nextOrderId());

        System.out.println(AtomicIdGenerator.nextOrderId());

        System.out.println(AtomicIdGenerator.nextTradeId());

        System.out.println(AtomicIdGenerator.nextTransactionId());
    }
}
