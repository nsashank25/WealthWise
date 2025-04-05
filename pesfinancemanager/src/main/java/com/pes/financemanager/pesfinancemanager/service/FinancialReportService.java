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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.YearMonth;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


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

    public byte[] generateMonthlyReport(Long userId, int year, int month) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findMonthlyExpenses(userId, year, month);
        return generateFinancialReport(userId, expenses, "Monthly Report - " + YearMonth.of(year, month));
    }

    public byte[] generateYearlyReport(Long userId, int year) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findYearlyExpenses(userId, year);
        return generateFinancialReport(userId, expenses, "Annual Report - " + year);
    }

    public Map<String, Double> getIncomeVsExpenses(Long userId) {
        double income = calculateTotalIncome(userId);
        double expenses = calculateTotalExpenses(userId);

        Map<String, Double> report = new HashMap<>();
        report.put("totalIncome", income);
        report.put("totalExpenses", expenses);

        return report;
    }

    public byte[] generateFinancialReport(Long userId, List<Expense> expenses, String reportTitle) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double income = user.getIncome();
        double expensePercentage = (income > 0) ? (totalExpenses / income) * 100 : 0;

        // Categorize expenses
        Map<String, Double> expenseByCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        // Generate Charts
        byte[] pieChart = generatePieChart(expenseByCategory);
        byte[] barChart = generateBarChart(expenses);

        // Generate PDF Report
        return generatePDF(user.getUsername(), reportTitle, income, totalExpenses, expensePercentage, pieChart, barChart);

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

private byte[] generateBarChart(List<Expense> expenses) throws IOException {
    Map<String, Double> expensesByMonth = expenses.stream()
            .collect(Collectors.groupingBy(
                    e -> e.getDate().getMonth().toString(), // Get month name
                    Collectors.summingDouble(Expense::getAmount)
            ));

    CategoryChart chart = new CategoryChartBuilder().width(600).height(400)
            .title("Monthly Expenses").xAxisTitle("Month").yAxisTitle("Amount").build();

    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
    chart.getStyler().setHasAnnotations(true);

    List<String> months = new ArrayList<>(expensesByMonth.keySet());
    List<Double> amounts = new ArrayList<>(expensesByMonth.values());

    chart.addSeries("Expenses", months, amounts);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);
    return outputStream.toByteArray();
}

private byte[] generatePDF(String username, String reportTitle, double income, double totalExpenses,
                           double expensePercentage, byte[] pieChart, byte[] barChart) throws IOException {
    PDDocument document = new PDDocument();
    PDPage page = new PDPage();
    document.addPage(page);

    PDPageContentStream contentStream = new PDPageContentStream(document, page);
    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
    contentStream.beginText();
    contentStream.newLineAtOffset(100, 700);
    contentStream.showText(reportTitle);
    contentStream.newLineAtOffset(0, -20);
    contentStream.showText("User: " + username);
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

    // Convert charts to images
    BufferedImage pieImage = ImageIO.read(new ByteArrayInputStream(pieChart));
    BufferedImage barImage = ImageIO.read(new ByteArrayInputStream(barChart));

    // Create new page for images
    PDPage imagePage = new PDPage();
    document.addPage(imagePage);

    PDPageContentStream imageStream = new PDPageContentStream(document, imagePage);

    // Convert images to PDF format
    PDImageXObject pieImageObject = PDImageXObject.createFromByteArray(document, pieChart, "pieChart");
    PDImageXObject barImageObject = PDImageXObject.createFromByteArray(document, barChart, "barChart");

    // Draw images in PDF
    imageStream.drawImage(pieImageObject, 50, 400, 500, 300);
    imageStream.drawImage(barImageObject, 50, 100, 500, 300);
    imageStream.close();

    // Convert to byte array
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    document.save(outputStream);
    document.close();

    return outputStream.toByteArray();
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
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
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