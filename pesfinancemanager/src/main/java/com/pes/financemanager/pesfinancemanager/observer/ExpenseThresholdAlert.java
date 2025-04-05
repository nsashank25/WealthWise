package com.pes.financemanager.pesfinancemanager.observer;

public class ExpenseThresholdAlert implements ExpenseObserver {

    @Override
    public void update(Long userId, double totalExpenses, double income) {
        double threshold = 0.8 * income;
        if (totalExpenses > threshold) {
            System.out.println("⚠️ Alert: User " + userId + " has exceeded 80% of their income in expenses!");
            // Here you could send an email, push notification, etc.
        }
    }
}
