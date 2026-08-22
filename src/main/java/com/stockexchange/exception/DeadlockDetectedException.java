package com.stockexchange.exception;

public class DeadlockDetectedException extends RuntimeException {

    public DeadlockDetectedException(String message) {
        super(message);
    }

    public DeadlockDetectedException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
