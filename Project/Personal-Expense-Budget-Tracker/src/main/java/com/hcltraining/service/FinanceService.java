package com.hcltraining.service;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.time.LocalDate;
import java.time.YearMonth;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.model.Account;
import com.hcltraining.model.Transaction;
import com.hcltraining.model.Budget;
import com.hcltraining.model.RecurringRule;
import com.hcltraining.repository.InMemoryRepository;

/** In-memory account and transaction operations for the console application. */
public class FinanceService {
    // Generic repositories retain insertion order and provide direct ID lookup.
    private final InMemoryRepository<Account, Integer> accounts =
            new InMemoryRepository<>(Account::getId);
    // LinkedHashSet prevents duplicate IDs while keeping deterministic ID iteration order.
    private final Set<Integer> accountIds = new LinkedHashSet<>();
    // ArrayList preserves the ledger order used when replaying a balance.
    private final List<Transaction> transactions = new ArrayList<>();
    private final InMemoryRepository<Transaction, Integer> transactionsById =
            new InMemoryRepository<>(Transaction::getId);
    // LinkedHashSet prevents ID reuse, including IDs of transactions that were later deleted.
    private final Set<Integer> transactionIds = new LinkedHashSet<>();
    // ArrayDeque processes submitted transaction work in FIFO order without permitting nulls.
    private final Queue<Transaction> pendingTransactions = new ArrayDeque<>();
    private final Set<Integer> pendingTransactionIds = new LinkedHashSet<>();
    // Rules iterate in creation order; applied occurrences remain separately tracked.
    private final InMemoryRepository<RecurringRule, Integer> recurringRules =
            new InMemoryRepository<>(RecurringRule::getId);
    private final Set<OccurrenceKey> appliedOccurrences = new java.util.HashSet<>();
    // Budget views are sorted by year/month/category when read.
    private final InMemoryRepository<Budget, Integer> budgets =
            new InMemoryRepository<>(Budget::getId);
    private int nextAccountId = 1;
    private int nextTransactionId = 1;
    private int nextRecurringRuleId = 1;
    private int nextBudgetId = 1;

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
        accounts.save(account);
        return account;
    }

    /** Loads a fully validated account seed file before adding any rows to the ledger. */
    public List<Account> loadSeedAccounts(Reader source) throws IOException {
        List<Account> parsed = SeedDataLoader.readAccounts(source);
        for (Account account : parsed) {
            if (accountIds.contains(account.getId())) {
                throw new IOException("Seed account ID already exists: " + account.getId());
            }
        }
        for (Account account : parsed) {
            accountIds.add(account.getId());
            accounts.save(account);
            nextAccountId = Math.max(nextAccountId, account.getId() + 1);
        }
        return List.copyOf(parsed);
    }

    public Transaction createAndAddTransaction(int accountId, String type, String category,
                                               BigDecimal amount, String description)
            throws InsufficientFundsException {
        return createAndAddTransaction(accountId, type, category, amount,
                LocalDate.now(), description);
    }

    private Transaction createAndAddTransaction(int accountId, String type, String category,
                                                BigDecimal amount, LocalDate date, String description)
            throws InsufficientFundsException {
        int transactionId = nextTransactionId();
        Transaction transaction = new Transaction(transactionId, accountId, type, category,
                amount, date, description);
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
            transactionsById.save(next);
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
        transactionsById.save(replacement);
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
        transactionsById.deleteById(removed.getId());
        account.updateBalanceFromLedger(balance);
    }

    public Account getAccount(int accountId) {
        return requireAccount(accountId);
    }

    public Transaction getTransaction(int transactionId) {
        Transaction transaction = transactionsById.findById(transactionId).orElse(null);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found: " + transactionId);
        }
        return transaction;
    }

    public List<Account> getAccounts() {
        return accounts.findAll();
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
        Account account = accounts.findById(accountId).orElse(null);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        return account;
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

    public RecurringRule addRecurringRule(int accountId, String type, String category,
                                          BigDecimal amount, int dayOfMonth,
                                          LocalDate startsOn, String description) {
        requireAccount(accountId);
        RecurringRule rule = new RecurringRule(nextRecurringRuleId, accountId, type, category,
                amount, dayOfMonth, startsOn, description);
        recurringRules.save(rule);
        return rule;
    }

    public List<RecurringRule> getRecurringRules() {
        return recurringRules.findAll();
    }

    /** Applies each due monthly occurrence once. Failed transactions remain retryable. */
    public List<Transaction> applyRecurringTransactions(LocalDate throughDate)
            throws InsufficientFundsException {
        Objects.requireNonNull(throughDate, "throughDate");
        List<DueOccurrence> due = new ArrayList<>();
        YearMonth lastMonth = YearMonth.from(throughDate);
        for (RecurringRule rule : recurringRules.findAll()) {
            YearMonth month = YearMonth.from(rule.getStartsOn());
            while (!month.isAfter(lastMonth)) {
                LocalDate occurrence = rule.occurrenceIn(month);
                OccurrenceKey key = new OccurrenceKey(rule.getId(), occurrence);
                if (!occurrence.isBefore(rule.getStartsOn()) && !occurrence.isAfter(throughDate)
                        && !appliedOccurrences.contains(key)) {
                    due.add(new DueOccurrence(rule, occurrence, key));
                }
                month = month.plusMonths(1);
            }
        }
        due.sort(java.util.Comparator.comparing(DueOccurrence::date)
                .thenComparing(item -> item.rule().getId()));
        List<Transaction> created = new ArrayList<>();
        for (DueOccurrence item : due) {
            Transaction transaction = createAndAddTransaction(item.rule().getAccountId(),
                    item.rule().getType(), item.rule().getCategory(), item.rule().getAmount(),
                    item.date(), item.rule().getDescription() + " (recurring)");
            appliedOccurrences.add(item.key());
            created.add(transaction);
        }
        return List.copyOf(created);
    }

    public Budget setMonthlyBudget(String category, BigDecimal limit, YearMonth month) {
        Objects.requireNonNull(month, "month");
        Budget current = findBudget(normalizeCategory(category), month);
        Budget budget = new Budget(current == null ? nextBudgetId++ : current.getId(),
                category, limit, month.getMonthValue(), month.getYear());
        budgets.save(budget);
        return budget;
    }

    public List<Budget> getBudgets() {
        return budgets.findAll().stream()
                .sorted(java.util.Comparator.comparing(Budget::getYear)
                        .thenComparing(Budget::getMonth)
                        .thenComparing(budget -> normalizeCategory(budget.getCategory())))
                .toList();
    }

    /** Expense totals for the requested month, sorted by normalized category name. */
    public Map<String, BigDecimal> getMonthlyCategoryTotals(YearMonth month) {
        Objects.requireNonNull(month, "month");
        Map<String, BigDecimal> totals = new TreeMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType().equalsIgnoreCase("EXPENSE")
                    && YearMonth.from(transaction.getDate()).equals(month)) {
                String category = normalizeCategory(transaction.getCategory());
                totals.merge(category, transaction.getAmount(), BigDecimal::add);
            }
        }
        return java.util.Collections.unmodifiableMap(totals);
    }

    public BigDecimal getMonthlyCategoryTotal(String category, YearMonth month) {
        return getMonthlyCategoryTotals(month).getOrDefault(normalizeCategory(category), BigDecimal.ZERO);
    }

    /** Returns threshold percentages in strategy order (80 then 100) for this budget month. */
    public List<Integer> getTriggeredBudgetAlerts(String category, YearMonth month) {
        Budget budget = findBudget(normalizeCategory(category), month);
        if (budget == null) {
            throw new IllegalArgumentException("No budget for " + category + " in " + month + ".");
        }
        BigDecimal spent = getMonthlyCategoryTotal(category, month);
        List<Integer> alerts = new ArrayList<>();
        if (spent.compareTo(budget.getLimit().multiply(BigDecimal.valueOf(
                BudgetThresholds.WARNING_PERCENT)).movePointLeft(2)) >= 0) {
            alerts.add(BudgetThresholds.WARNING_PERCENT);
        }
        if (spent.compareTo(budget.getLimit()) >= 0) {
            alerts.add(BudgetThresholds.LIMIT_PERCENT);
        }
        return List.copyOf(alerts);
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }
        return category.trim().toLowerCase(Locale.ROOT);
    }

    private Budget findBudget(String normalizedCategory, YearMonth month) {
        return budgets.findAll().stream()
                .filter(budget -> budget.getYear() == month.getYear()
                        && budget.getMonth() == month.getMonthValue()
                        && normalizeCategory(budget.getCategory()).equals(normalizedCategory))
                .findFirst().orElse(null);
    }

    private static final class OccurrenceKey {
        private final int ruleId;
        private final LocalDate date;
        private OccurrenceKey(int ruleId, LocalDate date) {
            this.ruleId = ruleId;
            this.date = date;
        }
        @Override public boolean equals(Object other) {
            return other instanceof OccurrenceKey key && ruleId == key.ruleId && date.equals(key.date);
        }
        @Override public int hashCode() { return Objects.hash(ruleId, date); }
    }
    private static final class DueOccurrence {
        private final RecurringRule rule;
        private final LocalDate date;
        private final OccurrenceKey key;
        private DueOccurrence(RecurringRule rule, LocalDate date, OccurrenceKey key) {
            this.rule = rule;
            this.date = date;
            this.key = key;
        }
        RecurringRule rule() { return rule; }
        LocalDate date() { return date; }
        OccurrenceKey key() { return key; }
    }
}
