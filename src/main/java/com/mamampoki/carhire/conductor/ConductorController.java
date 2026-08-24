package com.mamampoki.carhire.conductor;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.common.PaginatedResponse;
import com.mamampoki.carhire.common.enums.StaffStatus;
import com.mamampoki.carhire.security.OwnerDetails;
import com.mamampoki.carhire.conductor.dto.ConductorRequest;
import com.mamampoki.carhire.conductor.dto.ConductorResponse;
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
@RequestMapping("/api/v1/conductors")
@RequiredArgsConstructor
@Tag(name = "Conductors", description = "Conductor management")
public class ConductorController {

    private final ConductorService conductorService;

    @Operation(summary = "List Conductors", description = "Get paginated list of conductors")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ConductorResponse>>> getConductors(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(required = false) StaffStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<ConductorResponse> conductors = conductorService.getConductors(
                ownerDetails.getOwner().getId(), status, pageable);

        PaginatedResponse<ConductorResponse> response = PaginatedResponse.of(
                conductors.getContent(),
                conductors.getTotalElements(),
                conductors.getTotalPages(),
                conductors.getNumber(),
                conductors.getSize());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get Conductor", description = "Get conductor by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConductorResponse>> getConductor(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        ConductorResponse conductor = conductorService.getConductorById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(conductor));
    }

    @Operation(summary = "Create Conductor", description = "Register a new conductor")
    @PostMapping
    public ResponseEntity<ApiResponse<ConductorResponse>> createConductor(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody ConductorRequest request) {

        ConductorResponse conductor = conductorService.createConductor(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Conductor created successfully", conductor));
    }

    @Operation(summary = "Update Conductor", description = "Update conductor details")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConductorResponse>> updateConductor(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody ConductorRequest request) {

        ConductorResponse conductor = conductorService.updateConductor(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Conductor updated successfully", conductor));
    }

    @Operation(summary = "Delete Conductor", description = "Soft-delete a conductor")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConductor(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        conductorService.deleteConductor(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Conductor deleted successfully", null));
    }

    @Operation(summary = "Update Conductor Status", description = "Update conductor status (ACTIVE, INACTIVE, SUSPENDED)")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ConductorResponse>> updateStatus(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @RequestParam StaffStatus status) {

        ConductorResponse conductor = conductorService.updateStatus(ownerDetails.getOwner().getId(), id, status);
        return ResponseEntity.ok(ApiResponse.success("Conductor status updated", conductor));
    }
}
