package com.stockexchange.portfolio;

import com.stockexchange.account.Position;

import java.util.Map;

public class PortfolioService {

    public double calculateHoldingsValue(
            Portfolio portfolio,
            Map<String, Double> currentPrices) {

        double totalValue = 0;

        for (Position position : portfolio.getHoldings().values()) {

            Double currentPrice =
                    currentPrices.get(position.getSymbol());

            if (currentPrice == null) {
                throw new IllegalArgumentException(
                        "Current price not available for "
                                + position.getSymbol()
                );
            }

            if (currentPrice <= 0) {
                throw new IllegalArgumentException(
                        "Current price must be greater than zero"
                );
            }

            totalValue +=
                    position.getQuantity() * currentPrice;
        }

        return totalValue;
    }

    public double calculatePortfolioValue(
            double cash,
            Portfolio portfolio,
            Map<String, Double> currentPrices) {

        if (cash < 0) {
            throw new IllegalArgumentException(
                    "Cash cannot be negative"
            );
        }

        return cash +
                calculateHoldingsValue(
                        portfolio,
                        currentPrices
                );
    }

    public double calculateUnrealizedPnl(
            Portfolio portfolio,
            Map<String, Double> currentPrices) {

        double totalPnl = 0;

        for (Position position : portfolio.getHoldings().values()) {

            Double currentPrice =
                    currentPrices.get(position.getSymbol());

            if (currentPrice == null) {
                throw new IllegalArgumentException(
                        "Current price not available for "
                                + position.getSymbol()
                );
            }

            if (currentPrice <= 0) {
                throw new IllegalArgumentException(
                        "Current price must be greater than zero"
                );
            }

            totalPnl +=
                    (currentPrice
                            - position.getAverageEntryPrice())
                            * position.getQuantity();
        }

        return totalPnl;
    }
}