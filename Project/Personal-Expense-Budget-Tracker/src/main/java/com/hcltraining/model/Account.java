package com.hcltraining.model;

import java.math.BigDecimal;

public class Account extends BaseEntity {

    private String name;
    private String type;
    private BigDecimal balance;

    public Account(int id, String name, String type,
                   BigDecimal balance) {
        super(id);
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

