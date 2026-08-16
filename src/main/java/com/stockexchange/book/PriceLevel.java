package com.stockexchange.book;
import com.stockexchange.model.Order;
import java.util.ArrayDeque;
import java.util.Deque;
public class PriceLevel {
    private final double price;
    private final Deque<Order> orders;

    public PriceLevel(double price) {
        this.price = price;
        this.orders = new ArrayDeque<>();
    }

    public void addOrder(Order order) {
        orders.addLast(order);
    }

    public Order removeFirstOrder() {
        return orders.pollFirst();
    }

    public Order peekFirstOrder() {
        return orders.peekFirst();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public Deque<Order> getOrders() {
        return orders;
    }

    public double getPrice() {
        return price;
    }
}
