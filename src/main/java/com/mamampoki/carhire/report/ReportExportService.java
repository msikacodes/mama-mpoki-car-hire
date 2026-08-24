package com.mamampoki.carhire.report;

import com.mamampoki.carhire.report.dto.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final ReportService reportService;

    // ==================== PDF EXPORT ====================

    public byte[] exportSpecialHireReportPdf(Long ownerId, LocalDate from, LocalDate to) throws IOException {
        SpecialHireReport report = reportService.getSpecialHireReport(ownerId, from, to);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, "Special Hire Report", from, to);

        // Summary table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{60, 40});

        addRow(table, "Total Bookings", String.valueOf(report.getTotalBookings()));
        addRow(table, "Completed Trips", String.valueOf(report.getCompletedTrips()));
        addRow(table, "Cancelled Bookings", String.valueOf(report.getCancelledBookings()));
        addRow(table, "Total Revenue", formatCurrency(report.getTotalRevenue()));
        addRow(table, "Total Expenses", formatCurrency(report.getTotalExpenses()));
        addRow(table, "Total Profit", formatCurrency(report.getTotalProfit()));
        addRow(table, "Profit Margin", String.format("%.1f%%", report.getProfitMargin()));
        addRow(table, "Average Booking Value", formatCurrency(report.getAverageBookingValue()));

        if (report.getTopVehicleRegNumber() != null) {
            addRow(table, "Top Vehicle", report.getTopVehicleRegNumber());
            addRow(table, "Top Vehicle Trips", String.valueOf(report.getTopVehicleTrips()));
        }

        if (report.getTopDestination() != null) {
            addRow(table, "Top Destination", report.getTopDestination());
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    public byte[] exportDaladalaReportPdf(Long ownerId, LocalDate from, LocalDate to) throws IOException {
        DaladalaReport report = reportService.getDaladalaReport(ownerId, from, to);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, "Daladala Report", from, to);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{60, 40});

        addRow(table, "Total Operations", String.valueOf(report.getTotalOperations()));
        addRow(table, "Completed Operations", String.valueOf(report.getCompletedOperations()));
        addRow(table, "Total Revenue", formatCurrency(report.getTotalRevenue()));
        addRow(table, "Total Expenses", formatCurrency(report.getTotalExpenses()));
        addRow(table, "Total Profit", formatCurrency(report.getTotalProfit()));
        addRow(table, "Profit Margin", String.format("%.1f%%", report.getProfitMargin()));
        addRow(table, "Average Daily Revenue", formatCurrency(report.getAverageDailyRevenue()));
        addRow(table, "Average Daily Expenses", formatCurrency(report.getAverageDailyExpenses()));
        addRow(table, "Total Passengers", String.valueOf(report.getTotalPassengers()));

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    public byte[] exportExpenseReportPdf(Long ownerId, LocalDate from, LocalDate to) throws IOException {
        ExpenseReport report = reportService.getExpenseReport(ownerId, from, to);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, "Expense Report", from, to);

        Paragraph totalPara = new Paragraph(
                String.format("Total Expenses: %s", formatCurrency(report.getTotalExpenses())),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        totalPara.setSpacingAfter(20);
        document.add(totalPara);

        // Expenses by category
        if (report.getByCategory() != null && !report.getByCategory().isEmpty()) {
            document.add(new Paragraph("Expenses by Category:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{60, 40});

            report.getByCategory().forEach((category, amount) -> {
                if (!"Total".equals(category)) {
                    addRow(table, category, formatCurrency(amount));
                }
            });

            document.add(table);
        }

        document.close();

        return baos.toByteArray();
    }

    public byte[] exportMonthlySummaryPdf(Long ownerId, int year, int month) throws IOException {
        MonthlySummary report = reportService.getMonthlySummary(ownerId, year, month);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, "Monthly Summary", LocalDate.of(year, month, 1),
                LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth()));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{30, 25, 25, 20});

        // Header
        addTableHeader(table, "Module", "Revenue", "Expenses", "Profit");
        addRow(table, "Special Hire",
                formatCurrency(report.getSpecialHire().getRevenue()),
                formatCurrency(report.getSpecialHire().getExpenses()),
                formatCurrency(report.getSpecialHire().getProfit()));
        addRow(table, "Daladala",
                formatCurrency(report.getDaladala().getRevenue()),
                formatCurrency(report.getDaladala().getExpenses()),
                formatCurrency(report.getDaladala().getProfit()));
        addRow(table, "Private Cars",
                formatCurrency(report.getPrivateCars().getRevenue()),
                formatCurrency(report.getPrivateCars().getExpenses()),
                formatCurrency(report.getPrivateCars().getProfit()));

        // Total row
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL", boldFont));
        PdfPCell totalRevenue = new PdfPCell(new Phrase(formatCurrency(report.getTotalRevenue()), boldFont));
        PdfPCell totalExpenses = new PdfPCell(new Phrase(formatCurrency(report.getTotalExpenses()), boldFont));
        PdfPCell totalProfit = new PdfPCell(new Phrase(formatCurrency(report.getNetProfit()), boldFont));
        table.addCell(totalLabel);
        table.addCell(totalRevenue);
        table.addCell(totalExpenses);
        table.addCell(totalProfit);

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    // ==================== EXCEL EXPORT ====================

    public byte[] exportSpecialHireReportExcel(Long ownerId, LocalDate from, LocalDate to) throws IOException {
        SpecialHireReport report = reportService.getSpecialHireReport(ownerId, from, to);

        try (org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Special Hire Report");

            int rowIdx = 0;
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowIdx++);
            headerRow.createCell(0).setCellValue("Metric");
            headerRow.createCell(1).setCellValue("Value");

            styleHeader(workbook, headerRow);

            createRow(sheet, rowIdx++, "Total Bookings", String.valueOf(report.getTotalBookings()));
            createRow(sheet, rowIdx++, "Completed Trips", String.valueOf(report.getCompletedTrips()));
            createRow(sheet, rowIdx++, "Cancelled Bookings", String.valueOf(report.getCancelledBookings()));
            createRow(sheet, rowIdx++, "Total Revenue", formatCurrency(report.getTotalRevenue()));
            createRow(sheet, rowIdx++, "Total Expenses", formatCurrency(report.getTotalExpenses()));
            createRow(sheet, rowIdx++, "Total Profit", formatCurrency(report.getTotalProfit()));
            createRow(sheet, rowIdx++, "Profit Margin", String.format("%.1f%%", report.getProfitMargin()));
            createRow(sheet, rowIdx++, "Average Booking Value", formatCurrency(report.getAverageBookingValue()));

            if (report.getTopVehicleRegNumber() != null) {
                createRow(sheet, rowIdx++, "Top Vehicle", report.getTopVehicleRegNumber());
                createRow(sheet, rowIdx++, "Top Vehicle Trips", String.valueOf(report.getTopVehicleTrips()));
            }

            if (report.getTopDestination() != null) {
                createRow(sheet, rowIdx++, "Top Destination", report.getTopDestination());
            }

            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] exportExpenseReportExcel(Long ownerId, LocalDate from, LocalDate to) throws IOException {
        ExpenseReport report = reportService.getExpenseReport(ownerId, from, to);

        try (org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Expense Report");

            int rowIdx = 0;
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowIdx++);
            headerRow.createCell(0).setCellValue("Category");
            headerRow.createCell(1).setCellValue("Amount (TZS)");
            headerRow.createCell(2).setCellValue("Count");

            styleHeader(workbook, headerRow);

            if (report.getTopExpenses() != null) {
                for (ExpenseReport.ExpenseDetail detail : report.getTopExpenses()) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(detail.getCategory());
                    row.createCell(1).setCellValue(detail.getAmount().doubleValue());
                    row.createCell(2).setCellValue(detail.getCount());
                }
            }

            // Total row
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowIdx++);
            org.apache.poi.ss.usermodel.Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("TOTAL");
            totalLabel.setCellStyle(createBoldStyle(workbook));
            org.apache.poi.ss.usermodel.Cell totalAmount = totalRow.createCell(1);
            totalAmount.setCellValue(report.getTotalExpenses().doubleValue());
            totalAmount.setCellStyle(createBoldStyle(workbook));

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] exportMonthlySummaryExcel(Long ownerId, int year, int month) throws IOException {
        MonthlySummary report = reportService.getMonthlySummary(ownerId, year, month);

        try (org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Monthly Summary");

            int rowIdx = 0;
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowIdx++);
            headerRow.createCell(0).setCellValue("Module");
            headerRow.createCell(1).setCellValue("Revenue (TZS)");
            headerRow.createCell(2).setCellValue("Expenses (TZS)");
            headerRow.createCell(3).setCellValue("Profit (TZS)");

            styleHeader(workbook, headerRow);

            createExcelRow(sheet, "Special Hire",
                    report.getSpecialHire().getRevenue(),
                    report.getSpecialHire().getExpenses(),
                    report.getSpecialHire().getProfit());
            createExcelRow(sheet, "Daladala",
                    report.getDaladala().getRevenue(),
                    report.getDaladala().getExpenses(),
                    report.getDaladala().getProfit());
            createExcelRow(sheet, "Private Cars",
                    report.getPrivateCars().getRevenue(),
                    report.getPrivateCars().getExpenses(),
                    report.getPrivateCars().getProfit());

            // Total row
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowIdx++);
            org.apache.poi.ss.usermodel.CellStyle boldStyle = createBoldStyle(workbook);
            org.apache.poi.ss.usermodel.Cell totalModule = totalRow.createCell(0);
            totalModule.setCellValue("TOTAL");
            totalModule.setCellStyle(boldStyle);
            org.apache.poi.ss.usermodel.Cell totalRevenue = totalRow.createCell(1);
            totalRevenue.setCellValue(report.getTotalRevenue().doubleValue());
            totalRevenue.setCellStyle(boldStyle);
            org.apache.poi.ss.usermodel.Cell totalExpenses = totalRow.createCell(2);
            totalExpenses.setCellValue(report.getTotalExpenses().doubleValue());
            totalExpenses.setCellStyle(boldStyle);
            org.apache.poi.ss.usermodel.Cell totalProfit = totalRow.createCell(3);
            totalProfit.setCellValue(report.getNetProfit().doubleValue());
            totalProfit.setCellStyle(boldStyle);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    // ==================== HELPERS ====================

    private void addHeader(Document document, String title, LocalDate from, LocalDate to) throws DocumentException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        Paragraph titlePara = new Paragraph("MAMA MPOKI CAR HIRE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        Paragraph subtitle = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(5);
        document.add(subtitle);

        Paragraph dateRange = new Paragraph(
                String.format("Period: %s to %s", from.format(fmt), to.format(fmt)),
                FontFactory.getFont(FontFactory.HELVETICA, 10));
        dateRange.setAlignment(Element.ALIGN_CENTER);
        dateRange.setSpacingAfter(20);
        document.add(dateRange);
    }

    private void addRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        labelCell.setPadding(5);
        valueCell.setPadding(5);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addRow(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 10)));
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void createRow(org.apache.poi.ss.usermodel.Sheet sheet, int rowIdx, String label, String value) {
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private void createExcelRow(org.apache.poi.ss.usermodel.Sheet sheet, String module,
                                java.math.BigDecimal revenue,
                                java.math.BigDecimal expenses,
                                java.math.BigDecimal profit) {
        int lastRow = sheet.getLastRowNum() + 1;
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(lastRow);
        row.createCell(0).setCellValue(module);
        row.createCell(1).setCellValue(revenue.doubleValue());
        row.createCell(2).setCellValue(expenses.doubleValue());
        row.createCell(3).setCellValue(profit.doubleValue());
    }

    private void styleHeader(org.apache.poi.ss.usermodel.Workbook workbook, org.apache.poi.ss.usermodel.Row row) {
        org.apache.poi.ss.usermodel.CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (org.apache.poi.ss.usermodel.Cell cell : row) {
            cell.setCellStyle(style);
        }
    }

    private org.apache.poi.ss.usermodel.CellStyle createBoldStyle(org.apache.poi.ss.usermodel.Workbook workbook) {
        org.apache.poi.ss.usermodel.CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "0.00 TZS";
        return String.format("%,.2f TZS", amount);
    }
}
