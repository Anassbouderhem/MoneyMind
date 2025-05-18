package com.MoneyMind.projet_javafx.controllers;

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

import java.time.LocalDate;

/** Class used to create SpendingApp GUI and add functionality
 *
 */
public class TransactionInput extends Tab {

    String fontDirectory = "/fonts/HankenGrotesk.ttf";

    // visual components
    // TransactionView chartTab;
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

    // lists
    ObservableList<Transaction> tableData; // hold purchases

    ObservableList<String> categoryList = FXCollections.observableArrayList( // list of categories
            "Clothing", "Debt payments", "Education", "Entertainment", "Food", "Gifts",
            "Health", "Housing", "Insurance", "Personal",
            "Savings", "Taxes", "Transportation", "Misc."
    );

    // tableview
    TableView<Transaction> table;

    private DataStorage dataStorage;
    public TransactionInput(TransactionList list) {
        tableData = list.getList();
        table = new TableView<>(tableData);
        start();
    }

    public TransactionInput(DataStorage dataStorage) {
        this.dataStorage = dataStorage;

        tableData = FXCollections.observableArrayList(dataStorage.getLoggedUser().getTransactions());
        table = new TableView<>(tableData);
        start();
    }

    public TransactionInput() {
        this((TransactionList) FXCollections.observableArrayList());
    }

    public void start() {

        categoryComboBox = new ComboBox<>(categoryList); // fill category dropdown with categories

        // methods
        gridStyling();
        allStyling();
        setButtonHandlers();
        createToolTips();
        populateGrids();
        tableSetup();
        addAmountRegex();
        bindTotalField();

//        // create and add tabs to tabPane
//        shopTab.setText("Purchases");
//        shopTab.setContent(outerGrid);
//        chartTab = createChartTab();
//        chartTab.setText("Spending Breakdown");
//        tabPane.getTabs().addAll(shopTab, chartTab);
//        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE); // prevent user from closing tabs

        setText("Transactions");
        setContent(outerGrid);

    }


    /** Input validation for amount field to prevent invalid input
     *
     */
    private void addAmountRegex() {
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Validate the new value to ensure that it only contains numeric characters, decimals and negative sign
            if (!newValue.matches("\\d*\\.?\\d*")) {
                // If the new value is non-numeric, replace it with the old value
                amountField.setText(oldValue);
            }
        });
    }

    /** Helper function binds purchaseList to the totalField to display the total cost of all items
     *
     */
    private void bindTotalField() {
        // Add a listener to the purchaseList
        table.getItems().addListener((ListChangeListener<Transaction>) change -> {
            // Recalculate the total cost
            double totalCost = 0;
            for (Transaction t : table.getItems()) {
                totalCost += t.getAmount();
            }
            // Update the total field with the new total cost
            String str = String.format("$ %.2f", totalCost);
            totalField.setText(str);
        });

        // Bind the total field to the total cost
        totalField.textProperty().bindBidirectional(new SimpleStringProperty("0"));
    }

    /** Helper function creates TableColumn objects and adds them to TableView object
     *
     */
    private void tableSetup() {
        // create table columns
        TableColumn<Transaction, String> itemCol = new TableColumn<>("Name");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemCol.setStyle( "-fx-alignment: CENTER;");
        itemCol.prefWidthProperty().bind(table.widthProperty().multiply(0.28));
        itemCol.setResizable(false);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setStyle( "-fx-alignment: CENTER;");
        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        amountCol.setResizable(false);

        amountCol.setCellFactory(column -> new TextFieldTableCell<Transaction, Double>(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return "$" + value;
            }

            @Override
            public Double fromString(String value) {
                // Implement if needed
                return null;
            }
        }));

    TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setStyle( "-fx-alignment: CENTER;");
        catCol.prefWidthProperty().bind(table.widthProperty().multiply(0.26));
        catCol.setResizable(false);

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date Ordered");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle( "-fx-alignment: CENTER;");
        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.21));
        dateCol.setResizable(false);

        TableColumn delCol = new TableColumn();
        delCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        delCol.setResizable(false);
        delCol.setStyle( "-fx-alignment: CENTER;");
        Image delete = new Image("delete.png");

        delCol.setCellFactory(ButtonTableCell.<Transaction>forTableColumn(delete, (Transaction Transaction) ->
        {
            removeHandler(Transaction);
            return Transaction;
        }));
        table.setSelectionModel(null);
        table.setEditable(false);
        table.getColumns().addAll(itemCol, amountCol, catCol, dateCol, delCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /** Helper function sets handlers for buttons
     *
     */
    private void setButtonHandlers() {
        // set button handlers
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

    /** Helper function adds widgets to boxes and adds those boxes to the GridPanes in the scene
     *
     */
    private void populateGrids() {
        // add to grid
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

        inputPanel.add(purchaseBox, 0, 2, 2, 1);
        purchaseBox.getChildren().add(purchaseButton);

        pTitleBox.getChildren().add(purchasedLabel);
        outerGrid.add(pTitleBox, 1, 0);
        tableBox.getChildren().add(table);
        outerGrid.add(tableBox, 1, 1);
        outerGrid.add(bottomGrid, 1, 2);
        bottomGrid.add(removeBox, 0, 0);
        totalBox.getChildren().addAll(totalLabel, totalField);


        quitBox.getChildren().add(quitButton);

        totalQuitBox.getChildren().addAll(totalBox, quitBox);

        bottomGrid.add(totalQuitBox, 1, 0);
    }

    /** Helper function sets box, label, and widget styling
     *
     */
    private void allStyling() {
        // box alignment
        nameBox.setAlignment(Pos.CENTER);
        amountBox.setAlignment(Pos.TOP_CENTER);
        categoryBox.setAlignment(Pos.TOP_CENTER);
        dateBox.setAlignment(Pos.TOP_CENTER);
        // dateField.setAlignment(Pos.TOP_CENTER);
        purchaseBox.setAlignment(Pos.BOTTOM_CENTER);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        removeBox.setAlignment(Pos.TOP_LEFT);
        quitBox.setAlignment(Pos.TOP_RIGHT);
        totalQuitBox.setAlignment(Pos.TOP_RIGHT);
        totalBox.setMargin(totalLabel, new Insets(0,5,0,0));

        // label styling (sizes, font)
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

        // widget styling
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

        // box/grid styling
        outerGrid.setAlignment(Pos.CENTER);
        //outerGrid.setStyle("-fx-background-color: linear-gradient(to right, #45F18A, #B3BCCB);");
        outerGrid.setPadding(new Insets(15, 10, 10, 10));
        outerGrid.setHgap(10);
        // addTitleBox.setStyle("-fx-background-color: transparent;");
        // pTitleBox.setStyle("-fx-background-color: transparent;");
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

    /** Helper function creates ColumnConstraints and RowConstraints for GridPanes and sets their height/width
     *
     */
    private void gridStyling() {
        // gridpane styling
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

    /** Helper function sets tooltips for widgets
     *
     */
    private void createToolTips(){
        // tooltips
        nameField.setTooltip(new Tooltip("Enter transaction name"));
        amountField.setTooltip(new Tooltip("Enter transaction amount"));
        categoryComboBox.setTooltip(new Tooltip("Select a category relevant to the transaction"));
        dateField.setTooltip(new Tooltip("Enter transaction date"));
        purchaseButton.setTooltip(new Tooltip("Add transaction to the table"));
        table.setTooltip(new Tooltip("Transaction will appear here"));
        // removeButton.setTooltip(new Tooltip("Click on a row and press this button to remove it from the table"));
        totalField.setTooltip(new Tooltip("The total amount of all transactions"));
        quitButton.setTooltip(new Tooltip("Close the application"));
    }

    /**
     * Method gets the sums for each item category, and calls TransactionView class to create a tab for a chart object
     * @return TransactionView: tab containing the chart
     */

    private TransactionView createChartTab() {
        TransactionView TransactionView = new TransactionView(dataStorage);
        TransactionView.setTooltip(new Tooltip("Shows a breakdown of the given purchased items"));
        return TransactionView;
    }

    /**
     * Creates new Transaction object based on user input values and adds Transaction to list
     */
    private void addTransactionHandler() {
        LocalDate date = dateField.getValue();
        double cost = Double.parseDouble(amountField.getText());
        String category = (String)categoryComboBox.getValue();
        String name = nameField.getText();
        Transaction newT = new Transaction(name, cost, category, date);
        amountField.setText(null);
        table.getItems().add(newT);
        dataStorage.createTransaction(dataStorage.getLoggedUser(), name, cost, category, date);
        // refreshChartTab();
    }

    /**
     * Removes selected row from the TableView object
     */
    private void removeHandler(Transaction toRemove) {
        table.getItems().remove(toRemove);
        dataStorage.removeTransaction(dataStorage.getLoggedUser(), toRemove.getName());
        // refreshChartTab();
    }

    /**
     * Quits out of the application
     */
    private void quitHandler() {
        Platform.exit();
    }

//    /**
//     * Creates new Chart tab and replaces the previous one
//     */
//    private void refreshChartTab() {
//        chartTab = createChartTab();
//        tabPane.getTabs().set(1, chartTab);
//    }

    public static void main(String[] args) {  }
}