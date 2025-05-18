package com.MoneyMind.projet_javafx.controllers;

import java.util.HashMap;

/**
 * This class is for storing all the information of an individual Bank Account:
 * withdraw and deposit method in account balance
 * Transaction History
 *
 */
class BankAccount {

    private final String accountNumber;
    private final String Name;
    private final String userId;
    private double accountBalance;
    private long pin;

    // Constructor for BankAccount that takes in the account number, government ID, name, and initial deposit
    BankAccount(String accountNumber,
                String id,
                HashMap<String, String> name,
                double initialDeposit) {
        this.accountNumber = accountNumber;
        this.userId = id;
        String firstName = name.get("firstname");
        String lastName = name.get("lastname");
        this.accountBalance = initialDeposit;

        this.Name = firstName + " " + lastName;
    }

    void generatePIN(long pin) {
        this.pin = pin;
    }

    // Get accessor methods for variables
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return Name;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public String getUserID() {
        return userId;
    }

    public String getPIN() {
        return String.valueOf(pin);
    }

    // deposit and withdrawal simply add/subtract the amount from the account balance
    void depositMoney(double amount) {
        accountBalance += amount;
    }

    void withdrawMoney(double amount) {
        accountBalance -= amount;
    }

}
