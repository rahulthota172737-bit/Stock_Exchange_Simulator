package com.stockexchange.service;

import com.stockexchange.model.Trade;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TradeServiceTest {

    @Test
    void shouldRecordTrade() {

        TradeService tradeService = new TradeService();

        Trade trade = new Trade(
                "T1",
                "BUY1",
                "SELL1",
                "AAPL",
                100.0,
                50,
                Instant.now()
        );

        tradeService.recordTrade(trade);

        List<Trade> history = tradeService.getTradeHistory();

        assertEquals(1, history.size());
        assertEquals("T1", history.get(0).getTradeId());
    }

    @Test
    void shouldFindTradesForSymbol() {

        TradeService tradeService = new TradeService();

        Trade trade1 = new Trade(
                "T1",
                "BUY1",
                "SELL1",
                "AAPL",
                100.0,
                50,
                Instant.now()
        );

        Trade trade2 = new Trade(
                "T2",
                "BUY2",
                "SELL2",
                "TSLA",
                200.0,
                20,
                Instant.now()
        );

        tradeService.recordTrade(trade1);
        tradeService.recordTrade(trade2);

        List<Trade> result =
                tradeService.getTradesForSymbol("AAPL");

        assertEquals(1, result.size());
        assertEquals("T1", result.get(0).getTradeId());
    }

    @Test
    void shouldFindTradesForOrder() {

        TradeService tradeService = new TradeService();

        Trade trade1 = new Trade(
                "T1",
                "BUY1",
                "SELL1",
                "AAPL",
                100.0,
                50,
                Instant.now()
        );

        Trade trade2 = new Trade(
                "T2",
                "BUY2",
                "SELL2",
                "AAPL",
                101.0,
                30,
                Instant.now()
        );

        tradeService.recordTrade(trade1);
        tradeService.recordTrade(trade2);

        List<Trade> result =
                tradeService.getTradesForOrder("BUY1");

        assertEquals(1, result.size());
        assertEquals("T1", result.get(0).getTradeId());
    }

    @Test
    void shouldRejectNullTrade() {

        TradeService tradeService = new TradeService();

        assertThrows(
                IllegalArgumentException.class,
                () -> tradeService.recordTrade(null)
        );
    }
}
