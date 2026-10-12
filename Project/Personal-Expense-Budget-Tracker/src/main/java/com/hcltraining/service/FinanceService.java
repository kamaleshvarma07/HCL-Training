package com.hcltraining.service;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.model.Account;
import com.hcltraining.model.Transaction;

/** In-memory account and transaction operations for the console application. */
public class FinanceService {
    // ArrayList preserves the account display order and is efficient for menu iteration.
    private final List<Account> accounts = new ArrayList<>();
    // LinkedHashSet prevents duplicate IDs while keeping deterministic ID iteration order.
    private final Set<Integer> accountIds = new LinkedHashSet<>();
    // ArrayList preserves the ledger order used when replaying a balance.
    private final List<Transaction> transactions = new ArrayList<>();
    // LinkedHashSet prevents ID reuse, including IDs of transactions that were later deleted.
    private final Set<Integer> transactionIds = new LinkedHashSet<>();
    // ArrayDeque processes submitted transaction work in FIFO order without permitting nulls.
    private final Queue<Transaction> pendingTransactions = new ArrayDeque<>();
    private final Set<Integer> pendingTransactionIds = new LinkedHashSet<>();
    private int nextAccountId = 1;
    private int nextTransactionId = 1;

    public Account createAccount(String name, String type, BigDecimal openingBalance) {
        if (name == null || name.isBlank() || type == null || type.isBlank()) {
            throw new IllegalArgumentException("Account name and type are required.");
        }
        Objects.requireNonNull(openingBalance, "openingBalance");
        if (openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }

        Account account = new Account(nextAccountId++, name.trim(), type.trim(), openingBalance);
        if (!accountIds.add(account.getId())) {
            throw new IllegalStateException("Duplicate account ID generated: " + account.getId());
        }
        accounts.add(account);
        return account;
    }

    public Transaction createAndAddTransaction(int accountId, String type, String category,
                                               BigDecimal amount, String description)
            throws InsufficientFundsException {
        int transactionId = nextTransactionId();
        Transaction transaction = new Transaction(transactionId, accountId, type, category,
                amount, java.time.LocalDate.now(), description);
        addTransaction(transaction);
        nextTransactionId = transactionId + 1;
        return transaction;
    }

    public void addTransaction(Transaction transaction) throws InsufficientFundsException {
        enqueueTransaction(transaction);
        processPendingTransactions();
    }

    /** Queues a validated transaction; queued requests are applied in FIFO order. */
    public void enqueueTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "transaction");
        if (transaction.getId() <= 0) {
            throw new IllegalArgumentException("Transaction ID must be positive.");
        }
        if (transactionIds.contains(transaction.getId())
                || pendingTransactionIds.contains(transaction.getId())) {
            throw new IllegalArgumentException(
                    "Transaction ID already exists or is pending: " + transaction.getId());
        }
        requireAccount(transaction.getAccountId());
        requireSupportedType(transaction.getType());
        pendingTransactionIds.add(transaction.getId());
        pendingTransactions.offer(transaction);
    }

    /** Applies all queued operations until empty or the first business-rule failure. */
    public void processPendingTransactions() throws InsufficientFundsException {
        while (!pendingTransactions.isEmpty()) {
            Transaction next = pendingTransactions.peek();
            Account account = requireAccount(next.getAccountId());
            List<Transaction> candidateLedger = new ArrayList<>(transactions);
            candidateLedger.add(next);
            final BigDecimal balance;
            try {
                balance = calculateBalance(account, candidateLedger);
            } catch (InsufficientFundsException | IllegalArgumentException failure) {
                pendingTransactions.remove();
                pendingTransactionIds.remove(next.getId());
                throw failure;
            }

            pendingTransactions.remove();
            pendingTransactionIds.remove(next.getId());
            transactions.add(next);
            transactionIds.add(next.getId());
            account.updateBalanceFromLedger(balance);
        }
    }

    public void editTransaction(int transactionId, Transaction replacement)
            throws InsufficientFundsException {
        Objects.requireNonNull(replacement, "replacement");
        int index = indexOfTransaction(transactionId);
        Transaction current = transactions.get(index);
        if (replacement.getId() != transactionId) {
            throw new IllegalArgumentException("An edit must retain the transaction ID.");
        }
        if (replacement.getAccountId() != current.getAccountId()) {
            throw new IllegalArgumentException("An edit cannot move a transaction to another account.");
        }
        requireSupportedType(replacement.getType());

        List<Transaction> candidateLedger = new ArrayList<>(transactions);
        candidateLedger.set(index, replacement);
        Account account = requireAccount(current.getAccountId());
        BigDecimal balance = calculateBalance(account, candidateLedger);

        transactions.set(index, replacement);
        account.updateBalanceFromLedger(balance);
    }

    public void deleteTransaction(int transactionId) throws InsufficientFundsException {
        int index = indexOfTransaction(transactionId);
        Transaction removed = transactions.get(index);
        List<Transaction> candidateLedger = new ArrayList<>(transactions);
        candidateLedger.remove(index);
        Account account = requireAccount(removed.getAccountId());
        BigDecimal balance = calculateBalance(account, candidateLedger);

        transactions.remove(index);
        account.updateBalanceFromLedger(balance);
    }

    public Account getAccount(int accountId) {
        return requireAccount(accountId);
    }

    public Transaction getTransaction(int transactionId) {
        return transactions.get(indexOfTransaction(transactionId));
    }

    public List<Account> getAccounts() {
        return List.copyOf(accounts);
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    private int nextTransactionId() {
        int candidate = nextTransactionId;
        while (transactionIds.contains(candidate) || pendingTransactionIds.contains(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private Account requireAccount(int accountId) {
        if (!accountIds.contains(accountId)) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        for (Account account : accounts) {
            if (account.getId() == accountId) {
                return account;
            }
        }
        throw new IllegalStateException("Account ID index is inconsistent: " + accountId);
    }

    private int indexOfTransaction(int transactionId) {
        for (int index = 0; index < transactions.size(); index++) {
            if (transactions.get(index).getId() == transactionId) {
                return index;
            }
        }
        throw new IllegalArgumentException("Transaction not found: " + transactionId);
    }

    private BigDecimal calculateBalance(Account account, List<Transaction> ledger)
            throws InsufficientFundsException {
        BigDecimal balance = account.getOpeningBalance();
        for (Transaction transaction : ledger) {
            if (transaction.getAccountId() != account.getId()) {
                continue;
            }
            String type = transaction.getType().trim().toUpperCase(Locale.ROOT);
            if (type.equals("INCOME")) {
                balance = balance.add(transaction.getAmount());
            } else if (type.equals("EXPENSE")) {
                if (balance.compareTo(transaction.getAmount()) < 0) {
                    throw new InsufficientFundsException(transaction.getAmount(), balance);
                }
                balance = balance.subtract(transaction.getAmount());
            } else {
                throw new IllegalArgumentException("Transaction type must be INCOME or EXPENSE.");
            }
        }
        return balance;
    }

    private void requireSupportedType(String type) {
        if (type == null || !(type.trim().equalsIgnoreCase("INCOME")
                || type.trim().equalsIgnoreCase("EXPENSE"))) {
            throw new IllegalArgumentException("Transaction type must be INCOME or EXPENSE.");
        }
    }
}
