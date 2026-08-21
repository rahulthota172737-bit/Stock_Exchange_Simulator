package com.stockexchange.simulation;

import com.stockexchange.concurrency.TradingExecutor;
import com.stockexchange.enums.OrderType;
import com.stockexchange.exchange.Exchange;
import com.stockexchange.exchange.ExchangeConfig;
import com.stockexchange.exchange.TradingSession;
import com.stockexchange.matching.TradeExecutor;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;
import com.stockexchange.service.TradeService;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MarketSimulationDemo {

    public static void main(String[] args) {

        TraderRepository traderRepository = new TraderRepository();

        StockRepository stockRepository = new StockRepository();

        TradeExecutor tradeExecutor = new TradeExecutor();

        TradeService tradeService = new TradeService();

        TradingSession tradingSession = new TradingSession();

        ExchangeConfig exchangeConfig = new ExchangeConfig(1, 1000, EnumSet.of(OrderType.LIMIT, OrderType.MARKET));

        Exchange exchange = new Exchange(traderRepository, stockRepository, tradeExecutor, tradeService, tradingSession, exchangeConfig);

        List<Trader> traders = createTraders(100);

        List<Stock> stocks = createStocks();

        for (Trader trader : traders) {
            exchange.registerTrader(trader);
        }

        for (Stock stock : stocks) {
            exchange.registerStock(stock);
        }

        exchange.openTrading();

        TradingExecutor tradingExecutor = new TradingExecutor(10);

        int ordersPerTrader = 10;

        MarketSimulation simulation = new MarketSimulation(exchange, traders, stocks, tradingExecutor, ordersPerTrader);
        simulation.run();

        SimulationMetrics metrics = simulation.getMetrics();

        System.out.println();

        System.out.println("====================================");

        System.out.println("       MARKET SIMULATION");

        System.out.println("====================================");

        System.out.println();

        System.out.println("Traders:             " + traders.size());

        System.out.println("Stocks:              " + stocks.size());

        System.out.println("Orders Submitted:    " + (traders.size() * ordersPerTrader));

        System.out.println("Trades Executed:     " + exchange.getTradeHistory().size());

        System.out.println("Rejected Orders:     " + metrics.getTotalOrdersRejected());

        System.out.println();

        System.out.println("Execution Time:      " + metrics.getTotalExecutionTime() + " ms");

        System.out.println("Orders/sec:          " + String.format("%.2f", metrics.getOrdersPerSecond()));

        System.out.println("Trades/sec:          " + String.format("%.2f", metrics.getTradesPerSecond()));

        System.out.println();

        System.out.println("====================================");
    }

    private static List<Trader> createTraders(int count) {

        List<Trader> traders = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            traders.add(new Trader("T" + String.format("%03d", i), "Trader " + i));
        }

        return traders;
    }

    private static List<Stock> createStocks() {

        List<Stock> stocks = new ArrayList<>();

        stocks.add(new Stock("AAPL", "Apple Inc."));

        stocks.add(new Stock("GOOG", "Alphabet Inc."));

        stocks.add(new Stock("TSLA", "Tesla Inc."));

        stocks.add(new Stock("MSFT", "Microsoft Corporation"));

        stocks.add(new Stock("AMZN", "Amazon.com Inc."));

        stocks.add(new Stock("META", "Meta Platforms Inc."));

        stocks.add(new Stock("NVDA", "NVIDIA Corporation"));

        stocks.add(new Stock("NFLX", "Netflix Inc."));

        stocks.add(new Stock("INTC", "Intel Corporation"));

        stocks.add(new Stock("ORCL", "Oracle Corporation"));

        return stocks;
    }
}
