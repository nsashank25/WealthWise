package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.command.CommandHistory;
import com.pes.financemanager.pesfinancemanager.command.expense.AddExpenseCommand;
import com.pes.financemanager.pesfinancemanager.command.expense.DeleteExpenseCommand;
import com.pes.financemanager.pesfinancemanager.command.expense.UpdateExpenseCommand;
import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import com.pes.financemanager.pesfinancemanager.observer.ExpenseObserver;
import com.pes.financemanager.pesfinancemanager.observer.ExpenseSubject;
import com.pes.financemanager.pesfinancemanager.observer.ExpenseThresholdAlert;
import com.pes.financemanager.pesfinancemanager.dto.ExpenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService implements ExpenseSubject {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommandHistory commandHistory;

    private final List<ExpenseObserver> observers = new ArrayList<>();

    public ExpenseService() {
        // register the observer(s)
        registerObserver(new ExpenseThresholdAlert());
    }

    @Override
    public void registerObserver(ExpenseObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ExpenseObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Long userId, double totalExpenses, double income) {
        for (ExpenseObserver observer : observers) {
            observer.update(userId, totalExpenses, income);
        }
    }

    public Expense addExpense(Long userId, Expense expense) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        expense.setUser(user);

        // Create and execute the command
        AddExpenseCommand command = new AddExpenseCommand(expenseRepository, expense);
        commandHistory.executeCommand(command);

        // Retrieve the saved expense
        Long savedExpenseId = command.getSavedExpenseId();
        Expense savedExpense = expenseRepository.findById(savedExpenseId)
                .orElseThrow(() -> new RuntimeException("Error retrieving saved expense"));

        // calculate total expenses after adding this one
        List<Expense> allExpenses = expenseRepository.findByUser(user);
        double total = allExpenses.stream().mapToDouble(Expense::getAmount).sum();

        // notify all observers
        notifyObservers(userId, total, user.getIncome());

        return savedExpense;
    }

    public ExpenseResponse addExpenseWithAlert(Long userId, Expense expense) {
        Expense savedExpense = addExpense(userId, expense); // original logic
        List<Expense> allExpenses = expenseRepository.findByUser(savedExpense.getUser());
        double total = allExpenses.stream().mapToDouble(Expense::getAmount).sum();
        double income = savedExpense.getUser().getIncome();

        String message = null;
        if (total > 0.8 * income) {
            message = "Warning: You have exceeded 80% of your income in expenses.";
        }

        return new ExpenseResponse(savedExpense, message);
    }

    public List<Expense> getExpensesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUser(user);
    }

    public void deleteExpense(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Expense not found");
        }

        // Create and execute the command
        DeleteExpenseCommand command = new DeleteExpenseCommand(expenseRepository, expenseId);
        commandHistory.executeCommand(command);
    }

    public Expense updateExpense(Long expenseId, String category, Double amount,
                                 LocalDate date, String description) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Expense not found");
        }

        // Create and execute the command
        UpdateExpenseCommand command = new UpdateExpenseCommand(
                expenseRepository, expenseId, category, amount, date, description);
        commandHistory.executeCommand(command);

        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Error retrieving updated expense"));
    }

    public void undoLastOperation() {
        if (commandHistory.canUndo()) {
            commandHistory.undo();
        } else {
            throw new RuntimeException("No operations to undo");
        }
    }

    public void redoLastOperation() {
        if (commandHistory.canRedo()) {
            commandHistory.redo();
        } else {
            throw new RuntimeException("No operations to redo");
        }
    }

    public boolean canUndo() {
        return commandHistory.canUndo();
    }

    public boolean canRedo() {
        return commandHistory.canRedo();
    }
}