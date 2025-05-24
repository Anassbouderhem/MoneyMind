package com.MoneyMind.projet_javafx.controllers;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.function.UnaryOperator;

/**
 * This class contains the styling and properties of each element in the application.
 * This separates the codes that handles the styling of each element to scenes
 */
abstract class Styling extends Panes {

    /**
     * This UnaryOperator, named 'digitFilter', checks if the user input contains only digits.
     * It is useful for restricting text fields to accept only numerical values.
     */
    UnaryOperator<TextFormatter.Change> digitFilter = change -> {
        String userInput = change.getText();
        if (userInput.matches("[0-9]*")) {
            return change;
        }
        return null;
    };
    /**
     * This UnaryOperator, named 'doubleFilter', checks if the user input contains only numbers
     * and an optional decimal point. It is useful for restricting text fields to accept
     * only floating-point values.
     */
    UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        String userinput = change.getText();
        if (userinput.matches("\\d*\\.?\\d*")) {
            return change;
        }
        return null;
    };

    public StackPane createLogoHeader() {
        StackPane headerStackPane = new StackPane();
        headerStackPane.setAlignment(Pos.CENTER);

        Text title = new Text("MoneyMind\nRegistration");
        title.setFill(Color.web("#50C878"));
        title.setFont(titleFont);
        title.setTextOrigin(VPos.TOP);
        title.setTextAlignment(TextAlignment.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.add(rectLarge(), 0, 0);
        gridPane.add(title, 0, 1);

        GridPane.setHalignment(title, HPos.CENTER);

        headerStackPane.getChildren().addAll(gridPane);
        return headerStackPane;
    }


    private Button button;
    private boolean isSubmitButton;

    /**
     * @param button         This is the parameter for the submit button when it is hovered upon
     * @param isSubmitButton This is the parameter for the submit button checking when
     * @return Button This returns the button with the needed effects
     */
    // BUTTON PROPERTIES AND STYLING
    public Button configureMajorButtonAppearance(Button button, boolean isSubmitButton) {
        this.button = button;
        this.isSubmitButton = isSubmitButton;
        button.setFont(bodyFont);
        button.setTextFill(Color.WHITE);
        button.setPrefWidth(150);
        button.setCursor(Cursor.HAND);

        //This is the configuration for the submit button when it is hovered upon
        //if statement is needed to check if the button is active
        if (isSubmitButton)
            buttonHoverEffectGreen(button);
        return button;
    }

    public Button setBackButtonAppearance() {
        backButton.setFont(bodyFont);
        backButton.setTextFill(Color.WHITE);
        backButton.setPrefWidth(40);
        backButton.setCursor(Cursor.HAND);
        buttonHoverEffectGray(backButton);
        return backButton;
    }

    /**
     * This method sets the hover effect for the button.
     * It changes the button color between light gray and dark gray
     * when the mouse is hovered over it.
     *
     * @param button This is the parameter for the back button when it is hovered upon
     */
    void buttonHoverEffectGray(Button button) {
        button.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #D2D4D6;"));
        button.setStyle("-fx-background-color:#5A646E;");
        button.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #5A646E;"));
    }

    /**
     * This method sets the hover effect for the button.
     * It changes the button color between maroon and maple red
     * when the mouse is hovered over it.
     *
     * @param button This is the parameter for the back button when it is hovered upon
     */
    void buttonHoverEffectGreen(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #AFE1AF;"));
        button.setStyle("-fx-background-color:#50C878;");
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #50C878;"));
    }

    public Button setLogOutButton() {
        logoutButton.setFont(bodyFont);
        logoutButton.setTextFill(Color.WHITE);
        logoutButton.setPrefWidth(100);
        logoutButton.setCursor(Cursor.HAND);
        logoutButton.setStyle("-fx-background-color:#5A646E;");
        return logoutButton;
    }

    void designTopPane(String maskedAccountNumber) {
        headerStackPane.getChildren().clear();
        accountNumberHBox.getChildren().clear();


        // elements for the Top Region of mainBorderPane
        //  Top Pane consists of the rectangle and the masked account number
        Text displayAccountNum = new Text(maskedAccountNumber);
        displayAccountNum.setFill(Color.WHITE);
        displayAccountNum.setFont(titleFont);

        accountNumberHBox.getChildren().add(displayAccountNum);
        accountNumberHBox.setPadding(new Insets(20, 0, 0, 100));

        headerStackPane.getChildren().addAll(rectSmall(), accountNumberHBox);
    }

    // method masks the String account number (the first 8 numbers only) which will be displayed on top left
    //   of the scene after user has logged in
    String maskDisplayNumber(String accountNumber) {
        char[] maskNumArray = accountNumber.toCharArray();

        for (int i = 0; i < 8; i++) {
            maskNumArray[i] = '*';
        }
        return String.valueOf(maskNumArray);
    }

    // Method sets the properties of 4 main buttons for mainPane
    void setMainButtonsProperties(Button button) {
        Font bodyFont = Font.font("DejaVu Serif", 16);
        button.setFont(bodyFont);
        buttonHoverEffectGreen(button);
        button.setTextFill(Color.WHITE);
        button.setPrefWidth(250);
        button.setPrefHeight(40);
        button.setCursor(Cursor.HAND);
    }

    protected abstract Scene loginButtonEvent(String username, String password);
}
