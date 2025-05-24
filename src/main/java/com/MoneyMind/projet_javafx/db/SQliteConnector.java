package com.MoneyMind.projet_javafx.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQliteConnector {
    private static final String DB_URL = "jdbc:sqlite:money_mind.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite introuvable");
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}