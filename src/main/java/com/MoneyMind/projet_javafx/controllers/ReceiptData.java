package com.MoneyMind.projet_javafx.controllers;

import javafx.beans.property.SimpleStringProperty;

import java.util.Random;

/**
 * This class is for storing all the information of an individual Receipt Transaction:
 * Transaction History Prelude
 *
 * @author Zul
 */
public class ReceiptData {
    final SimpleStringProperty date;
    final SimpleStringProperty time;
     final SimpleStringProperty location;
     final SimpleStringProperty receiptNumber;

     final SimpleStringProperty accountNumber;

     final SimpleStringProperty transaction;

     final SimpleStringProperty amount;
     final SimpleStringProperty currentBalance;


    /**
     * Constructor for ReceiptData that takes in the account number, transaction, and amount
     * @param accountNumber
     * @param transaction
     * @param amount amount of money deposited or withdrawn
     */
    public ReceiptData(String accountNumber,
                       String transaction,
                       String amount) {
        this.date = new SimpleStringProperty((String.valueOf(java.time.LocalDate.now())));
        this.time = new SimpleStringProperty((String.valueOf(java.time.LocalTime.now())));
        this.location = new SimpleStringProperty("COOP 'O CONNER CENTER");
        Random randomNum = new Random();
        this.receiptNumber = new SimpleStringProperty(String.format("%10d", randomNum.nextInt(100000000)));
        this.accountNumber = new SimpleStringProperty(accountNumber);
        this.transaction = new SimpleStringProperty(transaction);
        this.amount = new SimpleStringProperty(amount);
        this.currentBalance = new SimpleStringProperty(BankDatabase.getBalance());
    }


//getter methods for all receipt data

public String getDate() {
    return date.get();
}

public String getTime() {
    return time.get();
}

public String getLocation() {
    return location.get();
}

public String getReceiptNumber() {
    return receiptNumber.get();
}

public String getAccountNumber() {
    return accountNumber.get();
}

public String getTransaction() {
    return transaction.get();
}

public String getAmount() {
    return amount.get();
}

public String getCurrentBalance() {
    return currentBalance.get();
}
}