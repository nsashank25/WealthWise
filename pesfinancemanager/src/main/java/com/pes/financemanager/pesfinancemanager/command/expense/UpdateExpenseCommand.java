package com.pes.financemanager.pesfinancemanager.command.expense;

import com.pes.financemanager.pesfinancemanager.command.Command;
import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.Optional;

public class UpdateExpenseCommand implements Command {
    private final ExpenseRepository expenseRepository;
    private final Long expenseId;
    private final String newCategory;
    private final Double newAmount;
    private final LocalDate newDate;
    private final String newDescription;

    private String oldCategory;
    private Double oldAmount;
    private LocalDate oldDate;
    private String oldDescription;
    private Expense expenseCopy; // Store a copy for undo

    public UpdateExpenseCommand(
            ExpenseRepository expenseRepository,
            Long expenseId,
            String newCategory,
            Double newAmount,
            LocalDate newDate,
            String newDescription) {
        this.expenseRepository = expenseRepository;
        this.expenseId = expenseId;
        this.newCategory = newCategory;
        this.newAmount = newAmount;
        this.newDate = newDate;
        this.newDescription = newDescription;
    }

    @Override
    public void execute() {
        Optional<Expense> expenseOptional = expenseRepository.findById(expenseId);
        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();

            // Create a copy for undo
            expenseCopy = new Expense();
            expenseCopy.setId(expense.getId());
            expenseCopy.setCategory(expense.getCategory());
            expenseCopy.setAmount(expense.getAmount());
            expenseCopy.setDate(expense.getDate());
            expenseCopy.setDescription(expense.getDescription());
            expenseCopy.setUser(expense.getUser());

            // Save old values for undo
            oldCategory = expense.getCategory();
            oldAmount = expense.getAmount();
            oldDate = expense.getDate();
            oldDescription = expense.getDescription();

            // Update with new values
            expense.setCategory(newCategory);
            expense.setAmount(newAmount);
            expense.setDate(newDate);
            expense.setDescription(newDescription);

            expenseRepository.save(expense);
        }
    }

    @Override
    public void undo() {
        try {
            // Use find-by-id to get the latest version from the database
            Optional<Expense> currentExpenseOpt = expenseRepository.findById(expenseId);
            if (currentExpenseOpt.isPresent()) {
                Expense currentExpense = currentExpenseOpt.get();

                // Update with old values
                currentExpense.setCategory(oldCategory);
                currentExpense.setAmount(oldAmount);
                currentExpense.setDate(oldDate);
                currentExpense.setDescription(oldDescription);

                expenseRepository.save(currentExpense);
            } else {
                // If expense was deleted somehow, recreate it
                expenseCopy.setId(null); // Set to null to create new
                expenseRepository.save(expenseCopy);
            }
        } catch (Exception e) {
            // If there's any issue, try to recreate the entity
            expenseCopy.setId(null);
            expenseRepository.save(expenseCopy);
        }
    }
}