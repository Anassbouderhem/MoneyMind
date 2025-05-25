package com.MoneyMind.projet_javafx.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class TransferMoneyTab extends Tab {

    private final TextField toUserField = new TextField();
    private final TextField amountField = new TextField();
    private final Button transferButton = new Button("Transfer");
    private final Label statusLabel = new Label();

    private final DataStorage dataStorage;

    public TransferMoneyTab(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        setText("Transfer Money");

        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Send Money to Another User");
        title.setFont(Font.font("Arial", 20));

        HBox toUserBox = new HBox(10, new Label("To User:"), toUserField);
        toUserBox.setAlignment(Pos.CENTER_LEFT);

        HBox amountBox = new HBox(10, new Label("Amount:"), amountField);
        amountBox.setAlignment(Pos.CENTER_LEFT);

        transferButton.setOnAction(e -> handleTransfer());

        form.getChildren().addAll(
                title,
                toUserBox,
                amountBox,
                transferButton,
                statusLabel
        );

        setContent(form);
    }

    private void handleTransfer() {
        String toUser = toUserField.getText().trim();
        String amountStr = amountField.getText().trim();
        if (toUser.isEmpty() || amountStr.isEmpty()) {
            statusLabel.setText("Please enter both recipient username and amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                statusLabel.setText("Amount must be positive.");
                return;
            }
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid amount.");
            return;
        }
        String fromUser = dataStorage.getLoggedUser().getUsername();
        if (fromUser.equals(toUser)) {
            statusLabel.setText("Cannot transfer to yourself.");
            return;
        }
        try {
            boolean success = dataStorage.transferMoney(fromUser, toUser, amount);
            if (success) {
                statusLabel.setText("Transfer successful!");
                toUserField.clear();
                amountField.clear();
            }
        } catch (Exception ex) {
            statusLabel.setText("Transfer failed: " + ex.getMessage());
        }
    }
}