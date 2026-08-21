package com.stockexchange.concurrency;

public abstract class TradingTask implements Runnable {

    @Override
    public final void run() {
        execute();
    }

    protected abstract void execute();
}
