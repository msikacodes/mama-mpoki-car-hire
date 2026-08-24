package com.mamampoki.carhire.report;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.report.dto.*;
import com.mamampoki.carhire.security.OwnerDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Financial and operational reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportExportService exportService;

    @Operation(summary = "Special Hire Report", description = "Get special hire financial report for date range")
    @GetMapping("/special-hire")
    public ResponseEntity<ApiResponse<SpecialHireReport>> getSpecialHireReport(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        SpecialHireReport report = reportService.getSpecialHireReport(
                ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @Operation(summary = "Daladala Report", description = "Get daladala performance report for date range")
    @GetMapping("/daladala")
    public ResponseEntity<ApiResponse<DaladalaReport>> getDaladalaReport(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        DaladalaReport report = reportService.getDaladalaReport(
                ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @Operation(summary = "Vehicle Profitability", description = "Get per-vehicle cost and profit breakdown")
    @GetMapping("/vehicle-profitability")
    public ResponseEntity<ApiResponse<VehicleProfitabilityReport>> getVehicleProfitabilityReport(
            @AuthenticationPrincipal OwnerDetails ownerDetails) {

        VehicleProfitabilityReport report = reportService.getVehicleProfitabilityReport(
                ownerDetails.getOwner().getId());
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @Operation(summary = "Expense Report", description = "Get expense breakdown by category")
    @GetMapping("/expenses")
    public ResponseEntity<ApiResponse<ExpenseReport>> getExpenseReport(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        ExpenseReport report = reportService.getExpenseReport(
                ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @Operation(summary = "Monthly Summary", description = "Get monthly P&L by module")
    @GetMapping("/monthly-summary")
    public ResponseEntity<ApiResponse<MonthlySummary>> getMonthlySummary(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam int year,
            @RequestParam int month) {

        MonthlySummary summary = reportService.getMonthlySummary(
                ownerDetails.getOwner().getId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @Operation(summary = "Quarterly Summary", description = "Get quarterly P&L by module")
    @GetMapping("/quarterly-summary")
    public ResponseEntity<ApiResponse<MonthlySummary>> getQuarterlySummary(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam int year,
            @RequestParam int quarter) {

        MonthlySummary summary = reportService.getQuarterlySummary(
                ownerDetails.getOwner().getId(), year, quarter);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    // ==================== PDF EXPORT ====================

    @Operation(summary = "Export Special Hire Report (PDF)", description = "Download special hire report as PDF")
    @GetMapping(value = "/special-hire/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportSpecialHirePdf(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {

        byte[] pdf = exportService.exportSpecialHireReportPdf(ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=special-hire-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Export Daladala Report (PDF)", description = "Download daladala report as PDF")
    @GetMapping(value = "/daladala/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportDaladalaPdf(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {

        byte[] pdf = exportService.exportDaladalaReportPdf(ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daladala-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Export Expense Report (PDF)", description = "Download expense report as PDF")
    @GetMapping(value = "/expenses/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportExpensePdf(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {

        byte[] pdf = exportService.exportExpenseReportPdf(ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expense-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Export Monthly Summary (PDF)", description = "Download monthly summary as PDF")
    @GetMapping(value = "/monthly-summary/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportMonthlySummaryPdf(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam int year,
            @RequestParam int month) throws IOException {

        byte[] pdf = exportService.exportMonthlySummaryPdf(ownerDetails.getOwner().getId(), year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly-summary.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ==================== EXCEL EXPORT ====================

    @Operation(summary = "Export Special Hire Report (Excel)", description = "Download special hire report as Excel")
    @GetMapping(value = "/special-hire/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportSpecialHireExcel(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {

        byte[] excel = exportService.exportSpecialHireReportExcel(ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=special-hire-report.xlsx")
                .body(excel);
    }

    @Operation(summary = "Export Expense Report (Excel)", description = "Download expense report as Excel")
    @GetMapping(value = "/expenses/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportExpenseExcel(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {

        byte[] excel = exportService.exportExpenseReportExcel(ownerDetails.getOwner().getId(), from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expense-report.xlsx")
                .body(excel);
    }

    @Operation(summary = "Export Monthly Summary (Excel)", description = "Download monthly summary as Excel")
    @GetMapping(value = "/monthly-summary/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportMonthlySummaryExcel(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam int year,
            @RequestParam int month) throws IOException {

        byte[] excel = exportService.exportMonthlySummaryExcel(ownerDetails.getOwner().getId(), year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly-summary.xlsx")
                .body(excel);
    }
}
