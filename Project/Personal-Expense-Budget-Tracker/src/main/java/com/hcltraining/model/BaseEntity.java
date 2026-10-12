package com.hcltraining.model;

public abstract class BaseEntity {

    private final int id;

    protected BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
