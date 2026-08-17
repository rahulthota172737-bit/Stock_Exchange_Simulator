package com.stockexchange.matching;

import com.stockexchange.book.OrderBook;
import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.enums.OrderStatus;
import com.stockexchange.model.Order;
import com.stockexchange.model.Trade;
import com.stockexchange.service.TradeService;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineTest {

    @Test
    void shouldPartiallyFillBuyOrder() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order sellOrder = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        sellOrder.open();
        orderBook.addOrder(sellOrder);

        Order buyOrder = new Order(
                "BUY1",
                "TRADER2",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                105.0,
                100,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(buyOrder);

        assertTrue(result.isMatched());

        assertEquals(1, result.getTrades().size());

        assertEquals(
                40,
                result.getTrades().get(0).getQuantity()
        );

        assertEquals(
                100.0,
                result.getTrades().get(0).getPrice()
        );

        assertEquals(
                60,
                buyOrder.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                buyOrder.getStatus()
        );

        assertEquals(
                OrderStatus.FILLED,
                sellOrder.getStatus()
        );

        assertEquals(
                1,
                tradeService.getTradeHistory().size()
        );
    }
    @Test
    void shouldMatchBuyOrderAgainstMultipleSellOrders() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order sellOrder1 = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        Order sellOrder2 = new Order(
                "SELL2",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                101.0,
                30,
                Instant.now()
        );

        Order sellOrder3 = new Order(
                "SELL3",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                102.0,
                50,
                Instant.now()
        );

        sellOrder1.open();
        sellOrder2.open();
        sellOrder3.open();

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        Order buyOrder = new Order(
                "BUY1",
                "TRADER2",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                105.0,
                100,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(buyOrder);

        assertTrue(result.isMatched());

        assertEquals(3, result.getTrades().size());

        assertEquals(
                40,
                result.getTrades().get(0).getQuantity()
        );

        assertEquals(
                100.0,
                result.getTrades().get(0).getPrice()
        );

        assertEquals(
                30,
                result.getTrades().get(1).getQuantity()
        );

        assertEquals(
                101.0,
                result.getTrades().get(1).getPrice()
        );

        assertEquals(
                30,
                result.getTrades().get(2).getQuantity()
        );

        assertEquals(
                102.0,
                result.getTrades().get(2).getPrice()
        );

        assertEquals(
                0,
                buyOrder.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.FILLED,
                buyOrder.getStatus()
        );

        assertEquals(
                OrderStatus.FILLED,
                sellOrder1.getStatus()
        );

        assertEquals(
                OrderStatus.FILLED,
                sellOrder2.getStatus()
        );

        assertEquals(
                20,
                sellOrder3.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                sellOrder3.getStatus()
        );

        assertEquals(
                3,
                tradeService.getTradeHistory().size()
        );
    }
    @Test
    void shouldMatchSellOrderAgainstBuyOrder() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order buyOrder = new Order(
                "BUY1",
                "TRADER1",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        buyOrder.open();
        orderBook.addOrder(buyOrder);

        Order sellOrder = new Order(
                "SELL1",
                "TRADER2",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                95.0,
                100,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(sellOrder);

        assertTrue(result.isMatched());

        assertEquals(1, result.getTrades().size());

        Trade trade = result.getTrades().get(0);

        assertEquals(40, trade.getQuantity());

        assertEquals(100.0, trade.getPrice());

        assertEquals(
                60,
                sellOrder.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                sellOrder.getStatus()
        );

        assertEquals(
                OrderStatus.FILLED,
                buyOrder.getStatus()
        );

        assertEquals(
                1,
                tradeService.getTradeHistory().size()
        );
    }
    @Test
    void shouldCancelUnfilledMarketBuy() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order sellOrder = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        sellOrder.open();
        orderBook.addOrder(sellOrder);

        Order marketBuy = new Order(
                "BUY1",
                "TRADER2",
                "AAPL",
                OrderSide.BUY,
                OrderType.MARKET,
                0.0,
                100,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(marketBuy);

        assertTrue(result.isMatched());

        assertEquals(
                60,
                marketBuy.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.CANCELLED,
                marketBuy.getStatus()
        );
    }
    @Test
    void shouldExecuteMarketSell() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order buyOrder1 = new Order(
                "BUY1",
                "TRADER1",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                102.0,
                40,
                Instant.now()
        );

        Order buyOrder2 = new Order(
                "BUY2",
                "TRADER1",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                101.0,
                30,
                Instant.now()
        );

        buyOrder1.open();
        buyOrder2.open();

        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);

        Order marketSell = new Order(
                "SELL1",
                "TRADER2",
                "AAPL",
                OrderSide.SELL,
                OrderType.MARKET,
                0.0,
                60,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(marketSell);

        assertTrue(result.isMatched());

        assertEquals(2, result.getTrades().size());

        assertEquals(
                40,
                result.getTrades().get(0).getQuantity()
        );

        assertEquals(
                102.0,
                result.getTrades().get(0).getPrice()
        );

        assertEquals(
                20,
                result.getTrades().get(1).getQuantity()
        );

        assertEquals(
                101.0,
                result.getTrades().get(1).getPrice()
        );

        assertEquals(
                0,
                marketSell.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.FILLED,
                marketSell.getStatus()
        );

        assertEquals(
                OrderStatus.FILLED,
                buyOrder1.getStatus()
        );

        assertEquals(
                10,
                buyOrder2.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                buyOrder2.getStatus()
        );
    }
    @Test
    void shouldCancelUnfilledMarketSell() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order buyOrder = new Order(
                "BUY1",
                "TRADER1",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        buyOrder.open();
        orderBook.addOrder(buyOrder);

        Order marketSell = new Order(
                "SELL1",
                "TRADER2",
                "AAPL",
                OrderSide.SELL,
                OrderType.MARKET,
                0.0,
                100,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(marketSell);

        assertTrue(result.isMatched());

        assertEquals(
                60,
                marketSell.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.CANCELLED,
                marketSell.getStatus()
        );
    }
    @Test
    void shouldMatchLowestSellPriceFirst() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order expensiveSell = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                105.0,
                50,
                Instant.now()
        );

        Order cheapSell = new Order(
                "SELL2",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                50,
                Instant.now()
        );

        expensiveSell.open();
        cheapSell.open();

        orderBook.addOrder(expensiveSell);
        orderBook.addOrder(cheapSell);

        Order buyOrder = new Order(
                "BUY1",
                "TRADER2",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                110.0,
                50,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(buyOrder);

        assertEquals(1, result.getTrades().size());

        Trade trade = result.getTrades().get(0);

        assertEquals(100.0, trade.getPrice());

        assertEquals(50, trade.getQuantity());

        assertEquals(
                OrderStatus.FILLED,
                cheapSell.getStatus()
        );

        assertEquals(
                OrderStatus.OPEN,
                expensiveSell.getStatus()
        );
    }
    @Test
    void shouldMatchEarlierOrderFirstAtSamePrice() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Instant firstTime = Instant.parse(
                "2026-08-17T10:00:00Z"
        );

        Instant secondTime = Instant.parse(
                "2026-08-17T10:01:00Z"
        );

        Order firstSell = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                firstTime
        );

        Order secondSell = new Order(
                "SELL2",
                "TRADER2",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                secondTime
        );

        firstSell.open();
        secondSell.open();

        orderBook.addOrder(firstSell);
        orderBook.addOrder(secondSell);

        Order buyOrder = new Order(
                "BUY1",
                "TRADER3",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                105.0,
                40,
                Instant.parse(
                        "2026-08-17T10:02:00Z"
                )
        );

        MatchResult result =
                matchingEngine.submitOrder(buyOrder);

        assertEquals(1, result.getTrades().size());

        Trade trade = result.getTrades().get(0);

        assertEquals(
                "SELL1",
                trade.getSellOrderId()
        );

        assertEquals(
                OrderStatus.FILLED,
                firstSell.getStatus()
        );

        assertEquals(
                OrderStatus.OPEN,
                secondSell.getStatus()
        );
    }
    @Test
    void shouldCancelMarketSellWhenBuyBookIsEmpty() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order marketSell = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.MARKET,
                0.0,
                100,
                Instant.now()
        );

        MatchResult result =
                matchingEngine.submitOrder(marketSell);

        assertFalse(result.isMatched());

        assertEquals(
                0,
                result.getTrades().size()
        );

        assertEquals(
                100,
                marketSell.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.CANCELLED,
                marketSell.getStatus()
        );

        assertTrue(orderBook.isEmpty());
    }
    @Test
    void shouldRemoveFilledSellOrderFromBook() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order sellOrder = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        sellOrder.open();
        orderBook.addOrder(sellOrder);

        Order buyOrder = new Order(
                "BUY1",
                "TRADER2",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                105.0,
                40,
                Instant.now()
        );

        matchingEngine.submitOrder(buyOrder);

        assertEquals(
                OrderStatus.FILLED,
                sellOrder.getStatus()
        );

        assertEquals(
                0,
                sellOrder.getRemainingQuantity()
        );

        assertNull(orderBook.getBestSellOrder());
    }
    @Test
    void shouldNeverCreateNegativeRemainingQuantity() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order sellOrder = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        sellOrder.open();
        orderBook.addOrder(sellOrder);

        Order buyOrder = new Order(
                "BUY1",
                "TRADER2",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                105.0,
                100,
                Instant.now()
        );

        matchingEngine.submitOrder(buyOrder);

        assertTrue(buyOrder.getRemainingQuantity() >= 0);
        assertTrue(sellOrder.getRemainingQuantity() >= 0);
    }
    @Test
    void shouldNotAddUnfilledMarketOrderToBook() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order marketBuy = new Order(
                "BUY1",
                "TRADER1",
                "AAPL",
                OrderSide.BUY,
                OrderType.MARKET,
                0.0,
                100,
                Instant.now()
        );

        matchingEngine.submitOrder(marketBuy);

        assertEquals(
                OrderStatus.CANCELLED,
                marketBuy.getStatus()
        );

        assertNull(orderBook.getBestBuyOrder());
        assertTrue(orderBook.isEmpty());
    }
    @Test
    void shouldExecuteMultipleTrades() {

        OrderBook orderBook = new OrderBook("AAPL");

        TradeExecutor tradeExecutor = new TradeExecutor();
        TradeService tradeService = new TradeService();

        MatchingEngine matchingEngine =
                new MatchingEngine(
                        orderBook,
                        tradeExecutor,
                        tradeService
                );

        Order sell1 = new Order(
                "SELL1",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                100.0,
                40,
                Instant.now()
        );

        Order sell2 = new Order(
                "SELL2",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                101.0,
                30,
                Instant.now()
        );

        Order sell3 = new Order(
                "SELL3",
                "TRADER1",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                102.0,
                50,
                Instant.now()
        );

        sell1.open();
        sell2.open();
        sell3.open();

        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);

        Order buy = new Order(
                "BUY1",
                "TRADER2",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                105.0,
                100,
                Instant.now()
        );

        MatchResult result = matchingEngine.submitOrder(buy);

        assertEquals(3, result.getTrades().size());

        assertEquals(40, result.getTrades().get(0).getQuantity());
        assertEquals(100.0, result.getTrades().get(0).getPrice());

        assertEquals(30, result.getTrades().get(1).getQuantity());
        assertEquals(101.0, result.getTrades().get(1).getPrice());

        assertEquals(30, result.getTrades().get(2).getQuantity());
        assertEquals(102.0, result.getTrades().get(2).getPrice());

        assertEquals(0, buy.getRemainingQuantity());
        assertEquals(OrderStatus.FILLED, buy.getStatus());

        assertEquals(OrderStatus.FILLED, sell1.getStatus());
        assertEquals(OrderStatus.FILLED, sell2.getStatus());

        assertEquals(20, sell3.getRemainingQuantity());
        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                sell3.getStatus()
        );
    }
}
