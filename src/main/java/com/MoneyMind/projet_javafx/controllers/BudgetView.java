package com.MoneyMind.projet_javafx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;


public class BudgetView extends Tab {
    String fontDirectory = "/fonts/HankenGrotesk.ttf";
    Font font = Font.loadFont(getClass().getResourceAsStream(fontDirectory), 14);
    Font titleFont = Font.loadFont(getClass().getResourceAsStream(fontDirectory), 20);
    ScrollPane scrollPane = new ScrollPane();
    VBox vBox = new VBox();
    HBox hBox = new HBox();
    Button editButton = new Button("Edit Budget");
    Button quitButton = new Button("Quit");
    private DataStorage dataStorage;
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    double totalBudget;


    public BudgetView(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        setText("Budget Overview");

        quitButton.setFont(font);
        editButton.setFont(font);
        quitButton.setOnAction(e -> {Platform.exit();});
        quitButton.setPrefWidth(75);
        hBox.getChildren().addAll(editButton, quitButton);
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().add(hBox);

        // Attach the refresh method to the onSelectionChanged event
        this.setOnSelectionChanged(event -> {
            if (this.isSelected()) {
                refresh();
            }
        });

        // Call refresh initially to populate the tab
        refresh();
    }


    public void refresh() {
        // Clear the old data
        vBox.getChildren().clear();
        pieChartData.clear();

        // Reload your data from DataStorage
        totalBudget = dataStorage.getLoggedUser().getTotalLimit();
        ObservableList<Budget> list = FXCollections.observableArrayList(dataStorage.getBudgets());
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList(dataStorage.getLoggedUser().getTransactions());

        // Re-populate your UI elements
        double sum = sumCategories(list);
        pieChartData.add(new PieChart.Data("Extra", totalBudget - sum));
        addToPieChartData(list);
        PieChart chart = new PieChart(pieChartData);
        chart.setStyle("-fx-font-family: \"Hanken Grotesk\"; -fx-font-size: 14px;");
        chart.setTitle("Budget Allocation");
        Label titleLabel = (Label) chart.lookup(".chart-title");
        titleLabel.setFont(titleFont);
        chart.setLabelLineLength(20);

        // show percentages in chart and legend
        chart.getData().forEach(data -> {
            String label = String.format("%s: %.2f%%", data.getName(), ((data.getPieValue()/totalBudget) * 100));
            data.setName(label);
        });

        StackedBarChart stackedBarChart = createSBC(list, transactionList, totalBudget);

        vBox.getChildren().addAll(chart, stackedBarChart);
        vBox.setPadding(new Insets(10));

        scrollPane.setContent(vBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        setContent(scrollPane);
    }

    private ArrayList<String> getBudgetNames(ObservableList<Budget> bList) {
        ArrayList<String> names = new ArrayList<>();
        for (Budget budget : bList) {
            names.add(budget.getName());
        }
        return names;
    }

    private StackedBarChart createSBC(ObservableList<Budget> bList, ObservableList<Transaction> tList, double totalBudget) {
        // get category names for xAxis
        ArrayList<String> categoryNames = getBudgetNames(bList);

        // set x axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(categoryNames));
        xAxis.setLabel("Category");

        // set y axis
        double tickUnit = totalBudget * 0.1; // make each tick in the chart occur every 10% of the upper bound
        NumberAxis yAxis = new NumberAxis("$", 0, totalBudget, tickUnit);

        StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
        stackedBarChart.setTitle("Budget Usage by Category");

        XYChart.Series<String, Number> spentSeries = new XYChart.Series<>();
        spentSeries.setName("Spent");
        for (Transaction item : tList) {
            spentSeries.getData().add(new XYChart.Data<>(item.getCategory(), item.getAmount()));
        }

        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        totalSeries.setName("Total");
        for (Budget item : bList) {
            totalSeries.getData().add(new XYChart.Data<>(item.getName(), item.getAmount()));
        }

        stackedBarChart.getData().addAll(spentSeries, totalSeries);

        return stackedBarChart;
    }


    private double sumCategories(ObservableList<Budget> list) {
        double sum = 0;
        for (Budget item : list) {
            sum += item.getAmount();
        }
        return sum;
    }

    private void addToPieChartData(ObservableList<Budget> list) {
        for (Budget item : list) {
            pieChartData.add(new PieChart.Data(item.getName(), item.getAmount()));
        }
    }
}
