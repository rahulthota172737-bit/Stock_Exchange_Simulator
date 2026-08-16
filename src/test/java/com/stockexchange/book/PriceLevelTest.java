package com.stockexchange.book;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PriceLevelTest {

    private Order createOrder(String orderId) {
        return new Order(
                orderId,
                "TRADER001",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                100.0,
                10,
                Instant.now()
        );
    }

    @Test
    void shouldStorePrice() {
        PriceLevel priceLevel = new PriceLevel(100.0);

        assertEquals(100.0, priceLevel.getPrice());
    }

    @Test
    void shouldBeEmptyInitially() {
        PriceLevel priceLevel = new PriceLevel(100.0);

        assertTrue(priceLevel.isEmpty());
    }

    @Test
    void shouldAddOrder() {
        PriceLevel priceLevel = new PriceLevel(100.0);

        Order order = createOrder("ORD001");

        priceLevel.addOrder(order);

        assertFalse(priceLevel.isEmpty());
        assertEquals(order, priceLevel.peekFirstOrder());
    }

    @Test
    void shouldMaintainFifoOrder() {
        PriceLevel priceLevel = new PriceLevel(100.0);

        Order orderA = createOrder("ORD001");
        Order orderB = createOrder("ORD002");
        Order orderC = createOrder("ORD003");

        priceLevel.addOrder(orderA);
        priceLevel.addOrder(orderB);
        priceLevel.addOrder(orderC);

        assertEquals(orderA, priceLevel.removeFirstOrder());
        assertEquals(orderB, priceLevel.removeFirstOrder());
        assertEquals(orderC, priceLevel.removeFirstOrder());
    }

    @Test
    void peekShouldNotRemoveOrder() {
        PriceLevel priceLevel = new PriceLevel(100.0);

        Order orderA = createOrder("ORD001");
        Order orderB = createOrder("ORD002");

        priceLevel.addOrder(orderA);
        priceLevel.addOrder(orderB);

        assertEquals(orderA, priceLevel.peekFirstOrder());

        // Order A should still be present
        assertEquals(orderA, priceLevel.removeFirstOrder());

        // Order B should come next
        assertEquals(orderB, priceLevel.removeFirstOrder());
    }

    @Test
    void removingFromEmptyPriceLevelShouldReturnNull() {
        PriceLevel priceLevel = new PriceLevel(100.0);

        assertNull(priceLevel.removeFirstOrder());
    }

    @Test
    void shouldBecomeEmptyAfterRemovingAllOrders() {
        PriceLevel priceLevel = new PriceLevel(100.0);

        Order order = createOrder("ORD001");

        priceLevel.addOrder(order);

        assertFalse(priceLevel.isEmpty());

        priceLevel.removeFirstOrder();

        assertTrue(priceLevel.isEmpty());
    }
}
