package com.stockexchange.service;

import com.stockexchange.concurrency.OrderSubmissionTask;
import com.stockexchange.concurrency.TradingExecutor;
import com.stockexchange.exchange.Exchange;
import com.stockexchange.model.Order;
import com.stockexchange.simulation.SimulationMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class ConcurrentOrderService {

    private final Exchange exchange;
    private final TradingExecutor tradingExecutor;
    private final SimulationMetrics metrics;

    public ConcurrentOrderService(
            Exchange exchange,
            TradingExecutor tradingExecutor,
            SimulationMetrics metrics) {

        this.exchange = exchange;
        this.tradingExecutor = tradingExecutor;
        this.metrics = metrics;
    }

    public Future<?> submitAsync(Order order) {

        OrderSubmissionTask task =
                new OrderSubmissionTask(
                        exchange,
                        order,
                        metrics
                );

        return tradingExecutor.submit(task);
    }

    public List<Future<?>> submitBatch(
            List<Order> orders) {

        List<OrderSubmissionTask> tasks =
                new ArrayList<>();

        for (Order order : orders) {

            tasks.add(
                    new OrderSubmissionTask(
                            exchange,
                            order,
                            metrics
                    )
            );
        }

        return tradingExecutor.submitAll(tasks);
    }
}
