package com.MoneyMind.projet_javafx.controllers;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private final List<User> users;
    private User loggedUser;
    private final String CSV_FILE_PATH = "data.csv"; // Define a constant file path

    public DataStorage() {
        users = new ArrayList<>();
        readFromCSV();
    }

    public void createBudget(String name, double amount, double limit) {
        if (loggedUser == null) {
            // Handle the case when there is no logged user.
            System.out.println("No logged user found.");
            return;
        }
        createBudgetForUser(loggedUser, name, amount, limit);
    }



    public void saveBudgetData() {
        writeToCSV();
    }

    public List<Budget> getBudgets() {
        return loggedUser != null ? loggedUser.getBudgets() : new ArrayList<>();
    }

    public double getTotalBudgetAmount() {
        return loggedUser != null ? loggedUser.getBudgets().stream().mapToDouble(Budget::getAmount).sum() : 0.0;
    }

    public void createTransaction(User user, String name, double amount, String category, LocalDate date) {
        Transaction newTransaction = new Transaction(name, amount, category, date);
        user.getTransactions().add(newTransaction);
        writeToCSV();
    }

    public void readFromCSV() {
        try (FileReader fileReader = new FileReader(CSV_FILE_PATH);
             CSVReader csvReader = new CSVReader(fileReader)) {

            String[] nextLine;
            User currentUser = null;
            while ((nextLine = csvReader.readNext()) != null) {
                String dataType = nextLine[0];

                switch (dataType) {
                    case "User" -> {
                        String username = nextLine[1];
                        String password = nextLine[2];
                        double totalLimit = Double.parseDouble(nextLine[3]);
                        currentUser = new User(username, password);
                        currentUser.setTotalLimit(totalLimit);
                        users.add(currentUser);
                    }
                    case "Budget" -> {
                        if (currentUser != null) {
                            String budgetName = nextLine[2];
                            double budgetAmount = Double.parseDouble(nextLine[3]);
                            double budgetCurrent = Double.parseDouble(nextLine[4]);
                            Budget budget = new Budget(budgetName, budgetAmount, budgetCurrent);
                            currentUser.getBudgets().add(budget);
                        }
                    }
                    case "Transaction" -> {
                        if (currentUser != null) {
                            String transactionName = nextLine[2];
                            double transactionAmount = Double.parseDouble(nextLine[3]);
                            String transactionCategory = nextLine[4];
                            LocalDate transactionDate = LocalDate.parse(nextLine[5]);
                            Transaction transaction = new Transaction(transactionName, transactionAmount, transactionCategory, transactionDate);
                            currentUser.getTransactions().add(transaction);
                        }
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }



    private void writeToCSV() {
        List<String[]> data = new ArrayList<>();

        for (User user : users) {
            String[] userData = {"User", user.getUsername(), user.getPassword(), Double.toString(user.getTotalLimit())};
            data.add(userData);

            for (Budget budget : user.getBudgets()) {
                String[] budgetData = {
                        "Budget",
                        user.getUsername(),
                        budget.getName(),
                        Double.toString(budget.getAmount()),
                        Double.toString(budget.getCurrent())
                };
                data.add(budgetData);
            }

            for (Transaction transaction : user.getTransactions()) {
                String[] transactionData = {
                        "Transaction",
                        user.getUsername(),
                        transaction.getName(),
                        Double.toString(transaction.getAmount()),
                        transaction.getCategory(),
                        transaction.getDate().toString()
                };
                data.add(transactionData);
            }
        }

        try (FileWriter fileWriter = new FileWriter(CSV_FILE_PATH, false);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            csvWriter.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public boolean checkUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                setLoggedUser(username);
                return true;
            }
        }
        return false;
    }

    public boolean checkUsernameAlreadyExisting(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void setLoggedUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                loggedUser = user;
                break;
            }
        }
    }

    public User getLoggedUser() {
        return loggedUser;
    }
    public void createBudgetForUser(User user, String name, double amount, double limit) {
        Budget newBudget = new Budget(name, amount, limit);
        user.getBudgets().add(newBudget);
        writeToCSV();
    }

    public void addUser(String username, String password) {
        User newUser = new User(username, password);
        users.add(newUser);

        writeToCSV();
    }


    public void removeBudget(User user, String budgetName) {
        user.getBudgets().removeIf(budget -> budget.getName().equals(budgetName));
        writeToCSV();
    }

    public void removeTransaction(User user, String transactionName) {
        user.getTransactions().removeIf(transaction -> transaction.getName().equals(transactionName));
        writeToCSV();
    }

    public void updateBudget(User user, String oldBudgetName, String newBudgetName, double newBudgetLimit) {
        for (Budget budget : user.getBudgets()) {
            if (budget.getName().equals(oldBudgetName)) {
                budget.setName(newBudgetName);
                budget.setAmount(newBudgetLimit);
                break;
            }
        }
        writeToCSV();
    }

    public void updateTransaction(User user, String oldTransactionName, String newTransactionName, double newTransactionAmount, String newTransactionCategory, LocalDate newTransactionDate) {
        for (Transaction transaction : user.getTransactions()) {
            if (transaction.getName().equals(oldTransactionName)) {
                transaction.setName(newTransactionName);
                transaction.setAmount(newTransactionAmount);
                transaction.setCategory(newTransactionCategory);
                transaction.setDate(newTransactionDate);
                break;
            }
        }
        writeToCSV();
    }

    public void updateUserTotalLimit(User user, double newTotalLimit) {
        user.setTotalLimit(newTotalLimit);
        writeToCSV();
    }

}

