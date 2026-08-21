package com.stockexchange.simulation;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;
import com.stockexchange.service.ConcurrentOrderService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;

public class TraderSimulator implements Runnable {

    private final Trader trader;
    private final List<Stock> stocks;
    private final ConcurrentOrderService orderService;
    private final Random random;
    private final int ordersPerTrader;
    private final SimulationMetrics metrics;

    private final List<Future<?>> submittedOrders;

    public TraderSimulator(
            Trader trader,
            List<Stock> stocks,
            ConcurrentOrderService orderService,
            int ordersPerTrader,
            SimulationMetrics metrics) {

        this.trader = trader;
        this.stocks = stocks;
        this.orderService = orderService;
        this.ordersPerTrader = ordersPerTrader;
        this.metrics = metrics;

        this.random = new Random();
        this.submittedOrders = new ArrayList<>();
    }

    @Override
    public void run() {

        for (int i = 0; i < ordersPerTrader; i++) {

            Order order = generateOrder();

            metrics.recordOrderSubmitted();

            Future<?> future =
                    orderService.submitAsync(order);

            submittedOrders.add(future);
        }
    }

    public List<Future<?>> getSubmittedOrders() {
        return List.copyOf(submittedOrders);
    }

    private Order generateOrder() {

        Stock stock = stocks.get(random.nextInt(stocks.size()));

        boolean buy = random.nextBoolean();

        if (buy) {
            return generateBuyOrder(stock);
        }

        return generateSellOrder(stock);
    }

    private Order generateBuyOrder(Stock stock) {

        OrderType type = randomOrderType();

        return createOrder(stock, OrderSide.BUY, type);
    }

    private Order generateSellOrder(Stock stock) {

        OrderType type = randomOrderType();

        return createOrder(stock, OrderSide.SELL, type);
    }

    private Order createOrder(Stock stock, OrderSide side, OrderType type) {

        long quantity = generateQuantity();

        double price = generatePrice(type);

        String orderId = UUID.randomUUID().toString();

        return new Order(orderId, trader.getTraderId(), stock.getSymbol(), side, type, price, quantity, Instant.now());
    }

    private OrderType randomOrderType() {

        if (random.nextBoolean()) {
            return OrderType.LIMIT;
        }

        return OrderType.MARKET;
    }

    private long generateQuantity() {

        return random.nextInt(100) + 1;
    }

    private double generatePrice(OrderType type) {

        if (type == OrderType.MARKET) {
            return 0.0;
        }

        double price = 50 + random.nextDouble() * 450;

        return Math.round(price * 100.0) / 100.0;
    }
}
