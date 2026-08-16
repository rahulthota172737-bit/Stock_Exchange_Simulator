# Stock Exchange Simulator

A Java-based stock exchange simulator that models how buy and sell orders are handled in an electronic trading system.

I'm building this project step by step to understand how a real exchange works internally, rather than just making a basic stock market CRUD application.

## Current Progress

* Day 1 — Project setup and core order model
* Day 2 — Price-time priority order book
* Day 3 — Matching engine *(in progress)*

## What the project is about

The main idea is to simulate the flow of orders inside a stock exchange.

A trader submits an order, the exchange places it in the appropriate order book, and when compatible buy and sell orders are available, they are matched and converted into trades.

The core structure currently looks like:

```text
Order
  |
  v
OrderBook
  |
  +-------------------+
  |                   |
  v                   v
BuyOrderBook      SellOrderBook
  |                   |
  v                   v
TreeMap             TreeMap
  |                   |
  v                   v
PriceLevel         PriceLevel
  |                   |
  v                   v
Deque<Order>       Deque<Order>
```

## Price-Time Priority

The order book follows price-time priority.

For buy orders, the highest price gets priority.

```text
BUY

110
105
102
100
```

For sell orders, the lowest price gets priority.

```text
SELL

102
105
110
115
```

If multiple orders have the same price, the order that arrived first gets priority.

```text
₹100

Order A
Order B
Order C
```

Priority:

```text
A -> B -> C
```

This is implemented using `TreeMap` for price priority and `Deque` for FIFO ordering within a price level.

## Features implemented

### Order

The `Order` model currently contains:

* Order ID
* Trader ID
* Stock symbol
* Order side
* Order type
* Price
* Quantity
* Remaining quantity
* Timestamp
* Order status

Orders can also be filled and cancelled.

### Order Book

The order book currently supports:

* Adding orders
* Removing orders
* Finding the best bid
* Finding the best ask
* Calculating the spread
* FIFO ordering for orders at the same price
* Separate buy and sell books

### Order Repository

Orders are stored using:

```text
orderId -> Order
```

using a `HashMap`, giving average O(1) lookup.

Duplicate order IDs are rejected.

## Project structure

```text
src
├── main
│   └── java
│       └── com.stockexchange
│           ├── book
│           │   ├── BuyOrderBook.java
│           │   ├── OrderBook.java
│           │   ├── PriceLevel.java
│           │   └── SellOrderBook.java
│           │
│           ├── enums
│           ├── exception
│           ├── model
│           │   └── Order.java
│           │
│           ├── repository
│           │   └── OrderRepository.java
│           │
│           └── service
│
└── test
    └── java
        └── com.stockexchange
```

## Tech Stack

* Java
* Maven
* JUnit 5
* Git / GitHub
* IntelliJ IDEA

## Testing

The project currently has **61 passing tests**.

```text
Tests run: 61
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

The tests cover things such as:

* Order creation
* Order filling
* Order cancellation
* Price-level FIFO ordering
* Buy price priority
* Sell price priority
* Best bid
* Best ask
* Spread calculation
* Order removal
* Repository lookup
* Duplicate order IDs

## Performance

The main data structures were chosen with the expected operations in mind.

| Operation                | Data Structure | Complexity   |
| ------------------------ | -------------- | ------------ |
| Order lookup             | HashMap        | O(1) average |
| Add order to price level | Deque          | O(1)         |
| Remove first order       | Deque          | O(1)         |
| Find best price          | TreeMap        | O(log n)     |
| Insert price level       | TreeMap        | O(log n)     |

## Roadmap

The project is being developed in stages.

* [x] Project setup
* [x] Order model
* [x] Order validation
* [x] Price levels
* [x] Buy order book
* [x] Sell order book
* [x] Order book
* [x] Order repository
* [x] Unit tests
* [ ] Matching engine
* [ ] Partial order execution
* [ ] Trade generation
* [ ] Order cancellation through the exchange
* [ ] Multi-symbol exchange
* [ ] Market orders
* [ ] Thread-safe order processing
* [ ] Concurrent order submission
* [ ] Performance testing
* [ ] Market data
* [ ] Persistence
* [ ] Monitoring

## Why I built this

I wanted a project where I could apply Java, data structures, OOP, concurrency and system design to something that behaves like a real system.

The interesting part of this project isn't the UI. It's the logic behind the exchange — especially how orders are prioritized, matched and processed.

More features will be added as the project develops.

## Running the project

Clone the repository:

```bash
git clone <repository-url>
```

Go into the project:

```bash
cd stock-exchange-simulator
```

Run the tests:

```bash
mvn test
```

The project is being developed using Java 17.

## Status

🚧 **Work in progress**

This project is being developed incrementally, with each major feature added and tested before moving to the next stage.
