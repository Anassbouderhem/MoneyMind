package com.MoneyMind.projet_javafx.controllers;

import com.MoneyMind.projet_javafx.controllers.DataStorage;
import javafx.application.Application;
import com.MoneyMind.projet_javafx.db.DBInitializer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.stage.Stage;

public class App extends Application{

    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;
    private static final String LOGO_PATH = "Logo.gif";

    public void start(Stage primaryStage) {

        DataStorage dataStorage = new DataStorage();

        Image icon = new Image("Logo.png");
        LoginScene loginScene = new LoginScene(dataStorage, primaryStage);
        Scene lScene = loginScene.getScene();



        BorderPane root = new BorderPane();

        Scene scene2 = new Scene(root, 600, 400);
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene2);
        primaryStage.setTitle("Money Mind");

        // Create the intro screen with the logo
        ImageView logo = new ImageView(new Image(LOGO_PATH));
        logo.setPreserveRatio(true);
        logo.setFitWidth(200);

        // Create the welcome text
        Text welcomeText = new Text("Welcome to Money Mind!");
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

        primaryStage.setResizable(true);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);

        // Apply the custom logo transition
        LogoTransition logoTransition = new LogoTransition(logo, welcomeText, separator, introPane);
        logoTransition.setOnFinished(event -> {
            primaryStage.setScene(lScene);
        });
        logoTransition.play();

        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Chaque dirham compte");
        primaryStage.show();
    }

    public static void main(String[] args) {
        DBInitializer.initializeDatabase();
        launch(args); }
}
