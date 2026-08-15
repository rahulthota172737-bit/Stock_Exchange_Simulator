package com.stockexchange.service;

import com.stockexchange.exception.InvalidOrderException;
import com.stockexchange.enums.OrderType;
import com.stockexchange.exception.InvalidPriceException;
import com.stockexchange.exception.InvalidQuantityException;
import com.stockexchange.model.Order;

public class OrderValidator {
    public void validate(Order order) {

        if (order == null) {
            throw new InvalidOrderException("Order cannot be null");
        }

        if (order.getQuantity() <= 0) {
            throw new InvalidQuantityException(
                    "Order quantity must be greater than zero"
            );
        }

        if (order.getSymbol() == null || order.getSymbol().isBlank()) {
            throw new InvalidOrderException(
                    "Stock symbol cannot be null or blank"
            );
        }

        if (order.getTraderId() == null || order.getTraderId().isBlank()) {
            throw new InvalidOrderException(
                    "Trader ID cannot be null or blank"
            );
        }

        if (order.getType() == OrderType.LIMIT && order.getPrice() <= 0) {
            throw new InvalidPriceException(
                    "Limit order price must be greater than zero"
            );
        }
    }
}
