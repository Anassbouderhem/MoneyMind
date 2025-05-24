package com.MoneyMind.projet_javafx.db;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class DBInitializer {
    private static final String FOREIGN_KEYS_ON = "PRAGMA foreign_keys = ON";
    private static final String DB_NAME = "money_mind.db";

    // Structure des tables avec vérification d'existence
    private static final List<TableDefinition> TABLES = Arrays.asList(
            new TableDefinition("users", """
            CREATE TABLE users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                total_limit REAL DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )"""),

            new TableDefinition("categories", """
            CREATE TABLE categories (
                category_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                type TEXT NOT NULL CHECK(type IN ('INCOME', 'EXPENSE')),
                icon_name TEXT
            )"""),

            new TableDefinition("budgets", """
            CREATE TABLE budgets (
                budget_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                amount REAL NOT NULL,
                current REAL DEFAULT 0,
                month_year TEXT NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                FOREIGN KEY(category_id) REFERENCES categories(category_id),
                UNIQUE(user_id, name, month_year)
            )"""),

            new TableDefinition("transactions", """
            CREATE TABLE transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                amount REAL NOT NULL,
                type TEXT CHECK(type IN ('INCOME', 'EXPENSE')),
                date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                description TEXT,
                FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                FOREIGN KEY(category_id) REFERENCES categories(category_id)
            )""")
    );

    // Catégories par défaut avec leur type
    private static final List<Category> DEFAULT_CATEGORIES = Arrays.asList(
            new Category("Loyer", "EXPENSE"),
            new Category("Nourriture", "EXPENSE"),
            new Category("Transport", "EXPENSE"),
            new Category("Salaire", "INCOME"),
            new Category("Loisirs", "EXPENSE"),
            new Category("Éducation", "EXPENSE")
    );

    public static void initializeDatabase() {
        try (Connection conn = SQliteConnector.connect()) {
            conn.setAutoCommit(false);

            enableForeignKeys(conn);
            createTables(conn);
            insertDefaultCategories(conn);

            conn.commit();
            System.out.println(" Base de données initialisée avec succès");

        } catch (SQLException e) {
            throw new DatabaseInitializationException(
                    "Échec de l'initialisation de la base de données", e);
        }
    }

    private static void enableForeignKeys(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(FOREIGN_KEYS_ON);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            for (TableDefinition table : TABLES) {
                if (!tableExists(conn, table.getName())) {
                    stmt.execute(table.getCreateSql());
                    System.out.println("Table " + table.getName() + " créée");
                }
            }
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private static void insertDefaultCategories(Connection conn) throws SQLException {
        String sql = "INSERT OR IGNORE INTO categories (name, type) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Category category : DEFAULT_CATEGORIES) {
                pstmt.setString(1, category.getName());
                pstmt.setString(2, category.getType());
                pstmt.addBatch();
            }
            int[] results = pstmt.executeBatch();
            System.out.println("Catégories par défaut insérées : " + Arrays.stream(results).sum());
        }
    }

    // Classes internes pour une meilleure organisation
    private static class TableDefinition {
        private final String name;
        private final String createSql;

        public TableDefinition(String name, String createSql) {
            this.name = name;
            this.createSql = createSql;
        }

        public String getName() { return name; }
        public String getCreateSql() { return createSql; }
    }

    private static class Category {
        private final String name;
        private final String type;

        public Category(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public String getType() { return type; }
    }

    public static class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}