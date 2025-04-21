package com.pes.financemanager.pesfinancemanager.report;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonthlyReport implements Report {
    private String title;
    private int year;
    private int month;

    public MonthlyReport(String title, int year, int month) {
        this.title = title;
        this.year = year;
        this.month = month;
    }

    @Override
    public byte[] generate(User user, List<Expense> expenses) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Filter expenses for the specified month
        List<Expense> monthlyExpenses = expenses.stream()
                .filter(e -> {
                    LocalDate date = e.getDate();
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());

        double totalMonthlyExpenses = monthlyExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        // Generate monthly report content
        StringBuilder report = new StringBuilder();
        report.append(title).append("\n");
        report.append("=".repeat(title.length())).append("\n\n");

        report.append("Period: ").append(Month.of(month)).append(" ").append(year).append("\n");
        report.append("User: ").append(user.getUsername()).append("\n");
        report.append("Monthly Income: Rs. ").append(String.format("%.2f", user.getIncome())).append("\n");
        report.append("Total Expenses: Rs. ").append(String.format("%.2f", totalMonthlyExpenses)).append("\n");
        report.append("Remaining Budget: Rs. ").append(String.format("%.2f", user.getIncome() - totalMonthlyExpenses)).append("\n\n");

        // Group by day
        Map<Integer, Double> byDay = monthlyExpenses.stream()
                .collect(Collectors.groupingBy(e -> e.getDate().getDayOfMonth(),
                        Collectors.summingDouble(Expense::getAmount)));

        report.append("Daily Breakdown:\n");
        byDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    report.append(String.format("- Day %2d: Rs. %.2f\n", entry.getKey(), entry.getValue()));
                });

        report.append("\nCategory Breakdown:\n");
        Map<String, Double> byCategory = monthlyExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        byCategory.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> {
                    report.append(String.format("- %-15s Rs. %.2f (%.1f%%)\n",
                            entry.getKey(),
                            entry.getValue(),
                            (entry.getValue() / totalMonthlyExpenses) * 100));
                });

        outputStream.write(report.toString().getBytes(StandardCharsets.UTF_8));
        return outputStream.toByteArray();
    }

    @Override
    public String getTitle() {
        return title;
    }
}