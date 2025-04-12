package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.dto.BudgetDTO;
import com.pes.financemanager.pesfinancemanager.model.Budget;
import com.pes.financemanager.pesfinancemanager.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Override
    public Budget createBudget(BudgetDTO dto) {
        Budget budget = new Budget();
        budget.setUserId(dto.userId);
        budget.setTimePeriod(dto.timePeriod);
        budget.setStartDate(dto.startDate);
        budget.setEndDate(dto.endDate);
        budget.setCategoryAllocations(dto.categoryAllocations);

        // Initialize categorySpent with all available categories at 0.0
        Map<String, Double> initialSpent = new HashMap<>();
        dto.categoryAllocations.keySet().forEach(category -> initialSpent.put(category, 0.0));
        budget.setCategorySpent(initialSpent);

        budget.setTotalBudget(dto.categoryAllocations.values().stream().mapToDouble(Double::doubleValue).sum());
        budget.setTotalSpent(0.0);
        budget.setVersion(1);
        return budgetRepository.save(budget);
    }

    @Override
    public List<Budget> getBudgetsForUser(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Override
    public Budget updateBudget(Long budgetId, BudgetDTO dto) {
        Optional<Budget> optionalBudget = budgetRepository.findById(budgetId);
        if (optionalBudget.isEmpty()) return null;

        Budget budget = optionalBudget.get();
        budget.setCategoryAllocations(dto.categoryAllocations);

        // Update categorySpent to include any new categories
        Map<String, Double> updatedSpent = budget.getCategorySpent();
        dto.categoryAllocations.keySet().forEach(category -> {
            if (!updatedSpent.containsKey(category)) {
                updatedSpent.put(category, 0.0);
            }
        });
        budget.setCategorySpent(updatedSpent);

        budget.setTotalBudget(dto.categoryAllocations.values().stream().mapToDouble(Double::doubleValue).sum());
        budget.setVersion(budget.getVersion() + 1);
        return budgetRepository.save(budget);
    }

    @Override
    public Budget trackExpense(Long userId, String category, double amount) {
        // Find active budgets (current date between start and end date)
        LocalDate today = LocalDate.now();

        List<Budget> userBudgets = budgetRepository.findByUserId(userId);
        List<Budget> activeBudgets = new ArrayList<>();

        for (Budget budget : userBudgets) {
            // Skip budgets with null dates
            if (budget.getStartDate() == null || budget.getEndDate() == null) {
                continue;
            }

            // Check if today is within the budget period
            if (!today.isBefore(budget.getStartDate()) && !today.isAfter(budget.getEndDate())) {
                activeBudgets.add(budget);
            }
        }

        // If no active budget found, return null
        if (activeBudgets.isEmpty()) {
            // Fallback: if no active budget found, use the most recent budget
            if (!userBudgets.isEmpty()) {
                Budget mostRecent = userBudgets.stream()
                        .max(Comparator.comparing(Budget::getId))
                        .orElse(null);

                if (mostRecent != null) {
                    // Update category spent
                    Map<String, Double> spent = mostRecent.getCategorySpent();
                    if (spent == null) {
                        spent = new HashMap<>();
                        mostRecent.setCategorySpent(spent);
                    }

                    // Ensure category exists
                    if (!mostRecent.getCategoryAllocations().containsKey(category)) {
                        mostRecent.getCategoryAllocations().put(category, 0.0);
                    }

                    spent.put(category, spent.getOrDefault(category, 0.0) + amount);
                    mostRecent.setTotalSpent(mostRecent.getTotalSpent() + amount);
                    return budgetRepository.save(mostRecent);
                }
            }
            return null;
        }

        // Choose the most recently created active budget (highest ID)
        Budget budget = activeBudgets.stream()
                .max(Comparator.comparing(Budget::getId))
                .orElse(null);

        if (budget != null) {
            // Update category spent
            Map<String, Double> spent = budget.getCategorySpent();
            if (spent == null) {
                spent = new HashMap<>();
                budget.setCategorySpent(spent);
            }

            // Check if the category exists in allocations, if not add it
            if (!budget.getCategoryAllocations().containsKey(category)) {
                budget.getCategoryAllocations().put(category, 0.0);
            }

            // Update the spent amount
            spent.put(category, spent.getOrDefault(category, 0.0) + amount);
            budget.setTotalSpent(budget.getTotalSpent() + amount);
            return budgetRepository.save(budget);
        }

        return null;
    }

    @Override
    public Optional<Budget> getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId);
    }

    @Override
    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }
}