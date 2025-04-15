package com.pes.financemanager.pesfinancemanager.report;

import java.time.YearMonth;

public class ReportFactory {

    public static Report createReport(String reportType, Object... params) {
        switch (reportType.toLowerCase()) {
            case "general":
                // Assuming you have an existing PDF or text-based report implementation
                return new StandardReport("General Financial Overview");

            case "monthly":
                if (params.length >= 2) {
                    int year = (int) params[0];
                    int month = (int) params[1];
                    return new MonthlyReport("Monthly Report: " + YearMonth.of(year, month), year, month);
                }
                throw new IllegalArgumentException("Monthly report requires year and month parameters");

            case "yearly":
                if (params.length >= 1) {
                    int year = (int) params[0];
                    return new YearlyReport("Annual Report: " + year, year);
                }
                throw new IllegalArgumentException("Yearly report requires year parameter");

            case "composite":
                // Default to PDF format if not specified
                String format = params.length >= 1 ? (String) params[0] : "pdf";
                return new CompositeReport(format);

            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }
    }
}
