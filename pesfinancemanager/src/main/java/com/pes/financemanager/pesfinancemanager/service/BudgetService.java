package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.dto.BudgetDTO;
import com.pes.financemanager.pesfinancemanager.model.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetService {
    Budget createBudget(BudgetDTO dto);
    List<Budget> getBudgetsForUser(Long userId);
    Budget updateBudget(Long budgetId, BudgetDTO dto);
    Budget trackExpense(Long userId, String category, double amount);
    Optional<Budget> getBudgetById(Long budgetId);
    Budget saveBudget(Budget budget);
}