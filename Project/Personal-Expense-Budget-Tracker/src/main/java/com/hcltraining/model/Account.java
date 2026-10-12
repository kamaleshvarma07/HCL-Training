package com.hcltraining.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.exception.InvalidTransactionAmountException;

public class Account extends BaseEntity {

    private String name;
    private String type;
    private BigDecimal balance;

    public Account(int id, String name, String type,
                   BigDecimal balance) {
        super(id);
        this.name = name;
        this.type = type;
        this.balance = Objects.requireNonNull(balance, "balance");
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /** Applies a simulated expense after enforcing amount and available-funds rules. */
    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        requirePositiveAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(amount, balance);
        }
        balance = balance.subtract(amount);
    }

    public static void requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException(
                    "Transaction amount must be greater than zero.");
        }
    }
}

