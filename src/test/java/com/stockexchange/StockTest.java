package com.stockexchange;

import com.stockexchange.model.Stock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StockTest {
    @Test
    void shouldCreateStock() {
        Stock stock = new Stock("AAPL", "Apple Inc.");

        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getCompanyName());
    }

    @Test
    void stocksWithSameSymbolShouldBeEqual() {
        Stock stock1 = new Stock("AAPL", "Apple Inc.");
        Stock stock2 = new Stock("AAPL", "Apple Inc.");

        assertEquals(stock1, stock2);
    }

    @Test
    void equalStocksShouldHaveSameHashCode() {
        Stock stock1 = new Stock("AAPL", "Apple Inc.");
        Stock stock2 = new Stock("AAPL", "Apple Inc.");

        assertEquals(stock1.hashCode(), stock2.hashCode());
    }
    @Test
    void stocksWithDifferentSymbolsShouldNotBeEqual() {
        Stock apple = new Stock("AAPL", "Apple Inc.");
        Stock google = new Stock("GOOG", "Google");

        assertNotEquals(apple, google);
    }

    @Test
    void toStringShouldContainStockInformation() {
        Stock stock = new Stock("AAPL", "Apple Inc.");

        String result = stock.toString();

        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("Apple Inc."));
    }
}
