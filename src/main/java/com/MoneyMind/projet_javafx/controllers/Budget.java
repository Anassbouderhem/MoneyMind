package com.MoneyMind.projet_javafx.controllers;

public class Budget {
    private String name;
    private double amount;
    private double current;
    private String category;


    public Budget(String name, double amount, double current) {
        this.name = name;
        this.amount = amount;
        this.current = current;
    }
    public Budget(String name, double amount) {
        this.name = name;
        this.amount = amount;
        this.current = 0;
    }

    public String getName() {
        return name;
    }

    public double getCurrent() {
        return current;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getCategory() {
        return category;
    }
}
