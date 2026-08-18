package com.stockexchange.exchange;

import com.stockexchange.book.OrderBook;
import com.stockexchange.exception.StockNotFoundException;
import com.stockexchange.exception.TraderNotFoundException;
import com.stockexchange.exception.TradingHaltedException;
import com.stockexchange.matching.MatchResult;
import com.stockexchange.matching.MatchingEngine;
import com.stockexchange.matching.TradeExecutor;
import com.stockexchange.model.Order;
import com.stockexchange.model.Stock;
import com.stockexchange.model.Trade;
import com.stockexchange.model.Trader;
import com.stockexchange.repository.OrderRepository;
import com.stockexchange.repository.StockRepository;
import com.stockexchange.repository.TraderRepository;
import com.stockexchange.service.OrderService;
import com.stockexchange.service.TradeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exchange {

    private final TraderRepository traderRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;

    private final Map<String, OrderBook> orderBooks;
    private final Map<String, MatchingEngine> matchingEngines;

    private final TradeExecutor tradeExecutor;
    private final TradeService tradeService;
    private final OrderService orderService;

    private final TradingSession tradingSession;
    private final ExchangeConfig exchangeConfig;

    public Exchange(
            TraderRepository traderRepository,
            StockRepository stockRepository,
            TradeExecutor tradeExecutor,
            TradeService tradeService,
            TradingSession tradingSession,
            ExchangeConfig exchangeConfig) {

        this.traderRepository = traderRepository;
        this.stockRepository = stockRepository;
        this.orderRepository = new OrderRepository();

        this.orderBooks = new HashMap<>();
        this.matchingEngines = new HashMap<>();

        this.tradeExecutor = tradeExecutor;
        this.tradeService = tradeService;

        this.tradingSession = tradingSession;
        this.exchangeConfig = exchangeConfig;

        this.orderService = new OrderService(
                orderRepository,
                traderRepository,
                stockRepository,
                orderBooks,
                matchingEngines,
                exchangeConfig
        );
    }

    public void registerTrader(Trader trader) {
        traderRepository.save(trader);
    }

    public Trader getTrader(String traderId) {

        if (!traderRepository.exists(traderId)) {
            throw new TraderNotFoundException(
                    "Trader not found: " + traderId
            );
        }

        return traderRepository.findById(traderId);
    }

    public void registerStock(Stock stock) {

        stockRepository.save(stock);

        String symbol = stock.getSymbol();

        OrderBook orderBook =
                new OrderBook(symbol);

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        orderBooks.put(symbol, orderBook);
        matchingEngines.put(symbol, matchingEngine);
    }

    public Stock getStock(String symbol) {

        if (!stockRepository.exists(symbol)) {
            throw new StockNotFoundException(
                    "Stock not found: " + symbol
            );
        }

        return stockRepository.findBySymbol(symbol);
    }

    public MatchResult submitOrder(Order order) {

        if (!tradingSession.isOpen()) {
            throw new TradingHaltedException(
                    "Trading is not currently open"
            );
        }

        return orderService.submitOrder(order);
    }

    public void cancelOrder(String orderId) {
        orderService.cancelOrder(orderId);
    }

    public Order getOrder(String orderId) {
        return orderService.getOrder(orderId);
    }

    public OrderBook getOrderBook(String symbol) {

        if (!stockRepository.exists(symbol)) {
            throw new StockNotFoundException(
                    "Stock not found: " + symbol
            );
        }

        return orderBooks.get(symbol);
    }

    public MatchingEngine getMatchingEngine(String symbol) {

        if (!stockRepository.exists(symbol)) {
            throw new StockNotFoundException(
                    "Stock not found: " + symbol
            );
        }

        return matchingEngines.get(symbol);
    }

    public Double getBestBid(String symbol) {
        return getOrderBook(symbol).getBestBid();
    }

    public Double getBestAsk(String symbol) {
        return getOrderBook(symbol).getBestAsk();
    }

    public void openTrading() {
        tradingSession.open();
    }

    public void haltTrading() {
        tradingSession.halt();
    }

    public void resumeTrading() {
        tradingSession.resume();
    }

    public void closeTrading() {
        tradingSession.close();
    }

    public boolean isTradingOpen() {
        return tradingSession.isOpen();
    }

    public List<Trade> getTradeHistory() {
        return tradeService.getTradeHistory();
    }

    public List<Trade> getTradeHistory(String symbol) {
        return tradeService.getTradesForSymbol(symbol);
    }
}
