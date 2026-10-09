
package com.bank.model;

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

   
    public BankAccount(String accountHolder, double balance) {
    if (balance < 0) {
        throw new IllegalArgumentException(
            "Initial balance cannot be negative"
        );
    }

    this.accountHolder = accountHolder;
    this.balance = balance;
    totalAccounts++;
}


    
    // Deposit money into the account
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Deposit amount must be greater than zero"
            );
        }

        balance += amount;
    }

    
    // Withdraw money from the account
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Withdrawal amount must be greater than zero"
            );
        }

        if (amount > balance) {
            throw new IllegalArgumentException(
                "Insufficient balance"
            );
        }

        balance -= amount;
    }

    // Return the account holder's name
public String getAccountHolder() {
    return accountHolder;
}

// Return the total number of accounts created
public static int getTotalAccounts() {
    return totalAccounts;
}

// Return the current account balance
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

    return java.util.Objects.equals(
            accountHolder, other.accountHolder)
            && Double.compare(balance, other.balance) == 0;
}

@Override
public int hashCode() {
    return java.util.Objects.hash(accountHolder, balance);
}



}
