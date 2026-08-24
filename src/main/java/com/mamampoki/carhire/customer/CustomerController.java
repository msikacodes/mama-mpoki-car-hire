package com.mamampoki.carhire.customer;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.common.PaginatedResponse;
import com.mamampoki.carhire.security.OwnerDetails;
import com.mamampoki.carhire.customer.dto.CustomerRequest;
import com.mamampoki.carhire.customer.dto.CustomerResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer/hirer management")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "List Customers", description = "Get paginated list of customers")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<CustomerResponse>>> getCustomers(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<CustomerResponse> customers = customerService.getCustomers(
                ownerDetails.getOwner().getId(), pageable);

        PaginatedResponse<CustomerResponse> response = PaginatedResponse.of(
                customers.getContent(),
                customers.getTotalElements(),
                customers.getTotalPages(),
                customers.getNumber(),
                customers.getSize());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get Customer", description = "Get customer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        CustomerResponse customer = customerService.getCustomerById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @Operation(summary = "Create Customer", description = "Register a new customer")
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse customer = customerService.createCustomer(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", customer));
    }

    @Operation(summary = "Update Customer", description = "Update customer details")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse customer = customerService.updateCustomer(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customer));
    }

    @Operation(summary = "Delete Customer", description = "Soft-delete a customer")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {

        customerService.deleteCustomer(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }

    @Operation(summary = "Search Customers", description = "Search customers by name or phone")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam String query) {

        List<CustomerResponse> customers = customerService.searchCustomers(
                ownerDetails.getOwner().getId(), query);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
}
