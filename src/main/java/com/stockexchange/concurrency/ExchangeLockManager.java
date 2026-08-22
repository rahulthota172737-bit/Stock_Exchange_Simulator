package com.stockexchange.concurrency;

import java.util.concurrent.ConcurrentHashMap;

public class ExchangeLockManager {

    private final ConcurrentHashMap<String, OrderBookLock> orderBookLocks =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, AccountLock> accountLocks =
            new ConcurrentHashMap<>();

    public OrderBookLock getOrderBookLock(String stockSymbol) {
        return orderBookLocks.computeIfAbsent(
                stockSymbol,
                key -> new OrderBookLock()
        );
    }

    public AccountLock getAccountLock(String accountId) {
        return accountLocks.computeIfAbsent(
                accountId,
                key -> new AccountLock()
        );
    }

    public void acquireOrderBookLock(String stockSymbol) {
        getOrderBookLock(stockSymbol).lock();
    }

    public void releaseOrderBookLock(String stockSymbol) {
        getOrderBookLock(stockSymbol).unlock();
    }

    public void acquireAccountLock(String accountId) {
        getAccountLock(accountId).lock();
    }

    public void releaseAccountLock(String accountId) {
        getAccountLock(accountId).unlock();
    }

    public boolean tryAcquireOrderBookLock(String stockSymbol) {
        return getOrderBookLock(stockSymbol).tryLock();
    }

    public boolean tryAcquireAccountLock(String accountId) {
        return getAccountLock(accountId).tryLock();
    }
}
