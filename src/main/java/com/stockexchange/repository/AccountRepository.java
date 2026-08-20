package com.stockexchange.repository;

import com.stockexchange.account.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountRepository {

    private final Map<String, Account> accounts;

    public AccountRepository() {
        this.accounts = new HashMap<>();
    }

    public void save(Account account) {
        accounts.put(
                account.getTraderId(),
                account
        );
    }

    public Account findByTraderId(String traderId) {
        return accounts.get(traderId);
    }

    public boolean contains(String traderId) {
        return accounts.containsKey(traderId);
    }

    public Account remove(String traderId) {
        return accounts.remove(traderId);
    }
}
