package com.MoneyMind.projet_javafx.controllers;

import com.MoneyMind.projet_javafx.assistant_AI.AIAssistant;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TransactionInput extends Tab {

    String fontDirectory = "/fonts/HankenGrotesk.ttf";

    // visual components
    GridPane outerGrid = new GridPane();
    GridPane inputPanel = new GridPane();
    VBox nameBox = new VBox();
    VBox amountBox = new VBox();
    VBox categoryBox = new VBox();
    VBox dateBox = new VBox();
    HBox purchaseBox = new HBox();
    GridPane bottomGrid = new GridPane();
    HBox totalBox = new HBox();
    HBox removeBox = new HBox();
    HBox quitBox = new HBox();
    VBox totalQuitBox = new VBox();
    HBox addTitleBox = new HBox();
    HBox pTitleBox = new HBox();
    VBox tableBox = new VBox();

    // labels
    Label addItemLabel = new Label("Add Transaction");
    Label purchasedLabel = new Label("Transactions");
    Label nameLabel = new Label("Name");
    Label categoryLabel = new Label("Category");
    Label amountLabel = new Label("Amount");
    Label dateLabel = new Label("Date");
    Label totalLabel = new Label("Total ");

    // widgets
    TextField nameField = new TextField();
    TextField amountField = new TextField();
    DatePicker dateField = new DatePicker(LocalDate.now());
    ComboBox<String> categoryComboBox = new ComboBox<>();
    Button purchaseButton = new Button("Add");
    TextField totalField = new TextField();
    Button quitButton = new Button("Quit");

    // radio buttons for transaction type
    private final ToggleGroup typeGroup = new ToggleGroup();
    private final RadioButton incomeRadio = new RadioButton("Income");
    private final RadioButton expenseRadio = new RadioButton("Expense");

    // filter controls
    private ComboBox<String> filterCategoryCombo = new ComboBox<>();
    private DatePicker filterMonthPicker = new DatePicker();

    // lists
    ObservableList<Transaction> tableData; // hold purchases

    // tableview
    TableView<Transaction> table;
    private BudgetInputTab budgetInputTab;
    private DataStorage dataStorage;

    public TransactionInput(DataStorage dataStorage, BudgetInputTab budgetInputTab) {
        this.budgetInputTab = budgetInputTab;
        this.dataStorage = dataStorage;
        this.tableData = FXCollections.observableArrayList();
        this.table = new TableView<>(tableData);
        loadInitialData();
        start();
    }

    private void loadInitialData() {
        if (dataStorage.getLoggedUser() != null) {
            List<Transaction> dbTransactions = dataStorage.getTransactions();
            tableData.setAll(dbTransactions);
            updateTotal();
        }
    }

    private void updateTotal() {
        double totalCost = 0;
        for (Transaction t : table.getItems()) {
            totalCost += t.getAmount();
        }
        String str = String.format("$ %.2f", totalCost);
        totalField.setText(str);
    }

    private ObservableList<String> getBudgetedCategories() {
        ObservableList<String> budgetedCategories = FXCollections.observableArrayList();
        if (dataStorage.getLoggedUser() != null) {
            System.out.println("[DEBUG] Logged user: " + dataStorage.getLoggedUser().getUsername());
            List<Budget> budgets = dataStorage.getLoggedUser().getBudgets();
            System.out.println("[DEBUG] User budgets count: " + (budgets != null ? budgets.size() : 0));
            if (budgets != null) {
                for (Budget b : budgets) {
                    System.out.println("[DEBUG] Budget found: " + b.getName() + " (current: " + b.getCurrent() + ")");
                    budgetedCategories.add(b.getName());
                }
            }
        } else {
            System.out.println("[DEBUG] No logged user found.");
        }
        System.out.println("[DEBUG] Budgeted categories for ComboBox: " + budgetedCategories);
        return budgetedCategories;
    }

    public void start() {
        // Only allow transactions for categories with a defined budget
        ObservableList<String> budgetedCategories = getBudgetedCategories();
        System.out.println("[DEBUG] Setting ComboBox items: " + budgetedCategories);
        categoryComboBox.getItems().setAll(budgetedCategories);

        // Setup radio buttons for transaction type
        incomeRadio.setToggleGroup(typeGroup);
        expenseRadio.setToggleGroup(typeGroup);
        expenseRadio.setSelected(true); // Default to expense

        HBox typeBox = new HBox(10, incomeRadio, expenseRadio);
        typeBox.setAlignment(Pos.CENTER);

        // Setup filter controls
        filterCategoryCombo.getItems().clear();
        filterCategoryCombo.getItems().add("All");
        filterCategoryCombo.getItems().addAll(budgetedCategories);
        filterCategoryCombo.setValue("All");
        filterMonthPicker.setPromptText("Filter by Month");

        HBox filterBox = new HBox(10, new Label("Category:"), filterCategoryCombo, new Label("Month:"), filterMonthPicker);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        gridStyling();
        allStyling();
        setButtonHandlers();
        createToolTips();
        populateGrids(typeBox, filterBox);
        tableSetup();
        addAmountRegex();
        bindTotalField();

        setText("Transactions");
        setContent(outerGrid);
        setupAIComponents();

        // Add filter listeners
        filterCategoryCombo.setOnAction(e -> applyFilters());
        filterMonthPicker.setOnAction(e -> applyFilters());
    }

    private void addAmountRegex() {
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || !newValue.matches("-?\\d*(\\.\\d*)?")) {
                amountField.setText(oldValue);
            }
        });
    }

    private void bindTotalField() {
        tableData.addListener((ListChangeListener<Transaction>) change -> {
            updateTotal();
        });
    }

    private void tableSetup() {
        TableColumn<Transaction, String> itemCol = new TableColumn<>("Name");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemCol.setStyle("-fx-alignment: CENTER;");
        itemCol.prefWidthProperty().bind(table.widthProperty().multiply(0.28));
        itemCol.setResizable(false);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setStyle("-fx-alignment: CENTER;");
        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        amountCol.setResizable(false);

        amountCol.setCellFactory(column -> new TextFieldTableCell<Transaction, Double>(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return "$" + value;
            }
            @Override
            public Double fromString(String value) {
                return null;
            }
        }));

        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setStyle("-fx-alignment: CENTER;");
        catCol.prefWidthProperty().bind(table.widthProperty().multiply(0.26));
        catCol.setResizable(false);

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date Ordered");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.21));
        dateCol.setResizable(false);

        TableColumn delCol = new TableColumn();
        delCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        delCol.setResizable(false);
        delCol.setStyle("-fx-alignment: CENTER;");
        Image delete = new Image("delete.png");

        delCol.setCellFactory(ButtonTableCell.<Transaction>forTableColumn(delete, (Transaction Transaction) -> {
            removeHandler(Transaction);
            return Transaction;
        }));
        table.setSelectionModel(null);
        table.setEditable(false);
        table.getColumns().addAll(itemCol, amountCol, catCol, dateCol, delCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setButtonHandlers() {
        purchaseButton.setOnAction(e -> addTransactionHandler());
        quitButton.setOnAction(e -> quitHandler());
        purchaseButton.disableProperty().bind(
                Bindings.or(
                        nameField.textProperty().isEmpty(),
                        Bindings.or(
                                amountField.textProperty().isEmpty(),
                                categoryComboBox.valueProperty().isNull()
                        )
                )
        );
    }

    private void populateGrids(HBox typeBox, HBox filterBox) {
        addTitleBox.getChildren().add(addItemLabel);
        outerGrid.add(addTitleBox, 0, 0);
        outerGrid.add(inputPanel, 0, 1);
        inputPanel.add(nameBox, 0, 0);
        nameBox.getChildren().addAll(nameLabel, nameField);

        inputPanel.add(amountBox, 1, 0);
        amountBox.getChildren().addAll(amountLabel, amountField);

        inputPanel.add(categoryBox, 0, 1);
        categoryBox.getChildren().addAll(categoryLabel, categoryComboBox);

        inputPanel.add(dateBox, 1, 1);
        dateBox.getChildren().addAll(dateLabel, dateField);

        // Add radio buttons for transaction type
        inputPanel.add(typeBox, 0, 2, 2, 1);

        inputPanel.add(purchaseBox, 0, 3, 2, 1);
        purchaseBox.getChildren().add(purchaseButton);

        pTitleBox.getChildren().add(purchasedLabel);
        outerGrid.add(pTitleBox, 1, 0);

        // Add filterBox above the table
        VBox tableSection = new VBox(8, filterBox, table);
        tableBox.getChildren().clear();
        tableBox.getChildren().add(tableSection);

        outerGrid.add(tableBox, 1, 1);
        outerGrid.add(bottomGrid, 1, 2);
        bottomGrid.add(removeBox, 0, 0);
        totalBox.getChildren().addAll(totalLabel, totalField);

        quitBox.getChildren().add(quitButton);

        totalQuitBox.getChildren().addAll(totalBox, quitBox);

        bottomGrid.add(totalQuitBox, 1, 0);
    }

    private void allStyling() {
        nameBox.setAlignment(Pos.CENTER);
        amountBox.setAlignment(Pos.TOP_CENTER);
        categoryBox.setAlignment(Pos.TOP_CENTER);
        dateBox.setAlignment(Pos.TOP_CENTER);
        purchaseBox.setAlignment(Pos.BOTTOM_CENTER);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        removeBox.setAlignment(Pos.TOP_LEFT);
        quitBox.setAlignment(Pos.TOP_RIGHT);
        totalQuitBox.setAlignment(Pos.TOP_RIGHT);
        totalBox.setMargin(totalLabel, new Insets(0,5,0,0));

        Font titlefont = Font.loadFont(getClass().getResourceAsStream(fontDirectory), 18);
        Font font = Font.loadFont(getClass().getResourceAsStream(fontDirectory), 14);
        addItemLabel.setFont(titlefont);
        addTitleBox.setMargin(addItemLabel, new Insets(0,0,12,0));
        addTitleBox.setAlignment(Pos.BOTTOM_CENTER);
        purchasedLabel.setFont(titlefont);

        pTitleBox.setAlignment(Pos.BOTTOM_CENTER);
        pTitleBox.setMargin(purchasedLabel, new Insets(0,0,12,0));

        nameLabel.setFont(font);
        amountLabel.setFont(font);
        categoryLabel.setFont(font);
        dateLabel.setFont(font);
        totalLabel.setFont(font);
        totalLabel.setAlignment(Pos.CENTER_RIGHT);

        nameField.setMaxWidth(125);
        nameField.setPrefWidth(125);
        amountField.setMaxWidth(100);
        amountField.setPromptText("$");
        dateField.setMaxWidth(100);
        purchaseButton.setPrefWidth(60);
        totalField.setPromptText("$");
        totalField.setEditable(false);
        totalField.setMaxWidth(75);
        quitButton.setPrefWidth(75);
        purchaseButton.setFont(font);
        amountField.setFont(font);
        quitButton.setFont(font);
        String style = "-fx-font-family: '" + font.getFamily() + "'; "
                + "-fx-font-size: " + font.getSize() + "px;";
        dateField.setStyle(style);
        totalField.setFont(font);
        totalQuitBox.setMargin(totalBox, new Insets(3,0,10,0));

        outerGrid.setAlignment(Pos.CENTER);
        outerGrid.setPadding(new Insets(15, 10, 10, 10));
        outerGrid.setHgap(10);
        inputPanel.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-color: black;"
                + "-fx-padding: 30;"
                + "-fx-background-color: #7AE1B5");
        tableBox.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-color: black;"
                + "-fx-background-color: LIGHTGREY;");
        bottomGrid.setStyle("-fx-background-color: transparent;");
    }

    private void gridStyling() {
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(40);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(60);

        RowConstraints row1 = new RowConstraints();
        row1.setMaxHeight(25);
        row1.setPrefHeight(0);
        row1.setVgrow(Priority.SOMETIMES);

        RowConstraints row2 = new RowConstraints();
        row2.setMaxHeight(261);
        row2.setMinHeight(10);
        row2.setPrefHeight(261);
        row2.setVgrow(Priority.SOMETIMES);

        RowConstraints row3 = new RowConstraints();
        row3.setMinHeight(10);
        row3.setPrefHeight(30);
        row3.setVgrow(Priority.SOMETIMES);

        outerGrid.getColumnConstraints().addAll(column1, column2);
        outerGrid.getRowConstraints().addAll(row1, row2, row3);

        inputPanel.setVgap(50);
        inputPanel.setHgap(10);

        ColumnConstraints innerColumn1 = new ColumnConstraints();
        innerColumn1.setHgrow(Priority.SOMETIMES);
        innerColumn1.setMinWidth(10);
        innerColumn1.setPrefWidth(50);

        ColumnConstraints innerColumn2 = new ColumnConstraints();
        innerColumn2.setHgrow(Priority.SOMETIMES);
        innerColumn2.setMinWidth(10);
        innerColumn2.setPrefWidth(50);

        RowConstraints innerRow1 = new RowConstraints();
        innerRow1.setMaxHeight(66.5);
        innerRow1.setMinHeight(10);
        innerRow1.setPrefHeight(40.5);
        innerRow1.setVgrow(Priority.SOMETIMES);

        RowConstraints innerRow2 = new RowConstraints();
        innerRow2.setMaxHeight(120.5);
        innerRow2.setMinHeight(10);
        innerRow2.setPrefHeight(57.0);
        innerRow2.setVgrow(Priority.SOMETIMES);

        RowConstraints innerRow3 = new RowConstraints();
        innerRow3.setMaxHeight(115.5);
        innerRow3.setMinHeight(10);
        innerRow3.setPrefHeight(103.5);
        innerRow3.setVgrow(Priority.SOMETIMES);

        inputPanel.getColumnConstraints().addAll(innerColumn1, innerColumn2);
        inputPanel.getRowConstraints().addAll(innerRow1, innerRow2, innerRow3);

        bottomGrid.setPadding(new Insets(10, 0, 0, 0));
        bottomGrid.setHgap(10);
        bottomGrid.setVgap(10);
        bottomGrid.setAlignment(Pos.TOP_RIGHT);

        ColumnConstraints bottomColumn1 = new ColumnConstraints();
        bottomColumn1.setPrefWidth(235);
        ColumnConstraints bottomColumn2 = new ColumnConstraints();
        bottomColumn2.setPrefWidth(235);

        RowConstraints bottomRow1 = new RowConstraints();
        bottomRow1.setPrefHeight(100);
        RowConstraints bottomRow2 = new RowConstraints();
        bottomRow2.setPrefHeight(100);

        bottomGrid.getColumnConstraints().addAll(bottomColumn1, bottomColumn2);
        bottomGrid.getRowConstraints().addAll(bottomRow1, bottomRow2);
    }

    private void createToolTips(){
        nameField.setTooltip(new Tooltip("Enter transaction name"));
        amountField.setTooltip(new Tooltip("Enter transaction amount"));
        categoryComboBox.setTooltip(new Tooltip("Select a category relevant to the transaction"));
        dateField.setTooltip(new Tooltip("Enter transaction date"));
        purchaseButton.setTooltip(new Tooltip("Add transaction to the table"));
        table.setTooltip(new Tooltip("Transaction will appear here"));
        totalField.setTooltip(new Tooltip("The total amount of all transactions"));
        quitButton.setTooltip(new Tooltip("Close the application"));
    }

    private TransactionView createChartTab() {
        TransactionView TransactionView = new TransactionView(dataStorage);
        TransactionView.setTooltip(new Tooltip("Shows a breakdown of the given purchased items"));
        return TransactionView;
    }

    private void addTransactionHandler() {
        try {
            LocalDate date = dateField.getValue();
            double enteredAmount = Double.parseDouble(amountField.getText());
            String category = categoryComboBox.getValue();
            String name = nameField.getText();

            System.out.println("[DEBUG] Attempting to add transaction: name=" + name + ", amount=" + enteredAmount + ", category=" + category + ", date=" + date);

            // Prevent transaction if category is not budgeted
            if (category == null || category.isEmpty()) {
                System.out.println("[DEBUG] No category selected or category is empty.");
                showAlert("Please define a budget for at least one category before adding transactions.");
                return;
            }

            boolean isExpense = expenseRadio.isSelected();
            double amount = isExpense ? -Math.abs(enteredAmount) : Math.abs(enteredAmount);

            // Check if expense exceeds category budget
            if (isExpense) {
                Budget selectedBudget = null;
                for (Budget b : dataStorage.getLoggedUser().getBudgets()) {
                    if (b.getName().equals(category)) {
                        selectedBudget = b;
                        break;
                    }
                }
                if (selectedBudget != null) {
                    System.out.println("[DEBUG] Selected budget for category: " + selectedBudget.getName() + ", current=" + selectedBudget.getCurrent());
                    if (Math.abs(amount) > selectedBudget.getCurrent()) {
                        System.out.println("[DEBUG] Expense exceeds current budget!");
                        showAlert("Expense exceeds the current budget for this category!");
                        return;
                    }
                } else {
                    System.out.println("[DEBUG] No budget found for selected category!");
                }
            }

            // Add transaction to database and update in-memory
            dataStorage.addTransaction(dataStorage.getLoggedUser(), name, amount, category, date);

            // Update in-memory Budget object and UI
            for (Budget b : dataStorage.getLoggedUser().getBudgets()) {
                if (b.getName().equals(category)) {
                    if (isExpense) {
                        b.setCurrent(b.getCurrent() - Math.abs(amount));
                    } else {
                        b.setCurrent(b.getCurrent() + Math.abs(amount));
                    }
                    System.out.println("[DEBUG] Updated budget '" + b.getName() + "' new current: " + b.getCurrent());
                    break;
                }
            }
            if (budgetInputTab != null) {
                budgetInputTab.refreshBudgets();
            }

            Transaction newT = new Transaction(name, amount, category, date);
            tableData.add(newT);
            applyFilters();
            updateTotal();

            amountField.setText(null);

        } catch (SQLException e) {
            System.out.println("[DEBUG] SQLException: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Failed to save transaction: " + e.getMessage()).show();
        } catch (NumberFormatException e) {
            System.out.println("[DEBUG] NumberFormatException: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Invalid amount format").show();
        }
    }

    private void removeHandler(Transaction toRemove) {
        tableData.remove(toRemove);
        applyFilters();
        dataStorage.removeTransaction(dataStorage.getLoggedUser(), toRemove.getName());
    }

    private void applyFilters() {
        String selectedCategory = filterCategoryCombo.getValue();
        LocalDate selectedMonth = filterMonthPicker.getValue();

        table.setItems(tableData.filtered(t -> {
            boolean matchesCategory = selectedCategory == null || selectedCategory.equals("All") || t.getCategory().equals(selectedCategory);
            boolean matchesMonth = selectedMonth == null ||
                    (t.getDate() != null && t.getDate().getMonth() == selectedMonth.getMonth() && t.getDate().getYear() == selectedMonth.getYear());
            return matchesCategory && matchesMonth;
        }));
        updateTotal();
    }

    private void setupAIComponents() {
        Button aiButton = new Button("Obtenir des conseils");
        TextArea adviceArea = new TextArea();
        adviceArea.setEditable(false);
        adviceArea.setWrapText(true);
        adviceArea.setPrefHeight(150);
        adviceArea.setFont(Font.font("Arial", 13));

        aiButton.setPrefWidth(180);

        VBox aiBox = new VBox(10, aiButton, adviceArea);
        aiBox.setAlignment(Pos.CENTER);
        aiBox.setPadding(new Insets(10));

        aiButton.setOnAction(e -> {
            try {
                AIAssistant assistant = new AIAssistant(dataStorage);
                String advice = assistant.generateMonthlyAdvice(dataStorage.getLoggedUser().getId());
                adviceArea.setText(advice);
            } catch (SQLException ex) {
                adviceArea.setText("Erreur lors de la génération des conseils: " + ex.getMessage());
            }
        });
        outerGrid.add(aiBox, 0, 2);
    }

    private void quitHandler() {
        dataStorage.close();
        Platform.exit();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
    /**
     * Call this method after adding a new budget/category to refresh the ComboBox.
     * You can call it from BudgetInputTab after a new budget is added.
     */
    public void refreshCategoryComboBox() {
        ObservableList<String> budgetedCategories = getBudgetedCategories();
        System.out.println("[DEBUG] [refreshCategoryComboBox] Setting ComboBox items: " + budgetedCategories);
        categoryComboBox.getItems().setAll(budgetedCategories);

        // Also refresh filter ComboBox if needed
        filterCategoryCombo.getItems().clear();
        filterCategoryCombo.getItems().add("All");
        filterCategoryCombo.getItems().addAll(budgetedCategories);
        filterCategoryCombo.setValue("All");
    }
    public static void main(String[] args) { }
}