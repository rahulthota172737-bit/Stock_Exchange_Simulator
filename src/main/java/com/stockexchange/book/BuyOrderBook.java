package com.stockexchange.book;

import com.stockexchange.model.Order;

import java.util.Comparator;
import java.util.TreeMap;

public class BuyOrderBook {

    private final TreeMap<Double, PriceLevel> priceLevels;

    public BuyOrderBook() {
        this.priceLevels = new TreeMap<>(Comparator.reverseOrder());
    }

    public void addOrder(Order order) {

        double price = order.getPrice();

        PriceLevel priceLevel = priceLevels.computeIfAbsent(
                price,
                PriceLevel::new
        );

        priceLevel.addOrder(order);
    }

    public void removeOrder(Order order) {

        double price = order.getPrice();

        PriceLevel priceLevel = priceLevels.get(price);

        if (priceLevel == null) {
            return;
        }

        priceLevel.removeOrder(order);

        if (priceLevel.isEmpty()) {
            priceLevels.remove(price);
        }
    }

    public Order getBestOrder() {

        if (priceLevels.isEmpty()) {
            return null;
        }

        return priceLevels.firstEntry()
                .getValue()
                .peekFirstOrder();
    }

    public Double getBestPrice() {

        if (priceLevels.isEmpty()) {
            return null;
        }

        return priceLevels.firstKey();
    }

    public boolean isEmpty() {
        return priceLevels.isEmpty();
    }
}
