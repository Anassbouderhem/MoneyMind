package com.MoneyMind.projet_javafx.controllers;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/* LINK External sources */
/*
 * [JavaFX API Documentation](https://docs.oracle.com/en/java/javase/19/)
 * [TableView](https://docs.oracle.com/javafx/2/ui_controls/table-view.htm)
 * [ProgressBar](https://docs.oracle.com/javafx/2/ui_controls/progress.htm)
 * [DatePicker](https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/date-picker.htm)
 * [How to add buttons to JavaFX TableViews](https://stackoverflow.com/questions/29489366/how-to-add-button-in-javafx-table-view)
 */

public class TransactionPage extends Application

{

    /* LINK Global Variables */
    /* ================================================================================================== */

    private double tasksCompleted = 0.0;
    private double tasksTotal = 0.0;
    private LocalDate currDateOnPicker = LocalDate.now();
    private LocalDate currDate = LocalDate.now();
    private Map<LocalDate, ObservableList<Transaction>> database = new HashMap<>();


    /* LINK Global Components */
    /* ================================================================================================== */

    private ProgressBar pbar;
    private TableView<Transaction> table;
    private DatePicker calendarPicker;
    private DatePicker currDatePicker = new DatePicker(LocalDate.now());
    private TextField currDateField;

    /* LINK Numeric Constants */
    /* ================================================================================================== */

    private final int FIELD_DROPDOWN_WIDTH = 100;
    private final int BUTTON_WIDTH = 175;
    private final int MARGIN_SAMECELL = 15;
    private final int MARGIN_DIFFCELL = 25;
    private final double PBAR_HEIGHT = 21;
    private final double PBAR_WIDTH = 100;
    private final int CALENDAR_WIDTH = 275;
    private final int END_BUTTON_WIDTH = 200;

    private final int WINDOW_HEIGHT = 450;
    private final int WINDOW_WIDTH = 750;

    private final int COL_CHECKBOX_WIDTH = 28;
    private final int COL_TASKNAME_WIDTH = 100;
    private final int COL_DATE_WIDTH = 100;
    private final int COL_DELETE_WIDTH = 32;

    private final int FORM_LABEL_WIDTH = 100;
    private final int FORM_FIELD_WIDTH = 150;
    private final int FORM_SUBMIT_WIDTH = 140;

    /* LINK Margins */
    /* ================================================================================================== */

    private final Insets PADDING_GRIDPANE = new Insets(15, 20, 20, 20);
    private final Insets MARGINS_TABLE = new Insets(10, 0, 0, 0);
    private final Insets MARGINS_PBAR = new Insets(0, 0, 10, 0);
    private final Insets MARGINS_ADD_BUTTON = new Insets(10, 0, 0, 15);
    private final Insets MARGINS_NEW_TASKFIELD = new Insets(10, 0, 0, 0);
    private final Insets MARGINS_CALENDAR_BOX = new Insets(0, 0, 0, 15);
    private final Insets MARGINS_CALENDAR = new Insets(10, 0, 20, 0);
    private final Insets MARGINS_CURR_DATE = new Insets(0, 10, 0, 5);
    private final Insets MARGINS_FORM_TITLE = new Insets(0, 0, 5, 20);
    private final Insets MARGINS_FORM_LABEL = new Insets(0, 0, 0, 20);
    private final Insets MARGINS_FORM_BOX = new Insets(10, 0, 0, 0);
    private final Insets MARGINS_FORM_BUTTON = new Insets(30, 0, 0, 0);

    /* LINK GridPane coordinate constants */
    /* ================================================================================================== */

    private final GridCoords coordProgBar = new GridCoords(0, 0, 2, 1);
    private final GridCoords coordTable = new GridCoords(0, 1);
    private final GridCoords coordAddField = new GridCoords(0, 2);
    private final GridCoords coordCalendar = new GridCoords(1, 1);
    private final GridCoords coordAddButton = new GridCoords(1, 2);


    /* SECTION Start Function */
    /* ================================================================================================== */

    public void start(Stage primaryStage)
    {
        /* LINK Create tab & grid pane */
        /* ---------------------------------------------------------- */

        TabPane mainPane = new TabPane();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(PADDING_GRIDPANE);


        Tab todo = new Tab("Transactions", gridPane);
        todo.setClosable(false);
        mainPane.getTabs().add(todo);


        /* LINK Create Components */
        /* ---------------------------------------------------------- */

        table = createTable();
        pbar = createPBar(primaryStage);

        Label tableTitle = createTableTitle();

        // VBox calendarAndButton = new VBox(currDateBox, calendar, endButton);
        VBox addForm = createAddTransactions(primaryStage);
        VBox tableAndTitle = new VBox(tableTitle, table);

        /* LINK Add Components to Pane */
        /* ---------------------------------------------------------- */

        // gridPane.add(pbar, coordProgBar.col(), coordProgBar.row(), coordProgBar.colSpan(), coordProgBar.rowSpan());
        gridPane.add(tableAndTitle, coordTable.col(), coordTable.row());
        gridPane.add(addForm, coordCalendar.col(), coordCalendar.row());

        /* LINK Configure Pane component margins */
        /* ---------------------------------------------------------- */

        gridPane.setMargin(pbar, MARGINS_PBAR);
        tableAndTitle.setMargin(table, MARGINS_TABLE);
        tableAndTitle.setAlignment(Pos.CENTER);

        /* LINK Configure Pane */
        /* ---------------------------------------------------------- */

        primaryStage.setTitle("Daily Habit Tracker");
        primaryStage.setScene(new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setResizable(false);
        primaryStage.show();

    }
    /* !SECTION */

    /* SECTION Functions */
    /* ================================================================================================== */

    /* LINK Create progress bar */
    /* ================================================================================================== */

    public ProgressBar createPBar(Stage stage)
    {
        ProgressBar pbar = new ProgressBar(0.0);
        pbar.setMinHeight(PBAR_HEIGHT);
        pbar.setMinWidth(PBAR_WIDTH);
        pbar.setStyle("-fx-accent: #00d700");

        pbar.prefWidthProperty().bind(Bindings.divide(stage.widthProperty(), 1.0));

        return pbar;
    }

    /* LINK Create table title */
    /* ================================================================================================== */

    public Label createTableTitle()
    {
        Label tableTitle = new Label("Transactions");

        Font font = new Font("Calibri", 18);
        tableTitle.setFont(font);

        return tableTitle;
    }

    /* SECTION Create Table */
    /* ================================================================================================== */

    public TableView<Transaction> createTable()
    {
        /* LINK Table */
        /* ---------------------------------------------------------- */

        TableView<Transaction> table = new TableView<>();
        table.setEditable(true);
        table.setMinWidth(COL_TASKNAME_WIDTH * 3 + COL_DATE_WIDTH + COL_DELETE_WIDTH);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("You have no transactions."));

        /* LINK Name */
        /* ---------------------------------------------------------- */

        TableColumn<Transaction, String> colName = new TableColumn<>("Transaction");
        colName.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("name"));

        /*
        // Line allows users to edit Transaction name seamlessly, but may not be desirable
        colName.setCellFactory(new TextFieldCellFactory());
        */
        colName.setReorderable(false);
        colName.setResizable(false);
        colName.setPrefWidth(COL_TASKNAME_WIDTH);
        colName.setStyle( "-fx-alignment: CENTER;");

        /* LINK Amount */
        /* ---------------------------------------------------------- */

        TableColumn<Transaction, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(
                new PropertyValueFactory<Transaction, Double>("amount"));

        colAmount.setReorderable(false);
        colAmount.setResizable(false);
        colAmount.setPrefWidth(COL_TASKNAME_WIDTH);

        colAmount.setStyle( "-fx-alignment: CENTER;");

        /* LINK Category */
        /* ---------------------------------------------------------- */

        TableColumn<Transaction, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("category"));

        /*
        // Line allows users to edit Transaction name seamlessly, but may not be desirable
        colName.setCellFactory(new TextFieldCellFactory());
        */
        colCategory.setReorderable(false);
        colCategory.setResizable(false);
        colCategory.setPrefWidth(COL_TASKNAME_WIDTH);

        colCategory.setStyle( "-fx-alignment: CENTER;");

        /* LINK Date */
        /* ---------------------------------------------------------- */

        TableColumn<Transaction, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(
                new PropertyValueFactory<Transaction, LocalDate>("date"));

        colDate.setReorderable(false);
        colDate.setResizable(false);
        colDate.setPrefWidth(COL_DATE_WIDTH);
        colDate.setStyle( "-fx-alignment: CENTER;");


        /* LINK Delete Button */
        /* ---------------------------------------------------------- */

        TableColumn colDelete = new TableColumn();

        colDelete.setCellFactory(ButtonTableCell.<Transaction>forTableColumn("X", (Transaction Transaction) ->
        {
            removeTransaction(table, Transaction);
            return Transaction;
        }));

        colDelete.setReorderable(false);
        colDelete.setResizable(false);
        colDelete.setSortable(false);
        colDelete.setPrefWidth(COL_DELETE_WIDTH);
        colDelete.setStyle( "-fx-alignment: CENTER;");

        /* LINK Finalize */
        /* ---------------------------------------------------------- */

        table.getColumns().addAll(colName, colAmount, colCategory, colDate, colDelete);
        table.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            row.prefHeightProperty().set(Control.USE_COMPUTED_SIZE);
            return row;
        });
        table.setFixedCellSize(Region.USE_COMPUTED_SIZE);

        return table;
    }

    /* !SECTION */

    /* LINK Create current date HBox */
    /* ================================================================================================== */

    public HBox createCurrDate(VBox calendar) {
        currDateField = new TextField(currDate.toString());
        currDateField.setEditable(false);

        Label currDateLabel = new Label("Today is:");
        HBox currDateBox = new HBox(currDateLabel, currDateField);

        currDateBox.setMargin(currDateLabel, MARGINS_CURR_DATE);
        currDateBox.setAlignment(Pos.CENTER_LEFT);

        return currDateBox;
    }

    /* SECTION Create form to add new transactions */
    /* ================================================================================================== */
    public VBox createAddTransactions(Stage stage)
    {
        /* LINK Title */

        Label title = new Label("Add New Transactions");
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        Font font = new Font("Calibri", 18);
        title.setFont(font);

        /* LINK Name */

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        HBox name = new HBox(nameLabel, nameField);

        nameLabel.setPrefWidth(FORM_LABEL_WIDTH);
        nameField.setPrefWidth(FORM_FIELD_WIDTH);
        name.setMargin(nameLabel, MARGINS_FORM_LABEL);
        name.setAlignment(Pos.CENTER_LEFT);

        /* LINK Amount */

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();

        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Validate the new value to ensure that it only contains numeric characters, decimals and negative sign
            if (!newValue.matches("\\d*\\.?\\d*")) {
                // If the new value is non-numeric, replace it with the old value
                amountField.setText(oldValue);
            }
        });

        HBox amount = new HBox(amountLabel, amountField);
        amountLabel.setPrefWidth(FORM_LABEL_WIDTH);
        amountField.setPrefWidth(FORM_FIELD_WIDTH);
        amount.setMargin(amountLabel, MARGINS_FORM_LABEL);
        amount.setAlignment(Pos.CENTER_LEFT);

        /* LINK Category */

        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.getItems().addAll("Essentials", "Entertainment", "Household", "Subscription", "Work Expenditures", "Other");
        categoryField.setValue(categoryField.getItems().get(0));

        HBox category = new HBox(categoryLabel, categoryField);
        categoryLabel.setPrefWidth(FORM_LABEL_WIDTH);
        categoryField.setPrefWidth(FORM_FIELD_WIDTH);
        category.setMargin(categoryLabel, MARGINS_FORM_LABEL);
        category.setAlignment(Pos.CENTER_LEFT);

        /* LINK Date */

        Label dateLabel = new Label("Transaction Date:");
        DatePicker dateField = new DatePicker(LocalDate.now());
        HBox date = new HBox(dateLabel, dateField);

        dateLabel.setPrefWidth(FORM_LABEL_WIDTH);
        dateField.setPrefWidth(FORM_FIELD_WIDTH);
        date.setMargin(dateLabel, MARGINS_FORM_LABEL);
        date.setAlignment(Pos.CENTER_LEFT);

        /* LINK Submit */

        Button submitButton = new Button("Add Transaction");
        HBox submit = new HBox(submitButton);

        submitButton.setPrefWidth(FORM_SUBMIT_WIDTH);
        submit.setAlignment(Pos.CENTER);
        submit.setMargin(submitButton, MARGINS_FORM_LABEL);

        submitButton.setOnAction(evt -> {
            String nameInput = nameField.getText();
            String amountInput = amountField.getText();
            String categoryInput = categoryField.getValue();
            LocalDate dateInput = dateField.getValue();

            if (nameInput != null && !nameInput.isEmpty()
                    && amountInput != null && !amountInput.isEmpty()
                    && categoryInput != null && !categoryInput.isEmpty()
                    && dateInput != null && dateField.valueProperty() != null) {
                Transaction Transaction = new Transaction(nameInput, Double.parseDouble(amountInput), categoryInput, dateInput);
                // Add event handler to Transaction

                table.getItems().add(Transaction);
                nameField.clear();
                amountField.clear();
            }
        });

        submitButton.disableProperty().bind(Bindings.or(
                Bindings.isEmpty(nameField.textProperty()),
                Bindings.isEmpty(amountField.textProperty())
        ));

        /* LINK Form styling */

        VBox form = new VBox(title, name, amount, category, date, submit);
        form.setMargin(title, MARGINS_FORM_TITLE);
        form.setMargin(name, MARGINS_FORM_BOX);
        form.setMargin(amount, MARGINS_FORM_BOX);
        form.setMargin(category, MARGINS_FORM_BOX);
        form.setMargin(date, MARGINS_FORM_BOX);
        form.setMargin(submit, MARGINS_FORM_BUTTON);

        form.setAlignment(Pos.TOP_CENTER);
        form.prefWidthProperty().bind(Bindings.divide(stage.widthProperty(), 2.0));

        return form;
    }

    /* !SECTION */

    /* LINK Create calendar */
    /* ================================================================================================== */

    public VBox createCalendar()
    {
        calendarPicker = new DatePicker();
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isAfter(currDate))
                                {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #dddddd;");
                                }

                                if (item.isEqual(LocalDate.now().minusDays(5)))
                                {
                                    setStyle("-fx-background-color: #F8696B;");
                                }

                                if (item.isEqual(LocalDate.now().minusDays(4)))
                                {
                                    setStyle("-fx-background-color: #FBAA77;");
                                }

                                if (item.isEqual(LocalDate.now().minusDays(3)))
                                {
                                    setStyle("-fx-background-color: #FFEB84;");
                                }

                                if (item.isEqual(LocalDate.now().minusDays(2)))
                                {
                                    setStyle("-fx-background-color: #B1D580;");
                                }

                                if (item.isEqual(LocalDate.now().minusDays(1)))
                                {
                                    setStyle("-fx-background-color: #63BE7B;");
                                }
                                // Disable and style a date cell when it is before the current date
                                // if (item.isBefore(
                                //         checkInDatePicker.getValue().plusDays(1))
                                //     ) {
                                //         setDisable(true);
                                //         setStyle("-fx-background-color: #ffc0cb;");
                                // }

                                // // Tooltip for hovering over a date
                                // long p = ChronoUnit.DAYS.between(
                                //         checkInDatePicker.getValue(), item
                                // );
                                // setTooltip(new Tooltip(
                                //     "You're about to stay for " + p + " days")
                                // );
                            }
                        };
                    }
                };
        calendarPicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                currDateOnPicker = newValue;

                // !IMPORTANT Update table data on date change
                database.put(oldValue, table.getItems());

                if (database.get(newValue) == null)
                    table.setItems(FXCollections.observableArrayList());
                else
                    table.setItems(database.get(newValue));
                updatePBar();
            }
        });

        calendarPicker.setDayCellFactory(dayCellFactory);
        calendarPicker.setValue(LocalDate.now());

        DatePickerSkin calendar = new DatePickerSkin(calendarPicker);
        Node calendarNode = calendar.getPopupContent();
        calendarNode.setStyle("-fx-background-color: transparent;");

        VBox calendarBox = new VBox(calendarNode);
        calendarBox.setAlignment(Pos.TOP_LEFT);
        calendarBox.setMaxWidth(CALENDAR_WIDTH);

        return calendarBox;
    }

    /* LINK Delete Transaction Button */
    /* ================================================================================================== */

    public void removeTransaction(TableView<Transaction> table, Transaction toRemove) {
        table.getItems().remove(toRemove);
    }

    /* LINK Update ProgressBar */
    /* ================================================================================================== */

    public void incrementPBar(int updateCompleted, int updateTotal) {
        tasksCompleted += updateCompleted;
        tasksTotal += updateTotal;
        pbar.setProgress(tasksCompleted / tasksTotal);
    }

    public void updatePBar() {
        tasksCompleted = 0;
        tasksTotal = table.getItems().size();
        pbar.setProgress(tasksCompleted / tasksTotal);
    }

    public void resetPBar() {
        tasksCompleted = 0;
        tasksTotal = table.getItems().size();
        pbar.setProgress(0);
    }


    /* !SECTION */

    /* SECTION Subclasses */
    /* ================================================================================================== */

    /* LINK GridPane Coordiantes */
    /* ---------------------------------------------------------- */

    static class GridCoords
    {
        private final int col;
        private final int row;
        private final int colSpan;
        private final int rowSpan;

        public GridCoords(int col, int row)
        {
            this.col = col;
            this.row = row;
            this.colSpan = 1;
            this.rowSpan = 1;
        }

        public GridCoords(int col, int row, int colSpan, int rowSpan)
        {
            this.col = col;
            this.row = row;
            this.colSpan = colSpan;
            this.rowSpan = rowSpan;
        }

        public int col() { return col; }
        public int row() { return row; }
        public int colSpan() { return colSpan; }
        public int rowSpan() { return rowSpan; }

    }

    /* !SECTION */
}