package com.stockexchange.concurrency;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

public class OrderGenerationTask implements Callable<Order> {

    private final Trader trader;
    private final Stock stock;
    private final Random random;

    public OrderGenerationTask(
            Trader trader,
            Stock stock) {

        this.trader = trader;
        this.stock = stock;
        this.random = new Random();
    }

    @Override
    public Order call() {

        OrderSide side =
                random.nextBoolean()
                        ? OrderSide.BUY
                        : OrderSide.SELL;

        OrderType type =
                random.nextBoolean()
                        ? OrderType.LIMIT
                        : OrderType.MARKET;

        long quantity =
                random.nextInt(100) + 1;

        double price;

        if (type == OrderType.MARKET) {
            price = 0.0;
        } else {
            price = 50 + random.nextDouble() * 450;
            price = Math.round(price * 100.0) / 100.0;
        }

        return new Order(
                UUID.randomUUID().toString(),
                trader.getTraderId(),
                stock.getSymbol(),
                side,
                type,
                price,
                quantity,
                Instant.now()
        );
    }
}