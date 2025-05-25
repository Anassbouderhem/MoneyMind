package com.MoneyMind.projet_javafx.controllers;

import com.MoneyMind.projet_javafx.db.UserDAO;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LoginScene extends Styling {

    private DataStorage dataStorage;
    private Scene scene;
    private Text errorText;
    private BorderPane mainBorderPane;
    private StackPane headerStackPane;

    private Stage primaryStage;

    public LoginScene(DataStorage dataStorage, Stage primaryStage) {
        this.dataStorage = dataStorage;
        this.primaryStage = primaryStage;
        mainBorderPane = new BorderPane();
        headerStackPane = new StackPane();
        scene = createLoginScene();
    }

    public Scene createLoginScene() {

        errorText = new Text();

        setErrorTexts(errorText);

        RegisterScene registerPane = new RegisterScene(dataStorage, primaryStage, this);
        Scene rScene = registerPane.getScene();

        headerStackPane = new StackPane();
        headerStackPane.setAlignment(Pos.CENTER);

        VBox loginForm = new VBox();
        VBox loginForm2 = new VBox();

        // elements for the Top Region of the mainBorderPane
        Text title = new Text("Money Mind");

        // StackPane automatically centers the objects
        title.setFill(Color.web("#50C878"));
        title.setFont(titleFont);
        //center title vertically
        title.setTextOrigin(VPos.TOP);
        title.setTextAlignment(TextAlignment.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.add(rectLarge(), 0, 0);
        gridPane.add(title, 0, 1);
        gridPane.add(errorText, 0, 2);

        GridPane.setHalignment(title, HPos.CENTER);
        GridPane.setHalignment(errorText, HPos.CENTER);

        headerStackPane.getChildren().clear();
        headerStackPane.getChildren().addAll(gridPane);

        // elements for the Center Region of mainBorderPane
        Label userLabel = new Label("Username: ");
        Label passLabel = new Label("Password: ");
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        Text registerNow = new Text("Click here if you don't have an account yet.");
        Button loginButton = new Button("Log in");

        Insets padding = new Insets(25, 250, 25, 250);

        // For both Labels and TextFields
        userLabel.setLabelFor(username);
        passLabel.setLabelFor(password);

        userLabel.setFont(bodyFont);
        passLabel.setFont(bodyFont);

        username.setFont(bodyFont);
        password.setFont(bodyFont);

        username.setPrefHeight(40);
        password.setPrefHeight(40);

        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButtonEvent(username.getText(), password.getText());
            }
        });

        // For log in button
        loginButton.setFont(bodyFont);
        buttonHoverEffectGreen(loginButton);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setPrefWidth(100);

        HBox errorTextContainer = new HBox();
        errorTextContainer.setAlignment(Pos.CENTER);
        errorTextContainer.getChildren().add(errorText);

        errorTextContainer.setPadding(new Insets(5, 0, 5, 0));
        errorTextContainer.setSpacing(5);

        // Set ActionEvent on the LogIn Button and register now text
        loginButton.setOnAction(event ->
                loginButtonEvent(username.getText(), password.getText()));
        registerNow.setOnMouseClicked(event -> primaryStage.setScene(rScene));

        // For register now text
        registerNow.setFont(smallFont);
        registerNow.setFill(Color.rgb(225, 0, 40));

        // For hovering the cursor on the elements
        username.setCursor(Cursor.TEXT);
        password.setCursor(Cursor.TEXT);
        loginButton.setCursor(Cursor.HAND);
        registerNow.setCursor(Cursor.HAND);

        // For the overall Log In Form (including the small text and the Login button in loginForm2 layout pane)
        loginForm.setSpacing(5.0);
        loginForm.setPadding(padding);
        loginForm.setStyle("-fx-margin-left: 20px;");

        loginForm2.setSpacing(10.0);
        loginForm2.setAlignment(Pos.BOTTOM_CENTER);
        loginForm2.getChildren().addAll(registerNow, loginButton);

        // Add errorText and other elements to loginForm
        loginForm.getChildren().addAll(errorTextContainer, userLabel, username, passLabel, password, loginForm2);

        // Set up the 4 regions of border pane
        mainBorderPane.setTop(headerStackPane);
        mainBorderPane.setCenter(loginForm);
        mainBorderPane.setLeft(null);
        mainBorderPane.setRight(null);

        // Set the scene instance variable
        scene = new Scene(mainBorderPane, 600, 400);

        return scene;
    }

    @Override
    protected Scene loginButtonEvent(String username, String password) {
        User user = UserDAO.getUserByCredentials(username, password);

        if (user != null) {
            dataStorage.setLoggedUser(user);
            // Pass primaryStage and loginScene to MainWindow
            MainWindow mainWindow = new MainWindow(dataStorage, primaryStage, this.getScene());
            errorText.setText(" ");
            Scene mainWindowScene = mainWindow.createScene();
            primaryStage.setScene(mainWindowScene);
            return mainWindowScene;
        } else {
            errorText.setText("Wrong username or password");
            return null;
        }
    }

    public Scene getScene() {
        return scene;
    }
}