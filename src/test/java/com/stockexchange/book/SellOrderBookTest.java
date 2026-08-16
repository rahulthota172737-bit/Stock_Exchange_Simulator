package com.stockexchange.book;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SellOrderBookTest {

    private Order createOrder(String orderId, double price) {
        return new Order(
                orderId,
                "TRADER001",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                price,
                10,
                Instant.now()
        );
    }

    @Test
    void shouldBeEmptyInitially() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        assertTrue(sellOrderBook.isEmpty());
    }

    @Test
    void shouldAddOrder() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        Order order = createOrder("ORD001", 100.0);

        sellOrderBook.addOrder(order);

        assertFalse(sellOrderBook.isEmpty());
        assertEquals(100.0, sellOrderBook.getBestPrice());
    }

    @Test
    void shouldGiveLowestPriceAsBestPrice() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        Order order110 = createOrder("ORD001", 110.0);
        Order order105 = createOrder("ORD002", 105.0);
        Order order102 = createOrder("ORD003", 102.0);
        Order order115 = createOrder("ORD004", 115.0);

        sellOrderBook.addOrder(order110);
        sellOrderBook.addOrder(order105);
        sellOrderBook.addOrder(order102);
        sellOrderBook.addOrder(order115);

        assertEquals(102.0, sellOrderBook.getBestPrice());
    }

    @Test
    void shouldReturnOrderAtLowestPrice() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        Order order110 = createOrder("ORD001", 110.0);
        Order order105 = createOrder("ORD002", 105.0);
        Order order102 = createOrder("ORD003", 102.0);

        sellOrderBook.addOrder(order110);
        sellOrderBook.addOrder(order105);
        sellOrderBook.addOrder(order102);

        assertEquals(order102, sellOrderBook.getBestOrder());
    }

    @Test
    void shouldMaintainFifoAtSamePrice() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        Order orderA = createOrder("ORD001", 100.0);
        Order orderB = createOrder("ORD002", 100.0);
        Order orderC = createOrder("ORD003", 100.0);

        sellOrderBook.addOrder(orderA);
        sellOrderBook.addOrder(orderB);
        sellOrderBook.addOrder(orderC);

        assertEquals(orderA, sellOrderBook.getBestOrder());
    }

    @Test
    void shouldRemoveOrder() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        Order order100 = createOrder("ORD001", 100.0);
        Order order105 = createOrder("ORD002", 105.0);
        Order order110 = createOrder("ORD003", 110.0);

        sellOrderBook.addOrder(order100);
        sellOrderBook.addOrder(order105);
        sellOrderBook.addOrder(order110);

        sellOrderBook.removeOrder(order100);

        assertEquals(105.0, sellOrderBook.getBestPrice());
        assertEquals(order105, sellOrderBook.getBestOrder());
    }

    @Test
    void shouldRemoveEmptyPriceLevel() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        Order order100 = createOrder("ORD001", 100.0);

        sellOrderBook.addOrder(order100);

        assertEquals(100.0, sellOrderBook.getBestPrice());

        sellOrderBook.removeOrder(order100);

        assertTrue(sellOrderBook.isEmpty());
        assertNull(sellOrderBook.getBestPrice());
        assertNull(sellOrderBook.getBestOrder());
    }

    @Test
    void bestOrderShouldBeNullWhenBookIsEmpty() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        assertNull(sellOrderBook.getBestOrder());
    }

    @Test
    void bestPriceShouldBeNullWhenBookIsEmpty() {
        SellOrderBook sellOrderBook = new SellOrderBook();

        assertNull(sellOrderBook.getBestPrice());
    }
}
