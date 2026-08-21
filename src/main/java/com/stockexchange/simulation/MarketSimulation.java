package com.stockexchange.simulation;

import com.stockexchange.concurrency.TradingExecutor;
import com.stockexchange.exchange.Exchange;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;
import com.stockexchange.service.ConcurrentOrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class MarketSimulation {

    private final Exchange exchange;
    private final List<Trader> traders;
    private final List<Stock> stocks;
    private final TradingExecutor tradingExecutor;
    private final ConcurrentOrderService orderService;
    private final int ordersPerTrader;
    private final SimulationMetrics metrics;

    public MarketSimulation(Exchange exchange, List<Trader> traders, List<Stock> stocks, TradingExecutor tradingExecutor, int ordersPerTrader) {

        this.exchange = exchange;
        this.metrics = new SimulationMetrics();
        this.traders = traders;
        this.stocks = stocks;
        this.tradingExecutor = tradingExecutor;
        this.ordersPerTrader = ordersPerTrader;

        this.orderService = new ConcurrentOrderService(exchange, tradingExecutor, metrics);
    }

    public void start() {

        List<TraderSimulator> simulations = new ArrayList<>();

        for (Trader trader : traders) {

            TraderSimulator simulator = new TraderSimulator(trader, stocks, orderService, ordersPerTrader, metrics);

            simulations.add(simulator);
        }

        List<Future<?>> traderFutures = tradingExecutor.submitAll(simulations);

        waitForTraderTasks(traderFutures);

        waitForOrderTasks(simulations);
    }

    private void waitForTraderTasks(List<Future<?>> futures) {

        for (Future<?> future : futures) {

            try {

                future.get();

            } catch (Exception e) {

                throw new RuntimeException("Trader simulation failed", e);
            }
        }
    }

    private void waitForOrderTasks(List<TraderSimulator> simulations) {

        for (TraderSimulator simulator : simulations) {

            for (Future<?> future : simulator.getSubmittedOrders()) {

                try {

                    future.get();

                } catch (Exception e) {

                    metrics.recordOrderRejected();
                }
            }
        }
    }

    public void run() {

        start();

        stop();
    }

    public void stop() {

        tradingExecutor.shutdown();

        tradingExecutor.awaitCompletion();
    }

    public SimulationMetrics getMetrics() {
        return metrics;
    }
}
