package com.MoneyMind.projet_javafx.controllers;

import java.util.HashMap;

/**
 * This class is for storing all the accounts in a HashMap structure
 * Opens + Deletes accounts
 * Generates 10-digit account numbers per account
 * Checks if the account number and PIN are correct
 * Checks if the government ID is already existing in the database
 */
public class BankDatabase {

    static HashMap<String, BankAccount> accountDatabase = new HashMap<>();
    static String loggedAccount = "0000000000";
    // Account Numbers will have 10 digits. When account is registered or opened, new account will use
    //    the current availableAccountNumber and increment it for the next new account
    private static int availableAccountNum = 1234567890;

    // This method is for accepting new accounts and storing it in a database (HashMap structure)
    static void openAccount(String accountNumber,
                            String userId,
                            HashMap<String, String> name,
                            double initialDeposit,
                            long pin) {
        BankAccount newAcc = new BankAccount(accountNumber, userId, name, initialDeposit);
        newAcc.generatePIN(pin);
        accountDatabase.put(accountNumber, newAcc);
    }

    // every new account number will be unique,
    // thus loop is considered to check if the generated number is already used
    static String generateAccountNumber() {
        int num;
        do num = ++availableAccountNum; while (accountDatabase.containsKey(num));
        return String.valueOf(num);

    }

    static boolean checkAccount(String inputAccNum, String inputPIN) {
        if (accountDatabase.containsKey(inputAccNum)) {
            return accountDatabase.get(inputAccNum).getPIN().equals(inputPIN);
        }
        return false;
    }

    static boolean checkUserIdAlreadyExisting(String userInputUserID) {
        for (BankAccount account : accountDatabase.values()) {
            if (account.getUserID().equals(userInputUserID)) {
                return true;
            }
        }
        return false;
    }

    // Get Accessor methods for specific BankAccounts
    static String getAccountNumber() {
        return accountDatabase.get(loggedAccount).getAccountNumber();
    }

    static String getBalance() {
        return String.valueOf(accountDatabase.get(loggedAccount).getAccountBalance());
    }

    static String getFullName() {
        return accountDatabase.get(loggedAccount).getName();
    }

    static String getUserID() {
        return accountDatabase.get(loggedAccount).getUserID();
    }

    static void accountDeposit(double amount) {
        accountDatabase.get(loggedAccount).depositMoney(amount);
    }

    static void accountWithdraw(double amount) {
        accountDatabase.get(loggedAccount).withdrawMoney(amount);
    }

}

