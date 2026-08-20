package com.stockexchange.portfolio;

import com.stockexchange.account.Position;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioServiceTest {

    @Test
    void shouldCalculateHoldingsValue() {

        Portfolio portfolio = new Portfolio();

        Position aapl = new Position("AAPL");
        aapl.buy(100, 150);

        Position goog = new Position("GOOG");
        goog.buy(50, 200);

        portfolio.addPosition(aapl);
        portfolio.addPosition(goog);

        PortfolioService service =
                new PortfolioService();

        Map<String, Double> prices = Map.of(
                "AAPL", 150.0,
                "GOOG", 200.0
        );

        double value =
                service.calculateHoldingsValue(
                        portfolio,
                        prices
                );

        assertEquals(25_000, value);
    }

    @Test
    void shouldCalculatePortfolioValue() {

        Portfolio portfolio = new Portfolio();

        Position aapl = new Position("AAPL");
        aapl.buy(100, 150);

        Position goog = new Position("GOOG");
        goog.buy(50, 200);

        portfolio.addPosition(aapl);
        portfolio.addPosition(goog);

        PortfolioService service =
                new PortfolioService();

        Map<String, Double> prices = Map.of(
                "AAPL", 150.0,
                "GOOG", 200.0
        );

        double value =
                service.calculatePortfolioValue(
                        50_000,
                        portfolio,
                        prices
                );

        assertEquals(75_000, value);
    }

    @Test
    void shouldCalculateUnrealizedProfit() {

        Portfolio portfolio = new Portfolio();

        Position aapl = new Position("AAPL");
        aapl.buy(100, 150);

        portfolio.addPosition(aapl);

        PortfolioService service =
                new PortfolioService();

        Map<String, Double> prices = Map.of(
                "AAPL", 180.0
        );

        double pnl =
                service.calculateUnrealizedPnl(
                        portfolio,
                        prices
                );

        assertEquals(3_000, pnl);
    }

    @Test
    void shouldCalculateUnrealizedLoss() {

        Portfolio portfolio = new Portfolio();

        Position aapl = new Position("AAPL");
        aapl.buy(100, 150);

        portfolio.addPosition(aapl);

        PortfolioService service =
                new PortfolioService();

        Map<String, Double> prices = Map.of(
                "AAPL", 120.0
        );

        double pnl =
                service.calculateUnrealizedPnl(
                        portfolio,
                        prices
                );

        assertEquals(-3_000, pnl);
    }

    @Test
    void shouldRejectMissingMarketPrice() {

        Portfolio portfolio = new Portfolio();

        Position aapl = new Position("AAPL");
        aapl.buy(100, 150);

        portfolio.addPosition(aapl);

        PortfolioService service =
                new PortfolioService();

        Map<String, Double> prices = Map.of();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateHoldingsValue(
                        portfolio,
                        prices
                )
        );
    }
}