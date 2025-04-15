package com.pes.financemanager.pesfinancemanager.report;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YearlyReport implements Report {
    private String title;
    private int year;

    public YearlyReport(String title, int year) {
        this.title = title;
        this.year = year;
    }

    @Override
    public byte[] generate(User user, List<Expense> expenses) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Filter expenses for the specified year
        List<Expense> yearlyExpenses = expenses.stream()
                .filter(e -> e.getDate().getYear() == year)
                .collect(Collectors.toList());

        double totalYearlyExpenses = yearlyExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        // Generate yearly report content
        StringBuilder report = new StringBuilder();
        report.append(title).append("\n");
        report.append("=".repeat(title.length())).append("\n\n");

        report.append("Year: ").append(year).append("\n");
        report.append("User: ").append(user.getUsername()).append("\n");
        report.append("Annual Income: Rs. ").append(String.format("%.2f", user.getIncome())).append("\n");
        report.append("Total Expenses: Rs. ").append(String.format("%.2f", totalYearlyExpenses)).append("\n");
        report.append("Savings: Rs. ").append(String.format("%.2f", (user.getIncome()) - totalYearlyExpenses)).append("\n\n");

        // Group by month
        Map<Integer, Double> byMonth = yearlyExpenses.stream()
                .collect(Collectors.groupingBy(e -> e.getDate().getMonthValue(),
                        Collectors.summingDouble(Expense::getAmount)));

        report.append("Monthly Breakdown:\n");
        for (int i = 1; i <= 12; i++) {
            double amount = byMonth.getOrDefault(i, 0.0);
            report.append(String.format("- %-9s: Rs. %.2f\n", Month.of(i), amount));
        }

        report.append("\nCategory Breakdown:\n");
        Map<String, Double> byCategory = yearlyExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        byCategory.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> {
                    report.append(String.format("- %-15s Rs. %.2f (%.1f%%)\n",
                            entry.getKey(),
                            entry.getValue(),
                            (entry.getValue() / totalYearlyExpenses) * 100));
                });

        outputStream.write(report.toString().getBytes(StandardCharsets.UTF_8));
        return outputStream.toByteArray();
    }

    @Override
    public String getTitle() {
        return title;
    }
}