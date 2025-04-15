package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.report.Report;
import com.pes.financemanager.pesfinancemanager.report.ReportFactory;
import com.pes.financemanager.pesfinancemanager.report.CompositeReport;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinancialReportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public double calculateTotalIncome(Long userId) {
        return userRepository.getIncomeByUserId(userId);
    }

    public double calculateTotalExpenses(Long userId) {
        return expenseRepository.getTotalExpensesByUserId(userId);
    }

    public List<Expense> getUserExpenses(Long userId) {
        return expenseRepository.findAllByUserId(userId);
    }

    public Map<String, Object> getIncomeVsExpenses(Long userId) {
        double income = calculateTotalIncome(userId);
        double expenses = calculateTotalExpenses(userId);

        Map<String, Object> report = new HashMap<>();
        report.put("totalIncome", income);
        report.put("totalExpenses", expenses);

        return report;
    }

    public byte[] generateFinancialReport(Long userId, List<Expense> expenses, String reportTitle) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get current year and month for the monthly and yearly reports
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based

        // Create a composite report with PDF format
        CompositeReport compositeReport = (CompositeReport) ReportFactory.createReport("composite", "pdf");
        compositeReport.setTitle(reportTitle);

        try {
            // Add individual report sections
            Report generalReport = ReportFactory.createReport("general");
            byte[] generalReportBytes = generalReport.generate(user, expenses);
            compositeReport.addSection("General Overview", generalReportBytes);

            // Add monthly report with proper filtering (generate a proper monthly report)
            Report monthlyReport = ReportFactory.createReport("monthly", currentYear, currentMonth);
            byte[] monthlyReportBytes = monthlyReport.generate(user, expenses);
            compositeReport.addSection("Monthly Analysis", monthlyReportBytes);

            // Add yearly report with proper filtering
            Report yearlyReport = ReportFactory.createReport("yearly", currentYear);
            byte[] yearlyReportBytes = yearlyReport.generate(user, expenses);
            compositeReport.addSection("Annual Summary", yearlyReportBytes);

            // Generate the final composite report
            return compositeReport.generate(user, expenses);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error generating financial report: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> getMonthlyTrend(Long userId) {
        List<Expense> expenses = expenseRepository.findAllByUserId(userId);
        Map<YearMonth, Double> incomeMap = new HashMap<>();
        Map<YearMonth, Double> expenseMap = new HashMap<>();

        for (Expense e : expenses) {
            YearMonth ym = YearMonth.from(e.getDate());
            expenseMap.put(ym, expenseMap.getOrDefault(ym, 0.0) + e.getAmount());
        }

        // Assume income is static; update if monthly income varies
        double income = calculateTotalIncome(userId);
        for (YearMonth ym : expenseMap.keySet()) {
            incomeMap.put(ym, income);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (YearMonth ym : expenseMap.keySet()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("month", ym.toString()); // format like "2024-03"
            entry.put("income", incomeMap.get(ym));
            entry.put("expenses", expenseMap.get(ym));
            result.add(entry);
        }

        return result.stream()
                .sorted(Comparator.comparing(e -> YearMonth.parse((String) e.get("month"))))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopCategories(Long userId, int limit) {
        List<Expense> expenses = expenseRepository.findAllByUserId(userId);

        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        return categoryTotals.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", e.getKey());
                    map.put("amount", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}