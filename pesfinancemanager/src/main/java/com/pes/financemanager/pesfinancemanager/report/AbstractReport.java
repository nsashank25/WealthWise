package com.pes.financemanager.pesfinancemanager.report;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class AbstractReport implements Report {

    protected byte[] generatePDF(String username, String reportTitle, double income, double totalExpenses,
                                 double expensePercentage) throws IOException {
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

        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }
}