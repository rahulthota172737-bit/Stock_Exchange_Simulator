package com.stockexchange.concurrency;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.exchange.Exchange;
import com.stockexchange.exchange.ExchangeConfig;
import com.stockexchange.exchange.TradingSession;
import com.stockexchange.matching.TradeExecutor;
import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trader;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;
import com.stockexchange.service.TradeService;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrentExchangeTest {

    @Test
    void shouldAcceptConcurrentOrderSubmissions() throws Exception {

        Exchange exchange = createExchange();

        Trader buyer = new Trader("T001", "Buyer");

        Trader seller = new Trader("T002", "Seller");

        Stock stock = new Stock("AAPL", "Apple Inc.");

        exchange.registerTrader(buyer);
        exchange.registerTrader(seller);
        exchange.registerStock(stock);

        exchange.openTrading();

        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);

        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        List<Throwable> errors = new ArrayList<>();

        try {

            for (int i = 0; i < threadCount; i++) {

                final int index = i;

                executor.submit(() -> {

                    try {

                        startLatch.await();

                        Order order;

                        if (index % 2 == 0) {

                            order = new Order("BUY-" + index, buyer.getTraderId(), stock.getSymbol(), OrderSide.BUY, OrderType.LIMIT, 100.0, 10, Instant.now());

                        } else {

                            order = new Order("SELL-" + index, seller.getTraderId(), stock.getSymbol(), OrderSide.SELL, OrderType.LIMIT, 100.0, 10, Instant.now());
                        }

                        exchange.submitOrder(order);

                    } catch (Throwable e) {

                        synchronized (errors) {
                            errors.add(e);
                        }

                    } finally {

                        finishLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            assertTrue(finishLatch.await(30, TimeUnit.SECONDS), "Concurrent tasks did not finish");

        } finally {

            executor.shutdown();

            executor.awaitTermination(30, TimeUnit.SECONDS);
        }

        if (!errors.isEmpty()) {

            errors.forEach(Throwable::printStackTrace);
        }
    }

    private Exchange createExchange() {

        TraderRepository traderRepository = new TraderRepository();

        StockRepository stockRepository = new StockRepository();

        TradeExecutor tradeExecutor = new TradeExecutor();

        TradeService tradeService = new TradeService();

        TradingSession tradingSession = new TradingSession();

        ExchangeConfig exchangeConfig = new ExchangeConfig(1, 1000, EnumSet.of(OrderType.LIMIT, OrderType.MARKET));

        return new Exchange(traderRepository, stockRepository, tradeExecutor, tradeService, tradingSession, exchangeConfig);
    }
}
