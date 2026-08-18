package com.stockexchange.exception;

public class TraderNotFoundException extends RuntimeException {

    public TraderNotFoundException(String message) {
        super(message);
    }
}
