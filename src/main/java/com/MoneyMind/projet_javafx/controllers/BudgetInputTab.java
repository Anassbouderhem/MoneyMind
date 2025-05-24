package com.MoneyMind.projet_javafx.controllers;

import com.MoneyMind.projet_javafx.assistant_AI.AIAssistant;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BudgetInputTab extends Tab {

    String fontDirectory = "/fonts/HankenGrotesk.ttf";
    Font font = Font.loadFont(getClass().getResourceAsStream(fontDirectory), 14);

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox vBox = new VBox();
    private final HBox totalBar = new HBox();
    private final Label totalLabel = new Label("Total Budget");
    private final TextField totalField = new TextField();
    private final Button totalButton = new Button("Set Total");
    private final VBox totalDisplayBox = new VBox();
    private final Label totalDisplayLabel = new Label("");

    private final HBox categoryBar = new HBox();
    private final VBox categoryLabelBox = new VBox();
    private final Label categoryLabel = new Label("Categories");
    private final VBox nameBox = new VBox();
    private final Label nameLabel = new Label("Name");
    private final VBox amountBox = new VBox();
    private final Label amountLabel = new Label("Amount");
    private final TextField amountField = new TextField();
    private final VBox buttonBox = new VBox();
    private final Label blank = new Label("");
    private final Button addButton = new Button("Add");

    private final VBox tableBox = new VBox();
    private final HBox tableLabelBox = new HBox();
    private final Label tableLabel = new Label("Current Categories");

    private final HBox bottomBar = new HBox();
    private final Button quitButton = new Button("Quit");


    

    private DataStorage dataStorage;
    private double totalBudgetAmount = 0.0;

    private final ObservableList<String> nameList = FXCollections.observableArrayList();
    private final ComboBox<String> nameCombo = new ComboBox<>(nameList);

    private ObservableList<Budget> budgetList = FXCollections.observableArrayList();
    private final TableView<Budget> table = new TableView<>();

    public BudgetInputTab(DataStorage dataStorageInstance) {
        this.dataStorage = dataStorageInstance;
        loadCategoriesFromDatabase();
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

    private void loadCategoriesFromDatabase() {
        try {
            List<String> categories = dataStorage.getAllCategories();
            nameList.setAll(categories);
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des catégories: " + e.getMessage());
            // Fallback sur des catégories par défaut si nécessaire
            nameList.setAll("Nourriture", "Logement", "Transport", "Loisirs", "Épargne");
        }
    }

    private void initializeTotalBudgetAmount() {
        if (dataStorage.getLoggedUser() != null){
        totalBudgetAmount = dataStorage.getLoggedUser().getTotalLimit();
    }
    }

    private void initializeBudgetTotalList() {
        if (totalBudgetAmount != 0) {
            totalDisplayLabel.setText("Budget Limit: $" + totalBudgetAmount);
        }
    }

    private void initializeBudgetList() {
        budgetList = FXCollections.observableArrayList(dataStorage.getBudgets());
    }

    private void allStyling() {
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        totalDisplayBox.setAlignment(Pos.CENTER_RIGHT);
        totalBar.setSpacing(10);
        totalBar.setAlignment(Pos.CENTER);
        totalBar.setPadding(new Insets(5));
        totalField.setPrefWidth(225);
        totalField.setPromptText("Enter amount in dollars (e.g. 1000)");
        totalField.setAlignment(Pos.CENTER);
        totalButton.setPrefWidth(75);

        categoryBar.setSpacing(10);
        categoryBar.setPadding(new Insets(10, 0, 10, 0));
        categoryBar.setAlignment(Pos.CENTER);
        categoryLabelBox.setAlignment(Pos.CENTER);
        nameBox.setAlignment(Pos.BOTTOM_CENTER);
        nameBox.setPrefWidth(160);
        nameCombo.setPrefWidth(175);
        nameCombo.setPromptText("Select/add a name");

        amountBox.setAlignment(Pos.BOTTOM_CENTER);
        amountBox.setPrefWidth(150);
        amountField.setPrefWidth(110);
        amountField.setPromptText("Enter amount in dollars");

        buttonBox.setAlignment(Pos.BOTTOM_LEFT);

        tableBox.setAlignment(Pos.CENTER);
        tableLabelBox.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        quitButton.setPrefWidth(80);

        // Fonts
        Label[] labels = {totalLabel, totalDisplayLabel, categoryLabel, nameLabel, amountLabel, tableLabel, blank};
        for (Label label : labels) label.setFont(font);
        Button[] buttons = {totalButton, addButton, quitButton};
        for (Button button : buttons) button.setFont(font);
    }

    private void populate() {
        totalDisplayBox.getChildren().add(totalDisplayLabel);
        vBox.getChildren().addAll(totalBar, categoryBar, tableBox, bottomBar);
        totalBar.getChildren().addAll(totalLabel, totalField, totalButton, totalDisplayBox);
        categoryBar.getChildren().addAll(categoryLabelBox, nameBox, amountBox, buttonBox);
        tableLabelBox.getChildren().add(tableLabel);
        tableBox.getChildren().addAll(tableLabelBox, table);
        bottomBar.getChildren().add(quitButton);

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
        amountColumn.setCellFactory(column -> new TextFieldTableCell<>(new StringConverter<>() {
            @Override public String toString(Double value) { return "$" + value; }
            @Override public Double fromString(String value) { return 0.0; }
        }));

        TableColumn<Budget, String> percentColumn = new TableColumn<>("% of Total Budget");
        percentColumn.setCellValueFactory(cellData -> {
            Budget b = cellData.getValue();
            double percent = totalBudgetAmount != 0 ? (b.getAmount() / totalBudgetAmount) * 100 : 0;
            return new SimpleStringProperty(String.format("%.2f%%", percent));
        });
        percentColumn.setStyle("-fx-alignment: CENTER;");
        percentColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.23));
        percentColumn.setResizable(false);

        TableColumn<Budget, Void> delCol = new TableColumn<>();
        delCol.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        delCol.setStyle("-fx-alignment: CENTER;");
        delCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button();

            {
                ImageView iv = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/delete.png"))));
                iv.setFitWidth(16); iv.setFitHeight(16);
                delBtn.setGraphic(iv);
                delBtn.setOnAction(e -> removeHandler(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : delBtn);
            }
        });

        table.setItems(budgetList);
        table.getColumns().addAll(nameColumn, amountColumn, percentColumn, delCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(false);
    }

    private void removeHandler(Budget toRemove) {
        budgetList.remove(toRemove);
        dataStorage.removeBudget(toRemove.getName());
        refreshTable();
    }

    private void refreshTable() {
        table.refresh();
    }

    private void addHandler() {
        String name = nameCombo.getValue();
        if (name == null || name.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountField.getText());
            Budget newBudget = new Budget(name, amount);
            budgetList.add(newBudget);
            dataStorage.addBudget(dataStorage.getLoggedUser(), newBudget);
            nameCombo.setValue(null);
            amountField.clear();
            refreshTable();
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
        }
    }

    private void setHandlers() {
        addButton.setOnAction(e -> addHandler());
        totalButton.setOnAction(e -> {
            try {
                totalBudgetAmount = Double.parseDouble(totalField.getText());
                dataStorage.getLoggedUser().setTotalLimit(totalBudgetAmount);
                dataStorage.updateUserTotalLimit(dataStorage.getLoggedUser(),totalBudgetAmount);
                System.out.println("HI"+totalBudgetAmount);
                initializeBudgetTotalList();
                refreshTable();
            } catch (NumberFormatException ex) {
                System.out.println("Invalid total budget.");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        quitButton.setOnAction(e -> Platform.exit());
    }

    private void createToolTips() {
        totalField.setTooltip(new Tooltip("Set the total budget for the month"));
        nameCombo.setTooltip(new Tooltip("Choose a category"));
        amountField.setTooltip(new Tooltip("Amount to allocate to this category"));
        addButton.setTooltip(new Tooltip("Add category to budget"));
    }




}


