package com.stockexchange.service;

import com.stockexchange.book.OrderBook;
import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.exception.StockNotFoundException;
import com.stockexchange.exception.TraderNotFoundException;
import com.stockexchange.exchange.ExchangeConfig;
import com.stockexchange.matching.MatchingEngine;
import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;
import com.stockexchange.repository.OrderRepository;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;
    private TraderRepository traderRepository;
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {

        OrderRepository orderRepository =
                new OrderRepository();

        traderRepository =
                new TraderRepository();

        stockRepository =
                new StockRepository();

        Map<String, OrderBook> orderBooks =
                new HashMap<>();

        Map<String, MatchingEngine> matchingEngines =
                new HashMap<>();

        ExchangeConfig exchangeConfig =
                new ExchangeConfig(
                        10,
                        1000,
                        EnumSet.allOf(OrderType.class)
                );

        orderService = new OrderService(
                orderRepository,
                traderRepository,
                stockRepository,
                orderBooks,
                matchingEngines,
                exchangeConfig
        );

        traderRepository.save(
                new Trader("TRADER-1", "Trader One")
        );

        stockRepository.save(
                new Stock("AAPL", "Apple Inc.")
        );
    }

    @Test
    void shouldCreateValidOrder() {

        Order order =
                orderService.createOrder(
                        "ORDER-1",
                        "TRADER-1",
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        100.0,
                        100,
                        Instant.now()
                );

        assertNotNull(order);
        assertEquals("ORDER-1", order.getOrderId());
        assertEquals("TRADER-1", order.getTraderId());
        assertEquals("AAPL", order.getSymbol());
        assertEquals(100, order.getQuantity());
    }

    @Test
    void shouldRejectUnknownTrader() {

        Order order = createOrder(
                "ORDER-1",
                "UNKNOWN",
                "AAPL",
                100
        );

        assertThrows(
                TraderNotFoundException.class,
                () -> orderService.validateOrder(order)
        );
    }

    @Test
    void shouldRejectUnknownStock() {

        Order order = createOrder(
                "ORDER-1",
                "TRADER-1",
                "UNKNOWN",
                100
        );

        assertThrows(
                StockNotFoundException.class,
                () -> orderService.validateOrder(order)
        );
    }

    @Test
    void shouldRejectQuantityBelowMinimum() {

        Order order = createOrder(
                "ORDER-1",
                "TRADER-1",
                "AAPL",
                5
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.validateOrder(order)
        );
    }

    @Test
    void shouldRejectQuantityAboveMaximum() {

        Order order = createOrder(
                "ORDER-1",
                "TRADER-1",
                "AAPL",
                1001
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.validateOrder(order)
        );
    }

    @Test
    void shouldRejectInvalidLimitPrice() {

        Order order =
                new Order(
                        "ORDER-1",
                        "TRADER-1",
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        0,
                        100,
                        Instant.now()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.validateOrder(order)
        );
    }

    @Test
    void shouldAcceptValidLimitPrice() {

        Order order = createOrder(
                "ORDER-1",
                "TRADER-1",
                "AAPL",
                100
        );

        assertDoesNotThrow(
                () -> orderService.validateOrder(order)
        );
    }

    @Test
    void shouldRejectNullOrder() {

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.validateOrder(null)
        );
    }

    @Test
    void shouldCreateOrderWithNewStatus() {

        Order order =
                orderService.createOrder(
                        "ORDER-1",
                        "TRADER-1",
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        100.0,
                        100,
                        Instant.now()
                );

        assertEquals(
                com.stockexchange.enums.OrderStatus.NEW,
                order.getStatus()
        );
    }

    private Order createOrder(
            String orderId,
            String traderId,
            String symbol,
            long quantity) {

        return new Order(
                orderId,
                traderId,
                symbol,
                OrderSide.BUY,
                OrderType.LIMIT,
                100.0,
                quantity,
                Instant.now()
        );
    }
}
