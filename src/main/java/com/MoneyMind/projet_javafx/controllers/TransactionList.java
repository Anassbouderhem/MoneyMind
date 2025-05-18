package com.MoneyMind.projet_javafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionList {
    private ObservableList<Transaction> list;

    public TransactionList(ObservableList<Transaction> list) {
        this.list = list;
    }

    public TransactionList() {
        this(FXCollections.observableArrayList());
    }

    public boolean addTransaction(Transaction t) {
        return list.add(t);
    }

    public boolean removeTransaction(Transaction t) {
        return list.remove(t);
    }

    public int size() {
        return list.size();
    }

    public Transaction peekLast() {
        return list.get(size() - 1);
    }

    public Transaction peekFirst() {
        return list.get(0);
    }

    public ObservableList<Transaction> getList() {
        return list;
    }

    public boolean isEmpty() {return list.isEmpty();}

    @Override
    public String toString() {return list.toString();}
}
