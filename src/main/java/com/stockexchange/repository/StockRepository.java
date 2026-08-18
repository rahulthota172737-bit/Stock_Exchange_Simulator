package com.stockexchange.repository;

import com.stockexchange.exception.DuplicateStockException;
import com.stockexchange.exception.StockNotFoundException;
import com.stockexchange.model.Stock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StockRepository {

    private final Map<String, Stock> stocks;

    public StockRepository() {
        this.stocks = new HashMap<>();
    }

    public void save(Stock stock) {

        if (exists(stock.getSymbol())) {
            throw new DuplicateStockException(
                    "Stock already exists: " + stock.getSymbol());
        }

        stocks.put(stock.getSymbol(), stock);
    }

    public Stock findBySymbol(String symbol) {

        Stock stock = stocks.get(symbol);

        if (stock == null) {
            throw new StockNotFoundException(
                    "Stock not found: " + symbol);
        }

        return stock;
    }

    public boolean exists(String symbol) {
        return stocks.containsKey(symbol);
    }

    public void remove(String symbol) {

        if (!exists(symbol)) {
            throw new StockNotFoundException(
                    "Stock not found: " + symbol);
        }

        stocks.remove(symbol);
    }

    public Collection<Stock> findAll() {
        return stocks.values();
    }
}