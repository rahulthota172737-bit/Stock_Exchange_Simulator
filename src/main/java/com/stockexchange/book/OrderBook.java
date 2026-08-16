package com.stockexchange.book;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.model.Order;

public class OrderBook {
    private final String symbol;
    private final BuyOrderBook buyOrders;
    private final SellOrderBook sellOrders;

    public OrderBook(String symbol) {
        this.symbol = symbol;
        this.buyOrders = new BuyOrderBook();
        this.sellOrders = new SellOrderBook();
    }

    public void addOrder(Order order) {

        if (order.getSide() == OrderSide.BUY) {
            buyOrders.addOrder(order);
        } else {
            sellOrders.addOrder(order);
        }
    }

    public void removeOrder(Order order) {

        if (order.getSide() == OrderSide.BUY) {
            buyOrders.removeOrder(order);
        } else {
            sellOrders.removeOrder(order);
        }
    }

    public Double getBestBid() {
        return buyOrders.getBestPrice();
    }
    public Double getBestAsk() {
        return sellOrders.getBestPrice();
    }

    public Double getSpread() {

        Double bid = getBestBid();
        Double ask = getBestAsk();

        if (bid == null || ask == null) {
            return null;
        }

        return ask - bid;
    }

    public boolean isEmpty() {
        return buyOrders.isEmpty() && sellOrders.isEmpty();
    }

    public String getSymbol() {
        return symbol;
    }
}
