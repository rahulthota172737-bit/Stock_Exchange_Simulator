package com.stockexchange.concurrency;

import java.util.concurrent.locks.ReentrantLock;

public class AccountLock {

    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public void unlock() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public boolean isLocked() {
        return lock.isLocked();
    }

    public boolean isHeldByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }

    public ReentrantLock getLock() {
        return lock;
    }
}