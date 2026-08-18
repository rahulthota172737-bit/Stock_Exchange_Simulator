package com.stockexchange.service;

import com.stockexchange.book.OrderBook;
import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderStatus;
import com.stockexchange.enums.OrderType;
import com.stockexchange.exception.StockNotFoundException;
import com.stockexchange.exception.TraderNotFoundException;
import com.stockexchange.exchange.ExchangeConfig;
import com.stockexchange.matching.MatchResult;
import com.stockexchange.matching.MatchingEngine;
import com.stockexchange.model.Order;
import com.stockexchange.repository.OrderRepository;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;

import java.time.Instant;
import java.util.Map;

public class OrderService {

    private final OrderRepository orderRepository;
    private final TraderRepository traderRepository;
    private final StockRepository stockRepository;
    private final Map<String, OrderBook> orderBooks;
    private final Map<String, MatchingEngine> matchingEngines;
    private final ExchangeConfig exchangeConfig;

    public OrderService(
            OrderRepository orderRepository,
            TraderRepository traderRepository,
            StockRepository stockRepository,
            Map<String, OrderBook> orderBooks,
            Map<String, MatchingEngine> matchingEngines,
            ExchangeConfig exchangeConfig) {

        this.orderRepository = orderRepository;
        this.traderRepository = traderRepository;
        this.stockRepository = stockRepository;
        this.orderBooks = orderBooks;
        this.matchingEngines = matchingEngines;
        this.exchangeConfig = exchangeConfig;
    }

    public Order createOrder(
            String orderId,
            String traderId,
            String symbol,
            OrderSide side,
            OrderType type,
            double price,
            long quantity,
            Instant timestamp) {

        Order order = new Order(
                orderId,
                traderId,
                symbol,
                side,
                type,
                price,
                quantity,
                timestamp
        );

        validateOrder(order);

        return order;
    }

    public void validateOrder(Order order) {

        if (order == null) {
            throw new IllegalArgumentException(
                    "Order cannot be null"
            );
        }

        if (!traderRepository.exists(order.getTraderId())) {
            throw new TraderNotFoundException(
                    "Trader not found: "
                            + order.getTraderId()
            );
        }

        if (!stockRepository.exists(order.getSymbol())) {
            throw new StockNotFoundException(
                    "Stock not found: "
                            + order.getSymbol()
            );
        }

        long quantity = order.getQuantity();

        if (quantity < exchangeConfig.getMinimumOrderQuantity()) {
            throw new IllegalArgumentException(
                    "Order quantity is below minimum allowed quantity"
            );
        }

        if (quantity > exchangeConfig.getMaximumOrderQuantity()) {
            throw new IllegalArgumentException(
                    "Order quantity exceeds maximum allowed quantity"
            );
        }

        if (!exchangeConfig.getSupportedOrderTypes()
                .contains(order.getType())) {

            throw new IllegalArgumentException(
                    "Order type is not supported: "
                            + order.getType()
            );
        }

        if (order.getType() == OrderType.LIMIT
                && order.getPrice() <= 0) {

            throw new IllegalArgumentException(
                    "Limit order price must be greater than zero"
            );
        }
    }

    public MatchResult submitOrder(Order order) {

        validateOrder(order);

        MatchingEngine matchingEngine =
                matchingEngines.get(order.getSymbol());

        if (matchingEngine == null) {
            throw new StockNotFoundException(
                    "No matching engine for stock: "
                            + order.getSymbol()
            );
        }

        orderRepository.save(order);

        return matchingEngine.submitOrder(order);
    }

    public void cancelOrder(String orderId) {

        Order order =
                orderRepository.findById(orderId);

        if (order == null) {
            throw new IllegalArgumentException(
                    "Order not found: " + orderId
            );
        }

        OrderBook orderBook =
                orderBooks.get(order.getSymbol());

        if (orderBook == null) {
            throw new StockNotFoundException(
                    "No order book for stock: "
                            + order.getSymbol()
            );
        }

        OrderStatus status = order.getStatus();

        if (status != OrderStatus.OPEN
                && status != OrderStatus.PARTIALLY_FILLED) {

            throw new IllegalStateException(
                    "Order cannot be cancelled from status: "
                            + status
            );
        }

        orderBook.removeOrder(order);
        order.cancel();
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }
}