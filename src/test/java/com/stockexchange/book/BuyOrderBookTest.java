package com.stockexchange.book;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BuyOrderBookTest {

    private Order createOrder(String orderId, double price) {
        return new Order(
                orderId,
                "TRADER001",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                price,
                10,
                Instant.now()
        );
    }

    @Test
    void shouldBeEmptyInitially() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        assertTrue(buyOrderBook.isEmpty());
    }

    @Test
    void shouldAddOrder() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        Order order = createOrder("ORD001", 100.0);

        buyOrderBook.addOrder(order);

        assertFalse(buyOrderBook.isEmpty());
        assertEquals(100.0, buyOrderBook.getBestPrice());
    }

    @Test
    void shouldGiveHighestPriceAsBestPrice() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        Order order100 = createOrder("ORD001", 100.0);
        Order order105 = createOrder("ORD002", 105.0);
        Order order102 = createOrder("ORD003", 102.0);
        Order order110 = createOrder("ORD004", 110.0);

        buyOrderBook.addOrder(order100);
        buyOrderBook.addOrder(order105);
        buyOrderBook.addOrder(order102);
        buyOrderBook.addOrder(order110);

        assertEquals(110.0, buyOrderBook.getBestPrice());
    }

    @Test
    void shouldReturnOrderAtHighestPrice() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        Order order100 = createOrder("ORD001", 100.0);
        Order order105 = createOrder("ORD002", 105.0);
        Order order110 = createOrder("ORD003", 110.0);

        buyOrderBook.addOrder(order100);
        buyOrderBook.addOrder(order105);
        buyOrderBook.addOrder(order110);

        assertEquals(order110, buyOrderBook.getBestOrder());
    }

    @Test
    void shouldMaintainFifoAtSamePrice() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        Order orderA = createOrder("ORD001", 100.0);
        Order orderB = createOrder("ORD002", 100.0);
        Order orderC = createOrder("ORD003", 100.0);

        buyOrderBook.addOrder(orderA);
        buyOrderBook.addOrder(orderB);
        buyOrderBook.addOrder(orderC);

        assertEquals(orderA, buyOrderBook.getBestOrder());
    }

    @Test
    void shouldRemoveOrder() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        Order order100 = createOrder("ORD001", 100.0);
        Order order105 = createOrder("ORD002", 105.0);
        Order order110 = createOrder("ORD003", 110.0);

        buyOrderBook.addOrder(order100);
        buyOrderBook.addOrder(order105);
        buyOrderBook.addOrder(order110);

        buyOrderBook.removeOrder(order110);

        assertEquals(105.0, buyOrderBook.getBestPrice());
        assertEquals(order105, buyOrderBook.getBestOrder());
    }

    @Test
    void shouldRemoveEmptyPriceLevel() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        Order order100 = createOrder("ORD001", 100.0);

        buyOrderBook.addOrder(order100);

        assertEquals(100.0, buyOrderBook.getBestPrice());

        buyOrderBook.removeOrder(order100);

        assertTrue(buyOrderBook.isEmpty());
        assertNull(buyOrderBook.getBestPrice());
        assertNull(buyOrderBook.getBestOrder());
    }

    @Test
    void bestOrderShouldBeNullWhenBookIsEmpty() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        assertNull(buyOrderBook.getBestOrder());
    }

    @Test
    void bestPriceShouldBeNullWhenBookIsEmpty() {
        BuyOrderBook buyOrderBook = new BuyOrderBook();

        assertNull(buyOrderBook.getBestPrice());
    }
}