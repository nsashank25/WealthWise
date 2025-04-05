package com.pes.financemanager.pesfinancemanager.controller;

import com.pes.financemanager.pesfinancemanager.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pes.financemanager.pesfinancemanager.model.Expense;
import java.util.List;
import java.util.Map;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
public class FinancialReportController {

    @Autowired
    private FinancialReportService financialReportService;

    @GetMapping("/{userId}/download")
    public ResponseEntity<byte[]> downloadFinancialReport(@PathVariable Long userId) {
        try {
            // Fetch expenses for the user
            List<Expense> expenses = financialReportService.getUserExpenses(userId);

            // Generate the report with correct parameters
            byte[] pdfBytes = financialReportService.generateFinancialReport(userId, expenses, "Financial Report");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "financial_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/monthly-trend/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyTrend(@PathVariable Long userId) {
        return ResponseEntity.ok(financialReportService.getMonthlyTrend(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getIncomeVsExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(financialReportService.getIncomeVsExpenses(userId));
    }

    @GetMapping("/top-categories/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getTopCategories(@PathVariable Long userId) {
        return ResponseEntity.ok(financialReportService.getTopCategories(userId, 5));
    }
}
