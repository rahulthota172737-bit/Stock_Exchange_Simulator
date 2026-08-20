package com.stockexchange.history;

import com.stockexchange.enums.TransactionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionHistory {

    private final List<Transaction> transactions;

    public TransactionHistory() {
        this.transactions = new ArrayList<>();
    }

    public void record(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null"
            );
        }

        transactions.add(transaction);
    }

    public List<Transaction> getAll() {
        return Collections.unmodifiableList(transactions);
    }

    public List<Transaction> getByTrader(String traderId) {
        return transactions.stream()
                .filter(transaction ->
                        transaction.getTraderId().equals(traderId))
                .collect(Collectors.toList());
    }

    public List<Transaction> getBySymbol(String symbol) {
        return transactions.stream()
                .filter(transaction ->
                        symbol.equals(transaction.getSymbol()))
                .collect(Collectors.toList());
    }

    public List<Transaction> getByType(
            TransactionType type) {

        return transactions.stream()
                .filter(transaction ->
                        transaction.getType() == type)
                .collect(Collectors.toList());
    }

    public void rollbackTo(int transactionCount) {
        if (transactionCount < 0
                || transactionCount > transactions.size()) {

            throw new IllegalArgumentException(
                    "Invalid rollback position"
            );
        }

        while (transactions.size() > transactionCount) {
            transactions.remove(
                    transactions.size() - 1
            );
        }
    }
}