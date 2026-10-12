
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
        this.category = category;
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
