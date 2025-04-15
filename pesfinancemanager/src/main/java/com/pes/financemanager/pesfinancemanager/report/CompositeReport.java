package com.pes.financemanager.pesfinancemanager.report;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompositeReport implements Report {

    private Map<String, byte[]> reportSections = new LinkedHashMap<>();
    private String reportFormat; // pdf, html, etc.
    private String title = "Comprehensive Financial Report";

    public CompositeReport(String format) {
        this.reportFormat = format;
    }

    public void addSection(String sectionTitle, byte[] reportContent) {
        if (reportContent != null && reportContent.length > 0) {
            reportSections.put(sectionTitle, reportContent);
        }
    }

    @Override
    public byte[] generate(User user, List<Expense> expenses) throws IOException {
        // The merging implementation depends on the report format
        switch(reportFormat.toLowerCase()) {
            case "pdf":
                return generatePdfReport();
            case "html":
                return mergeHtmlReports();
            case "csv":
                return mergeCsvReports();
            default:
                return mergeDefaultReports();
        }
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private byte[] generatePdfReport() throws IOException {
        PDDocument document = new PDDocument();

        // Create cover page
        createCoverPage(document);

        // Create table of contents
        createTableOfContents(document);

        // Add each section
        for (Map.Entry<String, byte[]> section : reportSections.entrySet()) {
            addReportSection(document, section.getKey(), new String(section.getValue()));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    private void createCoverPage(PDDocument document) throws IOException {
        PDPage coverPage = new PDPage(PDRectangle.A4);
        document.addPage(coverPage);

        PDPageContentStream contentStream = new PDPageContentStream(document, coverPage);

        // Add background color/gradient (light gray)
        contentStream.setNonStrokingColor(0.95f, 0.95f, 0.95f); // Light gray
        contentStream.addRect(0, 0, coverPage.getMediaBox().getWidth(), coverPage.getMediaBox().getHeight());
        contentStream.fill();

        // Add border
        contentStream.setStrokingColor(0.8f, 0.8f, 0.8f); // Darker gray
        contentStream.setLineWidth(2f);
        float margin = 30;
        contentStream.addRect(margin, margin,
                coverPage.getMediaBox().getWidth() - 2 * margin,
                coverPage.getMediaBox().getHeight() - 2 * margin);
        contentStream.stroke();

        // Add title
        contentStream.beginText();
        contentStream.setNonStrokingColor(0.2f, 0.4f, 0.6f); // Blue-ish color
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);

        // Center the title text
        String titleText = this.title;
        float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(titleText) / 1000 * 24;
        float centerX = (coverPage.getMediaBox().getWidth() - titleWidth) / 2;
        contentStream.newLineAtOffset(centerX, 500);
        contentStream.showText(titleText);
        contentStream.endText();

        // Add date
        contentStream.beginText();
        contentStream.setNonStrokingColor(0.4f, 0.4f, 0.4f);
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        String dateText = "Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        float dateWidth = PDType1Font.HELVETICA.getStringWidth(dateText) / 1000 * 12;
        centerX = (coverPage.getMediaBox().getWidth() - dateWidth) / 2;
        contentStream.newLineAtOffset(centerX, 470);
        contentStream.showText(dateText);
        contentStream.endText();

        // Add a decorative line
        contentStream.setStrokingColor(0.2f, 0.4f, 0.6f);
        contentStream.setLineWidth(1f);
        contentStream.moveTo(100, 450);
        contentStream.lineTo(coverPage.getMediaBox().getWidth() - 100, 450);
        contentStream.stroke();

        // Add company name/logo placeholder
        contentStream.beginText();
        contentStream.setNonStrokingColor(0.3f, 0.3f, 0.3f);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        String companyText = "Personal Finance Manager";
        float companyWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(companyText) / 1000 * 14;
        centerX = (coverPage.getMediaBox().getWidth() - companyWidth) / 2;
        contentStream.newLineAtOffset(centerX, 150);
        contentStream.showText(companyText);
        contentStream.endText();

        contentStream.close();
    }

    private void createTableOfContents(PDDocument document) throws IOException {
        PDPage tocPage = new PDPage(PDRectangle.A4);
        document.addPage(tocPage);

        PDPageContentStream contentStream = new PDPageContentStream(document, tocPage);

        // Title of TOC
        contentStream.beginText();
        contentStream.setNonStrokingColor(0.2f, 0.4f, 0.6f);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Table of Contents");
        contentStream.endText();

        // Add a decorative line
        contentStream.setStrokingColor(0.2f, 0.4f, 0.6f);
        contentStream.setLineWidth(1f);
        contentStream.moveTo(50, 740);
        contentStream.lineTo(550, 740);
        contentStream.stroke();

        // Add each section to TOC
        contentStream.beginText();
        contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f);
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 710);

        int pageNum = 3; // Start from page 3 (after cover and TOC)
        float leading = 20f;
        float yPosition = 710;

        for (String sectionTitle : reportSections.keySet()) {
            // Add section title
            contentStream.showText(sectionTitle);

            // Add dots
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(250, 0);
            contentStream.showText("........................");

            // Add page number
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(150, 0);
            contentStream.showText(String.valueOf(pageNum));

            // Reset for next line
            contentStream.newLineAtOffset(-400, -leading);
            yPosition -= leading;

            // Each section takes at least one page
            pageNum++;
        }

        contentStream.endText();
        contentStream.close();
    }

    private void addReportSection(PDDocument document, String sectionTitle, String content) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Add a colored section header
        contentStream.setNonStrokingColor(0.95f, 0.95f, 0.95f);
        contentStream.addRect(0, 770, page.getMediaBox().getWidth(), 50);
        contentStream.fill();

        // Add section title
        contentStream.beginText();
        contentStream.setNonStrokingColor(0.2f, 0.4f, 0.6f);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.newLineAtOffset(50, 790);
        contentStream.showText(sectionTitle);
        contentStream.endText();

        // Add a decorative line below header
        contentStream.setStrokingColor(0.2f, 0.4f, 0.6f);
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(50, 770);
        contentStream.lineTo(550, 770);
        contentStream.stroke();

        // Add page number
        int pageCount = document.getNumberOfPages();
        contentStream.beginText();
        contentStream.setNonStrokingColor(0.5f, 0.5f, 0.5f);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(530, 25);
        contentStream.showText(String.valueOf(pageCount));
        contentStream.endText();

        // Add content
        addFormattedContent(document, page, contentStream, content);
    }

    private void addFormattedContent(PDDocument document, PDPage page, PDPageContentStream contentStream, String content) throws IOException {
        try {
            // Process content
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f);
            contentStream.setLeading(14); // Line spacing

            float marginX = 50;
            float marginY = 750; // Start below header
            contentStream.newLineAtOffset(marginX, marginY);

            // Break content into paragraphs and lines
            String[] paragraphs = content.split("\n\n");

            float yPosition = marginY;

            for (int i = 0; i < paragraphs.length; i++) {
                // For each paragraph
                String[] lines = paragraphs[i].split("\n");

                // Add spacing between paragraphs
                if (i > 0) {
                    yPosition -= 10;
                    contentStream.newLineAtOffset(0, -10);
                }

                for (String line : lines) {
                    // Check if we need a new page
                    yPosition -= 14;
                    if (yPosition < 50) {
                        // End current page
                        contentStream.endText();
                        contentStream.close();

                        // Create new page
                        PDPage newPage = new PDPage(PDRectangle.A4);
                        document.addPage(newPage);
                        contentStream = new PDPageContentStream(document, newPage);

                        // Add page number
                        int pageCount = document.getNumberOfPages();
                        contentStream.beginText();
                        contentStream.setNonStrokingColor(0.5f, 0.5f, 0.5f);
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        contentStream.newLineAtOffset(530, 25);
                        contentStream.showText(String.valueOf(pageCount));
                        contentStream.endText();

                        // Continue with text
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, 11);
                        contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f);
                        contentStream.setLeading(14);
                        yPosition = 750; // Reset Y position for new page
                        contentStream.newLineAtOffset(marginX, yPosition);
                    }

                    // Wrap long lines
                    int maxCharsPerLine = 90; // Approximate fit for font size
                    if (line.length() > maxCharsPerLine) {
                        int startPos = 0;
                        while (startPos < line.length()) {
                            int endPos = Math.min(startPos + maxCharsPerLine, line.length());

                            // Try to find a space to break at
                            if (endPos < line.length()) {
                                int spacePos = line.lastIndexOf(' ', endPos);
                                if (spacePos > startPos) {
                                    endPos = spacePos;
                                }
                            }

                            String subLine = line.substring(startPos, endPos);
                            contentStream.showText(subLine);
                            contentStream.newLine();

                            yPosition -= 14;
                            if (yPosition < 50) {
                                // Create new page (same code as above)
                                contentStream.endText();
                                contentStream.close();

                                PDPage newPage = new PDPage(PDRectangle.A4);
                                document.addPage(newPage);
                                contentStream = new PDPageContentStream(document, newPage);

                                // Add page number
                                int pageCount = document.getNumberOfPages();
                                contentStream.beginText();
                                contentStream.setNonStrokingColor(0.5f, 0.5f, 0.5f);
                                contentStream.setFont(PDType1Font.HELVETICA, 10);
                                contentStream.newLineAtOffset(530, 25);
                                contentStream.showText(String.valueOf(pageCount));
                                contentStream.endText();

                                contentStream.beginText();
                                contentStream.setFont(PDType1Font.HELVETICA, 11);
                                contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f);
                                contentStream.setLeading(14);
                                yPosition = 750;
                                contentStream.newLineAtOffset(marginX, yPosition);
                            }

                            startPos = endPos;
                            if (startPos < line.length() && line.charAt(startPos) == ' ') {
                                startPos++;
                            }
                        }
                    } else {
                        contentStream.showText(line);
                        contentStream.newLine();
                    }
                }
            }

            contentStream.endText();
        } finally {
            contentStream.close();
        }
    }

    private byte[] mergeHtmlReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Write HTML header
        outputStream.write("<!DOCTYPE html><html><head><title>".getBytes());
        outputStream.write(title.getBytes());
        outputStream.write("</title>".getBytes());
        outputStream.write("<style>.report-section{margin:30px 0;padding:20px;border:1px solid #ddd;} pre{white-space:pre-wrap;font-family:monospace;}</style>".getBytes());
        outputStream.write("</head><body><h1>".getBytes());
        outputStream.write(title.getBytes());
        outputStream.write("</h1>".getBytes());

        // Write each section with proper HTML formatting
        for (Map.Entry<String, byte[]> section : reportSections.entrySet()) {
            outputStream.write(("<div class='report-section'><h2>" + section.getKey() + "</h2><pre>").getBytes());
            outputStream.write(section.getValue());
            outputStream.write("</pre></div>".getBytes());
        }

        // Write HTML footer
        outputStream.write("</body></html>".getBytes());

        return outputStream.toByteArray();
    }

    private byte[] mergeCsvReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        boolean isFirst = true;
        for (Map.Entry<String, byte[]> section : reportSections.entrySet()) {
            if (!isFirst) {
                // Add section separator
                outputStream.write(("\n\n\"" + section.getKey() + "\"\n").getBytes());
            } else {
                outputStream.write(("\"" + section.getKey() + "\"\n").getBytes());
                isFirst = false;
            }
            outputStream.write(section.getValue());
        }

        return outputStream.toByteArray();
    }

    private byte[] mergeDefaultReports() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write((title + "\n" + "=".repeat(title.length()) + "\n\n").getBytes());

        for (Map.Entry<String, byte[]> section : reportSections.entrySet()) {
            outputStream.write(("=== " + section.getKey() + " ===\n\n").getBytes());
            outputStream.write(section.getValue());
            outputStream.write("\n\n".getBytes());
        }

        return outputStream.toByteArray();
    }
}