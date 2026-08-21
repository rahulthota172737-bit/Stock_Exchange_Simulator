package com.stockexchange.concurrency;

import com.stockexchange.exchange.Exchange;
import com.stockexchange.matching.MatchResult;
import com.stockexchange.model.Order;
import com.stockexchange.simulation.SimulationMetrics;

public class OrderSubmissionTask extends TradingTask {

    private final Exchange exchange;
    private final Order order;
    private final SimulationMetrics metrics;

    public OrderSubmissionTask(Exchange exchange, Order order, SimulationMetrics metrics) {

        this.exchange = exchange;
        this.order = order;
        this.metrics = metrics;
    }

    @Override
    protected void execute() {

        try {

            MatchResult result = exchange.submitOrder(order);

            metrics.recordOrderExecuted();

            for (int i = 0; i < result.getTrades().size(); i++) {

                metrics.recordTrade();
            }

        } catch (Exception e) {

            metrics.recordOrderRejected();

            throw e;
        }
    }
}