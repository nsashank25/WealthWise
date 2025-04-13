package com.pes.financemanager.pesfinancemanager.controller;

import com.pes.financemanager.pesfinancemanager.dto.ExpenseResponse;
import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/{userId}/add")
    public ExpenseResponse addExpense(@PathVariable Long userId, @RequestBody Expense expense) {
        return expenseService.addExpenseWithAlert(userId, expense);
    }

    @GetMapping("/{userId}")
    public List<Expense> getUserExpenses(@PathVariable Long userId) {
        return expenseService.getExpensesByUser(userId);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long expenseId,
            @RequestBody Map<String, Object> updates) {

        String category = (String) updates.get("category");
        Double amount = Double.valueOf(updates.get("amount").toString());
        LocalDate date = LocalDate.parse(updates.get("date").toString());
        String description = (String) updates.get("description");

        Expense updatedExpense = expenseService.updateExpense(
                expenseId, category, amount, date, description);

        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/undo")
    public ResponseEntity<String> undoLastOperation() {
        try {
            expenseService.undoLastOperation();
            return ResponseEntity.ok("Last operation undone successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/redo")
    public ResponseEntity<String> redoLastOperation() {
        try {
            expenseService.redoLastOperation();
            return ResponseEntity.ok("Last operation redone successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}