package com.MoneyMind.projet_javafx.controllers;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Three sample accounts for testing purposes without registration:
 * Account 1: 1234567891, PIN: 123456
 * Account 2: 1234567892, PIN: 123456
 * Account 3: 1234567893, PIN: 123456
 * This class contains the main method and the receipt method
 * Which generate Receipts for each transaction
 * The main method also sets up the main application window
 * and opens the sample accounts
 *

 */
public class BankingTransaction extends Application {

    // Create a set of scenes for the application
    Scenes scenes = new Scenes();

    private static final String LOGO_PATH = "Logo.gif";

    @Override
    public void start(Stage mainStage) {


        Image icon = new Image("Logo.png");

        // Open sample accounts and set up the main application window
        openSampleAccounts();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);
        mainStage.getIcons().add(icon);
        mainStage.setScene(scene);
        mainStage.setTitle("Better Budget");

        // Create the intro screen with the logo
        ImageView logo = new ImageView(new Image(LOGO_PATH));
        logo.setPreserveRatio(true);
        logo.setFitWidth(200);

// Create the welcome text
        Text welcomeText = new Text("Welcome to Better Budget!");
// Set font style to Bubbleboddy.ttf
        welcomeText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Bubbleboddy.ttf"), 24));
        welcomeText.setFill(Color.web("#00BB62"));

        // Create a thin light green line
        Separator separator = new Separator();
        separator.setMaxWidth(200);
        separator.setMinHeight(1);
        separator.setStyle("-fx-background-color: #00BB62");

        VBox introContent = new VBox(5, logo, separator, welcomeText);
        introContent.setAlignment(Pos.CENTER);

        StackPane introPane = new StackPane(introContent);
        introPane.setAlignment(Pos.CENTER);
        root.setCenter(introPane);

        Scene loginScene = scenes.loginPane();


        // Apply the custom logo transition
        LogoTransition logoTransition = new LogoTransition(logo, welcomeText, separator, introPane); 
        logoTransition.setOnFinished(event -> {
            mainStage.setScene(loginScene);
        });
        logoTransition.play();

        mainStage.getIcons().add(icon);
        mainStage.setTitle("BetterBudget Spending Tracker");

        mainStage.setResizable(false);
        mainStage.show();

    }


    // Generate a receipt for a given transaction
    public static void receipt(String accountNumber, String transaction, String amount) {

        Image icon = new Image("Colgate Logo.png");

        // Set up the receipt window
        Stage recieptStage = new Stage();
        TableView<ReceiptData> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final ObservableList<ReceiptData> data =
                FXCollections.observableArrayList(
                        new ReceiptData(accountNumber, transaction, amount)
                );

        // Create columns for the table view

        TableColumn dateColumn = new TableColumn("Date");
        dateColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("date"));
        dateColumn.setMaxWidth(95);
        dateColumn.setPrefWidth(80);
        dateColumn.setMinWidth(70);

        TableColumn timeColumn = new TableColumn("Time");
        timeColumn.setMaxWidth(100);
        timeColumn.setPrefWidth(55);
        timeColumn.setMinWidth(50);
        timeColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("time"));

        TableColumn locationColumn = new TableColumn("Location");
        locationColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("location"));

        TableColumn receiptNoColumn = new TableColumn("Receipt Number");
        receiptNoColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("receiptNumber"));

        TableColumn accountNoColumn = new TableColumn("Account Number");
        accountNoColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("accountNumber"));
        accountNoColumn.setMaxWidth(110);
        accountNoColumn.setPrefWidth(105);
        accountNoColumn.setMinWidth(105);

        TableColumn transactionColumn = new TableColumn("Transaction");
        transactionColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("transaction"));

        TableColumn amountColumn = new TableColumn("Amount");
        amountColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("amount"));
        amountColumn.setMaxWidth(105);
        amountColumn.setPrefWidth(74);
        amountColumn.setMinWidth(70);

        TableColumn currentBalColumn = new TableColumn("Current Balance");
        currentBalColumn.setCellValueFactory(
                new PropertyValueFactory<ReceiptData, String>("currentBalance"));
        currentBalColumn.setMaxWidth(110);
        currentBalColumn.setPrefWidth(100);
        currentBalColumn.setMinWidth(100);


        table.setItems(data);

        // Add the columns to the table view
        //for some reason addAll doesn't work
        table.getColumns().addAll(dateColumn, timeColumn, locationColumn, receiptNoColumn, accountNoColumn, transactionColumn, amountColumn, currentBalColumn);


        // Create the main layout
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20));

        // Add a title label
        Label titleLabel = new Label("COLGATE BANK TRANSACTION RECORD");
        titleLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 18;");
        VBox titleBox = new VBox(titleLabel);


        BorderPane mainBorderPane = new BorderPane();

        StackPane topPane = new StackPane();
        StackPane centerPane = new StackPane();
        StackPane bottomPane = new StackPane();

        //I went with this font because it is the most similar to the font used in receipts
        Font receiptFont = Font.font("Courier New", 13);

        // Configure the top region of the receipt
        Text receiptTitle = new Text("COLGATE BANK TRANSACTION RECORD");
        receiptTitle.setFont(receiptFont);

        topPane.getChildren().add(receiptTitle);
        topPane.setPadding(new Insets(25, 0, 25, 0));


        centerPane.getChildren().add(table);
        // elements for the Bottom Region of receipt mainBorderPane

        VBox bottomVBox = new VBox();
        Text bottomText = new Text("THANK YOU FOR USING COLGATE BANKING!");
        Text bottomText2 = new Text("GO GATE!");
        bottomText.setFont(receiptFont);
        bottomText2.setFont(receiptFont);

        bottomVBox.setSpacing(5);
        bottomVBox.setAlignment(Pos.BOTTOM_CENTER);
        bottomVBox.getChildren().addAll(bottomText, bottomText2);
        bottomPane.setPadding(new Insets(25, 0, 25, 0));
        bottomPane.getChildren().add(bottomVBox);


        // Set up 3 regions to be used in border pane
        mainBorderPane.setTop(topPane);
        mainBorderPane.setCenter(centerPane);
        mainBorderPane.setBottom(bottomPane);


        Scene receiptScene = new Scene(mainBorderPane, 850, 210, Color.WHITE);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - receiptScene.getWidth()) * 0.85;
        double y = bounds.getMinY() + (bounds.getHeight() - receiptScene.getHeight()) * 0.3;
        recieptStage.setX(x);
        recieptStage.setY(y);

        recieptStage.getIcons().add(icon);
        recieptStage.setResizable(false);
        recieptStage.setScene(receiptScene);
        recieptStage.show();
    }

    // This method will format the labels, used in Scenes
    static void labelFormatting(GridPane centerGrid, Label dateLabel, Label timeLabel, Label locationLabel, Label receiptNoLabel, Label accountNoLabel) {
        centerGrid.add(dateLabel, 0, 0, 1, 1);
        centerGrid.add(timeLabel, 0, 1, 1, 1);
        centerGrid.add(locationLabel, 0, 2, 1, 1);
        centerGrid.add(receiptNoLabel, 0, 3, 1, 1);
        centerGrid.add(accountNoLabel, 0, 4, 1, 1);
    }

    public static void main(String[] args) {
        launch();
    }

    /*
     * This method will open 3 sample accounts for testing purposes
     */
    public static void openSampleAccounts() {
        HashMap<String, String> sampleName1 = new HashMap<>();
        sampleName1.put("firstname", "Zulnorain");
        sampleName1.put("lastname", "Ahmed");
        HashMap<String, String> sampleName2 = new HashMap<>();
        sampleName2.put("firstname", "Elodie");
        sampleName2.put("lastname", "Fourquet");
        HashMap<String, String> sampleName3 = new HashMap<>();
        sampleName3.put("firstname", "Dan");
        sampleName3.put("lastname", "Kim");

        long pin = 123456;
        BankDatabase.openAccount(BankDatabase.generateAccountNumber(), "1234", sampleName1, 3000, pin);
        BankDatabase.openAccount(BankDatabase.generateAccountNumber(), "4321", sampleName2, 5000, pin);
        BankDatabase.openAccount(BankDatabase.generateAccountNumber(), "0000", sampleName3, 6500, pin);
    }
}
