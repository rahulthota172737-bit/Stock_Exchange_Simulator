package com.stockexchange.exchange;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.exception.StockNotFoundException;
import com.stockexchange.exception.TradingHaltedException;
import com.stockexchange.matching.TradeExecutor;
import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;
import com.stockexchange.service.TradeService;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeTest {

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
                        1000,
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
    void shouldRegisterTrader() {

        Trader trader =
                new Trader(
                        "TRADER-1",
                        "Trader One"
                );

        exchange.registerTrader(trader);

        assertEquals(
                "TRADER-1",
                exchange.getTrader("TRADER-1")
                        .getTraderId()
        );
    }

    @Test
    void shouldRejectDuplicateTrader() {

        Trader trader =
                new Trader(
                        "TRADER-1",
                        "Trader One"
                );

        exchange.registerTrader(trader);

        assertThrows(
                com.stockexchange.exception.DuplicateTraderException.class,
                () -> exchange.registerTrader(
                        new Trader(
                                "TRADER-1",
                                "Another Trader"
                        )
                )
        );
    }

    @Test
    void shouldRejectUnknownTrader() {

        assertThrows(
                com.stockexchange.exception.TraderNotFoundException.class,
                () -> exchange.getTrader("UNKNOWN")
        );
    }

    @Test
    void shouldRegisterStock() {

        Stock stock =
                new Stock(
                        "AAPL",
                        "Apple Inc."
                );

        exchange.registerStock(stock);

        assertEquals(
                "AAPL",
                exchange.getStock("AAPL")
                        .getSymbol()
        );
    }

    @Test
    void shouldRejectDuplicateStock() {

        exchange.registerStock(
                new Stock(
                        "AAPL",
                        "Apple Inc."
                )
        );

        assertThrows(
                com.stockexchange.exception.DuplicateStockException.class,
                () -> exchange.registerStock(
                        new Stock(
                                "AAPL",
                                "Apple Inc."
                        )
                )
        );
    }

    @Test
    void shouldRejectUnknownStock() {

        assertThrows(
                StockNotFoundException.class,
                () -> exchange.getStock("UNKNOWN")
        );
    }

    @Test
    void shouldRejectOrderWhenMarketIsClosed() {

        exchange.registerTrader(
                new Trader("TRADER-1", "Trader One")
        );

        exchange.registerStock(
                new Stock("AAPL", "Apple Inc.")
        );

        Order order = createOrder(
                "ORDER-1",
                "TRADER-1",
                "AAPL",
                OrderSide.BUY,
                100.0
        );

        assertThrows(
                TradingHaltedException.class,
                () -> exchange.submitOrder(order)
        );
    }

    @Test
    void shouldOpenTrading() {

        exchange.openTrading();

        assertTrue(
                exchange.isTradingOpen()
        );
    }

    @Test
    void shouldCloseTrading() {

        exchange.openTrading();

        exchange.closeTrading();

        assertFalse(
                exchange.isTradingOpen()
        );
    }

    @Test
    void shouldReturnOrderBook() {

        exchange.registerStock(
                new Stock("AAPL", "Apple Inc.")
        );

        assertNotNull(
                exchange.getOrderBook("AAPL")
        );
    }

    @Test
    void shouldReturnNullBestBidWhenBookIsEmpty() {

        exchange.registerStock(
                new Stock("AAPL", "Apple Inc.")
        );

        assertNull(
                exchange.getBestBid("AAPL")
        );
    }

    @Test
    void shouldReturnNullBestAskWhenBookIsEmpty() {

        exchange.registerStock(
                new Stock("AAPL", "Apple Inc.")
        );

        assertNull(
                exchange.getBestAsk("AAPL")
        );
    }

    @Test
    void shouldReturnEmptyTradeHistoryInitially() {

        assertTrue(
                exchange.getTradeHistory().isEmpty()
        );
    }

    private Order createOrder(
            String orderId,
            String traderId,
            String symbol,
            OrderSide side,
            double price) {

        return new Order(
                orderId,
                traderId,
                symbol,
                side,
                OrderType.LIMIT,
                price,
                100,
                Instant.now()
        );
    }
}