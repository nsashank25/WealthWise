package com.pes.financemanager.pesfinancemanager.controller;

import com.pes.financemanager.pesfinancemanager.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
public class FinancialReportController {

    @Autowired
    private FinancialReportService financialReportService;

    @GetMapping("/{userId}/download")
    public ResponseEntity<byte[]> downloadFinancialReport(@PathVariable Long userId) {
        try {
            double income = financialReportService.calculateTotalIncome(userId); // Fetch income from DB
            byte[] pdfBytes = financialReportService.generateFinancialReport(userId, income);

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

    @GetMapping("/{userId}")
    public ResponseEntity<?> getIncomeVsExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(financialReportService.getIncomeVsExpenses(userId));
    }

}
