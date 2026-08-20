package com.stockexchange.account;

import com.stockexchange.portfolio.Portfolio;

public class Account {

    private final String accountId;
    private final String traderId;
    private final Balance balance;
    private final Portfolio portfolio;

    public Account(String accountId, String traderId) {

        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException(
                    "Account ID cannot be empty"
            );
        }

        if (traderId == null || traderId.isBlank()) {
            throw new IllegalArgumentException(
                    "Trader ID cannot be empty"
            );
        }

        this.accountId = accountId;
        this.traderId = traderId;
        this.balance = new Balance(0);
        this.portfolio = new Portfolio();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getTraderId() {
        return traderId;
    }

    public Balance getBalance() {
        return balance;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}
