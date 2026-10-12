package com.hcltraining.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.exception.InvalidTransactionAmountException;

public class Account extends BaseEntity {

    private String name;
    private String type;
    private final BigDecimal openingBalance;
    private BigDecimal balance;

    public Account(int id, String name, String type,
                   BigDecimal balance) {
        super(id);
        this.name = name;
        this.type = type;
        this.openingBalance = Objects.requireNonNull(balance, "balance");
        if (openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.balance = openingBalance;
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

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    /** Updates the current balance after the service validates the complete transaction ledger. */
    public void updateBalanceFromLedger(BigDecimal ledgerBalance) {
        Objects.requireNonNull(ledgerBalance, "ledgerBalance");
        if (ledgerBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A calculated account balance cannot be negative.");
        }
        balance = ledgerBalance;
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

