package com.pes.financemanager.pesfinancemanager.dto;

import com.pes.financemanager.pesfinancemanager.model.Expense;

public class ExpenseResponse {
    private Expense expense;
    private String message;

    public ExpenseResponse(Expense expense, String message) {
        this.expense = expense;
        this.message = message;
    }

    public Expense getExpense() { return expense; }
    public String getMessage() { return message; }
}
