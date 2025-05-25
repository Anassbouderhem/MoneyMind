package com.MoneyMind.projet_javafx.controllers;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;



public class MainWindow {

    TabPane tp = new TabPane();
    private BudgetInputTab budgetInputTab;
    private TransactionInput transactionInputTab;
    private DataStorage dataStorage;
    private Stage primaryStage;
    private Scene loginScene;

    public MainWindow(DataStorage dataStorage, Stage primaryStage, Scene loginScene) {
        this.dataStorage = dataStorage;
        this.primaryStage = primaryStage;
        this.loginScene = loginScene;
    }

    public Scene createScene() {
        final int WIDTH = 750;
        final int HEIGHT = 450;

        // 1. Create TransactionInput first (with null for budgetInputTab for now)
        transactionInputTab = new TransactionInput(dataStorage, null);

        // 2. Create BudgetInputTab and pass the TransactionInput reference
        budgetInputTab = new BudgetInputTab(dataStorage, primaryStage, loginScene, transactionInputTab);

        // 3. Now set the BudgetInputTab reference in TransactionInput (if you need two-way communication)
        transactionInputTab.setBudgetInputTab(budgetInputTab);

        BudgetView budgetViewTab = new BudgetView(dataStorage);
        TransactionView transactionViewTab = new TransactionView(dataStorage);
        TransferMoneyTab transferMoneyTab = new TransferMoneyTab(dataStorage);
        tp.getTabs().addAll(budgetInputTab, budgetViewTab, transactionInputTab, transactionViewTab, transferMoneyTab);
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tp, WIDTH, HEIGHT, Color.LIGHTBLUE);
        scene.getStylesheets().add("stylesheet.css");

        return scene;
    }
}