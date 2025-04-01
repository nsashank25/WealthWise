package com.pes.financemanager.pesfinancemanager.controller;
import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/{userId}/add")
    public Expense addExpense(@PathVariable Long userId, @RequestBody Expense expense) {
        return expenseService.addExpense(userId, expense);
    }

    @GetMapping("/{userId}")
    public List<Expense> getUserExpenses(@PathVariable Long userId) {
        return expenseService.getExpensesByUser(userId);
    }
}