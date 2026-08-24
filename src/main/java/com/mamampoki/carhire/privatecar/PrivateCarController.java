package com.mamampoki.carhire.privatecar;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.common.PaginatedResponse;
import com.mamampoki.carhire.fuel.dto.FuelRecordRequest;
import com.mamampoki.carhire.fuel.dto.FuelRecordResponse;
import com.mamampoki.carhire.maintenance.dto.MaintenanceRecordRequest;
import com.mamampoki.carhire.maintenance.dto.MaintenanceRecordResponse;
import com.mamampoki.carhire.security.OwnerDetails;
import com.mamampoki.carhire.privatecar.dto.PrivateCarRequest;
import com.mamampoki.carhire.privatecar.dto.PrivateCarResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private-cars")
@RequiredArgsConstructor
@Tag(name = "Private Cars", description = "Private car management")
public class PrivateCarController {

    private final PrivateCarService privateCarService;

    @Operation(summary = "List Private Cars", description = "Get all private cars")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PrivateCarResponse>>> getPrivateCars(
            @AuthenticationPrincipal OwnerDetails ownerDetails) {
        List<PrivateCarResponse> cars = privateCarService.getPrivateCars(
                ownerDetails.getOwner().getId(), Pageable.unpaged()).getContent();
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @Operation(summary = "Get Private Car", description = "Get private car details with costs")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrivateCarResponse>> getPrivateCar(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        PrivateCarResponse car = privateCarService.getPrivateCarById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(car));
    }

    @Operation(summary = "Create Private Car", description = "Register a private car with insurance info")
    @PostMapping
    public ResponseEntity<ApiResponse<PrivateCarResponse>> createPrivateCar(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody PrivateCarRequest request) {
        PrivateCarResponse car = privateCarService.createPrivateCar(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Private car created successfully", car));
    }

    @Operation(summary = "Update Private Car", description = "Update private car details")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PrivateCarResponse>> updatePrivateCar(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody PrivateCarRequest request) {
        PrivateCarResponse car = privateCarService.updatePrivateCar(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Private car updated successfully", car));
    }

    @Operation(summary = "Delete Private Car", description = "Delete a private car")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePrivateCar(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        privateCarService.deletePrivateCar(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Private car deleted successfully", null));
    }

    // ==================== FUEL RECORDS ====================

    @Operation(summary = "Get Fuel Records", description = "Get fuel records for a private car")
    @GetMapping("/{id}/fuel")
    public ResponseEntity<ApiResponse<List<FuelRecordResponse>>> getFuelRecords(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<FuelRecordResponse> records = privateCarService.getFuelRecords(
                ownerDetails.getOwner().getId(), id, pageable).getContent();
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @Operation(summary = "Add Fuel Record", description = "Record a fuel purchase with liters and cost")
    @PostMapping("/{id}/fuel")
    public ResponseEntity<ApiResponse<FuelRecordResponse>> addFuelRecord(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody FuelRecordRequest request) {
        FuelRecordResponse record = privateCarService.addFuelRecord(
                ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fuel record added successfully", record));
    }

    // ==================== MAINTENANCE RECORDS ====================

    @Operation(summary = "Get Maintenance Records", description = "Get maintenance records for a private car")
    @GetMapping("/{id}/maintenance")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordResponse>>> getMaintenanceRecords(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<MaintenanceRecordResponse> records = privateCarService.getMaintenanceRecords(
                ownerDetails.getOwner().getId(), id, pageable).getContent();
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @Operation(summary = "Add Maintenance Record", description = "Record maintenance service or repair")
    @PostMapping("/{id}/maintenance")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> addMaintenanceRecord(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRecordRequest request) {
        MaintenanceRecordResponse record = privateCarService.addMaintenanceRecord(
                ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Maintenance record added successfully", record));
    }

    // ==================== DOCUMENT EXPIRY ALERTS ====================

    @Operation(summary = "Get Expiring Documents", description = "Get private cars with expiring insurance/registration")
    @GetMapping("/expiring-docs")
    public ResponseEntity<ApiResponse<List<PrivateCarResponse>>> getExpiringDocuments(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(defaultValue = "30") int days) {
        List<PrivateCarResponse> cars = privateCarService.getExpiringDocuments(
                ownerDetails.getOwner().getId(), days);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }
}
