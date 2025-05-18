package com.MoneyMind.projet_javafx.controllers;

import javafx.beans.property.*;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable
{
    private StringProperty name;
    private DoubleProperty amount;
    private StringProperty category;
    private ObjectProperty<LocalDate> date;

    public Transaction(String name, double amount, String category, LocalDate date)
    {

        this.name = new SimpleStringProperty(name);
        this.amount = new SimpleDoubleProperty(amount);
        this.category = new SimpleStringProperty(category);
        this.date = new SimpleObjectProperty<>(date);
    }

    public Transaction(String name, double amount, String category)
    {
        this(name, amount, category, LocalDate.now());
    }

    public Transaction(Transaction toClone) {
        this.name = toClone.name;
        this.amount = toClone.amount;
        this.category = toClone.category;
        this.date = toClone.date;
    }

    public Transaction() {

    }

    public StringProperty nameProperty  () { return name; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty categoryProperty () { return category; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    public String getName() { return this.nameProperty().get(); }
    public void setName(String name) { this.name.set(name); }

    public double getAmount() { return this.amountProperty().get(); }
    public void setAmount(double amount) { this.amount.set(amount); }

    public String getCategory() { return this.categoryProperty().get(); }
    public void setCategory(String category) { this.category.set(category); }

    public LocalDate getDate() { return this.dateProperty().get(); }
    public void setDate(LocalDate date) { this.date.set(date); }

    @Override
    public String toString() {
        return "Transaction{" +
            "name='" + name + '\'' +
            ", amount=" + amount +
            ", category=" + category +
            ", date=" + date +
            '}';
    }

}
