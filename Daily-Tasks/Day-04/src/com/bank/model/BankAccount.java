package com.bank.model;

import java.util.Objects;

public class BankAccount {

    private String accountHolder;
    private double balance;
    private static int totalAccounts = 0;

    // First constructor
    public BankAccount() {
        this("Unknown", 0.0);
    }

    // Second constructor
    public BankAccount(String accountHolder) {
        this(accountHolder, 0.0);
    }

    // Third constructor
    public BankAccount(String accountHolder, double balance) {
        if (balance < 0) {
            System.out.println("Initial balance cannot be negative.");
            return;
        }

        this.accountHolder = accountHolder;
        this.balance = balance;
        totalAccounts++;
    }

    // Deposit money
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }

        balance += amount;
        return true;
    }

    // Withdraw money
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }

        balance -= amount;
        return true;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public static int getTotalAccounts() {
        return totalAccounts;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BankAccount other = (BankAccount) obj;

        return Objects.equals(accountHolder, other.accountHolder)
                && Double.compare(balance, other.balance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountHolder, balance);
    }
}
