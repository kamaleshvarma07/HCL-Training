package com.hcltraining.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** A monthly transaction template. The requested day is clamped to month end. */
public class RecurringRule extends BaseEntity {
    private final int accountId;
    private final String type;
    private final String category;
    private final BigDecimal amount;
    private final int dayOfMonth;
    private final LocalDate startsOn;
    private final String description;

    public RecurringRule(int id, int accountId, String type, String category,
                         BigDecimal amount, int dayOfMonth, LocalDate startsOn,
                         String description) {
        super(id);
        if (accountId <= 0 || dayOfMonth < 1 || dayOfMonth > 31) {
            throw new IllegalArgumentException("Account ID must be positive and day must be 1..31.");
        }
        if (type == null || !(type.trim().equalsIgnoreCase("INCOME")
                || type.trim().equalsIgnoreCase("EXPENSE"))) {
            throw new IllegalArgumentException("Transaction type must be INCOME or EXPENSE.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Recurring category is required.");
        }
        Account.requirePositiveAmount(amount);
        if (startsOn == null || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Start date and description are required.");
        }
        this.accountId = accountId;
        this.type = type.trim().toUpperCase(java.util.Locale.ROOT);
        this.category = category.trim();
        this.amount = amount;
        this.dayOfMonth = dayOfMonth;
        this.startsOn = startsOn;
        this.description = description.trim();
    }

    public int getAccountId() { return accountId; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public int getDayOfMonth() { return dayOfMonth; }
    public LocalDate getStartsOn() { return startsOn; }
    public String getDescription() { return description; }

    public LocalDate occurrenceIn(java.time.YearMonth month) {
        int day = Math.min(dayOfMonth, month.lengthOfMonth());
        return month.atDay(day);
    }
}
