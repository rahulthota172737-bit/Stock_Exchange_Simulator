package com.stockexchange.account;

import com.stockexchange.exception.InsufficientHoldingsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void shouldBuyShares() {
        Position position = new Position("AAPL");

        position.buy(100, 150);

        assertEquals(100, position.getQuantity());
        assertEquals(150, position.getAverageEntryPrice());
    }

    @Test
    void shouldCalculateAverageEntryPrice() {
        Position position = new Position("AAPL");

        position.buy(100, 150);
        position.buy(100, 200);

        assertEquals(200, position.getQuantity());
        assertEquals(175, position.getAverageEntryPrice());
    }

    @Test
    void shouldSellShares() {
        Position position = new Position("AAPL");

        position.buy(100, 150);
        position.sell(40);

        assertEquals(60, position.getQuantity());
        assertEquals(150, position.getAverageEntryPrice());
    }

    @Test
    void shouldResetAveragePriceWhenAllSharesAreSold() {
        Position position = new Position("AAPL");

        position.buy(100, 150);
        position.sell(100);

        assertEquals(0, position.getQuantity());
        assertEquals(0, position.getAverageEntryPrice());
    }

    @Test
    void shouldRejectInsufficientHoldings() {
        Position position = new Position("AAPL");

        position.buy(100, 150);

        assertThrows(
                InsufficientHoldingsException.class,
                () -> position.sell(150)
        );
    }

    @Test
    void shouldRejectZeroQuantity() {
        Position position = new Position("AAPL");

        assertThrows(
                IllegalArgumentException.class,
                () -> position.buy(0, 150)
        );
    }

    @Test
    void shouldRejectNegativePrice() {
        Position position = new Position("AAPL");

        assertThrows(
                IllegalArgumentException.class,
                () -> position.buy(100, -150)
        );
    }
}