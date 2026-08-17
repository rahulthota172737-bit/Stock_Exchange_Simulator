package com.stockexchange.matching;

import com.stockexchange.model.Order;
import com.stockexchange.model.Trade;

import java.util.List;

public class MatchResult {
    private final Order incomingOrder;
    private final List<Trade> trades;
    private final long remainingQuantity;
    private final boolean matched;

    public MatchResult(Order incomingOrder,
                       List<Trade> trades,
                       long remainingQuantity,
                       boolean matched) {

        this.incomingOrder = incomingOrder;
        this.trades = trades;
        this.remainingQuantity = remainingQuantity;
        this.matched = matched;
    }

    public Order getIncomingOrder() {
        return incomingOrder;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    public boolean isMatched() {
        return matched;
    }

    public boolean hasRemainingQuantity() {
        return remainingQuantity > 0;
    }
}
