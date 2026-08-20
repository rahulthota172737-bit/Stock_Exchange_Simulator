package com.stockexchange.account;

import com.stockexchange.exception.InsufficientHoldingsException;

public class Position {

    private final String symbol;
    private long quantity;
    private double averageEntryPrice;

    public Position(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be empty");
        }

        this.symbol = symbol;
        this.quantity = 0;
        this.averageEntryPrice = 0;
    }

    public void buy(long quantity, double price) {
        validateQuantity(quantity);
        validatePrice(price);

        long oldQuantity = this.quantity;
        long newQuantity = oldQuantity + quantity;

        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        double totalCost =
                (oldQuantity * this.averageEntryPrice)
                        + (quantity * price);

        this.quantity = newQuantity;
        this.averageEntryPrice = totalCost / newQuantity;
    }

    public void sell(long quantity) {
        validateQuantity(quantity);

        if (quantity > this.quantity) {
            throw new InsufficientHoldingsException(
                    "Insufficient holdings for " + symbol
            );
        }

        this.quantity -= quantity;

        if (this.quantity == 0) {
            this.averageEntryPrice = 0;
        }
    }

    public void increase(long quantity) {
        validateQuantity(quantity);
        this.quantity += quantity;
    }

    public void decrease(long quantity) {
        validateQuantity(quantity);

        if (quantity > this.quantity) {
            throw new InsufficientHoldingsException(
                    "Insufficient holdings for " + symbol
            );
        }

        this.quantity -= quantity;

        if (this.quantity == 0) {
            this.averageEntryPrice = 0;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getAverageEntryPrice() {
        return averageEntryPrice;
    }

    private void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero"
            );
        }
    }
}