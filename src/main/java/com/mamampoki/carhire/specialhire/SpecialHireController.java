package com.mamampoki.carhire.specialhire;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.common.PaginatedResponse;
import com.mamampoki.carhire.common.enums.BookingStatus;
import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.security.OwnerDetails;
import com.mamampoki.carhire.specialhire.dto.*;
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
@RequestMapping("/api/v1/special-hire")
@RequiredArgsConstructor
@Tag(name = "Special Hire", description = "Special hire bookings, trips, and payments")
public class SpecialHireController {

    private final SpecialHireService specialHireService;

    // ==================== BOOKINGS ====================

    @Operation(summary = "List Bookings", description = "Get paginated list of hire bookings")
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<PaginatedResponse<HireBookingResponse>>> getBookings(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<HireBookingResponse> bookings = specialHireService.getBookings(
                ownerDetails.getOwner().getId(), status, pageable);

        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(
                bookings.getContent(), bookings.getTotalElements(),
                bookings.getTotalPages(), bookings.getNumber(), bookings.getSize())));
    }

    @Operation(summary = "Get Booking", description = "Get booking details by ID")
    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<HireBookingResponse>> getBooking(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        HireBookingResponse booking = specialHireService.getBookingById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @Operation(summary = "Create Booking", description = "Create a new hire booking")
    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<HireBookingResponse>> createBooking(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody HireBookingRequest request) {
        HireBookingResponse booking = specialHireService.createBooking(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", booking));
    }

    @Operation(summary = "Update Booking", description = "Update booking details")
    @PutMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<HireBookingResponse>> updateBooking(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody HireBookingRequest request) {
        HireBookingResponse booking = specialHireService.updateBooking(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Booking updated successfully", booking));
    }

    @Operation(summary = "Update Booking Status", description = "Change booking status (PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED)")
    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<HireBookingResponse>> updateBookingStatus(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        HireBookingResponse booking = specialHireService.updateBookingStatus(ownerDetails.getOwner().getId(), id, status);
        return ResponseEntity.ok(ApiResponse.success("Booking status updated", booking));
    }

    @Operation(summary = "Get Booking Financials", description = "Get financial summary for a booking")
    @GetMapping("/bookings/{id}/financials")
    public ResponseEntity<ApiResponse<BookingFinancialSummary>> getBookingFinancials(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        BookingFinancialSummary summary = specialHireService.getBookingFinancials(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    // ==================== TRIPS ====================

    @Operation(summary = "List Trips", description = "Get paginated list of trips")
    @GetMapping("/trips")
    public ResponseEntity<ApiResponse<PaginatedResponse<TripResponse>>> getTrips(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @RequestParam(required = false) TripStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<TripResponse> trips = specialHireService.getTrips(
                ownerDetails.getOwner().getId(), status, pageable);

        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(
                trips.getContent(), trips.getTotalElements(),
                trips.getTotalPages(), trips.getNumber(), trips.getSize())));
    }

    @Operation(summary = "Get Trip", description = "Get trip details by ID")
    @GetMapping("/trips/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> getTrip(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id) {
        TripResponse trip = specialHireService.getTripById(ownerDetails.getOwner().getId(), id);
        return ResponseEntity.ok(ApiResponse.success(trip));
    }

    @Operation(summary = "Create Trip", description = "Create a trip for a booking")
    @PostMapping("/trips")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody TripRequest request) {
        TripResponse trip = specialHireService.createTrip(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip created successfully", trip));
    }

    @Operation(summary = "Complete Trip", description = "Mark a trip as completed with final odometer reading")
    @PutMapping("/trips/{id}/complete")
    public ResponseEntity<ApiResponse<TripResponse>> completeTrip(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long id,
            @Valid @RequestBody TripCompleteRequest request) {
        TripResponse trip = specialHireService.completeTrip(ownerDetails.getOwner().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Trip completed successfully", trip));
    }

    // ==================== TRIP EXPENSES ====================

    @Operation(summary = "List Trip Expenses", description = "Get all expenses for a trip")
    @GetMapping("/trips/{tripId}/expenses")
    public ResponseEntity<ApiResponse<List<TripExpenseResponse>>> getTripExpenses(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long tripId) {
        List<TripExpenseResponse> expenses = specialHireService.getTripExpenses(ownerDetails.getOwner().getId(), tripId);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }

    @Operation(summary = "Add Trip Expense", description = "Add an expense to a trip (fuel, allowance, tolls, etc.)")
    @PostMapping("/trips/{tripId}/expenses")
    public ResponseEntity<ApiResponse<TripExpenseResponse>> addTripExpense(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody TripExpenseRequest request) {
        TripExpenseResponse expense = specialHireService.addTripExpense(ownerDetails.getOwner().getId(), tripId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense added successfully", expense));
    }

    @Operation(summary = "Delete Trip Expense", description = "Remove an expense from a trip")
    @DeleteMapping("/trips/{tripId}/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteTripExpense(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long tripId,
            @PathVariable Long expenseId) {
        specialHireService.deleteTripExpense(ownerDetails.getOwner().getId(), tripId, expenseId);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    // ==================== PAYMENTS ====================

    @Operation(summary = "List Payments", description = "Get all payments for a booking")
    @GetMapping("/bookings/{bookingId}/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPayments(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long bookingId) {
        List<PaymentResponse> payments = specialHireService.getPayments(ownerDetails.getOwner().getId(), bookingId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @Operation(summary = "Record Payment", description = "Record a payment for a booking (cash, M-Pesa, bank transfer)")
    @PostMapping("/bookings/{bookingId}/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> addPayment(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long bookingId,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = specialHireService.addPayment(ownerDetails.getOwner().getId(), bookingId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded successfully", payment));
    }

    @Operation(summary = "Delete Payment", description = "Remove a payment record")
    @DeleteMapping("/bookings/{bookingId}/payments/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @PathVariable Long bookingId,
            @PathVariable Long paymentId) {
        specialHireService.deletePayment(ownerDetails.getOwner().getId(), bookingId, paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
    }

    // ==================== HELPERS ====================

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }
}
