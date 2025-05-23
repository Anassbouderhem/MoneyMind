package com.MoneyMind.projet_javafx.controllers;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int user_id;
    private String username;
    private String password;
    private List<Budget> budgets = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private double totalLimit = 0.0;

    public User(int id, String username, String password) {
        this.user_id = id;
        this.username = username;
        this.password = password;

        this.budgets = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public User(int id, String username, String password, double totalLimit) {
        this.user_id = id;
        this.username = username;
        this.password = password;
        this.totalLimit=totalLimit;
        this.budgets = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }


    // Getters et setters
    public int getId() { return user_id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setId(int id) { this.user_id = id; }
    public void setPassword(String password) { this.password = password; }
    public List<Budget> getBudgets() { return budgets; }
    public void setBudgets(List<Budget> budgets) { this.budgets = budgets != null ? budgets : new ArrayList<>();}
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }
    public double getTotalLimit() { return totalLimit; }
    public void setTotalLimit(double totalLimit) { this.totalLimit = totalLimit; }
}