package com.stockexchange;

import com.stockexchange.model.Trade;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TradeTest {
    @Test
    void tradeShouldBeCreatedWithCorrectValues() {

        Instant timestamp = Instant.now();

        Trade trade = new Trade(
                "TRADE-1",
                "BUY-1",
                "SELL-1",
                "AAPL",
                150.50,
                100,
                timestamp
        );

        assertEquals("TRADE-1", trade.getTradeId());
        assertEquals("BUY-1", trade.getBuyOrderId());
        assertEquals("SELL-1", trade.getSellOrderId());
        assertEquals("AAPL", trade.getSymbol());
        assertEquals(150.50, trade.getPrice());
        assertEquals(100, trade.getQuantity());
        assertEquals(timestamp, trade.getTimestamp());
    }
}
