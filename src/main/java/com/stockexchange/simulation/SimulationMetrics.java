package com.stockexchange.simulation;

import java.util.concurrent.atomic.AtomicInteger;

public class SimulationMetrics {

    private final AtomicInteger totalOrdersSubmitted = new AtomicInteger();

    private final AtomicInteger totalOrdersExecuted = new AtomicInteger();

    private final AtomicInteger totalOrdersRejected = new AtomicInteger();

    private final AtomicInteger totalTrades = new AtomicInteger();

    private long simulationStartTime;
    private long simulationEndTime;

    public void start() {
        simulationStartTime = System.currentTimeMillis();
    }

    public void finish() {
        simulationEndTime = System.currentTimeMillis();
    }

    public void recordOrderSubmitted() {
        totalOrdersSubmitted.incrementAndGet();
    }

    public void recordOrderExecuted() {
        totalOrdersExecuted.incrementAndGet();
    }

    public void recordOrderRejected() {
        totalOrdersRejected.incrementAndGet();
    }

    public void recordTrade() {
        totalTrades.incrementAndGet();
    }

    public int getTotalOrdersSubmitted() {
        return totalOrdersSubmitted.get();
    }

    public int getTotalOrdersExecuted() {
        return totalOrdersExecuted.get();
    }

    public int getTotalOrdersRejected() {
        return totalOrdersRejected.get();
    }

    public int getTotalTrades() {
        return totalTrades.get();
    }

    public long getSimulationStartTime() {
        return simulationStartTime;
    }

    public long getSimulationEndTime() {
        return simulationEndTime;
    }

    public long getTotalExecutionTime() {
        return simulationEndTime - simulationStartTime;
    }

    public double getOrdersPerSecond() {

        double seconds = getTotalExecutionTime() / 1000.0;

        if (seconds <= 0) {
            return 0;
        }

        return getTotalOrdersSubmitted() / seconds;
    }

    public double getTradesPerSecond() {

        double seconds = getTotalExecutionTime() / 1000.0;

        if (seconds <= 0) {
            return 0;
        }

        return getTotalTrades() / seconds;
    }

    @Override
    public String toString() {

        return "====================================\n" + "       MARKET SIMULATION\n" + "====================================\n\n" +

                "Orders Submitted:    " + getTotalOrdersSubmitted() + "\n" +

                "Orders Executed:     " + getTotalOrdersExecuted() + "\n" +

                "Rejected Orders:     " + getTotalOrdersRejected() + "\n" +

                "Trades Executed:     " + getTotalTrades() + "\n\n" +

                "Execution Time:      " + getTotalExecutionTime() + " ms\n" +

                "Orders/sec:          " + String.format("%.2f", getOrdersPerSecond()) + "\n" +

                "Trades/sec:          " + String.format("%.2f", getTradesPerSecond()) + "\n" +

                "====================================";
    }
}
