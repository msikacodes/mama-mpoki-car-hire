package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.common.PaginatedResponse;
import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.security.OwnerDetails;
import com.mamampoki.carhire.daladala.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/daladala")
@RequiredArgsConstructor
@Tag(name = "Daladala", description = "Daladala routes and daily operations")
public class DaladalaController {

    private final DaladalaService daladalaService;

    // ==================== ROUTES ====================

    @Operation(summary = "List Routes", description = "Get all daladala routes")
    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<PaginatedResponse<RouteResponse>>> getRoutes(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<RouteResponse> routes = daladalaService.getRoutes(ownerDetails.getOwner().getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(
                routes.getContent(), routes.getTotalElements(),
                routes.getTotalPages(), routes.getNumber(), routes.getSize())));
    }

    @Operation(summary = "Get Route", description = "Get route details by ID")
    @GetMapping("/routes/{id}")
    public ResponseEntity<ApiResponse<RouteResponse>> getRoute(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        RouteResponse route = daladalaService.getRouteById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(route));
    }

    @Operation(summary = "Create Route", description = "Create a new daladala route")
    @PostMapping("/routes")
    public ResponseEntity<ApiResponse<RouteResponse>> createRoute(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody RouteRequest request) {
        RouteResponse route = daladalaService.createRoute(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Route created successfully", route));
    }

    @Operation(summary = "Update Route", description = "Update route details")
    @PutMapping("/routes/{id}")
    public ResponseEntity<ApiResponse<RouteResponse>> updateRoute(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody RouteRequest request) {
        RouteResponse route = daladalaService.updateRoute(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Route updated successfully", route));
    }

    @Operation(summary = "Delete Route", description = "Delete a route")
    @DeleteMapping("/routes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        daladalaService.deleteRoute(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Route deleted successfully", null));
    }

    // ==================== DAILY OPERATIONS ====================

    @Operation(summary = "List Operations", description = "Get daily operations with optional filters")
    @GetMapping("/operations")
    public ResponseEntity<ApiResponse<PaginatedResponse<DailyOperationResponse>>> getOperations(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(required = false) TripStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "operationDate,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<DailyOperationResponse> operations = daladalaService.getOperations(
                ownerDetails.getOwner().getId(), status, startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(
                operations.getContent(), operations.getTotalElements(),
                operations.getTotalPages(), operations.getNumber(), operations.getSize())));
    }

    @Operation(summary = "Get Operation", description = "Get operation details with revenue/expenses")
    @GetMapping("/operations/{id}")
    public ResponseEntity<ApiResponse<DailyOperationResponse>> getOperation(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        DailyOperationResponse operation = daladalaService.getOperationById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(operation));
    }

    @Operation(summary = "Create Operation", description = "Create a daily operation record")
    @PostMapping("/operations")
    public ResponseEntity<ApiResponse<DailyOperationResponse>> createOperation(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody DailyOperationRequest request) {
        DailyOperationResponse operation = daladalaService.createOperation(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Operation created successfully", operation));
    }

    @Operation(summary = "Complete Operation", description = "Mark operation as completed")
    @PutMapping("/operations/{id}/complete")
    public ResponseEntity<ApiResponse<DailyOperationResponse>> completeOperation(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @RequestParam(required = false) Integer totalPassengers,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime returnTime) {
        DailyOperationResponse operation = daladalaService.completeOperation(
                ownerDetails.getOwner().getId(), id, totalPassengers, returnTime);
        return ResponseEntity.ok(ApiResponse.success("Operation completed", operation));
    }

    // ==================== DAILY REVENUE ====================

    @Operation(summary = "List Revenues", description = "Get revenues for an operation")
    @GetMapping("/operations/{operationId}/revenues")
    public ResponseEntity<ApiResponse<List<DailyRevenueResponse>>> getRevenues(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long operationId) {
        List<DailyRevenueResponse> revenues = daladalaService.getRevenues(ownerDetails.getOwner().getId(), operationId);
        return ResponseEntity.ok(ApiResponse.success(revenues));
    }

    @Operation(summary = "Add Revenue", description = "Add revenue to an operation")
    @PostMapping("/operations/{operationId}/revenues")
    public ResponseEntity<ApiResponse<DailyRevenueResponse>> addRevenue(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long operationId,
            @Valid @RequestBody DailyRevenueRequest request) {
        DailyRevenueResponse revenue = daladalaService.addRevenue(ownerDetails.getOwner().getId(), operationId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Revenue added successfully", revenue));
    }

    @Operation(summary = "Delete Revenue", description = "Remove a revenue record")
    @DeleteMapping("/operations/{operationId}/revenues/{revenueId}")
    public ResponseEntity<ApiResponse<Void>> deleteRevenue(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long operationId,
            @PathVariable Long revenueId) {
        daladalaService.deleteRevenue(ownerDetails.getOwner().getId(), operationId, revenueId);
        return ResponseEntity.ok(ApiResponse.success("Revenue deleted successfully", null));
    }

    // ==================== DAILY EXPENSES ====================

    @Operation(summary = "List Expenses", description = "Get expenses for an operation")
    @GetMapping("/operations/{operationId}/expenses")
    public ResponseEntity<ApiResponse<List<DailyExpenseResponse>>> getExpenses(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long operationId) {
        List<DailyExpenseResponse> expenses = daladalaService.getExpenses(ownerDetails.getOwner().getId(), operationId);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }

    @Operation(summary = "Add Expense", description = "Add an expense to an operation")
    @PostMapping("/operations/{operationId}/expenses")
    public ResponseEntity<ApiResponse<DailyExpenseResponse>> addExpense(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long operationId,
            @Valid @RequestBody DailyExpenseRequest request) {
        DailyExpenseResponse expense = daladalaService.addExpense(ownerDetails.getOwner().getId(), operationId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense added successfully", expense));
    }

    @Operation(summary = "Delete Expense", description = "Remove an expense record")
    @DeleteMapping("/operations/{operationId}/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long operationId,
            @PathVariable Long expenseId) {
        daladalaService.deleteExpense(ownerDetails.getOwner().getId(), operationId, expenseId);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }
}
