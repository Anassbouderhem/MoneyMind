package com.MoneyMind.projet_javafx.assistant_AI;

import com.MoneyMind.projet_javafx.controllers.Budget;
import com.MoneyMind.projet_javafx.controllers.Transaction;
import com.MoneyMind.projet_javafx.controllers.DataStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class AIAssistant {
    private final DataStorage dataStorage;

    public AIAssistant(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public String generateMonthlyAdvice(int userId) throws SQLException {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());

        // Récupération des transactions
        List<Transaction> monthlyTransactions = dataStorage.getTransactionsBetweenDates(userId, firstDay, lastDay);

        // Dépenses par catégorie (en utilisant type)
        Map<String, Double> spendingByCategory = monthlyTransactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase("EXPENSE"))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Budgets
        List<Budget> budgets = dataStorage.getUserBudgets(userId);
        Map<String, Double> budgetLimits = budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getName, // ← Remplacer getName si nécessaire
                        Budget::getAmount,
                        Double::sum // Si plusieurs budgets pour une catégorie
                ));

        StringBuilder advice = new StringBuilder(" Conseils pour " + now.getMonth() + ":\n\n");

        // Analyse par catégorie
        spendingByCategory.forEach((category, amount) -> {
            double limit = budgetLimits.getOrDefault(category, 0.0);
            if (limit > 0) {
                double percentage = (amount / limit) * 100;
                if (percentage > 90) {
                    advice.append("⚠ Vous avez dépensé ")
                            .append(String.format("%.0f%%", percentage))
                            .append(" de votre budget pour la catégorie ")
                            .append(category)
                            .append(". Pensez à ralentir !\n");
                }
            }
        });

        // Détection de comportements inhabituels
        detectUnusualSpending(monthlyTransactions, advice);

        // Conseil général
        double totalSpent = spendingByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalBudget = budgetLimits.values().stream().mapToDouble(Double::doubleValue).sum();

        advice.append("\n Conseil général : ");
        if (totalBudget > 0 && totalSpent > totalBudget * 0.7) {
            advice.append("Vous avez dépensé ")
                    .append(String.format("%.0f%%", (totalSpent / totalBudget) * 100))
                    .append(" de votre budget total. Pensez à revoir vos priorités.");
        } else {
            advice.append("Votre gestion budgétaire est bonne ce mois-ci. Continuez ainsi !");
        }

        // 🔍 Recommandation de réduction
        Optional<Map.Entry<String, Double>> mostExpensiveCategory = spendingByCategory.entrySet().stream()
                .sorted((e1, e2) -> {
                    double limit1 = budgetLimits.getOrDefault(e1.getKey(), 1.0);
                    double limit2 = budgetLimits.getOrDefault(e2.getKey(), 1.0);
                    double ratio1 = e1.getValue() / limit1;
                    double ratio2 = e2.getValue() / limit2;
                    return Double.compare(ratio2, ratio1); // Ordre décroissant
                })
                .findFirst();

        mostExpensiveCategory.ifPresent(entry -> {
            String category = entry.getKey();
            double spent = entry.getValue();
            double limit = budgetLimits.getOrDefault(category, 0.0);
            if (limit > 0 && spent > limit * 0.8) {
                advice.append("\nConseil ciblé : La catégorie \"")
                        .append(category)
                        .append("\" représente une part importante de vos dépenses ce mois-ci. Essayez de la réduire !");
            } else if (limit == 0) {
                advice.append("\nConseil ciblé : Vous dépensez beaucoup dans \"")
                        .append(category)
                        .append("\" sans budget alloué. Il est recommandé de définir un budget pour cette catégorie ou de la limiter.");
            }
        });


        return advice.toString();
    }

    private void detectUnusualSpending(List<Transaction> transactions, StringBuilder advice) {
        // Détection des grosses dépenses
        Optional<Transaction> largestExpense = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .max(Comparator.comparingDouble(t -> Math.abs(t.getAmount())));

        largestExpense.ifPresent(expense ->
                advice.append("\nDépense la plus importante: ")
                        .append(expense.getName())
                        .append(" (")
                        .append(String.format("%.2f", Math.abs(expense.getAmount())))
                        .append("DH)\n"));

        // Détection des dépenses fréquentes
        Map<String, Long> frequentCategories = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.counting()
                ));

        frequentCategories.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry ->
                        advice.append("️ Catégorie la plus fréquente: ")
                                .append(entry.getKey())
                                .append(" (")
                                .append(entry.getValue())
                                .append(" transactions)\n"));
    }
}
