
package com.hcltraining.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private int id;
    private int accountId;
    private String type;
    private String category;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    public Transaction(int id, int accountId, String type,
                       String category, BigDecimal amount,
                       LocalDate date, String description) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
