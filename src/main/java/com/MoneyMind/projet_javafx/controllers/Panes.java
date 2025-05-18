package com.MoneyMind.projet_javafx.controllers;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/*
 * This class contains all the panes being used in the program
 * Main panes are BorderPane, StackPane, HBox, VBox, GridPane
 */
class Panes {
    BorderPane mainBorderPane = new BorderPane();
    StackPane headerStackPane = new StackPane();
    HBox accountNumberHBox = new HBox();
    VBox centerContentVBox = new VBox();
    VBox rightSidebarVBox = new VBox();
    VBox leftSidebarVBox = new VBox();


    Rectangle maroonTopBanner = new Rectangle();
    // BUTTONS ------------
    Button backButton = new Button("<—");
    Button logoutButton = new Button("Log out");

    Text errorText = new Text(" ");
    //Font messinaSerif = Font.loadFont("file:resources/MessinaSerif-Regular.ttf", 45);
    //doesn't work at all times, or doesn't load properly, I can't tell.
    //FONTS
    // Create the welcome text

    Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Bubbleboddy.ttf"), 24);
    Font titleFont2 = Font.font("DejaVu Serif", 32);
    Font biggerBodyFont = Font.font("DejaVu Serif", 22);
    Font bodyFont = Font.font("DejaVu Serif", 16);
    Font smallFont = Font.font("DejaVu Serif", 10);
    Scene scene = new Scene(mainBorderPane, 820, 470, Color.WHITE);

    public ImageRectangle rectSmall() {
        ImageRectangle imageRectangle = new ImageRectangle(scene.getWidth(), 80);
        imageRectangle.bindWidth(scene.widthProperty());
        return imageRectangle;
    }

    public ImageRectangle rectLarge() {
        ImageRectangle imageRectangle = new ImageRectangle(scene.getWidth(), 100);
        imageRectangle.bindWidth(scene.widthProperty());
        return imageRectangle;
    }


    public Text setErrorTexts(Text errorText) {
        errorText.setFont(smallFont);
        errorText.setFill(Color.rgb(225, 0, 40));
        errorText.setText("");
        return errorText;
    }
}
