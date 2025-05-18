package com.MoneyMind.projet_javafx.controllers;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<Budget> budgets;
    private List<Transaction> transactions;
    private double totalLimit;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.budgets = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.totalLimit = 0; // Initialize the total limit to 0 or any default value you want
    }

    // Getters and setters for username, password, budgets, and transactions

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Budget> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Getter and setter for totalLimit
    public double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(double totalLimit) {
        this.totalLimit = totalLimit;
    }
}
