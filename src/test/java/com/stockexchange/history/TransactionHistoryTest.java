package com.stockexchange.history;

import com.stockexchange.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionHistoryTest {

    private Transaction createTransaction(
            String id,
            String traderId,
            String symbol,
            TransactionType type,
            long quantity,
            double price,
            double totalValue) {

        return new Transaction(
                id,
                traderId,
                symbol,
                type,
                quantity,
                price,
                totalValue,
                LocalDateTime.now()
        );
    }

    @Test
    void shouldRecordTransaction() {

        TransactionHistory history =
                new TransactionHistory();

        Transaction transaction = createTransaction(
                "TXN001",
                "TRADER_A",
                "AAPL",
                TransactionType.BUY,
                100,
                150,
                15_000
        );

        history.record(transaction);

        assertEquals(1, history.getAll().size());
    }

    @Test
    void shouldGetTransactionsByTrader() {

        TransactionHistory history =
                new TransactionHistory();

        history.record(createTransaction(
                "TXN001",
                "TRADER_A",
                "AAPL",
                TransactionType.BUY,
                100,
                150,
                15_000
        ));

        history.record(createTransaction(
                "TXN002",
                "TRADER_B",
                "GOOG",
                TransactionType.BUY,
                50,
                200,
                10_000
        ));

        assertEquals(
                1,
                history.getByTrader("TRADER_A").size()
        );
    }

    @Test
    void shouldGetTransactionsBySymbol() {

        TransactionHistory history =
                new TransactionHistory();

        history.record(createTransaction(
                "TXN001",
                "TRADER_A",
                "AAPL",
                TransactionType.BUY,
                100,
                150,
                15_000
        ));

        history.record(createTransaction(
                "TXN002",
                "TRADER_A",
                "GOOG",
                TransactionType.BUY,
                50,
                200,
                10_000
        ));

        history.record(createTransaction(
                "TXN003",
                "TRADER_B",
                "AAPL",
                TransactionType.SELL,
                50,
                180,
                9_000
        ));

        assertEquals(
                2,
                history.getBySymbol("AAPL").size()
        );
    }

    @Test
    void shouldGetTransactionsByType() {

        TransactionHistory history =
                new TransactionHistory();

        history.record(createTransaction(
                "TXN001",
                "TRADER_A",
                "AAPL",
                TransactionType.BUY,
                100,
                150,
                15_000
        ));

        history.record(createTransaction(
                "TXN002",
                "TRADER_A",
                "AAPL",
                TransactionType.SELL,
                50,
                180,
                9_000
        ));

        history.record(createTransaction(
                "TXN003",
                "TRADER_A",
                null,
                TransactionType.DEPOSIT,
                0,
                0,
                20_000
        ));

        assertEquals(
                1,
                history.getByType(TransactionType.BUY).size()
        );

        assertEquals(
                1,
                history.getByType(TransactionType.SELL).size()
        );

        assertEquals(
                1,
                history.getByType(TransactionType.DEPOSIT).size()
        );
    }

    @Test
    void shouldRejectNullTransaction() {

        TransactionHistory history =
                new TransactionHistory();

        assertThrows(
                IllegalArgumentException.class,
                () -> history.record(null)
        );
    }
}