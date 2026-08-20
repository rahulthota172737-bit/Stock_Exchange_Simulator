package com.stockexchange.service;

import com.stockexchange.account.Account;
import com.stockexchange.account.Position;
import com.stockexchange.enums.TransactionType;
import com.stockexchange.exception.InsufficientBalanceException;
import com.stockexchange.exception.InsufficientHoldingsException;
import com.stockexchange.exception.SettlementException;
import com.stockexchange.history.Transaction;
import com.stockexchange.history.TransactionHistory;
import com.stockexchange.model.Trade;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SettlementServiceTest {

    private Trade createTrade(
            String symbol,
            double price,
            long quantity) {

        return new Trade(
                "TRADE001",
                "BUY_ORDER_001",
                "SELL_ORDER_001",
                symbol,
                price,
                quantity,
                Instant.now()
        );
    }

    private SettlementService createService(
            TransactionHistory history) {

        return new SettlementService(history);
    }

    @Test
    void shouldSettleBuyAndSellSuccessfully() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(20_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(100, 150);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 150, 100);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        service.settle(
                trade,
                buyer,
                seller
        );

        assertEquals(
                5_000,
                buyer.getBalance().getAvailableCash()
        );

        assertEquals(
                100,
                buyer.getPortfolio()
                        .getTotalQuantity("AAPL")
        );

        assertEquals(
                15_000,
                seller.getBalance().getAvailableCash()
        );

        assertEquals(
                0,
                seller.getPortfolio()
                        .getTotalQuantity("AAPL")
        );
    }

    @Test
    void shouldTransferExactlyTheTradeValue() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(50_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(200, 100);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 120, 100);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        service.settle(
                trade,
                buyer,
                seller
        );

        assertEquals(
                12_000,
                trade.getPrice()
                        * trade.getQuantity()
        );

        assertEquals(
                38_000,
                buyer.getBalance().getAvailableCash()
        );

        assertEquals(
                12_000,
                seller.getBalance().getAvailableCash()
        );
    }

    @Test
    void shouldRejectInsufficientBuyerBalance() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(10_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(100, 150);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 150, 100);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        assertThrows(
                InsufficientBalanceException.class,
                () -> service.settle(
                        trade,
                        buyer,
                        seller
                )
        );

        assertEquals(
                10_000,
                buyer.getBalance().getAvailableCash()
        );

        assertEquals(
                100,
                seller.getPortfolio()
                        .getTotalQuantity("AAPL")
        );

        assertEquals(
                0,
                history.getAll().size()
        );
    }

    @Test
    void shouldRejectInsufficientSellerHoldings() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(20_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(50, 150);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 150, 100);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        assertThrows(
                InsufficientHoldingsException.class,
                () -> service.settle(
                        trade,
                        buyer,
                        seller
                )
        );

        assertEquals(
                20_000,
                buyer.getBalance().getAvailableCash()
        );

        assertEquals(
                50,
                seller.getPortfolio()
                        .getTotalQuantity("AAPL")
        );

        assertEquals(
                0,
                history.getAll().size()
        );
    }

    @Test
    void shouldCreateBuyerPositionWhenBuyerOwnsNothing() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(20_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(100, 150);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 150, 100);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        service.settle(
                trade,
                buyer,
                seller
        );

        Position buyerPosition =
                buyer.getPortfolio()
                        .getPosition("AAPL");

        assertNotNull(buyerPosition);

        assertEquals(
                100,
                buyerPosition.getQuantity()
        );

        assertEquals(
                150,
                buyerPosition.getAverageEntryPrice()
        );
    }

    @Test
    void shouldCalculateRealizedPnl() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(20_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(100, 100);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 130, 40);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        service.settle(
                trade,
                buyer,
                seller
        );

        assertEquals(
                1_200,
                service.getLastRealizedPnl()
        );
    }

    @Test
    void shouldKeepRemainingPositionAfterPartialSell() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(20_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(100, 100);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 130, 40);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        service.settle(
                trade,
                buyer,
                seller
        );

        Position remaining =
                seller.getPortfolio()
                        .getPosition("AAPL");

        assertNotNull(remaining);

        assertEquals(
                60,
                remaining.getQuantity()
        );

        assertEquals(
                100,
                remaining.getAverageEntryPrice()
        );
    }

    @Test
    void shouldRecordBuyAndSellTransactions() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(20_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(100, 150);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 150, 100);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        service.settle(
                trade,
                buyer,
                seller
        );

        assertEquals(
                2,
                history.getAll().size()
        );

        assertEquals(
                1,
                history.getByTrader(
                        "TRADER_BUYER"
                ).size()
        );

        assertEquals(
                1,
                history.getByTrader(
                        "TRADER_SELLER"
                ).size()
        );
    }

    @Test
    void shouldRecordCorrectTransactionDetails() {

        Account buyer =
                new Account("ACC_BUYER", "TRADER_BUYER");

        Account seller =
                new Account("ACC_SELLER", "TRADER_SELLER");

        buyer.getBalance().deposit(20_000);

        Position sellerPosition =
                new Position("AAPL");

        sellerPosition.buy(100, 150);

        seller.getPortfolio()
                .addPosition(sellerPosition);

        Trade trade =
                createTrade("AAPL", 150, 100);

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        service.settle(
                trade,
                buyer,
                seller
        );

        Transaction buyTransaction =
                history
                        .getByTrader("TRADER_BUYER")
                        .get(0);

        assertEquals(
                TransactionType.BUY,
                buyTransaction.getType()
        );

        assertEquals(
                "AAPL",
                buyTransaction.getSymbol()
        );

        assertEquals(
                100,
                buyTransaction.getQuantity()
        );

        assertEquals(
                150,
                buyTransaction.getPrice()
        );

        assertEquals(
                15_000,
                buyTransaction.getTotalValue()
        );
    }

    @Test
    void shouldRejectNullTrade() {

        Account buyer =
                new Account(
                        "ACC_BUYER",
                        "TRADER_BUYER"
                );

        Account seller =
                new Account(
                        "ACC_SELLER",
                        "TRADER_SELLER"
                );

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        assertThrows(
                SettlementException.class,
                () -> service.settle(
                        null,
                        buyer,
                        seller
                )
        );
    }

    @Test
    void shouldRejectNullBuyerAccount() {

        Account seller =
                new Account(
                        "ACC_SELLER",
                        "TRADER_SELLER"
                );

        Trade trade =
                createTrade(
                        "AAPL",
                        150,
                        100
                );

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        assertThrows(
                SettlementException.class,
                () -> service.settle(
                        trade,
                        null,
                        seller
                )
        );
    }

    @Test
    void shouldRejectNullSellerAccount() {

        Account buyer =
                new Account(
                        "ACC_BUYER",
                        "TRADER_BUYER"
                );

        Trade trade =
                createTrade(
                        "AAPL",
                        150,
                        100
                );

        TransactionHistory history =
                new TransactionHistory();

        SettlementService service =
                createService(history);

        assertThrows(
                SettlementException.class,
                () -> service.settle(
                        trade,
                        buyer,
                        null
                )
        );
    }
}