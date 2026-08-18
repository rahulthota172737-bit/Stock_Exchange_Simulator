package com.stockexchange.exception;

public class TradingHaltedException extends RuntimeException {

    public TradingHaltedException(String message) {
        super(message);
    }
}