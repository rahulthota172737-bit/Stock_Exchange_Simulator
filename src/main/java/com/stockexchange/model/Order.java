package com.stockexchange.model;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderStatus;
import com.stockexchange.enums.OrderType;

import java.time.Instant;

public class Order {

    private final String orderId;
    private final String traderId;
    private final String symbol;
    private final OrderSide side;
    private final OrderType type;
    private final double price;
    private final long quantity;
    private long remainingQuantity;
    private final Instant timestamp;
    private OrderStatus status;

    public Order(
            String orderId,
            String traderId,
            String symbol,
            OrderSide side,
            OrderType type,
            double price,
            long quantity,
            Instant timestamp
    ) {
        this.orderId = orderId;
        this.traderId = traderId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.timestamp = timestamp;
        this.status = OrderStatus.NEW;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void open() {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException(
                    "Only a NEW order can be opened"
            );
        }

        status = OrderStatus.OPEN;
    }

    public void fill(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Fill quantity must be greater than zero"
            );
        }

        if (quantity > remainingQuantity) {
            throw new IllegalArgumentException(
                    "Fill quantity cannot exceed remaining quantity"
            );
        }

        remainingQuantity -= quantity;

        if (remainingQuantity == 0) {
            status = OrderStatus.FILLED;
        } else {
            status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void cancel() {
        if (isFilled()) {
            throw new IllegalStateException(
                    "Filled order cannot be cancelled"
            );
        }

        status = OrderStatus.CANCELLED;
    }

    public boolean isFilled() {
        return remainingQuantity == 0;
    }
}
