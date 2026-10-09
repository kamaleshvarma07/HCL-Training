
package com.hcltraining.model;

import java.math.BigDecimal;

public class Budget {

    private int id;
    private String category;
    private BigDecimal limit;
    private int month;
    private int year;

    public Budget(int id, String category, BigDecimal limit,
                  int month, int year) {
        this.id = id;
        this.category = category;
        this.limit = limit;
        this.month = month;
        this.year = year;
    }

    public int getId() {
        return id;
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
