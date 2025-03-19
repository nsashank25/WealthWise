package com.pes.financemanager.pesfinancemanager.service;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;
import com.pes.financemanager.pesfinancemanager.repository.ExpenseRepository;
import com.pes.financemanager.pesfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public Map<String, Double> getIncomeVsExpenses(Long userId) {
        double income = calculateTotalIncome(userId);
        double expenses = calculateTotalExpenses(userId);

        Map<String, Double> report = new HashMap<>();
        report.put("totalIncome", income);
        report.put("totalExpenses", expenses);

        return report;
    }

    public byte[] generateFinancialReport(Long userId, double income) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findByUser(user);
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double expensePercentage = (totalExpenses / income) * 100;

        // Categorize expenses
        Map<String, Double> expenseByCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        // Generate Charts
        byte[] pieChart = generatePieChart(expenseByCategory);
        byte[] barChart = generateBarChart(expenseByCategory);

        // Generate PDF Report
        return generatePDF(user.getUsername(), income, totalExpenses, expensePercentage, pieChart, barChart);
    }

    private byte[] generatePieChart(Map<String, Double> expenseByCategory) throws IOException {
        PieChart chart = new PieChartBuilder().width(500).height(400).title("Expense Distribution").build();
        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            chart.addSeries(entry.getKey(), entry.getValue());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);
        return outputStream.toByteArray();
    }

    private byte[] generateBarChart(Map<String, Double> expenseByCategory) throws IOException {
        CategoryChart chart = new CategoryChartBuilder().width(600).height(400).title("Expenses by Category")
                .xAxisTitle("Category").yAxisTitle("Amount").build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);

        List<String> categories = new ArrayList<>(expenseByCategory.keySet());
        List<Double> amounts = new ArrayList<>(expenseByCategory.values());

        chart.addSeries("Expenses", categories, amounts);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);
        return outputStream.toByteArray();
    }

    private byte[] generatePDF(String username, double income, double totalExpenses, double expensePercentage, byte[] pieChart, byte[] barChart) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Financial Report for: " + username);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Total Income: Rs. " + income);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Total Expenses: Rs. " + totalExpenses);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Total Savings: Rs. " + (income - totalExpenses));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Percentage of Income Spent: " + String.format("%.2f", expensePercentage) + "%");
        contentStream.endText();
        contentStream.close();

        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }
}
