package com.MoneyMind.projet_javafx.controllers;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

public class BudgetInputTab extends Tab {

    private final String fontDirectory = "/fonts/HankenGrotesk.ttf";
    private final Font font = Font.loadFont(getClass().getResourceAsStream(fontDirectory), 15);

    private final VBox mainVBox = new VBox(20);
    private final HBox topBar = new HBox(15);
    private final VBox totalBox = new VBox(5);
    private final Label totalLabel = new Label("Total Budget (MAD):");
    private final HBox totalInputBox = new HBox(8);
    private final TextField totalField = new TextField();
    private final Button totalButton = new Button("Set Total");
    private final Label totalDisplayLabel = new Label();

    private final Separator separator1 = new Separator();

    private final GridPane addCategoryPane = new GridPane();
    private final Label addCategoryLabel = new Label("Add Category");
    private final Label categoryNameLabel = new Label("Category:");
    private final ComboBox<String> categoryCombo = new ComboBox<>();
    private final Label amountLabel = new Label("Amount (MAD):");
    private final TextField amountField = new TextField();
    private final Button addButton = new Button("Add");

    private final Separator separator2 = new Separator();

    private final Label tableLabel = new Label("Allocated Categories");
    private final TableView<Budget> table = new TableView<>();
    private final ObservableList<Budget> budgetList = FXCollections.observableArrayList();

    private final HBox bottomBar = new HBox();
    private final Button quitButton = new Button("Quit");
    private final Button logoutButton = new Button("Logout");

    private DataStorage dataStorage;
    private double totalBudgetAmount = 0.0;
    private Stage primaryStage;
    private Scene loginScene;

    // Reference to TransactionInput for refreshing categories
    private TransactionInput transactionInput;

    public BudgetInputTab(DataStorage dataStorageInstance, Stage primaryStage, Scene loginScene) {
        this(dataStorageInstance, primaryStage, loginScene, null);
    }

    // Overloaded constructor to accept TransactionInput reference
    public BudgetInputTab(DataStorage dataStorageInstance, Stage primaryStage, Scene loginScene, TransactionInput transactionInput) {
        this.dataStorage = dataStorageInstance;
        this.primaryStage = primaryStage;
        this.loginScene = loginScene;
        this.transactionInput = transactionInput;
        setText("Budget Setup");

        setupFonts();
        setupLayout();
        setupTable();
        setupHandlers();
        loadCategories();
        loadBudgets();
        updateTotalDisplay();

        ScrollPane scrollPane = new ScrollPane(mainVBox);
        scrollPane.setFitToWidth(true);
        setContent(scrollPane);
    }

    private void setupFonts() {
        totalLabel.setFont(font);
        totalField.setFont(font);
        totalButton.setFont(font);
        totalDisplayLabel.setFont(font);
        addCategoryLabel.setFont(Font.font(font.getFamily(), 16));
        categoryNameLabel.setFont(font);
        categoryCombo.setStyle("-fx-font-size: 14px;");
        amountLabel.setFont(font);
        amountField.setFont(font);
        addButton.setFont(font);
        tableLabel.setFont(Font.font(font.getFamily(), 16));
        quitButton.setFont(font);
        logoutButton.setFont(font);
    }

    private void setupLayout() {
        // Top bar: Total budget
        totalInputBox.getChildren().addAll(totalField, totalButton);
        totalInputBox.setAlignment(Pos.CENTER_LEFT);
        totalBox.getChildren().addAll(totalLabel, totalInputBox, totalDisplayLabel);
        totalBox.setAlignment(Pos.CENTER_LEFT);
        categoryCombo.setEditable(true);
        topBar.getChildren().addAll(totalBox);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Add category pane
        addCategoryPane.setHgap(10);
        addCategoryPane.setVgap(10);
        addCategoryPane.setPadding(new Insets(10, 0, 10, 0));
        addCategoryPane.add(addCategoryLabel, 0, 0, 2, 1);
        addCategoryPane.add(categoryNameLabel, 0, 1);
        addCategoryPane.add(categoryCombo, 1, 1);
        addCategoryPane.add(amountLabel, 0, 2);
        addCategoryPane.add(amountField, 1, 2);
        addCategoryPane.add(addButton, 1, 3);

        // Table section
        VBox tableSection = new VBox(8, tableLabel, table);
        tableSection.setPadding(new Insets(10, 0, 0, 0));
        tableSection.setAlignment(Pos.TOP_LEFT);

        // Bottom bar
        bottomBar.getChildren().addAll(logoutButton, quitButton);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setSpacing(10);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));

        mainVBox.setPadding(new Insets(25, 40, 25, 40));
        mainVBox.getChildren().addAll(
                topBar,
                separator1,
                addCategoryPane,
                separator2,
                tableSection,
                bottomBar
        );
    }

    private void setupTable() {
        TableColumn<Budget, String> nameCol = new TableColumn<>("Category");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        TableColumn<Budget, Double> amountCol = new TableColumn<>("Initial Amount (MAD)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(120);

        TableColumn<Budget, Double> currentCol = new TableColumn<>("Current (MAD)");
        currentCol.setCellValueFactory(new PropertyValueFactory<>("current"));
        currentCol.setPrefWidth(120);

        TableColumn<Budget, Void> removeCol = new TableColumn<>("Remove");
        removeCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.setOnAction(e -> {
                    Budget b = getTableView().getItems().get(getIndex());
                    removeHandler(b);
                });
                btn.setFont(Font.font(font.getFamily(), 13));
                btn.setStyle("-fx-background-color: #e57373; -fx-text-fill: white;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.setItems(budgetList);
        table.getColumns().addAll(nameCol, amountCol, currentCol, removeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(220);
        table.setMinHeight(150);
        table.setMaxHeight(300);
        table.setPlaceholder(new Label("No categories allocated yet."));
    }

    private void setupHandlers() {
        addButton.setOnAction(e -> addHandler());
        quitButton.setOnAction(e -> Platform.exit());
        logoutButton.setOnAction(e -> handleLogout());
        totalButton.setOnAction(e -> {
            try {
                double newTotal = Double.parseDouble(totalField.getText());
                totalBudgetAmount = newTotal;
                dataStorage.getLoggedUser().setTotalLimit(totalBudgetAmount);
                dataStorage.updateUserTotalLimit(dataStorage.getLoggedUser(), totalBudgetAmount);
                updateTotalDisplay();
            } catch (NumberFormatException | SQLException ex) {
                showAlert("Invalid total budget.");
            }
        });
    }

    private void handleLogout() {
        dataStorage.setLoggedUser(null);
        if (primaryStage != null && loginScene != null) {
            primaryStage.setScene(loginScene);
        }
    }

    private void loadCategories() {
        try {
            categoryCombo.getItems().clear();
            categoryCombo.getItems().addAll(dataStorage.getAllCategories());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBudgets() {
        if (dataStorage.getLoggedUser() != null) {
            budgetList.setAll(dataStorage.getBudgets());
            dataStorage.getLoggedUser().setBudgets(budgetList);
            totalBudgetAmount = dataStorage.getLoggedUser().getTotalLimit();
        }
    }

    private void updateTotalDisplay() {
        totalDisplayLabel.setText("Available Budget: " + String.format("%.2f", totalBudgetAmount) + " MAD");
    }

    private void addHandler() {
        String baseName = categoryCombo.getEditor().getText().trim();
        if (baseName.isEmpty()) {
            showAlert("Please enter a category.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert("Amount must be positive.");
                return;
            }
            if (amount > totalBudgetAmount) {
                showAlert("Not enough total budget available.");
                return;
            }

            // Auto-increment name if duplicate in budget list
            String candidateName = baseName;
            int suffix = 2;
            Set<String> existingNames = budgetList.stream()
                    .map(Budget::getName)
                    .collect(Collectors.toSet());
            while (existingNames.contains(candidateName)) {
                candidateName = baseName + "-" + suffix;
                suffix++;
            }
            String name = candidateName;

            // Ensure the final category name exists in the DB and ComboBox
            if (!categoryCombo.getItems().contains(name)) {
                dataStorage.addCategory(name, "EXPENSE"); // Or let user choose type
                categoryCombo.getItems().add(name);
            }

            Budget newBudget = new Budget(name, amount);
            budgetList.add(newBudget);
            dataStorage.addBudget(dataStorage.getLoggedUser(), newBudget);

            // Subtract the allocated amount from the total budget
            totalBudgetAmount -= amount;
            dataStorage.getLoggedUser().setTotalLimit(totalBudgetAmount);
            dataStorage.updateUserTotalLimit(dataStorage.getLoggedUser(), totalBudgetAmount);

            amountField.clear();
            updateTotalDisplay();
            table.refresh();

            // Refresh TransactionInput's category ComboBox if reference is set
            if (transactionInput != null) {
                transactionInput.refreshCategoryComboBox();
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid amount entered.");
        } catch (SQLException e) {
            showAlert("Failed to add new category: " + e.getMessage());
        }
    }
    private void removeHandler(Budget toRemove) {
        budgetList.remove(toRemove);
        dataStorage.removeBudget(toRemove.getName());

        // Add the deallocated amount back to the total budget
        totalBudgetAmount += toRemove.getCurrent();
        dataStorage.getLoggedUser().setTotalLimit(totalBudgetAmount);
        try {
            dataStorage.updateUserTotalLimit(dataStorage.getLoggedUser(), totalBudgetAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateTotalDisplay();
        table.refresh();

        // Refresh TransactionInput's category ComboBox if reference is set
        if (transactionInput != null) {
            transactionInput.refreshCategoryComboBox();
        }
    }

    public void refreshBudgets() {
        loadBudgets();      // reloads the budgets from DataStorage
        table.refresh();    // refreshes the TableView display
        updateTotalDisplay();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    // Setter to inject TransactionInput reference after construction if needed
    public void setTransactionInput(TransactionInput transactionInput) {
        this.transactionInput = transactionInput;
    }
}