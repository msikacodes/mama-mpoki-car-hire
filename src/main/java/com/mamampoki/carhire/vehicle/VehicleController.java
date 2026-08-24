package com.mamampoki.carhire.vehicle;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.common.PaginatedResponse;
import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.VehicleStatus;
import com.mamampoki.carhire.security.OwnerDetails;
import com.mamampoki.carhire.vehicle.dto.VehicleRequest;
import com.mamampoki.carhire.vehicle.dto.VehicleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Fleet vehicle management")
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "List Vehicles", description = "Get paginated list of vehicles with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<VehicleResponse>>> getVehicles(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(required = false) ModuleType moduleType,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<VehicleResponse> vehicles = vehicleService.getVehicles(
                ownerDetails.getOwner().getId(), moduleType, status, pageable);

        PaginatedResponse<VehicleResponse> response = PaginatedResponse.of(
                vehicles.getContent(),
                vehicles.getTotalElements(),
                vehicles.getTotalPages(),
                vehicles.getNumber(),
                vehicles.getSize());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get Vehicle", description = "Get vehicle by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicle(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        VehicleResponse vehicle = vehicleService.getVehicleById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(vehicle));
    }

    @Operation(summary = "Create Vehicle", description = "Register a new vehicle in the fleet")
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody VehicleRequest request) {

        VehicleResponse vehicle = vehicleService.createVehicle(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle created successfully", vehicle));
    }

    @Operation(summary = "Update Vehicle", description = "Update vehicle details")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {

        VehicleResponse vehicle = vehicleService.updateVehicle(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", vehicle));
    }

    @Operation(summary = "Delete Vehicle", description = "Soft-delete a vehicle")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        vehicleService.deleteVehicle(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully", null));
    }

    @Operation(summary = "Update Vehicle Status", description = "Update vehicle operating status")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateStatus(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {

        VehicleResponse vehicle = vehicleService.updateStatus(ownerDetails.getOwner().getId(), id, status);
        return ResponseEntity.ok(ApiResponse.success("Vehicle status updated", vehicle));
    }

    @Operation(summary = "Check Available Vehicles", description = "Get vehicles available for a date range")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAvailableVehicles(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<VehicleResponse> vehicles = vehicleService.getAvailableVehicles(
                ownerDetails.getOwner().getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(vehicles));
    }

    @Operation(summary = "Fleet Summary", description = "Get fleet statistics by module type")
    @GetMapping("/fleet-summary")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getFleetSummary(
            @AuthenticationPrincipal OwnerDetails ownerDetails) {

        Map<String, Long> summary = vehicleService.getFleetSummary(ownerDetails.getOwner().getId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
