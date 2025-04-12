package com.pes.financemanager.pesfinancemanager.controller;

import com.pes.financemanager.pesfinancemanager.dto.BudgetDTO;
import com.pes.financemanager.pesfinancemanager.model.Budget;
import com.pes.financemanager.pesfinancemanager.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody BudgetDTO dto) {
        // Handle potential date parsing issues
        try {
            // We don't need to parse dates as the @JsonFormat in BudgetDTO
            // should handle this automatically
            Budget budget = budgetService.createBudget(dto);
            return ResponseEntity.ok(budget);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating budget: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getBudgets(@PathVariable Long userId) {
        try {
            List<Budget> budgets = budgetService.getBudgetsForUser(userId);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching budgets: " + e.getMessage());
        }
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<?> updateBudget(@PathVariable Long budgetId, @RequestBody BudgetDTO dto) {
        try {
            Budget budget = budgetService.updateBudget(budgetId, dto);
            if (budget == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(budget);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating budget: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/track")
    public ResponseEntity<?> trackExpense(
            @PathVariable Long userId,
            @RequestParam String category,
            @RequestParam double amount) {
        try {
            Budget budget = budgetService.trackExpense(userId, category, amount);
            if (budget == null) {
                return ResponseEntity.badRequest().body("No active budget found");
            }
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error tracking expense: " + e.getMessage());
        }
    }

    @GetMapping("/{budgetId}/details")
    public ResponseEntity<?> getBudgetDetails(@PathVariable Long budgetId) {
        try {
            return budgetService.getBudgetById(budgetId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching budget: " + e.getMessage());
        }
    }

    @PostMapping("/{budgetId}/track-specific")
    public ResponseEntity<?> trackSpecificBudgetExpense(
            @PathVariable Long budgetId,
            @RequestParam String category,
            @RequestParam double amount) {
        try {
            return budgetService.getBudgetById(budgetId)
                    .map(budget -> {
                        // Update category spent
                        budget.getCategorySpent().put(
                                category,
                                budget.getCategorySpent().getOrDefault(category, 0.0) + amount
                        );
                        budget.setTotalSpent(budget.getTotalSpent() + amount);
                        return ResponseEntity.ok(budgetService.saveBudget(budget));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error tracking expense: " + e.getMessage());
        }
    }
}