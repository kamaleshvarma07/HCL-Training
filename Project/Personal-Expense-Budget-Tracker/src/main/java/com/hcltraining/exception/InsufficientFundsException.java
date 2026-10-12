package com.hcltraining.exception;

import java.math.BigDecimal;

/** Checked business-rule failure raised when an expense exceeds an account balance. */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(BigDecimal requested, BigDecimal available) {
        super("Insufficient funds: requested " + requested + ", available " + available + ".");
    }
}
