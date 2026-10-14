
package com.hcltraining.model;

import java.math.BigDecimal;

public class Budget extends BaseEntity {

    private String category;
    private BigDecimal limit;
    private int month;
    private int year;

    public Budget(int id, String category, BigDecimal limit,
                  int month, int year) {
        super(id);
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Budget category is required.");
        }
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget limit must be greater than zero.");
        }
        if (month < 1 || month > 12 || year < 1) {
            throw new IllegalArgumentException("Budget month or year is invalid.");
        }
        this.category = category.trim();
        this.limit = limit;
        this.month = month;
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
