
package com.bank.app;

import com.bank.model.BankAccount;

public class BankAccountTest {

    public static void main(String[] args) {

        BankAccount account = new BankAccount("Arun", 5000.0);

        System.out.println("Account Holder: "
                + account.getAccountHolder());

        System.out.println("Initial Balance: "
                + account.getBalance());

        account.deposit(1000.0);
        System.out.println("After Deposit: "
                + account.getBalance());

        account.withdraw(2000.0);
        System.out.println("After Withdrawal: "
                + account.getBalance());
        try {
            account.withdraw(10000.0);
        } catch (IllegalArgumentException e) {
        System.out.println("Withdrawal rejected: " + e.getMessage());
        }

        System.out.println("Balance after rejected withdrawal: "
        + account.getBalance());

        System.out.println("Total Accounts: "
                + BankAccount.getTotalAccounts());
        
        BankAccount anotherAccount = new BankAccount("Arun", 4000.0);

        System.out.println("Accounts equal: "
                + account.equals(anotherAccount));

        System.out.println("Same hash code: "
                + (account.hashCode() == anotherAccount.hashCode()));
    }
}
