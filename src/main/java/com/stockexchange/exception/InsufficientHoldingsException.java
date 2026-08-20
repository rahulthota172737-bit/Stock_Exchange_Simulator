package com.stockexchange.exception;

public class InsufficientHoldingsException extends RuntimeException {

    public InsufficientHoldingsException(String message) {
        super(message);
    }
}
