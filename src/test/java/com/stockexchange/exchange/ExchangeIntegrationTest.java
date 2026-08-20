package com.stockexchange.exchange;

import com.stockexchange.account.Account;
import com.stockexchange.account.Position;
import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderStatus;
import com.stockexchange.enums.OrderType;
import com.stockexchange.enums.TransactionType;
import com.stockexchange.history.Transaction;
import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;
import com.stockexchange.matching.TradeExecutor;
import com.stockexchange.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeIntegrationTest {

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
    void completeTradingScenarioShouldWork() {

        exchange.openTrading();

        exchange.registerTrader(
                new Trader(
                        "TRADER-A",
                        "Trader A"
                )
        );

        exchange.registerTrader(
                new Trader(
                        "TRADER-B",
                        "Trader B"
                )
        );

        exchange.registerStock(
                new Stock(
                        "AAPL",
                        "Apple Inc."
                )
        );

        Account sellerAccount =
                exchange.getAccount("TRADER-A");

        Account buyerAccount =
                exchange.getAccount("TRADER-B");

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(
                100,
                100.0
        );

        sellerAccount
                .getPortfolio()
                .addPosition(sellerPosition);

        buyerAccount
                .getBalance()
                .deposit(20_000);

        Order sellOrder =
                new Order(
                        "SELL-1",
                        "TRADER-A",
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        100.0,
                        100,
                        Instant.now()
                );

        Order buyOrder =
                new Order(
                        "BUY-1",
                        "TRADER-B",
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        105.0,
                        100,
                        Instant.now()
                );

        exchange.submitOrder(sellOrder);
        exchange.submitOrder(buyOrder);

        assertEquals(
                OrderStatus.FILLED,
                sellOrder.getStatus()
        );

        assertEquals(
                OrderStatus.FILLED,
                buyOrder.getStatus()
        );

        assertEquals(
                1,
                exchange.getTradeHistory().size()
        );

        assertTrue(
                exchange.getOrderBook("AAPL").isEmpty()
        );

        assertEquals(
                10_000,
                sellerAccount
                        .getBalance()
                        .getAvailableCash()
        );

        assertEquals(
                0,
                sellerAccount
                        .getPortfolio()
                        .getTotalQuantity("AAPL")
        );

        assertEquals(
                10_000,
                buyerAccount
                        .getBalance()
                        .getAvailableCash()
        );

        assertEquals(
                100,
                buyerAccount
                        .getPortfolio()
                        .getTotalQuantity("AAPL")
        );

        List<Transaction> transactions =
                exchange
                        .getTransactionHistory()
                        .getAll();

        assertEquals(
                2,
                transactions.size()
        );

        assertEquals(
                1,
                exchange
                        .getTransactionHistory()
                        .getByTrader("TRADER-A")
                        .size()
        );

        assertEquals(
                1,
                exchange
                        .getTransactionHistory()
                        .getByTrader("TRADER-B")
                        .size()
        );

        assertEquals(
                TransactionType.SELL,
                exchange
                        .getTransactionHistory()
                        .getByTrader("TRADER-A")
                        .get(0)
                        .getType()
        );

        assertEquals(
                TransactionType.BUY,
                exchange
                        .getTransactionHistory()
                        .getByTrader("TRADER-B")
                        .get(0)
                        .getType()
        );

        assertEquals(
                10_000,
                exchange
                        .getTransactionHistory()
                        .getByTrader("TRADER-A")
                        .get(0)
                        .getTotalValue()
        );

        assertEquals(
                10_000,
                exchange
                        .getTransactionHistory()
                        .getByTrader("TRADER-B")
                        .get(0)
                        .getTotalValue()
        );
    }
}
