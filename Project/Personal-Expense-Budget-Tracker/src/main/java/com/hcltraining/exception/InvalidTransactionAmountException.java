package com.hcltraining.exception;

/** Signals that a transaction amount is missing or is not positive. */
public class InvalidTransactionAmountException extends IllegalArgumentException {
    public InvalidTransactionAmountException(String message) {
        super(message);
    }

    public InvalidTransactionAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
