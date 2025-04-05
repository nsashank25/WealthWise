package com.pes.financemanager.pesfinancemanager.observer;

public interface ExpenseSubject {
    void registerObserver(ExpenseObserver observer);
    void removeObserver(ExpenseObserver observer);
    void notifyObservers(Long userId, double totalExpenses, double income);
}
