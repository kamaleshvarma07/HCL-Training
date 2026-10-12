package com.hcltraining.model;

public class User extends BaseEntity {

    public User(int id) {
        super(id);
    }

    public String getRole() {
        return "USER";
    }
}
