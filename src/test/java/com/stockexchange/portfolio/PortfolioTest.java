package com.stockexchange.portfolio;

import com.stockexchange.account.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    @Test
    void shouldAddPosition() {
        Portfolio portfolio = new Portfolio();

        Position position = new Position("AAPL");
        position.buy(100, 150);

        portfolio.addPosition(position);

        assertNotNull(portfolio.getPosition("AAPL"));
        assertEquals(100, portfolio.getTotalQuantity("AAPL"));
    }

    @Test
    void shouldReturnZeroForUnknownStock() {
        Portfolio portfolio = new Portfolio();

        assertEquals(0, portfolio.getTotalQuantity("AAPL"));
    }

    @Test
    void shouldStoreMultipleStocks() {
        Portfolio portfolio = new Portfolio();

        Position aapl = new Position("AAPL");
        aapl.buy(100, 150);

        Position goog = new Position("GOOG");
        goog.buy(50, 200);

        Position tsla = new Position("TSLA");
        tsla.buy(25, 300);

        portfolio.addPosition(aapl);
        portfolio.addPosition(goog);
        portfolio.addPosition(tsla);

        assertEquals(100, portfolio.getTotalQuantity("AAPL"));
        assertEquals(50, portfolio.getTotalQuantity("GOOG"));
        assertEquals(25, portfolio.getTotalQuantity("TSLA"));
    }

    @Test
    void shouldRemovePosition() {
        Portfolio portfolio = new Portfolio();

        Position position = new Position("AAPL");
        position.buy(100, 150);

        portfolio.addPosition(position);

        portfolio.removePosition("AAPL");

        assertNull(portfolio.getPosition("AAPL"));
        assertEquals(0, portfolio.getTotalQuantity("AAPL"));
    }

    @Test
    void shouldReturnAllHoldings() {
        Portfolio portfolio = new Portfolio();

        Position aapl = new Position("AAPL");
        aapl.buy(100, 150);

        Position goog = new Position("GOOG");
        goog.buy(50, 200);

        portfolio.addPosition(aapl);
        portfolio.addPosition(goog);

        assertEquals(2, portfolio.getHoldings().size());
    }
}
