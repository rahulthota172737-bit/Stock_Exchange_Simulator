package com.stockexchange.repository;

import com.stockexchange.exception.DuplicateTraderException;
import com.stockexchange.exception.TraderNotFoundException;
import com.stockexchange.model.Trader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TraderRepository {

    private final Map<String, Trader> traders;

    public TraderRepository() {
        this.traders = new HashMap<>();
    }

    public void save(Trader trader) {

        if (exists(trader.getTraderId())) {
            throw new DuplicateTraderException(
                    "Trader already exists: " + trader.getTraderId());
        }

        traders.put(trader.getTraderId(), trader);
    }

    public Trader findById(String traderId) {

        Trader trader = traders.get(traderId);

        if (trader == null) {
            throw new TraderNotFoundException(
                    "Trader not found: " + traderId);
        }

        return trader;
    }

    public boolean exists(String traderId) {
        return traders.containsKey(traderId);
    }

    public void remove(String traderId) {

        if (!exists(traderId)) {
            throw new TraderNotFoundException(
                    "Trader not found: " + traderId);
        }

        traders.remove(traderId);
    }

    public Collection<Trader> findAll() {
        return traders.values();
    }
}