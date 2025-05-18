package com.MoneyMind.projet_javafx.controllers;

import javafx.animation.Transition;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LogoTransition extends Transition {

    private final ImageView logo;
    private final Text welcomeText;
    private final Separator separator;
    private final Pane introPane;

    public LogoTransition(ImageView logo, Text welcomeText, Separator separator, Pane introPane) {
        this.logo = logo;
        this.welcomeText = welcomeText;
        this.separator = separator;
        this.introPane = introPane;
        setCycleDuration(Duration.millis(2900));
    }

    @Override
    protected void interpolate(double frac) {
        double translateY = -60 * frac;
        double fade = Math.min(1, 2 * frac);

        logo.setTranslateY(translateY);
        welcomeText.setTranslateY(translateY);
        separator.setTranslateY(translateY);
        introPane.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 1 - fade),
                CornerRadii.EMPTY,
                null)));
    }
}