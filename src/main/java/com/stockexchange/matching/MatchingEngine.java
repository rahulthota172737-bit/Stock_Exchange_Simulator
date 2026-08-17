package com.stockexchange.matching;

import com.stockexchange.book.OrderBook;
import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderStatus;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import com.stockexchange.model.Trade;
import com.stockexchange.service.TradeService;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {

    private final OrderBook orderBook;
    private final TradeExecutor tradeExecutor;
    private final TradeService tradeService;

    public MatchingEngine(
            OrderBook orderBook,
            TradeExecutor tradeExecutor,
            TradeService tradeService
    ) {
        this.orderBook = orderBook;
        this.tradeExecutor = tradeExecutor;
        this.tradeService = tradeService;
    }

    public MatchResult submitOrder(Order incomingOrder) {

        if (incomingOrder.getSide() == OrderSide.BUY) {

            if (incomingOrder.getType() == OrderType.MARKET) {
                return matchMarketBuyOrder(incomingOrder);
            }

            return matchBuyOrder(incomingOrder);
        }

        if (incomingOrder.getType() == OrderType.MARKET) {
            return matchMarketSellOrder(incomingOrder);
        }

        return matchSellOrder(incomingOrder);
    }

    private MatchResult matchMarketSellOrder(Order sellOrder) {

        List<Trade> trades = new ArrayList<>();

        while (sellOrder.getRemainingQuantity() > 0) {

            Order buyOrder = orderBook.getBestBuyOrder();

            if (buyOrder == null) {
                break;
            }

            long executionQuantity =
                    tradeExecutor.calculateExecutionQuantity(
                            buyOrder,
                            sellOrder
                    );

            Trade trade =
                    tradeExecutor.execute(
                            buyOrder,
                            sellOrder,
                            buyOrder
                    );

            trades.add(trade);

            buyOrder.fill(executionQuantity);
            sellOrder.fill(executionQuantity);

            tradeService.recordTrade(trade);

            if (buyOrder.isFilled()) {
                orderBook.removeOrder(buyOrder);
            }
        }

        if (sellOrder.getRemainingQuantity() > 0) {
            sellOrder.cancel();
        }

        return new MatchResult(
                sellOrder,
                trades,
                sellOrder.getRemainingQuantity(),
                !trades.isEmpty()
        );
    }

    private MatchResult matchMarketBuyOrder(Order buyOrder) {

        List<Trade> trades = new ArrayList<>();

        while (buyOrder.getRemainingQuantity() > 0) {

            Order sellOrder = orderBook.getBestSellOrder();

            if (sellOrder == null) {
                break;
            }

            long executionQuantity =
                    tradeExecutor.calculateExecutionQuantity(
                            buyOrder,
                            sellOrder
                    );

            Trade trade =
                    tradeExecutor.execute(
                            buyOrder,
                            sellOrder,
                            sellOrder
                    );

            trades.add(trade);

            buyOrder.fill(executionQuantity);
            sellOrder.fill(executionQuantity);

            tradeService.recordTrade(trade);

            if (sellOrder.isFilled()) {
                orderBook.removeOrder(sellOrder);
            }
        }

        if (buyOrder.getRemainingQuantity() > 0) {
            buyOrder.cancel();
        }

        return new MatchResult(
                buyOrder,
                trades,
                buyOrder.getRemainingQuantity(),
                !trades.isEmpty()
        );
    }

    private MatchResult matchSellOrder(Order sellOrder) {

        List<Trade> trades = new ArrayList<>();

        if (sellOrder.getType() != OrderType.LIMIT) {
            throw new UnsupportedOperationException(
                    "Market orders will be implemented later"
            );
        }

        while (sellOrder.getRemainingQuantity() > 0) {

            Order buyOrder = orderBook.getBestBuyOrder();

            if (buyOrder == null) {
                break;
            }

            if (sellOrder.getPrice() > buyOrder.getPrice()) {
                break;
            }

            long executionQuantity =
                    tradeExecutor.calculateExecutionQuantity(
                            buyOrder,
                            sellOrder
                    );

            Trade trade =
                    tradeExecutor.execute(
                            buyOrder,
                            sellOrder,
                            buyOrder
                    );

            trades.add(trade);

            buyOrder.fill(executionQuantity);
            sellOrder.fill(executionQuantity);

            tradeService.recordTrade(trade);

            if (buyOrder.isFilled()) {
                orderBook.removeOrder(buyOrder);
            }
        }

        if (sellOrder.getRemainingQuantity() > 0) {

            if (sellOrder.getStatus() == OrderStatus.NEW) {
                sellOrder.open();
            }

            orderBook.addOrder(sellOrder);
        }

        return new MatchResult(
                sellOrder,
                trades,
                sellOrder.getRemainingQuantity(),
                !trades.isEmpty()
        );
    }

    private MatchResult matchBuyOrder(Order buyOrder) {

        List<Trade> trades = new ArrayList<>();

        if (buyOrder.getType() != OrderType.LIMIT) {
            throw new UnsupportedOperationException(
                    "Market orders will be implemented later"
            );
        }

        while (buyOrder.getRemainingQuantity() > 0) {

            Order sellOrder = orderBook.getBestSellOrder();

            if (sellOrder == null) {
                break;
            }

            if (buyOrder.getPrice() < sellOrder.getPrice()) {
                break;
            }

            long executionQuantity =
                    tradeExecutor.calculateExecutionQuantity(
                            buyOrder,
                            sellOrder
                    );

            Trade trade =
                    tradeExecutor.execute(
                            buyOrder,
                            sellOrder,
                            sellOrder
                    );

            trades.add(trade);

            buyOrder.fill(executionQuantity);
            sellOrder.fill(executionQuantity);

            tradeService.recordTrade(trade);

            if (sellOrder.isFilled()) {
                orderBook.removeOrder(sellOrder);
            }
        }

        if (buyOrder.getRemainingQuantity() > 0) {
            if (buyOrder.getStatus() == com.stockexchange.enums.OrderStatus.NEW) {
                buyOrder.open();
            }

            orderBook.addOrder(buyOrder);
        }

        return new MatchResult(
                buyOrder,
                trades,
                buyOrder.getRemainingQuantity(),
                !trades.isEmpty()
        );
    }
}
