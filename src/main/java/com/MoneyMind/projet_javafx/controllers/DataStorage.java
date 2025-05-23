package com.MoneyMind.projet_javafx.controllers;

import com.MoneyMind.projet_javafx.db.SQliteConnector;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {

    private User loggedUser;
    private Connection connection;

    // ==================== Constructeur ====================
    public DataStorage() {
        try {
            this.connection = SQliteConnector.connect();
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

    // ==================== Méthodes Utilisateur ====================

    public boolean registerUser(String username, String password) throws SQLException {
        if (usernameExists(username)) {
            return false;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.loggedUser = new User(rs.getInt(1), username, password );
                    connection.commit();
                    return true;
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        return false;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==================== Méthodes Budget ====================


    public List<Budget> getUserBudgets() throws SQLException {
        if (loggedUser == null) return new ArrayList<>();

        String sql = "SELECT b.name, b.amount, b.current FROM budgets b WHERE b.user_id = ?";
        List<Budget> budgets = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    budgets.add(new Budget(
                            rs.getString("name"),
                            rs.getDouble("amount"),
                            rs.getDouble("current")
                    ));
                }
            }
        }
        return budgets;
    }

    // ==================== Méthodes Transaction ====================

    public void addTransaction(User loggedUser, String name, double amount, String category, LocalDate date) throws SQLException {
        if (this.loggedUser == null) throw new IllegalStateException("Aucun utilisateur connecté");

        String sql = """
            INSERT INTO transactions (user_id, name, amount, category_id, date, type)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try {
            int categoryId = getCategoryId(category);
            String type = amount >= 0 ? "INCOME" : "EXPENSE";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, this.loggedUser.getId());
                pstmt.setString(2, name);
                pstmt.setDouble(3, amount);
                pstmt.setInt(4, categoryId);
                pstmt.setString(5, date.toString());
                pstmt.setString(6, type);
                pstmt.executeUpdate();

                updateBudgetSpending(categoryId, amount);
                this.loggedUser.getTransactions().add(new Transaction(name, amount, category, date));
                connection.commit();
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public List<Transaction> getUserTransactions() throws SQLException {
        if (loggedUser == null) return new ArrayList<>();

        String sql = """
            SELECT t.name, t.amount, t.date, c.name as category 
            FROM transactions t
            JOIN categories c ON t.category_id = c.category_id
            WHERE t.user_id = ?
            ORDER BY t.date DESC
        """;

        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                            rs.getString("name"),
                            rs.getDouble("amount"),
                            rs.getString("category"),
                            LocalDate.parse(rs.getString("date"))
                    ));
                }
            }
        }
        return transactions;
    }

    // ==================== Méthodes Utilitaires ====================

    private void loadUserData() throws SQLException {
        loggedUser.setBudgets(getUserBudgets());
        loggedUser.setTransactions(getUserTransactions());
    }

    private int getCategoryId(String categoryName) throws SQLException {
        String sql = "SELECT category_id FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                }
            }
        }
        throw new SQLException("Catégorie non trouvée: " + categoryName);
    }

    private void updateBudgetSpending(int categoryId, double amount) throws SQLException {
        if (amount >= 0) return;

        String currentMonthYear = getCurrentMonthYear();
        String sql = """
            UPDATE budgets 
            SET current = current + ? 
            WHERE category_id = ? 
            AND month_year = ?
            AND user_id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, Math.abs(amount));
            pstmt.setInt(2, categoryId);
            pstmt.setString(3, currentMonthYear);
            pstmt.setInt(4, loggedUser.getId());
            pstmt.executeUpdate();
        }
    }

    private String getCurrentMonthYear() {
        return LocalDate.now().getMonthValue() + "-" + LocalDate.now().getYear();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture: " + e.getMessage());
        }
    }

    // ==================== Getters ====================

    public User getLoggedUser() {
        return loggedUser;
    }



    public List<Budget> getBudgets() {
        try {
            return getUserBudgets();
        } catch (SQLException e) {
            e.printStackTrace(); // Ou logger proprement
            return new ArrayList<>(); // Retour vide en cas d'erreur
        }
    }

    public List<Transaction> getTransactions() {
        try {
            return getUserTransactions();
        } catch (SQLException e) {
            e.printStackTrace(); // Ou logger proprement
            return new ArrayList<>(); // Retour vide en cas d'erreur
        }
    }

    public void removeBudget(String name) {
        if (loggedUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }

        String sql = "DELETE FROM budgets WHERE user_id = ? AND name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            pstmt.setString(2, name);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Budget supprimé avec succès.");
            } else {
                System.out.println("Aucun budget trouvé à supprimer.");
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback : " + ex.getMessage());
            }
            System.err.println("Erreur lors de la suppression du budget.");
        }
    }


    public void updateUserTotalLimit(User loggedUser, double totalBudgetAmount) throws SQLException {
        if (loggedUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }

        String sql = "UPDATE users SET total_limit = ? WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, totalBudgetAmount);
            pstmt.setInt(2, loggedUser.getId());
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                loggedUser.setTotalLimit(totalBudgetAmount);
                connection.commit();
                System.out.println("Limite totale mise à jour: " + totalBudgetAmount);
            } else {
                System.out.println("Aucun utilisateur trouvé avec l'ID: " + loggedUser.getId());
            }
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Erreur lors de la mise à jour de la limite totale", e);
        }
    }

    public void addBudget(User loggedUser, Budget newBudget) {
        if (loggedUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }

        String sql = """
        INSERT INTO budgets (user_id, name, amount, current, category_id, month_year)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try {
            int categoryId = getCategoryId(newBudget.getName()); // Utilisez getName() si c'est le nom de la catégorie
            String currentMonthYear = getCurrentMonthYear();

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // L'ordre des paramètres doit correspondre exactement à votre requête SQL
                pstmt.setInt(1, loggedUser.getId());
                pstmt.setString(2, newBudget.getName());
                pstmt.setDouble(3, newBudget.getAmount());
                pstmt.setDouble(4, newBudget.getCurrent());
                pstmt.setInt(5, categoryId);
                pstmt.setString(6, currentMonthYear);

                pstmt.executeUpdate();
                connection.commit();
                System.out.println("Budget ajouté avec succès : " + newBudget.getName());
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback : " + ex.getMessage());
            }
            throw new RuntimeException("Erreur lors de l'ajout du budget: " + e.getMessage(), e);
        }
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        if (loggedUser != null) {
            if (loggedUser.getTransactions() == null) {
                loggedUser.setTransactions(new ArrayList<>());
            }
            if (loggedUser.getBudgets() == null) {
                loggedUser.setBudgets(new ArrayList<>());
            }

        }
    }

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM categories";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    public void removeTransaction(User loggedUser, String name) {
        if (loggedUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }

        String sql = "DELETE FROM transactions WHERE user_id = ? AND name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            pstmt.setString(2, name);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transactions supprimé avec succès.");
            } else {
                System.out.println("Aucun transaction trouvé à supprimer.");
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback : " + ex.getMessage());
            }
            System.err.println("Erreur lors de la suppression du transaction.");
        }
    }

    // ==================== Assistant ====================

    public List<Transaction> getTransactionsBetweenDates(int userId, LocalDate start, LocalDate end) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = """
        SELECT t.name, t.amount, c.name AS category_name, t.date
        FROM transactions t
        JOIN categories c ON t.category_id = c.category_id
        WHERE t.user_id = ? AND t.date BETWEEN ? AND ?
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, start.toString());
            pstmt.setString(3, end.toString());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("name"),
                        rs.getDouble("amount"),
                        rs.getString("category_name"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        }
        return transactions;
    }




    public List<Budget> getUserBudgets(int userId) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT name, amount FROM budgets WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                budgets.add(new Budget(
                        rs.getString("name"),
                        rs.getDouble("amount"),
                        0 // current amount
                ));
            }
        }
        return budgets;
    }
}
