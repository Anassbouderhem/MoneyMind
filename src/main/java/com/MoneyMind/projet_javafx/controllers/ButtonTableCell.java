package com.MoneyMind.projet_javafx.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.function.Function;

/* Button table cells */
/* Creates table cells with buttons - compatible with event handlers */
/* ---------------------------------------------------------- */
/* Source: https://stackoverflow.com/questions/29489366/how-to-add-button-in-javafx-table-view */

public class ButtonTableCell<S> extends TableCell<S, Button> {
    private final Button actionButton;

    public ButtonTableCell(String label, Function< S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        this.actionButton = new Button(label);
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        this.actionButton.setMaxWidth(10);
//        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Bubbleboddy.ttf"), 14);
//        this.actionButton.setFont(font);

    }

    public ButtonTableCell(Image img, Function< S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(15); // Set the desired width
        imageView.setFitHeight(15); // Set the desired height

        this.actionButton = new Button();
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        // Make the button transparent
        this.actionButton.setStyle("-fx-background-color: transparent;");

        // Set the button's borders and fill to be transparent as well
        this.actionButton.setBorder(null);
        this.actionButton.setBackground(null);


        this.actionButton.setGraphic(imageView);

    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(String label, Function< S, S> function) {
        return param -> new ButtonTableCell<>(label, function);
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(Image img, Function< S, S> function) {
        return param -> new ButtonTableCell<>(img, function);
    }

    @Override
    public void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }
}
