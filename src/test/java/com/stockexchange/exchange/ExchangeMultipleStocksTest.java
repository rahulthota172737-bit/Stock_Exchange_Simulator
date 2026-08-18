package com.stockexchange.exchange;

import com.stockexchange.book.OrderBook;
import com.stockexchange.enums.OrderType;
import com.stockexchange.matching.MatchingEngine;
import com.stockexchange.matching.TradeExecutor;
import com.stockexchange.model.Stock;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;
import com.stockexchange.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeMultipleStocksTest {

    private Exchange exchange;

    @BeforeEach
    void setUp() {

        TraderRepository traderRepository =
                new TraderRepository();

        StockRepository stockRepository =
                new StockRepository();

        TradeExecutor tradeExecutor =
                new TradeExecutor();

        TradeService tradeService =
                new TradeService();

        TradingSession tradingSession =
                new TradingSession();

        ExchangeConfig exchangeConfig =
                new ExchangeConfig(
                        1,
                        1_000_000,
                        EnumSet.allOf(OrderType.class)
                );

        exchange = new Exchange(
                traderRepository,
                stockRepository,
                tradeExecutor,
                tradeService,
                tradingSession,
                exchangeConfig
        );
    }

    @Test
    void eachStockShouldHaveSeparateOrderBook() {

        Stock aapl =
                new Stock("AAPL", "Apple Inc.");

        Stock goog =
                new Stock("GOOG", "Alphabet Inc.");

        exchange.registerStock(aapl);
        exchange.registerStock(goog);

        OrderBook aaplBook =
                exchange.getOrderBook("AAPL");

        OrderBook googBook =
                exchange.getOrderBook("GOOG");

        assertNotNull(aaplBook);
        assertNotNull(googBook);

        assertNotSame(aaplBook, googBook);
    }

    @Test
    void eachStockShouldHaveSeparateMatchingEngine() {

        exchange.registerStock(
                new Stock("AAPL", "Apple Inc.")
        );

        exchange.registerStock(
                new Stock("GOOG", "Alphabet Inc.")
        );

        MatchingEngine aaplEngine =
                exchange.getMatchingEngine("AAPL");

        MatchingEngine googEngine =
                exchange.getMatchingEngine("GOOG");

        assertNotNull(aaplEngine);
        assertNotNull(googEngine);

        assertNotSame(aaplEngine, googEngine);
    }

    @Test
    void unknownStockShouldBeRejected() {

        assertThrows(
                com.stockexchange.exception.StockNotFoundException.class,
                () -> exchange.getOrderBook("UNKNOWN")
        );
    }

    @Test
    void unknownStockMatchingEngineShouldBeRejected() {

        assertThrows(
                com.stockexchange.exception.StockNotFoundException.class,
                () -> exchange.getMatchingEngine("UNKNOWN")
        );
    }

    @Test
    void duplicateStockShouldBeRejected() {

        exchange.registerStock(
                new Stock("AAPL", "Apple Inc.")
        );

        assertThrows(
                com.stockexchange.exception.DuplicateStockException.class,
                () -> exchange.registerStock(
                        new Stock("AAPL", "Apple Inc.")
                )
        );
    }
}