package com.hcltraining.model;

import java.math.BigDecimal;

public class Account {

    private int id;
    private String name;
    private String type;
    private BigDecimal balance;

    public Account(int id, String name, String type,
                   BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public int getId() {
        return id;
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

