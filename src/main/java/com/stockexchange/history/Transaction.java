package com.stockexchange.history;


import com.stockexchange.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

public class Transaction {

    private final String transactionId;
    private final String traderId;
    private final String symbol;
    private final TransactionType type;
    private final long quantity;
    private final double price;
    private final double totalValue;
    private final LocalDateTime timestamp;

    public Transaction(
            String transactionId,
            String traderId,
            String symbol,
            TransactionType type,
            long quantity,
            double price,
            double totalValue,
            LocalDateTime timestamp) {

        this.transactionId = transactionId;
        this.traderId = traderId;
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.totalValue = totalValue;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public TransactionType getType() {
        return type;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
