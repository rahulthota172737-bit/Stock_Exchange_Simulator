package com.stockexchange.concurrency;

public final class ConcurrencyGuard {

    private ConcurrencyGuard() {

    }

    public static void verifyNonNegativeBalance(double balance) {
        if (balance < 0) {
            throw new IllegalStateException(
                    "Account balance cannot be negative: " + balance
            );
        }
    }

    public static void verifyNonNegativeHoldings(long holdings) {
        if (holdings < 0) {
            throw new IllegalStateException(
                    "Holdings cannot be negative: " + holdings
            );
        }
    }

    public static void verifyOrderQuantity(long quantity) {
        if (quantity <= 0) {
            throw new IllegalStateException(
                    "Order quantity must be greater than zero: " + quantity
            );
        }
    }

    public static void verifyRemainingQuantity(
            long originalQuantity,
            long remainingQuantity
    ) {
        if (remainingQuantity < 0) {
            throw new IllegalStateException(
                    "Remaining quantity cannot be negative: "
                            + remainingQuantity
            );
        }

        if (remainingQuantity > originalQuantity) {
            throw new IllegalStateException(
                    "Remaining quantity cannot exceed original quantity. "
                            + "Original=" + originalQuantity
                            + ", Remaining=" + remainingQuantity
            );
        }
    }

    public static void verifyTrade(
            long quantity,
            double price
    ) {
        if (quantity <= 0) {
            throw new IllegalStateException(
                    "Trade quantity must be greater than zero: " + quantity
            );
        }

        if (price <= 0) {
            throw new IllegalStateException(
                    "Trade price must be greater than zero: " + price
            );
        }
    }

    public static void verifyNotNull(
            Object object,
            String objectName
    ) {
        if (object == null) {
            throw new IllegalStateException(
                    objectName + " cannot be null"
            );
        }
    }
}