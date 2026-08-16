package com.stockexchange.repository;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

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
    void shouldBeEmptyInitially() {
        OrderRepository repository = new OrderRepository();

        assertFalse(repository.contains("ORD001"));
        assertNull(repository.findById("ORD001"));
    }

    @Test
    void shouldSaveOrder() {
        OrderRepository repository = new OrderRepository();

        Order order = createOrder("ORD001");

        repository.save(order);

        assertTrue(repository.contains("ORD001"));
        assertEquals(order, repository.findById("ORD001"));
    }

    @Test
    void shouldFindOrderById() {
        OrderRepository repository = new OrderRepository();

        Order order = createOrder("ORD001");

        repository.save(order);

        Order foundOrder = repository.findById("ORD001");

        assertNotNull(foundOrder);
        assertEquals("ORD001", foundOrder.getOrderId());
        assertEquals(order, foundOrder);
    }

    @Test
    void shouldReturnNullForNonexistentOrder() {
        OrderRepository repository = new OrderRepository();

        assertNull(repository.findById("DOES_NOT_EXIST"));
    }

    @Test
    void shouldCheckIfOrderExists() {
        OrderRepository repository = new OrderRepository();

        Order order = createOrder("ORD001");

        assertFalse(repository.contains("ORD001"));

        repository.save(order);

        assertTrue(repository.contains("ORD001"));
    }

    @Test
    void shouldRemoveOrder() {
        OrderRepository repository = new OrderRepository();

        Order order = createOrder("ORD001");

        repository.save(order);

        assertTrue(repository.contains("ORD001"));

        Order removedOrder = repository.remove("ORD001");

        assertEquals(order, removedOrder);
        assertFalse(repository.contains("ORD001"));
        assertNull(repository.findById("ORD001"));
    }

    @Test
    void shouldReturnNullWhenRemovingNonexistentOrder() {
        OrderRepository repository = new OrderRepository();

        assertNull(repository.remove("DOES_NOT_EXIST"));
    }

    @Test
    void shouldStoreMultipleOrders() {
        OrderRepository repository = new OrderRepository();

        Order order1 = createOrder("ORD001");
        Order order2 = createOrder("ORD002");
        Order order3 = createOrder("ORD003");

        repository.save(order1);
        repository.save(order2);
        repository.save(order3);

        assertEquals(order1, repository.findById("ORD001"));
        assertEquals(order2, repository.findById("ORD002"));
        assertEquals(order3, repository.findById("ORD003"));
    }
}
