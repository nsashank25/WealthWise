package com.pes.financemanager.pesfinancemanager.command.expense;

import com.pes.financemanager.pesfinancemanager.command.Command;
import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;

public class AddExpenseCommand implements Command {
    private final ExpenseRepository expenseRepository;
    private Expense expense;
    private Long savedExpenseId;
    private Expense savedExpenseCopy; // Keep a full copy

    public AddExpenseCommand(ExpenseRepository expenseRepository, Expense expense) {
        this.expenseRepository = expenseRepository;
        this.expense = expense;
    }

    @Override
    public void execute() {
        try {
            // If this is a redo operation and we have an ID, check if the expense still exists
            if (savedExpenseId != null) {
                boolean exists = expenseRepository.existsById(savedExpenseId);
                if (!exists && savedExpenseCopy != null) {
                    // Entity doesn't exist anymore, create a new one from our copy
                    savedExpenseCopy.setId(null); // Clear ID to create new
                    Expense newExpense = expenseRepository.save(savedExpenseCopy);
                    this.savedExpenseId = newExpense.getId();
                    return;
                }
            }

            // Normal path - save the expense
            Expense savedExpense = expenseRepository.save(expense);
            this.savedExpenseId = savedExpense.getId();

            // Keep a full copy for potential redo operations
            savedExpenseCopy = new Expense();
            savedExpenseCopy.setId(savedExpense.getId());
            savedExpenseCopy.setCategory(savedExpense.getCategory());
            savedExpenseCopy.setAmount(savedExpense.getAmount());
            savedExpenseCopy.setDate(savedExpense.getDate());
            savedExpenseCopy.setDescription(savedExpense.getDescription());
            savedExpenseCopy.setUser(savedExpense.getUser());
        } catch (Exception e) {
            System.err.println("Error in AddExpenseCommand.execute: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void undo() {
        try {
            if (savedExpenseId != null) {
                if (expenseRepository.existsById(savedExpenseId)) {
                    expenseRepository.deleteById(savedExpenseId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in AddExpenseCommand.undo: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Long getSavedExpenseId() {
        return savedExpenseId;
    }
}