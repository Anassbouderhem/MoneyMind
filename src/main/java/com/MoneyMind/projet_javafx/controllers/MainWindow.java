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
    private DataStorage dataStorage;
    private Stage primaryStage;
    private Scene loginScene;

    // Update constructor
    public MainWindow(DataStorage dataStorage, Stage primaryStage, Scene loginScene) {
        this.dataStorage = dataStorage;
        this.primaryStage = primaryStage;
        this.loginScene = loginScene;
    }

    public Scene createScene() {
        final int WIDTH = 750;
        final int HEIGHT = 450;

        // Pass primaryStage and loginScene to BudgetInputTab
        BudgetInputTab budgetInputTab = new BudgetInputTab(dataStorage, primaryStage, loginScene);
        BudgetView budgetViewTab = new BudgetView(dataStorage);
        TransactionInput transactionInputTab = new TransactionInput(dataStorage, budgetInputTab);
        TransactionView transactionViewTab = new TransactionView(dataStorage);

        tp.getTabs().addAll(budgetInputTab, budgetViewTab, transactionInputTab, transactionViewTab);
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tp, WIDTH, HEIGHT, Color.LIGHTBLUE);
        scene.getStylesheets().add("stylesheet.css");

        return scene;
    }
}