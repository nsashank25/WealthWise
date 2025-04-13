package com.pes.financemanager.pesfinancemanager.command.expense;

import com.pes.financemanager.pesfinancemanager.command.Command;
import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;

import java.util.Optional;

public class DeleteExpenseCommand implements Command {
    private final ExpenseRepository expenseRepository;
    private final Long expenseId;
    private Expense deletedExpense;

    public DeleteExpenseCommand(ExpenseRepository expenseRepository, Long expenseId) {
        this.expenseRepository = expenseRepository;
        this.expenseId = expenseId;
    }

    @Override
    public void execute() {
        Optional<Expense> expenseOptional = expenseRepository.findById(expenseId);
        if (expenseOptional.isPresent()) {
            // Create a deep copy of the expense before deleting
            deletedExpense = new Expense();
            Expense originalExpense = expenseOptional.get();
            deletedExpense.setId(originalExpense.getId());
            deletedExpense.setCategory(originalExpense.getCategory());
            deletedExpense.setAmount(originalExpense.getAmount());
            deletedExpense.setDate(originalExpense.getDate());
            deletedExpense.setDescription(originalExpense.getDescription());
            deletedExpense.setUser(originalExpense.getUser());

            expenseRepository.deleteById(expenseId);
        }
    }

    @Override
    public void undo() {
        if (deletedExpense != null) {
            // Set ID to null to create a new entity rather than update
            // This avoids the "Row was updated or deleted" error
            Long originalId = deletedExpense.getId();
            deletedExpense.setId(null);
            Expense savedExpense = expenseRepository.save(deletedExpense);
            // Restore the original ID in our copy for reference
            deletedExpense.setId(originalId);
        }
    }
}