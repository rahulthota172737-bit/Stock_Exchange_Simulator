package com.stockexchange.concurrency;

import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;

import java.util.concurrent.Future;

public class OrderGenerationDemo {

    public static void main(String[] args) {

        Trader trader =
                new Trader("T001", "Trader One");

        Stock stock =
                new Stock("AAPL", "Apple Inc.");

        TradingExecutor executor =
                new TradingExecutor(3);

        OrderGenerationTask task =
                new OrderGenerationTask(
                        trader,
                        stock
                );

        Future<Order> future =
                executor.submit(task);

        try {

            Order order = future.get();

            System.out.println(
                    "Order Generated"
            );

            System.out.println(
                    "Order ID: " + order.getOrderId()
            );

            System.out.println(
                    "Trader: " + order.getTraderId()
            );

            System.out.println(
                    "Stock: " + order.getSymbol()
            );

            System.out.println(
                    "Side: " + order.getSide()
            );

            System.out.println(
                    "Type: " + order.getType()
            );

            System.out.println(
                    "Price: " + order.getPrice()
            );

            System.out.println(
                    "Quantity: " + order.getQuantity()
            );

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            executor.shutdown();
            executor.awaitCompletion();
        }
    }
}
