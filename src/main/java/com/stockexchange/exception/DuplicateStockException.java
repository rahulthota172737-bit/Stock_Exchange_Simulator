package com.stockexchange.exception;

public class DuplicateStockException extends RuntimeException {

    public DuplicateStockException(String message) {
        super(message);
    }
}
