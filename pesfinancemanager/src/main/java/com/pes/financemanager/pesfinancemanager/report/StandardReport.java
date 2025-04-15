package com.pes.financemanager.pesfinancemanager.report;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class StandardReport implements Report {
    private String title;

    public StandardReport(String title) {
        this.title = title;
    }

    @Override
    public byte[] generate(User user, List<Expense> expenses) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Calculate totals
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();

        // Create simple text report
        StringBuilder report = new StringBuilder();
        report.append(title).append("\n");
        report.append("=".repeat(title.length())).append("\n\n");
        report.append("User: ").append(user.getUsername()).append("\n");
        report.append("Total Expenses: Rs. ").append(String.format("%.2f", totalExpenses)).append("\n\n");
        report.append("Expense Breakdown:\n");

        // Group by category
        Map<String, Double> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            report.append(String.format("- %-20s Rs. %.2f\n", entry.getKey(), entry.getValue()));
        }

        outputStream.write(report.toString().getBytes(StandardCharsets.UTF_8));
        return outputStream.toByteArray();
    }

    @Override
    public String getTitle() {
        return title;
    }
}