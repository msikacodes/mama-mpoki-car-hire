package com.mamampoki.carhire.driver;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.common.PaginatedResponse;
import com.mamampoki.carhire.common.enums.StaffStatus;
import com.mamampoki.carhire.security.OwnerDetails;
import com.mamampoki.carhire.driver.dto.DriverRequest;
import com.mamampoki.carhire.driver.dto.DriverResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Driver management")
public class DriverController {

    private final DriverService driverService;

    @Operation(summary = "List Drivers", description = "Get paginated list of drivers")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<DriverResponse>>> getDrivers(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(required = false) StaffStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<DriverResponse> drivers = driverService.getDrivers(
                ownerDetails.getOwner().getId(), status, pageable);

        PaginatedResponse<DriverResponse> response = PaginatedResponse.of(
                drivers.getContent(),
                drivers.getTotalElements(),
                drivers.getTotalPages(),
                drivers.getNumber(),
                drivers.getSize());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get Driver", description = "Get driver by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriver(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        DriverResponse driver = driverService.getDriverById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @Operation(summary = "Create Driver", description = "Register a new driver")
    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponse>> createDriver(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody DriverRequest request) {

        DriverResponse driver = driverService.createDriver(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Driver created successfully", driver));
    }

    @Operation(summary = "Update Driver", description = "Update driver details")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponse>> updateDriver(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody DriverRequest request) {

        DriverResponse driver = driverService.updateDriver(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", driver));
    }

    @Operation(summary = "Delete Driver", description = "Soft-delete a driver")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        driverService.deleteDriver(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully", null));
    }

    @Operation(summary = "Update Driver Status", description = "Update driver status (ACTIVE, INACTIVE, SUSPENDED)")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DriverResponse>> updateStatus(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @RequestParam StaffStatus status) {

        DriverResponse driver = driverService.updateStatus(ownerDetails.getOwner().getId(), id, status);
        return ResponseEntity.ok(ApiResponse.success("Driver status updated", driver));
    }
}
