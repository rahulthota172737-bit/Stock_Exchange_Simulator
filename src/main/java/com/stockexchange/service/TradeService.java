package com.stockexchange.service;

import com.stockexchange.model.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TradeService {

    private final List<Trade> trades;

    public TradeService() {
        this.trades = new ArrayList<>();
    }

    public void recordTrade(Trade trade) {

        if (trade == null) {
            throw new IllegalArgumentException("Trade cannot be null");
        }

        trades.add(trade);
    }

    public List<Trade> getTradeHistory() {
        return List.copyOf(trades);
    }

    public List<Trade> getTradesForSymbol(String symbol) {

        return trades.stream()
                .filter(trade -> trade.getSymbol().equals(symbol))
                .collect(Collectors.toList());
    }

    public List<Trade> getTradesForOrder(String orderId) {

        return trades.stream()
                .filter(trade ->
                        trade.getBuyOrderId().equals(orderId)
                                || trade.getSellOrderId().equals(orderId))
                .collect(Collectors.toList());
    }
}
