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
            e.printStackTrace();
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
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) return false;
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                this.loggedUser = new User(userId, username, password);
                connection.commit();
                return true;
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
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    // ==================== Méthodes Budget ====================

    public List<Budget> getUserBudgets() throws SQLException {
        if (loggedUser == null) return new ArrayList<>();
        String sql = "SELECT b.name, b.amount, b.current FROM budgets b WHERE b.user_id = ?";
        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                budgets.add(new Budget(rs.getString("name"), rs.getDouble("amount"), rs.getDouble("current")));
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
            int categoryId = -1;
            boolean hasCategory = category != null && !category.isEmpty();
            if (hasCategory) {
                categoryId = getCategoryId(category);
            }
            String type = amount >= 0 ? "INCOME" : "EXPENSE";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, this.loggedUser.getId());
                pstmt.setString(2, name);
                pstmt.setDouble(3, amount);
                if (hasCategory) {
                    pstmt.setInt(4, categoryId);
                } else {
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                }
                pstmt.setString(5, date.toString());
                pstmt.setString(6, type);
                pstmt.executeUpdate();

                // Update budget or total
                if (hasCategory) {
                    updateBudgetSpending(categoryId, amount);

                    // Update in-memory Budget object
                    for (Budget b : this.loggedUser.getBudgets()) {
                        if (b.getName().equals(category)) {
                            if (amount < 0) { // Expense
                                b.setCurrent(b.getCurrent() - Math.abs(amount));
                            } else { // Income
                                b.setCurrent(b.getCurrent() + amount);
                            }
                            break;
                        }
                    }
                } else {
                    // No category: subtract from total budget
                    double newTotal = this.loggedUser.getTotalLimit() - Math.abs(amount);
                    this.loggedUser.setTotalLimit(newTotal);
                    updateUserTotalLimit(this.loggedUser, newTotal);
                }

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
        String sql = "SELECT t.name, t.amount, c.name as category, t.date FROM transactions t LEFT JOIN categories c ON t.category_id = c.category_id WHERE t.user_id = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("name"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        }
        return transactions;
    }

    // ==================== Méthodes Utilitaires ====================

    private void loadUserData() throws SQLException {
        // Implementation omitted for brevity
    }

    private int getCategoryId(String categoryName) throws SQLException {
        String sql = "SELECT category_id FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("category_id");
        }
        throw new SQLException("Category not found: " + categoryName);
    }

    private void updateBudgetSpending(int categoryId, double amount) throws SQLException {
        String sql = "UPDATE budgets SET current = current + ? WHERE category_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, categoryId);
            pstmt.setInt(3, loggedUser.getId());
            pstmt.executeUpdate();
            connection.commit();
        }
    }

    private String getCurrentMonthYear() {
        LocalDate now = LocalDate.now();
        return now.getMonthValue() + "-" + now.getYear();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Transaction> getTransactions() {
        try {
            return getUserTransactions();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void removeBudget(String name) {
        String sql = "DELETE FROM budgets WHERE user_id = ? AND name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserTotalLimit(User loggedUser, double totalBudgetAmount) throws SQLException {
        String sql = "UPDATE users SET total_limit = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, totalBudgetAmount);
            pstmt.setInt(2, loggedUser.getId());
            pstmt.executeUpdate();
            connection.commit();
        }
    }

    public void addBudget(User loggedUser, Budget newBudget) {
        String sql = "INSERT INTO budgets (user_id, category_id, name, amount, current, month_year) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            int categoryId = getCategoryId(newBudget.getName());
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, loggedUser.getId());
                pstmt.setInt(2, categoryId);
                pstmt.setString(3, newBudget.getName());
                pstmt.setDouble(4, newBudget.getAmount());
                pstmt.setDouble(5, newBudget.getCurrent());
                pstmt.setString(6, getCurrentMonthYear());
                pstmt.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public List<String> getAllCategories() throws SQLException {
        String sql = "SELECT name FROM categories";
        List<String> categories = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    public void removeTransaction(User loggedUser, String name) {
        String sql = "DELETE FROM transactions WHERE user_id = ? AND name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loggedUser.getId());
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== Assistant ====================

    public List<Transaction> getTransactionsBetweenDates(int userId, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT t.name, t.amount, c.name as category, t.date FROM transactions t LEFT JOIN categories c ON t.category_id = c.category_id WHERE t.user_id = ? AND t.date BETWEEN ? AND ?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, start.toString());
            pstmt.setString(3, end.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("name"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        }
        return transactions;
    }

    public List<Budget> getUserBudgets(int userId) throws SQLException {
        String sql = "SELECT b.name, b.amount, b.current FROM budgets b WHERE b.user_id = ?";
        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                budgets.add(new Budget(rs.getString("name"), rs.getDouble("amount"), rs.getDouble("current")));
            }
        }
        return budgets;
    }
}