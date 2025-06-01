package com.MoneyMind.projet_javafx.assistant_AI;

import com.MoneyMind.projet_javafx.controllers.Budget;
import com.MoneyMind.projet_javafx.controllers.Transaction;
import com.MoneyMind.projet_javafx.controllers.DataStorage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class AIAssistant {
    private final DataStorage dataStorage;
    private static final double HIGH_SPENDING_THRESHOLD = 0.90; // 90% of budget
    private static final double MODERATE_SPENDING_THRESHOLD = 0.70; // 70% of budget
    private static final double TARGETED_ADVICE_THRESHOLD = 0.80; // 80% of budget

    public AIAssistant(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public String generateMonthlyAdvice(int userId) throws SQLException, IOException {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());

        // Get monthly transactions
        List<Transaction> monthlyTransactions = dataStorage.getTransactionsBetweenDates(userId, firstDay, lastDay);

        if (monthlyTransactions.isEmpty()) {
            return "Aucune transaction trouvée pour ce mois. Commencez à enregistrer vos dépenses pour recevoir des conseils personnalisés.";
        }

        // Calculate spending by category (only expenses - negative amounts)
        Map<String, Double> spendingByCategory = monthlyTransactions.stream()
                .filter(t -> t.getAmount() < 0) // Fixed: filter actual expenses (negative amounts)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> Math.abs(t.getAmount())) // Use absolute values for spending
                ));

        // Get budget limits
        List<Budget> budgets = dataStorage.getUserBudgets(userId);
        Map<String, Double> budgetLimits = budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getName,
                        Budget::getAmount,
                        Double::sum // Handle multiple budgets for same category
                ));

        StringBuilder advice = new StringBuilder("📊 Conseils pour " + now.getMonth() + " " + now.getYear() + ":\n\n");

        // Category-by-category analysis
        analyzeSpendingByCategory(spendingByCategory, budgetLimits, advice);

        // Detect unusual spending patterns
        detectUnusualSpending(monthlyTransactions, advice);

        // General advice
        addGeneralAdvice(spendingByCategory, budgetLimits, advice);

        // Targeted recommendation
        addTargetedRecommendation(spendingByCategory, budgetLimits, advice);

        // Generate AI-powered advice
        String aiAdvice = generateAIAdvice(spendingByCategory, budgetLimits);

        if (aiAdvice != null && !aiAdvice.trim().isEmpty()) {
            advice.append("\n\n🤖 Conseils IA personnalisés:\n").append(aiAdvice);
        }

        return advice.toString();
    }

    private void analyzeSpendingByCategory(Map<String, Double> spendingByCategory,
                                           Map<String, Double> budgetLimits,
                                           StringBuilder advice) {
        boolean hasWarnings = false;

        for (Map.Entry<String, Double> entry : spendingByCategory.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            double limit = budgetLimits.getOrDefault(category, 0.0);

            if (limit > 0) {
                double percentage = (amount / limit) * 100;
                if (percentage > HIGH_SPENDING_THRESHOLD * 100) {
                    advice.append("⚠️ **ALERTE** - Vous avez dépensé ")
                            .append(String.format("%.1f%%", percentage))
                            .append(" de votre budget pour \"")
                            .append(category)
                            .append("\" (")
                            .append(String.format("%.2f", amount))
                            .append(" DH sur ")
                            .append(String.format("%.2f", limit))
                            .append(" DH). Budget dépassé !\n");
                    hasWarnings = true;
                } else if (percentage > MODERATE_SPENDING_THRESHOLD * 100) {
                    advice.append("⚡ **ATTENTION** - Vous avez dépensé ")
                            .append(String.format("%.1f%%", percentage))
                            .append(" de votre budget pour \"")
                            .append(category)
                            .append("\". Surveillez vos dépenses !\n");
                    hasWarnings = true;
                }
            } else if (amount > 0) {
                advice.append("💡 **SUGGESTION** - \"")
                        .append(category)
                        .append("\" : ")
                        .append(String.format("%.2f", amount))
                        .append(" DH dépensés sans budget défini. Considérez créer un budget !\n");
            }
        }

        if (hasWarnings) {
            advice.append("\n");
        }
    }

    private void addGeneralAdvice(Map<String, Double> spendingByCategory,
                                  Map<String, Double> budgetLimits,
                                  StringBuilder advice) {
        double totalSpent = spendingByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalBudget = budgetLimits.values().stream().mapToDouble(Double::doubleValue).sum();

        advice.append("📈 **Analyse générale** :\n");
        advice.append("Total dépensé : ").append(String.format("%.2f", totalSpent)).append(" DH\n");
        advice.append("Budget total : ").append(String.format("%.2f", totalBudget)).append(" DH\n");

        if (totalBudget > 0) {
            double percentage = (totalSpent / totalBudget) * 100;
            advice.append("Utilisation du budget : ").append(String.format("%.1f%%", percentage)).append("\n");

            if (percentage > 100) {
                advice.append("🚨 Vous avez dépassé votre budget total ! Il est urgent de revoir vos dépenses.\n");
            } else if (percentage > MODERATE_SPENDING_THRESHOLD * 100) {
                advice.append("⚠️ Vous approchez de votre limite budgétaire. Soyez prudent avec vos prochaines dépenses.\n");
            } else {
                advice.append("✅ Votre gestion budgétaire est sur la bonne voie. Continuez ainsi !\n");
            }
        } else {
            advice.append("💡 Vous n'avez pas encore défini de budgets. C'est essentiel pour un bon contrôle financier !\n");
        }
        advice.append("\n");
    }

    private void addTargetedRecommendation(Map<String, Double> spendingByCategory,
                                           Map<String, Double> budgetLimits,
                                           StringBuilder advice) {
        // Find category with highest spending ratio relative to budget
        Optional<Map.Entry<String, Double>> problematicCategory = spendingByCategory.entrySet().stream()
                .filter(entry -> budgetLimits.containsKey(entry.getKey()))
                .max((e1, e2) -> {
                    double limit1 = budgetLimits.get(e1.getKey());
                    double limit2 = budgetLimits.get(e2.getKey());
                    double ratio1 = e1.getValue() / limit1;
                    double ratio2 = e2.getValue() / limit2;
                    return Double.compare(ratio1, ratio2);
                });

        problematicCategory.ifPresent(entry -> {
            String category = entry.getKey();
            double spent = entry.getValue();
            double limit = budgetLimits.get(category);
            double ratio = spent / limit;

            if (ratio > TARGETED_ADVICE_THRESHOLD) {
                advice.append("🎯 **Conseil ciblé** : La catégorie \"")
                        .append(category)
                        .append("\" nécessite votre attention (")
                        .append(String.format("%.1f%%", ratio * 100))
                        .append(" du budget utilisé). ");

                // Add specific suggestions based on category
                addCategorySpecificAdvice(category, advice);
            }
        });

        // Also check for categories without budgets but high spending
        Optional<Map.Entry<String, Double>> highSpendingNoBudget = spendingByCategory.entrySet().stream()
                .filter(entry -> !budgetLimits.containsKey(entry.getKey()))
                .max(Map.Entry.comparingByValue());

        highSpendingNoBudget.ifPresent(entry -> {
            if (entry.getValue() > 100) { // Arbitrary threshold for "high" spending
                advice.append("💰 **Catégorie à budgétiser** : \"")
                        .append(entry.getKey())
                        .append("\" (")
                        .append(String.format("%.2f", entry.getValue()))
                        .append(" DH) mériterait un budget dédié.\n");
            }
        });
    }

    private void addCategorySpecificAdvice(String category, StringBuilder advice) {
        switch (category.toLowerCase()) {
            case "alimentation":
            case "nourriture":
                advice.append("Essayez de planifier vos repas et de cuisiner plus à la maison.\n");
                break;
            case "transport":
                advice.append("Considérez les transports en commun ou le covoiturage.\n");
                break;
            case "loisirs":
            case "divertissement":
                advice.append("Cherchez des activités gratuites ou moins chères dans votre région.\n");
                break;
            case "shopping":
            case "achats":
                advice.append("Faites une liste avant d'acheter et évitez les achats impulsifs.\n");
                break;
            default:
                advice.append("Analysez chaque dépense pour identifier les économies possibles.\n");
        }
    }

    private void detectUnusualSpending(List<Transaction> transactions, StringBuilder advice) {
        // Find largest expense
        Optional<Transaction> largestExpense = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .min(Comparator.comparingDouble(Transaction::getAmount)); // min because amounts are negative

        largestExpense.ifPresent(expense ->
                advice.append("💸 **Dépense la plus importante** : ")
                        .append(expense.getName())
                        .append(" (")
                        .append(String.format("%.2f", Math.abs(expense.getAmount())))
                        .append(" DH) dans la catégorie \"")
                        .append(expense.getCategory())
                        .append("\"\n"));

        // Find most frequent spending category
        Map<String, Long> frequentCategories = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.counting()
                ));

        frequentCategories.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .filter(entry -> entry.getValue() > 5) // Only show if significant frequency
                .ifPresent(entry ->
                        advice.append("🔄 **Catégorie la plus fréquente** : \"")
                                .append(entry.getKey())
                                .append("\" (")
                                .append(entry.getValue())
                                .append(" transactions)\n"));

        advice.append("\n");
    }

    private String generateAIAdvice(Map<String, Double> spendingByCategory, Map<String, Double> budgetLimits) {
        try {
            StringBuilder prompt = new StringBuilder("Je suis un assistant budgétaire intelligent. ");
            prompt.append("Analyse les dépenses mensuelles suivantes et donne 3 conseils pratiques et personnalisés :\n\n");

            if (spendingByCategory.isEmpty()) {
                return "Commencez par enregistrer vos transactions pour recevoir des conseils personnalisés !";
            }

            spendingByCategory.forEach((category, amount) -> {
                double budget = budgetLimits.getOrDefault(category, 0.0);
                double percentage = budget > 0 ? (amount / budget) * 100 : 0;

                prompt.append("- ").append(category).append(" : ")
                        .append(String.format("%.2f", amount)).append(" DH dépensés");

                if (budget > 0) {
                    prompt.append(" (").append(String.format("%.1f%%", percentage))
                            .append(" du budget de ").append(String.format("%.2f", budget)).append(" DH)");
                } else {
                    prompt.append(" (aucun budget défini)");
                }
                prompt.append("\n");
            });

            prompt.append("\nDonne des conseils concrets, spécifiques et réalisables en français. ");
            prompt.append("Sois encourageant mais réaliste. Maximum 200 mots.");

            AIAgent chatGPT = new AIAgent();
            return chatGPT.getAdvice(prompt.toString());

        } catch (Exception e) {
            System.err.println("Erreur lors de la génération des conseils IA : " + e.getMessage());
            return "Conseils IA temporairement indisponibles. Utilisez les conseils automatiques ci-dessus.";
        }
    }
}