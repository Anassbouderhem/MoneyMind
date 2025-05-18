package com.MoneyMind.projet_javafx.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This class contains methods to design all panes in the application
 * Refer to the README for the layout of the panes
 */
public class Scenes extends Styling {

    // This provides a few second delay before going back to loginPane (after transaction)
    Timeline logoutDelay = new Timeline(
            new KeyFrame(Duration.seconds(5),
                    actionEvent -> logOutButtonEvent()
            ));
    private String displayBalance;
    private String type;
    private double amount;
    private String inputgovID;
    private String inputfirstname;
    private String inputlastname;
    private String inputpin;

    /**
     * The first scene displayed when the program runs.
     * Login Pane consists of:
     * topPane with rectangle1 (100px height) and the name of the bank
     * centerPane (or named loginForm) with two text fields, one small clickable
     * text for registration of account and one login button
     */
    public Scene loginPane() {

        VBox loginForm = new VBox();
        VBox loginForm2 = new VBox();

        // elements for the Top Region of the mainBorderPane
        Text title = new Text("BetterBudget");

        // StackPane automatically centers the objects
        title.setFill(Color.web("#50C878"));
        title.setFont(titleFont);
        //center title vertically
        title.setTextOrigin(VPos.TOP);
        title.setTextAlignment(TextAlignment.CENTER);


        // The StackPane is used to layer items after each other

        headerStackPane.getChildren().clear();
        headerStackPane.getChildren().addAll(rectLarge(), title);



        // elements for the Center Region of mainBorderPane
        Label accountNumLabel = new Label("Username: ");
        Label pinLabel = new Label("Password: ");
        TextField accountNumText = new TextField();
        PasswordField pinText = new PasswordField();
        Text registerNow = new Text("Click here if you don't have an account yet.");
        Button loginButton = new Button("Log in");

        Insets padding = new Insets(25, 250, 25, 250);

        // For both Labels and TextFields
        accountNumLabel.setLabelFor(accountNumText);
        pinLabel.setLabelFor(pinText);

        accountNumLabel.setFont(bodyFont);
        pinLabel.setFont(bodyFont);


        accountNumText.setFont(bodyFont);
        pinText.setFont(bodyFont);

        accountNumText.setTextFormatter(new TextFormatter<String>(digitFilter));
        pinText.setTextFormatter(new TextFormatter<String>(digitFilter));

        accountNumText.setPrefHeight(40);
        pinText.setPrefHeight(40);


        // For log in button
        loginButton.setFont(bodyFont);
        buttonHoverEffectGreen(loginButton);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setPrefWidth(100);

        // Set ActionEvent on the LogIn Button and register now text
        loginButton.setOnAction(event ->
                loginButtonEvent(accountNumText.getText(), pinText.getText()));
        registerNow.setOnMouseClicked(event -> registerPane());

        // For register now text
        registerNow.setFont(smallFont);
        //maple red from color picker
        registerNow.setFill(Color.rgb(225, 0, 40));

        // For hovering the cursor on the elements
        accountNumText.setCursor(Cursor.TEXT);
        pinText.setCursor(Cursor.TEXT);
        loginButton.setCursor(Cursor.HAND);
        registerNow.setCursor(Cursor.HAND);

        // For the overall Log In Form (including the small text and the Login button in loginForm2 layout pane)
        loginForm.setSpacing(10.0);
        loginForm.setPadding(padding);
        loginForm.setStyle("-fx-margin-left: 20px;");

        loginForm2.setSpacing(10.0);
        loginForm2.setAlignment(Pos.BOTTOM_CENTER);
        loginForm2.getChildren().addAll(registerNow, loginButton);



        loginForm.getChildren().addAll(accountNumLabel, accountNumText, pinLabel, pinText, loginForm2);

        // Set up the 4 regions of border pane
        mainBorderPane.setTop(headerStackPane);
        mainBorderPane.setCenter(loginForm);
        mainBorderPane.setLeft(null);
        mainBorderPane.setRight(null);
        return scene;
    }

    /**
     * @param accountNumber the account number of the user
     * @return the main scene of the application
     * Scene displays after the user has logged in
     * giving user choice on what to do next
     * -----------------------------------------------
     * Main Pane consists of
     * - topPane with rectangle (80px height) and masked account number
     * - centerPane for 3 main buttons (deposit, withdraw, check balance)
     * - rightPane for Logout button (displayed on bottom right)
     */
    public Scene mainPane(String accountNumber) {
        String maskedAccountNumber = maskDisplayNumber(accountNumber);

        // elements for Top Region of mainBorderPane
        designTopPane(maskedAccountNumber);

        // elements for Center Region of mainBorderPane

        // centerPane elements and additional properties
        Button toDepositButton;
        toDepositButton = new Button("Deposit");
        Button toWithdrawButton;
        toWithdrawButton = new Button("Withdraw");
        Button toCheckBalanceButton;
        toCheckBalanceButton = new Button("Check Balance");
        for (Button button : Arrays.asList(toDepositButton, toWithdrawButton, toCheckBalanceButton)) {
            setMainButtonsProperties(button);
        }

        // ------ Button Action Events
        toDepositButton.setOnAction(event -> depositPane());
        toWithdrawButton.setOnAction(event -> withdrawPane());
        toCheckBalanceButton.setOnAction(event -> checkBalancePane());

        // centerPane properties
        Insets padding = new Insets(50, 100, 25, 300);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);

        // Add elements to centerPane
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        centerContentVBox.getChildren().addAll(toDepositButton, toWithdrawButton, toCheckBalanceButton);

        // elements of Right Region of mainBorderPane

        // Logout Button Action Event
        logoutButton.setOnAction(event -> logOutButtonEvent());
        buttonHoverEffectGray(logoutButton);

        // rightPane properties
        rightSidebarVBox.setPadding(new Insets(300, 50, 0, 50));

        // Add elements to rightPane
        rightSidebarVBox.getChildren().clear(); // clear first before adding designated element
        rightSidebarVBox.getChildren().add(setLogOutButton());

        // Set up the 4 regions of border pane
        mainBorderPane.setTop(headerStackPane);
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setRight(rightSidebarVBox);
        mainBorderPane.setLeft(null);

        return scene;
    }

    /*
     * Used without Parameters
     * This is when switching back to the main Pane
     */
    public Scene mainPane() {
        // centerPane elements and additional properties
        Button toDepositButton = new Button("Deposit");
        Button toWithdrawButton = new Button("Withdraw");
        Button toCheckBalanceButton = new Button("Check Balance");

        setMainButtonsProperties(toDepositButton);
        setMainButtonsProperties(toWithdrawButton);
        setMainButtonsProperties(toCheckBalanceButton);


        // Button Action Events
        toDepositButton.setOnAction(event -> depositPane());
        toWithdrawButton.setOnAction(event -> withdrawPane());
        toCheckBalanceButton.setOnAction(event -> checkBalancePane());

        // centerPane properties
        Insets padding = new Insets(50, 100, 30, 300);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);

        // Add elements to centerPane
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        centerContentVBox.getChildren().addAll(toDepositButton, toWithdrawButton, toCheckBalanceButton);


        // elements of Right Region of mainBorderPane

        // Logout Button Action Event
        logoutButton.setOnAction(event -> logOutButtonEvent());
        buttonHoverEffectGray(logoutButton);

        // rightPane properties
        rightSidebarVBox.setPadding(new Insets(300, 50, 0, 50));

        // Add elements to rightPane
        rightSidebarVBox.getChildren().clear(); // clear first before adding designated element
        rightSidebarVBox.getChildren().add(setLogOutButton());

        // ------ Set up the 4 regions of border pane
        // Top Pane has already been set in mainPane w/ param
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setRight(rightSidebarVBox);
        mainBorderPane.setLeft(null);

        return scene;
    }

    /* -----------------------------------------------
     * Scene displays after user has clicked deposit button
     * -----------------------------------------------
     * Deposit Pane consists of
     *  - topPane with rectangle (80px height) and masked account number
     *  - centerPane for displaying:
     *      - Label "Amount"
     *      - Text field which accepts amount
     *      - Deposit Button
     *  - leftPane with left arrow button
     */
    public Scene depositPane() {

        // elements for Center Region of mainBorderPane

        // This VBox pane will align button placement on bottom center of CenterPane
        VBox depositButtonPlacement = new VBox();

        // centerPane elements and additional properties
        Label depositLabel = new Label("Amount:");
        TextField depositText = new TextField();
        Button depositButton = new Button("Deposit");
        depositLabel.setFont(bodyFont);
        depositText.setFont(bodyFont);
        depositButton.setFont(bodyFont);

        depositText.setPrefHeight(40);
        depositText.setTextFormatter(new TextFormatter<String>(doubleFilter));

        configureMajorButtonAppearance(depositButton, true);
        depositButton.disableProperty().bind(Bindings.isEmpty(depositText.textProperty()));

        // Button Action Events
        depositButton.setOnAction(event -> depositButtonEvent(depositText.getText()));

        // Add element to VBox
        depositButtonPlacement.setAlignment(Pos.BOTTOM_CENTER);
        depositButtonPlacement.getChildren().add(depositButton);

        // centerPane properties
        Insets padding = new Insets(25, 300, 0, 150);
        centerContentVBox.setSpacing(15.0);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());
        centerContentVBox.setAlignment(Pos.TOP_LEFT);

        // Add elements to centerPane
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        centerContentVBox.getChildren().addAll(setErrorTexts(errorText), depositLabel, depositText, depositButtonPlacement);

        // elements of Left Region of mainBorderPane

        //  Back Button Action Events
        backButton.setOnAction(event -> backButtonEvent());

        // leftPane properties
        leftSidebarVBox.setPadding(new Insets(25, 50, 0, 50));

        // Add elements to leftPane
        leftSidebarVBox.getChildren().clear(); // clear first before add the designated element
        leftSidebarVBox.getChildren().add(setBackButtonAppearance());

        // Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setLeft(leftSidebarVBox);
        mainBorderPane.setRight(null);

        return scene;
    }

    /* -----------------------------------------------
     * Scene displays after user has clicked withdraw button
     * -----------------------------------------------
     * Deposit Pane consists of
     *  - topPane with rectangle (80px height) and masked account number
     *  - centerPane for displaying GridPane with checkboxes labeled from P1,000 to P10,000 and other amount
     *  - leftPane with left arrow button
     */
    public Scene withdrawPane() {


        // elements for Center Region of mainBorderPane

        // GridPane is used for the layout of radio buttons
        GridPane centerGridPane = new GridPane();
        // This VBox pane will align button placement on bottom center of CenterPane
        VBox withdrawButtonPlacement = new VBox();
        final ToggleGroup buttonGrouping = new ToggleGroup();

        // centerPane elements and additional properties
        // elements for centerGridPane
        RadioButton optionOne = new RadioButton("$100");
        RadioButton optionTwo = new RadioButton("$200");
        RadioButton optionThree = new RadioButton("$300");
        RadioButton optionFour = new RadioButton("$400");
        RadioButton optionFive = new RadioButton("$500");
        RadioButton optionSix = new RadioButton("$600");
        RadioButton optionSeven = new RadioButton("$700");
        RadioButton optionEight = new RadioButton("$800");
        RadioButton optionNine = new RadioButton("$900");
        RadioButton optionTen = new RadioButton("$1000");
        RadioButton otherAmount = new RadioButton("Other Amount?");
        TextField otherAmountTxtField = new TextField();

        fiveOptionSetting(buttonGrouping, optionOne, optionTwo, optionThree, optionFour, optionFive);
        fiveOptionSetting(buttonGrouping, optionSix, optionSeven, optionEight, optionNine, optionTen);
        otherAmount.setFont(biggerBodyFont);
        otherAmount.setToggleGroup(buttonGrouping);
        otherAmount.setCursor(Cursor.HAND);

        otherAmountTxtField.visibleProperty().bind(otherAmount.selectedProperty());
        otherAmountTxtField.setTextFormatter(new TextFormatter<String>(doubleFilter));


        centerGridPane.setHgap(50);
        centerGridPane.setVgap(20);
        centerGridPane.setAlignment(Pos.CENTER);
        centerGridPane.add(optionOne, 0, 0, 1, 1);
        centerGridPane.add(optionTwo, 0, 1, 1, 1);
        centerGridPane.add(optionThree, 0, 2, 1, 1);
        centerGridPane.add(optionFour, 0, 3, 1, 1);
        centerGridPane.add(optionFive, 1, 0, 1, 1);
        centerGridPane.add(optionSix, 1, 1, 1, 1);
        centerGridPane.add(optionSeven, 1, 2, 1, 1);
        centerGridPane.add(optionEight, 1, 3, 1, 1);
        centerGridPane.add(optionNine, 2, 0, 1, 1);
        centerGridPane.add(optionTen, 2, 1, 1, 1);
        centerGridPane.add(otherAmount, 2, 2, 1, 1);
        centerGridPane.add(otherAmountTxtField, 2, 3, 1, 1);

        Button withdrawButton = new Button("Withdraw");
        configureMajorButtonAppearance(withdrawButton, true);
        withdrawButton.setDisable(true);

        // listens if radio buttons are selected or not to whether disable withdrawButton or not
        // also includes the "Other Amount" choice to keep the button disabled until user typed amount in textfield
        buttonGrouping.selectedToggleProperty().addListener((ob, o, n) -> {
            if (otherAmount.isSelected()) {
                withdrawButton.disableProperty().bind(
                        Bindings.isEmpty(otherAmountTxtField.textProperty()));
            } else {
                // makes sure that button unbinds to the textfield if other choice is selected
                withdrawButton.disableProperty().unbind();
                if (withdrawButton.isDisabled()) {
                    withdrawButton.setDisable(false);
                }
            }
        });

        // Button Action Events
        withdrawButton.setOnAction(event -> {
            // basically gets the text of the choice, if it's "other amount", gets the input from textfield
            RadioButton selectedRadioButton = (RadioButton) buttonGrouping.getSelectedToggle();
            String toggledButtonAmount = selectedRadioButton.getText();
            if (toggledButtonAmount.equals(otherAmount.getText())) {
                withdrawButtonEvent(otherAmountTxtField.getText());
            } else {
                withdrawButtonEvent(toggledButtonAmount);
            }
        });


        // Add element to VBox
        withdrawButtonPlacement.setAlignment(Pos.BOTTOM_CENTER);
        withdrawButtonPlacement.getChildren().add(withdrawButton);

        // centerPane properties
        Insets padding = new Insets(25, 150, 0, 25);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());

        // Add elements to centerPane
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        centerContentVBox.getChildren().addAll(setErrorTexts(errorText), centerGridPane, withdrawButtonPlacement);

        // Back Button Action Events
        backButton.setOnAction(event -> backButtonEvent());

        // leftPane properties
        leftSidebarVBox.setPadding(new Insets(25, 50, 0, 50));

        // Add elements to leftPane
        leftSidebarVBox.getChildren().clear(); // clear first before add the designated element
        leftSidebarVBox.getChildren().add(setBackButtonAppearance());

        // Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setLeft(leftSidebarVBox);
        mainBorderPane.setRight(null);

        return scene;
    }

    /*
     * Method to set the appearance of the back button
     * reduces code duplication
     */
    public void fiveOptionSetting(ToggleGroup buttonGroup, RadioButton optionOne, RadioButton optionTwo, RadioButton optionThree, RadioButton optionFour, RadioButton optionFive) {
        optionOne.setFont(biggerBodyFont);
        optionOne.setToggleGroup(buttonGroup);
        optionOne.setCursor(Cursor.HAND);
        optionTwo.setFont(biggerBodyFont);
        optionTwo.setToggleGroup(buttonGroup);
        optionTwo.setCursor(Cursor.HAND);
        optionThree.setFont(biggerBodyFont);
        optionThree.setToggleGroup(buttonGroup);
        optionThree.setCursor(Cursor.HAND);
        optionFour.setFont(biggerBodyFont);
        optionFour.setToggleGroup(buttonGroup);
        optionFour.setCursor(Cursor.HAND);
        optionFive.setFont(biggerBodyFont);
        optionFive.setToggleGroup(buttonGroup);
        optionFive.setCursor(Cursor.HAND);
    }

    /* -----------------------------------------------
     * Scene displays after user clicked Check Balance Button
     * -----------------------------------------------
     * Check Balance Pane consists of
     *  - topPane with rectangle (80px height) and masked account number
     *  - centerPane for two button display
     *      1. Show Balance on Screen Button
     *      2. Print Balance Receipt Button
     *  - leftPane with left Arrow Button
     */
    public Scene checkBalancePane() {

        // elements of Center Region of mainBorderPane

        Button balanceScreenButton = new Button("Show Balance on Screen");
        Button balanceReceiptButton = new Button("Print Balance Receipt");
        setMainButtonsProperties(balanceScreenButton);
        setMainButtonsProperties(balanceReceiptButton);

        //  set Button Action Events
        balanceScreenButton.setOnAction(event -> balanceScreenButtonEvent());
        balanceReceiptButton.setOnAction(event -> balanceReceiptButtonEvent());


        // centerPane properties
        Insets padding = new Insets(50, 300, 30, 150);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);

        // Add elements to centerPane ----------------------------------------------------------
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        boolean combineButtons = centerContentVBox.getChildren().addAll(balanceScreenButton
                , balanceReceiptButton);

        // elements of Left Region of mainBorderPane

        //  Back Button Action Events
        backButton.setOnAction(event -> backButtonEvent());

        // leftPane properties
        leftSidebarVBox.setPadding(new Insets(20, 50, 0, 50));

        // Add elements to leftPane
        leftSidebarVBox.getChildren().clear(); // clear first before add the designated element
        leftSidebarVBox.getChildren().add(setBackButtonAppearance());

        // Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setLeft(leftSidebarVBox);
        mainBorderPane.setRight(null);

        return scene;
    }

    /**
     * @param displayBalance - the amount of balance to be displayed
     * @return Scene displays after user clicked "Show Balance on Screen" Button on Check Balance Pane
     * -----------------------------------------------
     * Screen Balance Pane consists of
     * - topPane with rectangle80 (80px height) and masked account number
     * - centerPane text display of amount balance
     * 1. Show Balance on Screen Button
     * 2. Print Balance Receipt Button
     * - leftPane with left Arrow Button
     */
    public Scene screenBalancePane(String displayBalance) {
        this.displayBalance = displayBalance;

        // elements of Center Region of mainBorderPane

        Text currentBalanceText = new Text("Current Balance");
        Text accountBalance = new Text("$" + displayBalance);
        currentBalanceText.setFont(titleFont);
        accountBalance.setFont(titleFont2);

        // centerPane properties
        Insets padding = new Insets(50, 300, 0, 150);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());
        centerContentVBox.setAlignment(Pos.TOP_CENTER);


        // Add elements to centerPane
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        centerContentVBox.getChildren().addAll(currentBalanceText, accountBalance);

        // elements of Left Region of mainBorderPane

        // Back Button Action Events
        backButton.setOnAction(event -> backButtonEvent());

        // leftPane properties
        leftSidebarVBox.setPadding(new Insets(20, 50, 0, 50));

        // Add elements to leftPane
        leftSidebarVBox.getChildren().clear(); // clear first before add the designated element
        leftSidebarVBox.getChildren().add(setBackButtonAppearance());

        // Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setLeft(leftSidebarVBox);
        mainBorderPane.setRight(null);

        return scene;
    }

    /**
     * @param type - type of message to be displayed
     * @return Scene displays after user has confirmed transaction or printed account balance
     * -----------------------------------------------
     * Message Pane consists of
     * - topPane with rectangle (80px height) and masked account number
     * - centerPane for text display
     * - Text Display would either be:
     * 1. "Processing..." title only or
     * 2. "Transaction Successful." or "Printing Successful." with a small text below them
     */
    public Scene messagePane(String type) {
        this.type = type;


        // elements of Center Region of mainBorderPane

        Text messageDisplay = new Text();
        Text messageLogOffAutomatically = new Text("You will be logged off automatically in 5 seconds");
        Text pleaseRememberMessage = new Text("Please remember your account Number"); // this text is for only on registration part
        Text displayAccountNum = new Text(); // this text is for only on registration part
        Button loginFormButton = new Button("Back to Login Form");
        messageDisplay.setFont(titleFont);
        messageLogOffAutomatically.setFont(smallFont);
        configureMajorButtonAppearance(loginFormButton, true);
        loginFormButton.setPrefWidth(180);

        // Button Action Events
        loginFormButton.setOnAction(actionEvent -> logOutButtonEvent());

        // centerPane properties
        Insets padding = new Insets(50, 0, 0, 0);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());
        centerContentVBox.setAlignment(Pos.TOP_CENTER);

        /*
         * Enhanced Switch-state management for processing
         * - type is the String value that determines what type of message to be displayed
         * - type can be either "process", "transactionSuccess", "printSuccess"
         * The elements will be added in centerPane
         */
        switch (type) {
            case "process" -> {
                messageDisplay.setText("Processing...");

                // Add elements to centerPane
                centerContentVBox.getChildren().clear(); // clear first before adding designated element
                centerContentVBox.getChildren().addAll(messageDisplay);
            }
            case "transactionSuccess" -> {
                messageDisplay.setText("Transaction Successful.");

                // Add elements to centerPane
                centerContentVBox.getChildren().clear(); // clear first before adding designated element
                centerContentVBox.getChildren().addAll(messageDisplay, messageLogOffAutomatically);
            }
            case "printSuccess" -> {
                messageDisplay.setText("Printing Successful.");

                // Add elements to centerPane
                centerContentVBox.getChildren().clear(); // clear first before adding designated element
                centerContentVBox.getChildren().addAll(messageDisplay, messageLogOffAutomatically);
            }
            case "registerSuccess" -> {
                pleaseRememberMessage.setFont(biggerBodyFont);
                displayAccountNum.setFont(biggerBodyFont);
                messageDisplay.setText("Thank you for registering with Colgate");
                messageLogOffAutomatically.setText(" ");
                displayAccountNum.setText(BankDatabase.loggedAccount);

                // Add elements to centerPane
                centerContentVBox.getChildren().clear(); // clear first before adding designated element
                centerContentVBox.getChildren().addAll(messageDisplay, pleaseRememberMessage, displayAccountNum, loginFormButton);
            }
        }

        // Make sure centerPane is only the only region of mainBorderPane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setRight(null);
        mainBorderPane.setLeft(null);

        return scene;
    }

    /**
     * @param type   'd' for deposit, 'w' for withdraw
     * @param amount amount of transaction
     * @return Scene *  displays after user has clicked deposit or withdraw button to confirm the transaction
     * -----------------------------------------------
     * Confirm Pane consists of
     * - topPane with rectangle80 (80px height) and masked account number
     * - centerPane for displaying the amount of confirmation and the two buttons to confirm or cancel
     */
    public Scene confirmPane(char type, double amount) {
        this.type = String.valueOf(type);
        this.amount = amount;

        // elements of Center Region of mainBorderPane
        Text confirmTransaction = new Text();
        if (type == 'd') // DEPOSIT TYPE
            confirmTransaction.setText("Confirm Deposit Transaction?");
        else if (type == 'w') // WITHDRAW TYPE
            confirmTransaction.setText("Confirm Withdrawal Transaction?");
        Text amountTransaction = new Text("$" + amount);
        confirmTransaction.setFont(titleFont);
        amountTransaction.setFont(titleFont2);

        // used to place confirm and cancel buttons side by side
        HBox confirmButtons;
        confirmButtons = new HBox();

        Button confirmButton;
        confirmButton = new Button("Confirm");
        confirmButton.setDefaultButton(true);
        confirmButton.setFont(bodyFont);
        configureMajorButtonAppearance(confirmButton, true);

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setFont(bodyFont);
        configureMajorButtonAppearance(cancelButton, false);

        // Button Action Events
        confirmButton.setOnAction(event -> confirmButtonEvent(type, amount));
        cancelButton.setOnAction(event -> cancelButtonEvent());

        // Add elements to HBox
        confirmButtons.getChildren().addAll(confirmButton, cancelButton);
        confirmButtons.setSpacing(10);
        confirmButtons.setAlignment(Pos.BOTTOM_CENTER);

        // centerPane properties
        Insets padding = new Insets(50, 0, 0, 0);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());
        centerContentVBox.setAlignment(Pos.TOP_CENTER);

        // Add elements to centerPane
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        centerContentVBox.getChildren().addAll(confirmTransaction, amountTransaction, confirmButtons);

        // Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setRight(null);
        mainBorderPane.setLeft(null);
        return scene;
    }

    /**
     * Scene displays when user clicks on small text "Create Account" in LoginPane
     * Purpose of this scene is for user to create a new account
     * <p>
     * Register Pane consists of
     * - topPane with rectangle (100px height) and name of the bank
     * - centerPane
     * - text fields needed on having new account
     *
     * @return Scene
     */
    public Scene registerPane() {

        // elements for Center Region of mainBorderPane
        // GridPane used to layout text-fields at center of window
        GridPane centerGridPane = new GridPane();

        // elements for GridPane
        Label govId = new Label("Government ID:* ");
        Label firstName = new Label("First Name:* ");
        Label lastName = new Label("Last Name:* ");
        Label pin = new Label("PIN (6 Digits): ");
        Label confirmPin = new Label("Confirm PIN: ");
        TextField govIdTxtField = new TextField("");
        TextField firstNameTxtField = new TextField("");
        TextField lastNameTxtField = new TextField("");
        PasswordField pinTxtField = new PasswordField();
        PasswordField confirmPinTxtField = new PasswordField();

        govId.setFont(bodyFont);
        govIdTxtField.setFont(bodyFont);
        govIdTxtField.setTextFormatter(new TextFormatter<>(digitFilter));
        firstName.setFont(bodyFont);
        firstNameTxtField.setFont(bodyFont);
        lastName.setFont(bodyFont);
        lastNameTxtField.setFont(bodyFont);
        pin.setFont(bodyFont);
        pinTxtField.setFont(bodyFont);
        pinTxtField.setTextFormatter(new TextFormatter<>(digitFilter));
        confirmPin.setFont(bodyFont);
        confirmPinTxtField.setFont(bodyFont);
        confirmPinTxtField.setTextFormatter(new TextFormatter<>(digitFilter));


        centerGridPane.setHgap(40);
        centerGridPane.setVgap(10);
        centerGridPane.setAlignment(Pos.CENTER);
        // Add the elements to the GridPane (All labels are on 1st col, all text-fields on 2nd col)
        BankingTransaction.labelFormatting(centerGridPane, govId, firstName, lastName, pin, confirmPin);
        centerGridPane.add(govIdTxtField, 1, 0, 1, 1);
        centerGridPane.add(firstNameTxtField, 1, 1, 1, 1);
        centerGridPane.add(lastNameTxtField, 1, 2, 1, 1);
        centerGridPane.add(pinTxtField, 1, 3, 1, 1);
        centerGridPane.add(confirmPinTxtField, 1, 4, 1, 1);

        Text loginNow = new Text("Already have an account? Log in.");
        Button registerButton = new Button("Open Account");
        loginNow.setFont(smallFont);
        loginNow.setCursor(Cursor.HAND);
        loginNow.setFill(Color.rgb(225,0,40));

        configureMajorButtonAppearance(registerButton, true);
        // properties for disabling button unless all text fields are filled
        registerButton.disableProperty().bind(Bindings.isEmpty(govIdTxtField.textProperty()));
        registerButton.disableProperty().bind(Bindings.isEmpty(firstNameTxtField.textProperty()));
        registerButton.disableProperty().bind(Bindings.isEmpty(lastNameTxtField.textProperty()));
        registerButton.disableProperty().bind(Bindings.isEmpty(pinTxtField.textProperty()));
        registerButton.disableProperty().bind(Bindings.isEmpty(confirmPinTxtField.textProperty()));

        // Button Action Events (including small font)
        // Event brings the user to another pane
        // that displays a textfield which asks user to input initial deposit
        registerButton.setOnAction(event ->
                registerButtonEvent(
                        govIdTxtField.getText(),
                        firstNameTxtField.getText(),
                        lastNameTxtField.getText(),
                        pinTxtField.getText(),
                        confirmPinTxtField.getText()));
        loginNow.setOnMouseClicked(event -> loginPane());

        // centerPane properties
        Insets padding = new Insets(30, 0, 0, 0);

        centerContentVBox.setSpacing(10.0);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());
        centerContentVBox.setAlignment(Pos.TOP_CENTER);

        // Add elements to centerPane
        // clear first before adding designated element
        centerContentVBox.getChildren().clear();
        centerContentVBox.getChildren().addAll(setErrorTexts(errorText), centerGridPane, registerButton, loginNow);

        //  Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setRight(null);
        mainBorderPane.setLeft(null);

        return scene;
    }

    /**
     * Scene displays to ask user initial deposit
     * <p>
     * initial deposit Register Pane consists of
     * - topPane with rectangle80 (80px height) and masked account number
     * - centerPane with textfield for user to enter initial deposit amount
     *
     * @param inputgovID user's government ID
     * @param inputfirstname user's first name
     * @param inputlastname user's last name
     * @param inputpin user's pin
     * @return Scene initial deposit register pane
     */
    public Scene initialDepositRegisterPane(String inputgovID, String inputfirstname, String inputlastname, String inputpin) {
        this.inputgovID = inputgovID;
        this.inputfirstname = inputfirstname;
        this.inputlastname = inputlastname;
        this.inputpin = inputpin;

        // elements for Center Region of mainBorderPane

        // This VBox pane will align button placement on bottom center of CenterPane
        VBox depositButtonPlacement = new VBox();

        // centerPane elements and additional properties
        Label depositLabel = new Label("Initial Deposit: (min. of $3,000.00)");
        TextField depositText = new TextField();
        Text loginNow = new Text("Already have an account? Log in.");
        Button initialDepositButton = new Button("Register");
        depositLabel.setFont(bodyFont);
        depositText.setFont(bodyFont);
        loginNow.setFont(smallFont);
        loginNow.setCursor(Cursor.HAND);
        loginNow.setFill(Color.rgb(130, 16, 25));
        configureMajorButtonAppearance(initialDepositButton, true);

        depositText.setPrefHeight(40);
        depositText.setTextFormatter(new TextFormatter<String>(doubleFilter));

        initialDepositButton.disableProperty().bind(Bindings.isEmpty(depositText.textProperty()));

        //  Button Action Events
        initialDepositButton.setOnAction(event ->
                initialDepositButtonEvent(depositText.getText(),
                        inputgovID, inputfirstname, inputlastname, inputpin));
        loginNow.setOnMouseClicked(event -> loginPane());

        // Add element to VBox
        depositButtonPlacement.setAlignment(Pos.BOTTOM_CENTER);
        depositButtonPlacement.setSpacing(15);
        depositButtonPlacement.getChildren().addAll(initialDepositButton, loginNow);

        // centerPane properties
        Insets padding = new Insets(25, 250, 0, 300);
        centerContentVBox.setSpacing(15.0);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());
        centerContentVBox.setAlignment(Pos.TOP_LEFT);

        // Add elements to centerPane
        centerContentVBox.getChildren().clear(); // clear first before adding designated element
        centerContentVBox.getChildren().addAll(setErrorTexts(errorText), depositLabel, depositText, depositButtonPlacement);

        // Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setRight(null);
        mainBorderPane.setLeft(null);


        return scene;
    }

    // BUTTON EVENT METHODS BELOW

    /**
     * Scene displays to ask user initial deposit
     * - topPane with rectangle (80px height) and masked account number
     * - centerPane displays all user input for confirmation
     *
     * @param inputInitialDeposit
     * @param inputgovID
     * @param inputfirstname
     * @param inputlastname
     * @param inputpin
     * @return Scene
     */
    public Scene registerConfirmPane(double inputInitialDeposit, String inputgovID, String inputfirstname, String inputlastname, String inputpin) {


        // elements for Center Region of mainBorderPane
        // GridPane used to layout textfields at center of window
        GridPane centerGridPane = new GridPane();

        Text pleaseConfirmTxt = new Text("Please Confirm your Details to Open Account.");
        pleaseConfirmTxt.setFont(biggerBodyFont);

        // elements for GridPane
        Label govIDLabel = new Label("Government ID: ");
        Label firstNameLabel = new Label("First Name: ");
        Label lastNameLabel = new Label("Last Name: ");
        Label initialDepositLabel = new Label("Initial Deposit: ");
        TextField govID = new TextField(inputgovID);
        TextField firstName = new TextField(inputfirstname);
        TextField lastName = new TextField(inputlastname);
        TextField initialDeposit = new TextField("$" + inputInitialDeposit);
        for (Label label : Arrays.asList(govIDLabel, firstNameLabel, lastNameLabel, initialDepositLabel)) {
            label.setFont(bodyFont);
        }
        govID.setFont(bodyFont);
        firstName.setFont(bodyFont);
        lastName.setFont(bodyFont);
        initialDeposit.setFont(bodyFont);
        govID.setEditable(false);
        firstName.setEditable(false);
        lastName.setEditable(false);
        initialDeposit.setEditable(false);

        centerGridPane.setHgap(10);
        centerGridPane.setVgap(10);
        centerGridPane.setAlignment(Pos.CENTER);
        // Add the elements to the GridPane (All labels are on 1st col, all user input display on 2nd col)
        centerGridPane.add(govIDLabel, 0, 0, 1, 1);
        centerGridPane.add(firstNameLabel, 0, 1, 1, 1);
        centerGridPane.add(lastNameLabel, 0, 2, 1, 1);
        centerGridPane.add(initialDepositLabel, 0, 3, 1, 1);
        centerGridPane.add(govID, 1, 0, 1, 1);
        centerGridPane.add(firstName, 1, 1, 1, 1);
        centerGridPane.add(lastName, 1, 2, 1, 1);
        centerGridPane.add(initialDeposit, 1, 3, 1, 1);
        // used to place confirm and cancel buttons side by side
        HBox confirmButtons = new HBox();

        Button confirmButton = new Button("Confirm");
        confirmButton.setDefaultButton(true);
        confirmButton.setFont(bodyFont);
        configureMajorButtonAppearance(confirmButton, true);

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setFont(bodyFont);
        configureMajorButtonAppearance(cancelButton, false);

        // Button Action Events
        confirmButton.setOnAction(event -> confirmRegisterButtonEvent(inputInitialDeposit,
                inputgovID,
                inputfirstname,
                inputlastname,
                inputpin
        ));
        cancelButton.setOnAction(event -> logOutButtonEvent());

        // Add elements to HBox
        confirmButtons.getChildren().addAll(confirmButton, cancelButton);
        confirmButtons.setSpacing(10);
        confirmButtons.setAlignment(Pos.BOTTOM_CENTER);

        // centerPane properties
        Insets padding = new Insets(25, 0, 0, 0);
        centerContentVBox.setSpacing(20);
        centerContentVBox.setPadding(padding);
        centerContentVBox.setPrefWidth(scene.getWidth());
        centerContentVBox.setAlignment(Pos.TOP_CENTER);

        // Add elements to centerPane
        centerContentVBox.getChildren().clear();
        // clear first before adding designated element
        centerContentVBox.getChildren().addAll(pleaseConfirmTxt, centerGridPane, confirmButtons);

        // Set up the 4 regions of border pane
        mainBorderPane.setCenter(centerContentVBox);
        mainBorderPane.setRight(null);
        mainBorderPane.setLeft(null);

        return scene;
    }

    public Scene loginButtonEvent(String accountNumber, String PIN) {
        if (BankDatabase.checkAccount(accountNumber, PIN)) {
            mainPane(accountNumber);
            BankDatabase.loggedAccount = accountNumber;
            errorText.setText(" ");
        } else {
            errorText.setText("Wrong Account Number or PIN");
        }
        return null;
    }

    private void logOutButtonEvent() {
        loginPane();
        BankDatabase.loggedAccount = "00000000";
    }

    private void initialDepositButtonEvent(String userInitialDeposit, String usergovID, String userfirstname, String userlastname, String userpin) {
        try {
            if (Double.parseDouble(userInitialDeposit) < 300) {
                errorText.setText("Initial Deposit's minimum is $300.00");
            } else {
                double initialDepositAmount = Double.parseDouble(userInitialDeposit);
                registerConfirmPane(initialDepositAmount, usergovID, userfirstname, userlastname, userpin);
                errorText.setText(" ");
            }
        } catch (Exception e) {
            errorText.setText("Invalid input");
        }
    }

    private void depositButtonEvent(String userInputAmount) {
        try {
            if (Double.parseDouble(userInputAmount) < 1) {
                errorText.setText("Initial Deposit minimum is $1.00");
            } else {
                double depositAmount = Double.parseDouble(userInputAmount);
                confirmPane('d', depositAmount);
                errorText.setText(" ");
            }
        } catch (Exception e) {
            errorText.setText("Invalid input");
        }
    }

    private void withdrawButtonEvent(String userSelectedAmount) {
        try {
            double withdrawAmount = 0;
            //  Since the method of withdrawing is choosing a radio button, switch case will
            //    be used to determine what radio button user has chosen based on its first three
            //    character in a string
            if (userSelectedAmount.charAt(0) == '$') {
                switch (userSelectedAmount.substring(0, 2)) {
                    case "$1" -> withdrawAmount = 100;
                    case "$2" -> withdrawAmount = 200;
                    case "$3" -> withdrawAmount = 300;
                    case "$4" -> withdrawAmount = 400;
                    case "$5" -> withdrawAmount = 500;
                    case "$6" -> withdrawAmount = 600;
                    case "$7" -> withdrawAmount = 700;
                    case "$8" -> withdrawAmount = 800;
                    case "$9" -> withdrawAmount = 900;
                }
                // Check if the amount is greater than the account balance, which will provide an error message if so
                if (Double.parseDouble(BankDatabase.getBalance()) < withdrawAmount) {
                    errorText.setText("Amount is greater than your account balance");
                } else {
                    confirmPane('w', withdrawAmount);
                    errorText.setText(" ");
                }
            } else {
                // This will run if user has chosen the Other Amount radio button
                withdrawAmount = Double.parseDouble(userSelectedAmount);
                if (Double.parseDouble(userSelectedAmount) < 1) {
                    errorText.setText("Amount should be more than $1.00");
                } else if (Double.parseDouble(BankDatabase.getBalance()) < withdrawAmount) {
                    errorText.setText("Amount is greater than your account balance");
                } else {
                    confirmPane('w', withdrawAmount);
                    errorText.setText(" ");
                }

            }

        } catch (Exception e) {
            errorText.setText("Invalid input");
        }
    }

    private void balanceScreenButtonEvent() {
        screenBalancePane(BankDatabase.getBalance());
    }

    private void balanceReceiptButtonEvent() {
        confirmButtonEvent();
    }

    private void backButtonEvent() {
        mainPane();
        errorText.setText(" ");
    }

    // This method is used for the confirm button event for balance and receipt
    private void confirmButtonEvent() {
        // only displays the "Processing..." text
        messagePane("process");

        // delays the display of "______ Successful" text after displaying processing... text
        Timeline processDelay = new Timeline(new KeyFrame(Duration.seconds(3),
                actionEvent -> {
                    messagePane("printSuccess");
                    BankingTransaction.receipt(BankDatabase.loggedAccount,
                            "CHECK BALANCE", BankDatabase.getBalance());
                }
        ));
        processDelay.setCycleCount(1);
        processDelay.play();

        logoutDelay.setCycleCount(1);
        logoutDelay.play();
    }

    private void confirmButtonEvent(char type, double amount) {
        // event for deposit or withdraw transaction

        messagePane("process"); // only displays the "Processing..." text

        if (type == 'd')
            BankDatabase.accountDeposit(amount);
        else if (type == 'w')
            BankDatabase.accountWithdraw(amount);

        // delays the display of "______ Successful" text after displaying processing... text
        Timeline processDelay = new Timeline(new KeyFrame(Duration.seconds(5),
                actionEvent -> {
                    messagePane("transactionSuccess");
                    if (type == 'd') {
                        //.reciept generates the reciept for the transaction
                        BankingTransaction.receipt(BankDatabase.loggedAccount, "DEPOSIT", String.valueOf(amount));
                    } else {
                        BankingTransaction.receipt(BankDatabase.loggedAccount, "WITHDRAW", String.valueOf(amount));
                    }
                }
        ));
        processDelay.setCycleCount(1);
        processDelay.play();

        logoutDelay.setCycleCount(1);
        logoutDelay.play();
    }

    private void confirmRegisterButtonEvent(double initialDeposit, String govID, String firstname, String lastname, String stringPin) {
        // event for registration

        messagePane("process");
        HashMap<String, String> accountName = new HashMap<>();
        accountName.put("firstname", firstname);
        accountName.put("lastname", lastname);
        long pin = Long.parseLong(stringPin);
        String newAccountNumber = BankDatabase.generateAccountNumber();
        BankDatabase.openAccount(newAccountNumber, govID, accountName, initialDeposit, pin);

        Timeline processDelay = new Timeline(
                new KeyFrame(Duration.seconds(3),
                        actionEvent -> {
                            BankDatabase.loggedAccount = newAccountNumber;
                            messagePane("registerSuccess");
                        }
                ));
        processDelay.setCycleCount(1);
        processDelay.play();

    }

    /*
     * This method is used to check the conditions before registering a new account
     * Enter pin twice
     * Pin must be 6 digits only
     * Government id must be unique
     */
    private void registerButtonEvent(String govID, String firstname, String lastname, String pin, String confirmPIN) {

        if (!BankDatabase.checkUserIdAlreadyExisting(govID)) {
            if (pin.length() == 6 && confirmPIN.length() == 6) {
                if (pin.equals(confirmPIN)) {
                    initialDepositRegisterPane(govID,
                            firstname,
                            lastname,
                            pin);
                } else {
                    errorText.setText("Please confirm your PIN correctly.");
                }
            } else {
                errorText.setText("PIN must be 6 digits only.");
            }
        } else {
            errorText.setText("Government ID already registered.");
        }
    }

    private void cancelButtonEvent() {
        mainPane();
    }
}