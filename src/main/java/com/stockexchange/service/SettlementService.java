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

import java.time.LocalDateTime;

public class SettlementService {

    private final TransactionHistory transactionHistory;
    private double lastRealizedPnl;

    public SettlementService(TransactionHistory transactionHistory) {
        if (transactionHistory == null) {
            throw new IllegalArgumentException(
                    "Transaction history cannot be null"
            );
        }

        this.transactionHistory = transactionHistory;
        this.lastRealizedPnl = 0;
    }

    public void settle(
            Trade trade,
            Account buyerAccount,
            Account sellerAccount) {

        validateSettlementInputs(
                trade,
                buyerAccount,
                sellerAccount
        );

        double tradeValue =
                calculateTradeValue(
                        trade.getPrice(),
                        trade.getQuantity()
                );

        Position sellerPosition =
                sellerAccount
                        .getPortfolio()
                        .getPosition(trade.getSymbol());

        validateBuyerAndSeller(
                buyerAccount,
                sellerPosition,
                tradeValue,
                trade
        );

        double buyerOriginalCash =
                buyerAccount
                        .getBalance()
                        .getAvailableCash();

        double sellerOriginalCash =
                sellerAccount
                        .getBalance()
                        .getAvailableCash();

        long sellerOriginalQuantity =
                sellerPosition.getQuantity();

        double sellerOriginalAveragePrice =
                sellerPosition.getAverageEntryPrice();

        Position buyerPosition =
                buyerAccount
                        .getPortfolio()
                        .getPosition(trade.getSymbol());

        long buyerOriginalQuantity = 0;
        double buyerOriginalAveragePrice = 0;

        if (buyerPosition != null) {
            buyerOriginalQuantity =
                    buyerPosition.getQuantity();

            buyerOriginalAveragePrice =
                    buyerPosition.getAverageEntryPrice();
        }

        int originalTransactionCount =
                transactionHistory.getAll().size();

        try {
            lastRealizedPnl =
                    (trade.getPrice()
                            - sellerOriginalAveragePrice)
                            * trade.getQuantity();

            buyerAccount
                    .getBalance()
                    .debit(tradeValue);

            if (buyerPosition == null) {
                buyerPosition =
                        new Position(trade.getSymbol());

                buyerAccount
                        .getPortfolio()
                        .addPosition(buyerPosition);
            }

            buyerPosition.buy(
                    trade.getQuantity(),
                    trade.getPrice()
            );

            sellerPosition.sell(
                    trade.getQuantity()
            );

            sellerAccount
                    .getBalance()
                    .credit(tradeValue);

            if (sellerPosition.getQuantity() == 0) {
                sellerAccount
                        .getPortfolio()
                        .removePosition(
                                trade.getSymbol()
                        );
            }

            Transaction buyTransaction =
                    new Transaction(
                            trade.getTradeId() + "-BUY",
                            buyerAccount.getTraderId(),
                            trade.getSymbol(),
                            TransactionType.BUY,
                            trade.getQuantity(),
                            trade.getPrice(),
                            tradeValue,
                            LocalDateTime.now()
                    );

            Transaction sellTransaction =
                    new Transaction(
                            trade.getTradeId() + "-SELL",
                            sellerAccount.getTraderId(),
                            trade.getSymbol(),
                            TransactionType.SELL,
                            trade.getQuantity(),
                            trade.getPrice(),
                            tradeValue,
                            LocalDateTime.now()
                    );

            transactionHistory.record(buyTransaction);
            transactionHistory.record(sellTransaction);

        } catch (RuntimeException e) {

            restoreBalance(
                    buyerAccount,
                    buyerOriginalCash
            );

            restoreBalance(
                    sellerAccount,
                    sellerOriginalCash
            );

            restorePosition(
                    buyerAccount,
                    trade.getSymbol(),
                    buyerOriginalQuantity,
                    buyerOriginalAveragePrice
            );

            restorePosition(
                    sellerAccount,
                    trade.getSymbol(),
                    sellerOriginalQuantity,
                    sellerOriginalAveragePrice
            );

            transactionHistory.rollbackTo(
                    originalTransactionCount
            );

            lastRealizedPnl = 0;

            throw new SettlementException(
                    "Settlement failed and was rolled back",
                    e
            );
        }
    }

    private void validateSettlementInputs(
            Trade trade,
            Account buyerAccount,
            Account sellerAccount) {

        if (trade == null) {
            throw new SettlementException(
                    "Trade cannot be null"
            );
        }

        if (buyerAccount == null) {
            throw new SettlementException(
                    "Buyer account cannot be null"
            );
        }

        if (sellerAccount == null) {
            throw new SettlementException(
                    "Seller account cannot be null"
            );
        }
    }

    private void validateBuyerAndSeller(
            Account buyerAccount,
            Position sellerPosition,
            double tradeValue,
            Trade trade) {

        if (sellerPosition == null) {
            throw new InsufficientHoldingsException(
                    "Seller does not own "
                            + trade.getSymbol()
            );
        }

        if (sellerPosition.getQuantity()
                < trade.getQuantity()) {

            throw new InsufficientHoldingsException(
                    "Seller has insufficient holdings"
            );
        }

        if (buyerAccount
                .getBalance()
                .getAvailableCash()
                < tradeValue) {

            throw new InsufficientBalanceException(
                    "Buyer has insufficient balance"
            );
        }
    }

    private double calculateTradeValue(
            double price,
            long quantity) {

        if (price <= 0) {
            throw new SettlementException(
                    "Trade price must be greater than zero"
            );
        }

        if (quantity <= 0) {
            throw new SettlementException(
                    "Trade quantity must be greater than zero"
            );
        }

        return price * quantity;
    }

    private void restoreBalance(
            Account account,
            double originalCash) {

        double currentCash =
                account.getBalance()
                        .getAvailableCash();

        if (currentCash > originalCash) {
            account.getBalance()
                    .withdraw(
                            currentCash - originalCash
                    );
        } else if (currentCash < originalCash) {
            account.getBalance()
                    .deposit(
                            originalCash - currentCash
                    );
        }
    }

    private void restorePosition(
            Account account,
            String symbol,
            long originalQuantity,
            double originalAveragePrice) {

        account.getPortfolio()
                .removePosition(symbol);

        if (originalQuantity > 0) {
            Position restored =
                    new Position(symbol);

            restored.buy(
                    originalQuantity,
                    originalAveragePrice
            );

            account.getPortfolio()
                    .addPosition(restored);
        }
    }

    public double getLastRealizedPnl() {
        return lastRealizedPnl;
    }
}