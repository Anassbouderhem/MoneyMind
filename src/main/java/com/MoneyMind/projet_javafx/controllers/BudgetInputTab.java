package com.MoneyMind.projet_javafx.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.util.Arrays;

public class BudgetInputTab extends Tab{

    String fontDirectory = "/fonts/HankenGrotesk.ttf";
    Font font = Font.loadFont(getClass().getResourceAsStream(fontDirectory), 14);
    // visual components
    Tab inputTab = new Tab();
    ScrollPane scrollPane = new ScrollPane();
    VBox vBox = new VBox();
    HBox totalBar = new HBox();
    Label totalLabel = new Label("Total Budget"); // tooltip -> amount allowed to spend/month
    TextField totalField = new TextField();
    Button totalButton = new Button("Set Total");
    VBox totalDisplayBox = new VBox();
    Label totalDisplayLabel = new Label("");

    HBox categoryBar = new HBox();
    VBox categoryLabelBox = new VBox();
    Label categoryLabel = new Label("Categories");
    VBox nameBox = new VBox();
    Label nameLabel = new Label("Name");
    VBox amountBox = new VBox();
    Label amountLabel = new Label("Amount");
    TextField amountField = new TextField();
    VBox buttonBox = new VBox();
    Label blank = new Label("");
    Button addButton = new Button("Add");

    VBox tableBox = new VBox();
    HBox tableLabelBox = new HBox();
    Label tableLabel = new Label("Current Categories"); // does this need its own box?

    HBox bottomBar = new HBox();
    Button quitButton = new Button("Quit");

    // initial budget, TODO should be loaded from user data

    private DataStorage dataStorage;

    double totalBudgetAmount = 0.0;

    // dropdown for budget category name
    ObservableList<String> nameList = FXCollections.observableArrayList(Arrays.asList( // list of categories
            "Clothing", "Debt payments", "Education", "Entertainment", "Food", "Gifts",
            "Health", "Housing", "Insurance", "Personal",
            "Savings", "Taxes", "Transportation", "Misc."
    ));
    ComboBox<String> nameCombo = new ComboBox<>(nameList);

    // table of budget categories
    ObservableList<Budget> budgetList;
    TableView<Budget> table = new TableView<>(budgetList);

    private void initializeTotalBudgetAmount() {
        totalBudgetAmount = dataStorage.getLoggedUser().getTotalLimit();
    }

    private void initializeBudgetTotalList() {
        if (totalBudgetAmount != 0) {
            totalDisplayLabel.setText("Budget Limit: $" + totalBudgetAmount);
        }
    }

    private void initializeBudgetList() {
        budgetList = FXCollections.observableArrayList(dataStorage.getBudgets());
    }

    /**
     *
     * @return scene budget input window
     */
    public BudgetInputTab(DataStorage dataStorageInstance) {
        dataStorage = dataStorageInstance;
        initializeBudgetList();
        initializeTotalBudgetAmount();
        allStyling();
        initializeBudgetTotalList();
        populate();
        setHandlers();
        tableSetup();
        createToolTips();
        scrollPane.setContent(vBox);
        setText("Budget Input");
        setContent(scrollPane);
    }

    private void allStyling() {
        inputTab.setClosable(false);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        totalDisplayBox.setAlignment(Pos.CENTER_RIGHT);
        totalBar.setSpacing(10);
        totalBar.setAlignment(Pos.CENTER);
        totalBar.setPadding(new Insets(5));
//        totalLabel.setPrefWidth(100);
//        totalLabel.getStyleClass().add("label");
//        totalField.setMinWidth(225);
        totalField.setPrefWidth(225);
        totalField.setPromptText("Enter amount in dollars (e.g. 1000)");
        totalField.setAlignment(Pos.CENTER);
        Text promptText = new Text(totalField.getPromptText());
        promptText.setFont(totalField.getFont());
//        totalField.setMinWidth(promptText.getBoundsInLocal().getWidth() + totalField.getPadding().getLeft() + totalField.getPadding().getRight());
        totalButton.setPrefWidth(75);

        categoryBar.setSpacing(10);
        categoryBar.setPadding(new Insets(10, 0, 10, 0));
        categoryBar.setAlignment(Pos.CENTER);
//        categoryBar.setPadding(new Insets(10));
        categoryLabelBox.setPadding(new Insets(10, 0, 0, 0));
//        categoryLabelBox.setSpacing(10);
        categoryLabelBox.setAlignment(Pos.CENTER);
//        nameLabel.setPrefWidth(100);
        nameBox.setAlignment(Pos.BOTTOM_CENTER);
        nameBox.setPrefWidth(160);
        nameBox.setMaxWidth(Double.MAX_VALUE);
        nameCombo.setPrefWidth(175);
        nameCombo.setPromptText("Select/add a name");
//        amountLabel.setPrefWidth(100);
        amountBox.setAlignment(Pos.BOTTOM_CENTER);
        amountBox.setPrefWidth(150);
        amountBox.setMaxWidth(Double.MAX_VALUE);
        amountField.setPrefWidth(110);
        amountField.setPromptText("Enter amount in dollars"); // or just "$"
//        buttonBox.setSpacing(10);
//        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);
//        addButton.setPrefWidth(80);
        tableBox.setAlignment(Pos.CENTER);
//        tableBox.setSpacing(10);
//        tableBox.setPadding(new Insets(10));
        tableLabelBox.setAlignment(Pos.CENTER);

        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        quitButton.setPrefWidth(80);

        totalLabel.getStyleClass().add("custom-label");
        totalDisplayLabel.getStyleClass().add("custom-label");
        categoryLabel.getStyleClass().add("custom-label");
        nameLabel.getStyleClass().add("custom-label");
        amountLabel.getStyleClass().add("custom-label");
        tableLabel.getStyleClass().add("custom-label");

        totalBar.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-color: black;"
                + "-fx-background-color: #7AE1B5");
        categoryBar.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-color: black;"
                + "-fx-background-color: #7AE1B5");
        tableBox.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-color: black;"
                + "-fx-background-color: #7AE1B5;");

        totalLabel.setFont(font);
        totalField.setFont(font);
         totalButton.setFont(font);
         totalDisplayLabel.setFont(font);
         categoryLabel.setFont(font);
         nameLabel.setFont(font);
         amountLabel.setFont(font);
         amountField.setFont(font);
         blank.setFont(font);
         addButton.setFont(font);
         tableLabel.setFont(font);

         quitButton.setFont(font);
    }

    private void populate() {
        totalDisplayBox.getChildren().add(totalDisplayLabel);
        vBox.getChildren().addAll(totalBar, categoryBar, tableBox, bottomBar);
        totalBar.getChildren().addAll(totalLabel, totalField, totalButton, totalDisplayBox);
        categoryBar.getChildren().addAll(categoryLabelBox, nameBox, amountBox, buttonBox);
        tableLabelBox.getChildren().add(tableLabel);
        tableLabelBox.setMargin(tableLabel, new Insets(6,0,6,0));
        tableBox.getChildren().addAll(tableLabelBox, table);
        bottomBar.getChildren().add(quitButton);


        // categoryBar Boxes
        categoryLabelBox.getChildren().add(categoryLabel);
        nameBox.getChildren().addAll(nameLabel, nameCombo);
        amountBox.getChildren().addAll(amountLabel, amountField);
        buttonBox.getChildren().addAll(blank, addButton);
    }

    private void tableSetup() {
        TableColumn<Budget, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.435));
        nameColumn.setResizable(false);

        TableColumn<Budget, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setStyle("-fx-alignment: CENTER;");
        amountColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.23));
        amountColumn.setResizable(false);

        amountColumn.setCellFactory(column -> new TextFieldTableCell<Budget, Double>(new StringConverter<Double>() {
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

        TableColumn<Budget, String> percentColumn = new TableColumn<>("% of Total Budget");
        percentColumn.setCellValueFactory(cellData -> {
            Budget budget = cellData.getValue();
            double percentage = (budget.getAmount() / totalBudgetAmount) * 100;
            return new SimpleStringProperty(String.format("%.2f%%", percentage));
        });
        percentColumn.setStyle("-fx-alignment: CENTER;");
        percentColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.23));
        percentColumn.setResizable(false);

        TableColumn delCol = new TableColumn();
        delCol.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        delCol.setResizable(false);
        delCol.setStyle("-fx-alignment: CENTER;");
        Image delete = new Image("delete.png");
        // TODO set delete column cell factory
        delCol.setCellFactory(ButtonTableCell.<Budget>forTableColumn(delete, (Budget Budget) ->
        {
            removeHandler(Budget);
            return Budget;
        }));

        table.setSelectionModel(null);
        table.setEditable(false);
        table.setItems(budgetList);
        table.getColumns().addAll(nameColumn, amountColumn, percentColumn, delCol); // removed timeColumn
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void removeHandler(Budget toRemove) {
        table.getItems().remove(toRemove);
        dataStorage.removeBudget(dataStorage.getLoggedUser(), toRemove.getName());
    }

    private void refreshTable() {
        ObservableList<Budget> list = table.getItems();
        table.setItems(null);
        table.layout();
        table.setItems(list);
    }


    private void addHandler() {
        if (nameCombo.getValue() == null) {
            System.out.println("Invalid name");
            return;
        }
        String name = (String)nameCombo.getValue();
        nameCombo.setValue(null);

        if (amountField.getText().isEmpty() || amountField.getText().equals("$")) {
            System.out.println("Invalid amount");
            return;
        }

        double amount = Double.parseDouble(amountField.getText());

        if (amount > totalBudgetAmount) {
            System.out.println("Amount exceeds budget");
            return;
        }
        amountField.setText(null);

        Budget newBudget = new Budget(name, amount, 0);
        budgetList.add(newBudget);
        dataStorage.createBudget(name, amount, amount);
    }

    private void quitHandler() {
        Platform.exit();
    }
    private void setTotalHandler() {
        System.out.println("Updated total");
        totalBudgetAmount = Double.parseDouble(totalField.getText());
        dataStorage.updateUserTotalLimit(dataStorage.getLoggedUser(), totalBudgetAmount);
        totalDisplayLabel.setText("Current Limit: $" + totalBudgetAmount);

        // refresh the table
        refreshTable();
    }
    private void setHandlers() {
        totalButton.setOnAction(e -> setTotalHandler());
        totalButton.disableProperty().bind(
                Bindings.isEmpty(totalField.textProperty())
        );
        addButton.setOnAction(e -> addHandler());
        addButton.disableProperty().bind(
                Bindings.or(
                    amountField.textProperty().isEmpty(),
                    nameCombo.valueProperty().isNull()
                )
        );
        quitButton.setOnAction(e -> quitHandler());
    }
    private void createToolTips(){
        // tooltips
        nameCombo.setTooltip(new Tooltip("Select or add a new category name"));
        totalButton.setTooltip(new Tooltip("Enter a total budget amount in $ and click here to set"));
        quitButton.setTooltip(new Tooltip("Save and quit the application"));
    }
}