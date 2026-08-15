package com.stockexchange.model;

public class Trader {
    private final String traderId;
    private final String name;

    public Trader(String traderId, String name) {
        this.traderId = traderId;
        this.name = name;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getName() {
        return name;
    }
}
