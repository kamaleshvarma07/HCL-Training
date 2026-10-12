package com.hcltraining.model;

public class Admin extends User {

    public Admin(int id) {
        super(id);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
