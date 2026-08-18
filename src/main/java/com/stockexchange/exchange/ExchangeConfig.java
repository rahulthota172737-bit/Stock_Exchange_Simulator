package com.stockexchange.exchange;

import com.stockexchange.enums.OrderType;

import java.util.Set;

public class ExchangeConfig {

    private final long minimumOrderQuantity;
    private final long maximumOrderQuantity;
    private final Set<OrderType> supportedOrderTypes;

    public ExchangeConfig(
            long minimumOrderQuantity,
            long maximumOrderQuantity,
            Set<OrderType> supportedOrderTypes) {

        if (minimumOrderQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Minimum order quantity must be greater than zero"
            );
        }

        if (maximumOrderQuantity < minimumOrderQuantity) {
            throw new IllegalArgumentException(
                    "Maximum order quantity cannot be less than minimum quantity"
            );
        }

        if (supportedOrderTypes == null
                || supportedOrderTypes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Supported order types cannot be empty"
            );
        }

        this.minimumOrderQuantity = minimumOrderQuantity;
        this.maximumOrderQuantity = maximumOrderQuantity;
        this.supportedOrderTypes = Set.copyOf(supportedOrderTypes);
    }

    public long getMinimumOrderQuantity() {
        return minimumOrderQuantity;
    }

    public long getMaximumOrderQuantity() {
        return maximumOrderQuantity;
    }

    public Set<OrderType> getSupportedOrderTypes() {
        return supportedOrderTypes;
    }
}