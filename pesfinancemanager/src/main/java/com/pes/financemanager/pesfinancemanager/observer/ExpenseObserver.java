package com.pes.financemanager.pesfinancemanager.observer;

public interface ExpenseObserver {
    void update(Long userId, double totalExpenses, double income);
}
