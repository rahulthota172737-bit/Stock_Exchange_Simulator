package com.stockexchange.matching;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.model.Order;
import com.stockexchange.model.Trade;

import java.time.Instant;
import java.util.UUID;

public class TradeExecutor {

    public Trade execute(Order buyOrder, Order sellOrder, Order restingOrder) {

        long executionQuantity =
                calculateExecutionQuantity(buyOrder, sellOrder);

        double executionPrice =
                calculateExecutionPrice(restingOrder);

        return new Trade(
                UUID.randomUUID().toString(),
                buyOrder.getOrderId(),
                sellOrder.getOrderId(),
                buyOrder.getSymbol(),
                executionPrice,
                executionQuantity,
                Instant.now()
        );
    }

    public long calculateExecutionQuantity(
            Order buyOrder,
            Order sellOrder
    ) {

        return Math.min(
                buyOrder.getRemainingQuantity(),
                sellOrder.getRemainingQuantity()
        );
    }

    public double calculateExecutionPrice(Order restingOrder) {
        return restingOrder.getPrice();
    }
}
