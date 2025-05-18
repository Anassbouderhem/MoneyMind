package com.MoneyMind.projet_javafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BudgetList {
    private ObservableList<Budget> list;

    public BudgetList(ObservableList<Budget> list) {
        this.list = list;
    }

    public BudgetList() {
        this(FXCollections.observableArrayList());
    }

    public boolean addBudget(Budget b) {
        return list.add(b);
    }

    public boolean removeBudget(Budget b) {
        return list.remove(b);
    }

    public ObservableList<Budget> getList() {
        return list;
    }

    /**
     * gives sum of all Budget objects in constructed list
     * @return sum
     */
    public double getSum() {
        double sum = 0;
        for (Budget item : list) {
            sum += item.getAmount();
        }
        return sum;
    }

    public boolean isEmpty() {return list.isEmpty();}

    @Override
    public String toString() {return list.toString();}
}
