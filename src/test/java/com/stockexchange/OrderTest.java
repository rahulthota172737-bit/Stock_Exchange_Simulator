package com.stockexchange;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderStatus;
import com.stockexchange.enums.OrderType;
import com.stockexchange.exception.InvalidPriceException;
import com.stockexchange.exception.InvalidQuantityException;
import com.stockexchange.model.Order;
import com.stockexchange.service.OrderValidator;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private Order createOrder(long quantity, double price) {
        return new Order(
                "ORD-1",
                "TRADER-1",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                price,
                quantity,
                Instant.now()
        );
    }

    @Test
    void orderShouldBeCreatedWithCorrectInitialState() {

        Order order = createOrder(100, 150.0);

        assertEquals(100, order.getQuantity());
        assertEquals(100, order.getRemainingQuantity());
        assertEquals(OrderStatus.NEW, order.getStatus());
    }

    @Test
    void fillShouldReduceRemainingQuantity() {

        Order order = createOrder(100, 150.0);

        order.fill(40);

        assertEquals(60, order.getRemainingQuantity());
        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                order.getStatus()
        );
    }

    @Test
    void completelyFilledOrderShouldHaveFilledStatus() {

        Order order = createOrder(100, 150.0);

        order.fill(100);

        assertEquals(0, order.getRemainingQuantity());
        assertEquals(OrderStatus.FILLED, order.getStatus());
        assertTrue(order.isFilled());
    }
    @Test
    void cancelShouldChangeStatusToCancelled() {

        Order order = createOrder(100, 150.0);

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void invalidQuantityShouldBeRejected() {

        Order order = createOrder(0, 150.0);

        OrderValidator validator = new OrderValidator();

        assertThrows(
                InvalidQuantityException.class,
                () -> validator.validate(order)
        );
    }
    @Test
    void invalidLimitPriceShouldBeRejected() {

        Order order = createOrder(100, 0);

        OrderValidator validator = new OrderValidator();

        assertThrows(
                InvalidPriceException.class,
                () -> validator.validate(order)
        );
    }
    @Test
    void marketOrderShouldNotRequirePositivePrice() {

        Order order = new Order(
                "ORD-2",
                "TRADER-1",
                "AAPL",
                OrderSide.BUY,
                OrderType.MARKET,
                0,
                100,
                Instant.now()
        );

        OrderValidator validator = new OrderValidator();

        assertDoesNotThrow(() -> validator.validate(order));
    }
}
