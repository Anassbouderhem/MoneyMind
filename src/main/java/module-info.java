module com.MoneyMind.projet_javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires com.opencsv;
    requires java.sql;
    requires okhttp3;
    requires org.json;
    // Packages ouverts au chargement FXML (réflexion)
    opens com.MoneyMind.projet_javafx.controllers to javafx.fxml;

    // Packages rendus publics aux autres modules éventuels
    exports com.MoneyMind.projet_javafx.controllers;
    exports com.MoneyMind.projet_javafx.db;
    opens com.MoneyMind.projet_javafx.db to javafx.fxml;
}
