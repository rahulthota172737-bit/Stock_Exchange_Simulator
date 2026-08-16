package com.stockexchange.book;

import com.stockexchange.enums.OrderSide;
import com.stockexchange.enums.OrderType;
import com.stockexchange.model.Order;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    private Order createBuyOrder(String orderId, double price) {
        return new Order(
                orderId,
                "TRADER001",
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                price,
                10,
                Instant.now()
        );
    }

    private Order createSellOrder(String orderId, double price) {
        return new Order(
                orderId,
                "TRADER001",
                "AAPL",
                OrderSide.SELL,
                OrderType.LIMIT,
                price,
                10,
                Instant.now()
        );
    }

    @Test
    void shouldStoreSymbol() {
        OrderBook orderBook = new OrderBook("AAPL");

        assertEquals("AAPL", orderBook.getSymbol());
    }

    @Test
    void shouldBeEmptyInitially() {
        OrderBook orderBook = new OrderBook("AAPL");

        assertTrue(orderBook.isEmpty());
    }

    @Test
    void shouldAddBuyOrder() {
        OrderBook orderBook = new OrderBook("AAPL");

        Order order = createBuyOrder("ORD001", 100.0);

        orderBook.addOrder(order);

        assertFalse(orderBook.isEmpty());
        assertEquals(100.0, orderBook.getBestBid());
    }

    @Test
    void shouldAddSellOrder() {
        OrderBook orderBook = new OrderBook("AAPL");

        Order order = createSellOrder("ORD001", 105.0);

        orderBook.addOrder(order);

        assertFalse(orderBook.isEmpty());
        assertEquals(105.0, orderBook.getBestAsk());
    }

    @Test
    void shouldReturnHighestBuyAsBestBid() {
        OrderBook orderBook = new OrderBook("AAPL");

        orderBook.addOrder(createBuyOrder("ORD001", 100.0));
        orderBook.addOrder(createBuyOrder("ORD002", 105.0));
        orderBook.addOrder(createBuyOrder("ORD003", 102.0));
        orderBook.addOrder(createBuyOrder("ORD004", 110.0));

        assertEquals(110.0, orderBook.getBestBid());
    }

    @Test
    void shouldReturnLowestSellAsBestAsk() {
        OrderBook orderBook = new OrderBook("AAPL");

        orderBook.addOrder(createSellOrder("ORD001", 110.0));
        orderBook.addOrder(createSellOrder("ORD002", 105.0));
        orderBook.addOrder(createSellOrder("ORD003", 102.0));
        orderBook.addOrder(createSellOrder("ORD004", 115.0));

        assertEquals(102.0, orderBook.getBestAsk());
    }

    @Test
    void shouldCalculateSpread() {
        OrderBook orderBook = new OrderBook("AAPL");

        orderBook.addOrder(createBuyOrder("BUY001", 100.0));
        orderBook.addOrder(createSellOrder("SELL001", 102.0));

        assertEquals(2.0, orderBook.getSpread());
    }

    @Test
    void spreadShouldBeNullWhenBuySideIsEmpty() {
        OrderBook orderBook = new OrderBook("AAPL");

        orderBook.addOrder(createSellOrder("SELL001", 102.0));

        assertNull(orderBook.getSpread());
    }

    @Test
    void spreadShouldBeNullWhenSellSideIsEmpty() {
        OrderBook orderBook = new OrderBook("AAPL");

        orderBook.addOrder(createBuyOrder("BUY001", 100.0));

        assertNull(orderBook.getSpread());
    }

    @Test
    void shouldRemoveBuyOrder() {
        OrderBook orderBook = new OrderBook("AAPL");

        Order buy1 = createBuyOrder("BUY001", 100.0);
        Order buy2 = createBuyOrder("BUY002", 105.0);

        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);

        assertEquals(105.0, orderBook.getBestBid());

        orderBook.removeOrder(buy2);

        assertEquals(100.0, orderBook.getBestBid());
    }

    @Test
    void shouldRemoveSellOrder() {
        OrderBook orderBook = new OrderBook("AAPL");

        Order sell1 = createSellOrder("SELL001", 105.0);
        Order sell2 = createSellOrder("SELL002", 102.0);

        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);

        assertEquals(102.0, orderBook.getBestAsk());

        orderBook.removeOrder(sell2);

        assertEquals(105.0, orderBook.getBestAsk());
    }

    @Test
    void shouldBecomeEmptyAfterRemovingLastOrder() {
        OrderBook orderBook = new OrderBook("AAPL");

        Order order = createBuyOrder("BUY001", 100.0);

        orderBook.addOrder(order);

        assertFalse(orderBook.isEmpty());

        orderBook.removeOrder(order);

        assertTrue(orderBook.isEmpty());
    }

    @Test
    void bestBidShouldBeNullWhenThereAreNoBuyOrders() {
        OrderBook orderBook = new OrderBook("AAPL");

        orderBook.addOrder(createSellOrder("SELL001", 102.0));

        assertNull(orderBook.getBestBid());
    }

    @Test
    void bestAskShouldBeNullWhenThereAreNoSellOrders() {
        OrderBook orderBook = new OrderBook("AAPL");

        orderBook.addOrder(createBuyOrder("BUY001", 100.0));

        assertNull(orderBook.getBestAsk());
    }
}
