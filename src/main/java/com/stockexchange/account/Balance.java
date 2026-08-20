package com.stockexchange.account;

import com.stockexchange.exception.InsufficientBalanceException;

public class Balance {

    private double cash;

    public Balance(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Initial cash cannot be negative");
        }

        this.cash = initialCash;
    }

    public void deposit(double amount) {
        validateAmount(amount);
        cash += amount;
    }

    public void withdraw(double amount) {
        validateAmount(amount);

        if (amount > cash) {
            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        cash -= amount;
    }

    public void credit(double amount) {
        validateAmount(amount);
        cash += amount;
    }

    public void debit(double amount) {
        validateAmount(amount);

        if (amount > cash) {
            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        cash -= amount;
    }

    public double getAvailableCash() {
        return cash;
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }
    }
}
