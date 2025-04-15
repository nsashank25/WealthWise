package com.pes.financemanager.pesfinancemanager.report;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;

import java.io.IOException;
import java.util.List;

public class GeneralFinancialReport extends AbstractReport {

    @Override
    public byte[] generate(User user, List<Expense> expenses) throws IOException {
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double income = user.getIncome();
        double expensePercentage = (income > 0) ? (totalExpenses / income) * 100 : 0;

        // Generate PDF Report without charts
        return generatePDF(user.getUsername(), getTitle(), income, totalExpenses, expensePercentage);
    }

    @Override
    public String getTitle() {
        return "Financial Report";
    }
}