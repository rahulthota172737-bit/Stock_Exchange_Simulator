package com.stockexchange.repository;

import com.stockexchange.model.Order;

import java.util.HashMap;
import java.util.Map;

public class OrderRepository {
    private final Map<String, Order> orders;

    public OrderRepository() {
        this.orders = new HashMap<>();
    }

    public void save(Order order) {
        orders.put(order.getOrderId(), order);
    }

    public Order findById(String orderId) {
        return orders.get(orderId);
    }

    public Order remove(String orderId) {
        return orders.remove(orderId);
    }

    public boolean contains(String orderId) {
        return orders.containsKey(orderId);
    }
}

