package com.mamampoki.carhire.specialhire;

import com.mamampoki.carhire.common.enums.BookingStatus;
import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.customer.Customer;
import com.mamampoki.carhire.customer.CustomerRepository;
import com.mamampoki.carhire.exception.BadRequestException;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.driver.Driver;
import com.mamampoki.carhire.driver.DriverRepository;
import com.mamampoki.carhire.specialhire.dto.*;
import com.mamampoki.carhire.vehicle.Vehicle;
import com.mamampoki.carhire.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecialHireService {

    private final HireBookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final TripExpenseRepository tripExpenseRepository;
    private final PaymentRepository paymentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    // ==================== BOOKINGS ====================

    public Page<HireBookingResponse> getBookings(Long ownerId, BookingStatus status, Pageable pageable) {
        Page<HireBooking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByOwnerIdAndStatusAndDeletedFalse(ownerId, status, pageable);
        } else {
            bookings = bookingRepository.findByOwnerIdAndDeletedFalse(ownerId, pageable);
        }
        return bookings.map(this::toBookingResponse);
    }

    public HireBookingResponse getBookingById(Long ownerId, Long bookingId) {
        HireBooking booking = findBooking(ownerId, bookingId);
        return toBookingResponse(booking);
    }

    @Transactional
    public HireBookingResponse createBooking(Long ownerId, HireBookingRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        }

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        }

        HireBooking booking = HireBooking.builder()
                .owner(owner)
                .vehicle(vehicle)
                .customer(customer)
                .hireDate(request.getHireDate())
                .endDate(request.getEndDate())
                .destination(request.getDestination())
                .tripPurpose(request.getTripPurpose())
                .agreedPrice(request.getAgreedPrice())
                .depositPaid(request.getDepositPaid() != null ? request.getDepositPaid() : BigDecimal.ZERO)
                .status(BookingStatus.PENDING)
                .notes(request.getNotes())
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking created: id={}, vehicle={}, destination={}", booking.getId(),
                vehicle.getRegNumber(), booking.getDestination());
        return toBookingResponse(booking);
    }

    @Transactional
    public HireBookingResponse updateBooking(Long ownerId, Long bookingId, HireBookingRequest request) {
        HireBooking booking = findBooking(ownerId, bookingId);

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        }

        booking.setVehicle(vehicle);
        booking.setCustomer(customer);
        booking.setHireDate(request.getHireDate());
        booking.setEndDate(request.getEndDate());
        booking.setDestination(request.getDestination());
        booking.setTripPurpose(request.getTripPurpose());
        booking.setAgreedPrice(request.getAgreedPrice());
        booking.setDepositPaid(request.getDepositPaid() != null ? request.getDepositPaid() : BigDecimal.ZERO);
        booking.setNotes(request.getNotes());

        booking = bookingRepository.save(booking);
        log.info("Booking updated: id={}", booking.getId());
        return toBookingResponse(booking);
    }

    @Transactional
    public HireBookingResponse updateBookingStatus(Long ownerId, Long bookingId, BookingStatus status) {
        HireBooking booking = findBooking(ownerId, bookingId);
        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        log.info("Booking status updated: id={}, status={}", booking.getId(), status);
        return toBookingResponse(booking);
    }

    // ==================== TRIPS ====================

    public Page<TripResponse> getTrips(Long ownerId, TripStatus status, Pageable pageable) {
        Page<Trip> trips;
        if (status != null) {
            trips = tripRepository.findByBookingOwnerIdAndStatusAndDeletedFalse(ownerId, status, pageable);
        } else {
            trips = tripRepository.findByBookingOwnerIdAndDeletedFalse(ownerId, pageable);
        }
        return trips.map(this::toTripResponse);
    }

    public TripResponse getTripById(Long ownerId, Long tripId) {
        Trip trip = findTrip(ownerId, tripId);
        return toTripResponse(trip);
    }

    @Transactional
    public TripResponse createTrip(Long ownerId, TripRequest request) {
        HireBooking booking = findBooking(ownerId, request.getBookingId());

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));

        Trip trip = Trip.builder()
                .booking(booking)
                .driver(driver)
                .vehicle(booking.getVehicle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .destination(request.getDestination() != null ? request.getDestination() : booking.getDestination())
                .actualPrice(request.getActualPrice())
                .odometerStart(request.getOdometerStart())
                .status(TripStatus.SCHEDULED)
                .notes(request.getNotes())
                .build();

        trip = tripRepository.save(trip);
        log.info("Trip created: id={}, booking={}, driver={}", trip.getId(),
                booking.getId(), driver.getFullName());
        return toTripResponse(trip);
    }

    @Transactional
    public TripResponse updateTripStatus(Long ownerId, Long tripId, TripStatus status) {
        Trip trip = findTrip(ownerId, tripId);
        trip.setStatus(status);
        if (status == TripStatus.COMPLETED && trip.getOdometerStart() != null && trip.getOdometerEnd() == null) {
            throw new BadRequestException("Odometer end reading is required to complete a trip");
        }
        trip = tripRepository.save(trip);
        log.info("Trip status updated: id={}, status={}", trip.getId(), status);
        return toTripResponse(trip);
    }

    @Transactional
    public TripResponse completeTrip(Long ownerId, Long tripId, TripCompleteRequest request) {
        Trip trip = findTrip(ownerId, tripId);
        trip.setEndDate(request.getEndDate());
        trip.setOdometerEnd(request.getOdometerEnd());
        trip.setActualPrice(request.getActualPrice());
        trip.setStatus(TripStatus.COMPLETED);
        trip = tripRepository.save(trip);
        log.info("Trip completed: id={}", trip.getId());
        return toTripResponse(trip);
    }

    // ==================== TRIP EXPENSES ====================

    public List<TripExpenseResponse> getTripExpenses(Long ownerId, Long tripId) {
        Trip trip = findTrip(ownerId, tripId);
        return tripExpenseRepository.findByTripIdAndDeletedFalse(trip.getId())
                .stream()
                .map(this::toTripExpenseResponse)
                .toList();
    }

    @Transactional
    public TripExpenseResponse addTripExpense(Long ownerId, Long tripId, TripExpenseRequest request) {
        Trip trip = findTrip(ownerId, tripId);

        TripExpense expense = TripExpense.builder()
                .trip(trip)
                .expenseType(request.getExpenseType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .build();

        expense = tripExpenseRepository.save(expense);
        log.info("Trip expense added: trip={}, type={}, amount={}", trip.getId(),
                expense.getExpenseType(), expense.getAmount());
        return toTripExpenseResponse(expense);
    }

    @Transactional
    public void deleteTripExpense(Long ownerId, Long tripId, Long expenseId) {
        Trip trip = findTrip(ownerId, tripId);
        TripExpense expense = tripExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("TripExpense", "id", expenseId));

        if (!expense.getTrip().getId().equals(trip.getId())) {
            throw new ResourceNotFoundException("TripExpense", "id", expenseId);
        }

        expense.softDelete();
        tripExpenseRepository.save(expense);
        log.info("Trip expense deleted: id={}", expenseId);
    }

    // ==================== PAYMENTS ====================

    public List<PaymentResponse> getPayments(Long ownerId, Long bookingId) {
        HireBooking booking = findBooking(ownerId, bookingId);
        return paymentRepository.findByBookingIdAndDeletedFalse(booking.getId())
                .stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional
    public PaymentResponse addPayment(Long ownerId, Long bookingId, PaymentRequest request) {
        HireBooking booking = findBooking(ownerId, bookingId);

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentDate(request.getPaymentDate())
                .referenceNumber(request.getReferenceNumber())
                .notes(request.getNotes())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment added: booking={}, amount={}, method={}", booking.getId(),
                payment.getAmount(), payment.getPaymentMethod());
        return toPaymentResponse(payment);
    }

    @Transactional
    public void deletePayment(Long ownerId, Long bookingId, Long paymentId) {
        HireBooking booking = findBooking(ownerId, bookingId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (!payment.getBooking().getId().equals(booking.getId())) {
            throw new ResourceNotFoundException("Payment", "id", paymentId);
        }

        payment.softDelete();
        paymentRepository.save(payment);
        log.info("Payment deleted: id={}", paymentId);
    }

    // ==================== FINANCIAL SUMMARY ====================

    public BookingFinancialSummary getBookingFinancials(Long ownerId, Long bookingId) {
        HireBooking booking = findBooking(ownerId, bookingId);

        BigDecimal totalPaid = bookingRepository.sumPaymentsByBookingId(bookingId);
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        BigDecimal outstanding = booking.getAgreedPrice().subtract(totalPaid);

        return BookingFinancialSummary.builder()
                .bookingId(booking.getId())
                .agreedPrice(booking.getAgreedPrice())
                .totalPaid(totalPaid)
                .outstandingBalance(outstanding)
                .paymentStatus(totalPaid.compareTo(booking.getAgreedPrice()) >= 0 ? "PAID" :
                        totalPaid.compareTo(BigDecimal.ZERO) > 0 ? "PARTIALLY_PAID" : "UNPAID")
                .currency("TZS")
                .build();
    }

    // ==================== HELPERS ====================

    private HireBooking findBooking(Long ownerId, Long bookingId) {
        HireBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("HireBooking", "id", bookingId));
        if (!booking.getOwner().getId().equals(ownerId) || booking.isDeleted()) {
            throw new ResourceNotFoundException("HireBooking", "id", bookingId);
        }
        return booking;
    }

    private Trip findTrip(Long ownerId, Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));
        if (!trip.getBooking().getOwner().getId().equals(ownerId) || trip.isDeleted()) {
            throw new ResourceNotFoundException("Trip", "id", tripId);
        }
        return trip;
    }

    private HireBookingResponse toBookingResponse(HireBooking booking) {
        BigDecimal totalPaid = bookingRepository.sumPaymentsByBookingId(booking.getId());
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        return HireBookingResponse.builder()
                .id(booking.getId())
                .vehicleId(booking.getVehicle().getId())
                .vehicleRegNumber(booking.getVehicle().getRegNumber())
                .customerId(booking.getCustomerIdValue())
                .customerName(booking.getCustomerNameValue())
                .hireDate(booking.getHireDate())
                .endDate(booking.getEndDate())
                .destination(booking.getDestination())
                .tripPurpose(booking.getTripPurpose())
                .agreedPrice(booking.getAgreedPrice())
                .depositPaid(booking.getDepositPaid())
                .totalPaid(totalPaid)
                .outstandingBalance(booking.getAgreedPrice().subtract(totalPaid))
                .status(booking.getStatus())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    private TripResponse toTripResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .bookingId(trip.getBooking().getId())
                .driverId(trip.getDriver().getId())
                .driverName(trip.getDriver().getFullName())
                .vehicleId(trip.getVehicle().getId())
                .vehicleRegNumber(trip.getVehicle().getRegNumber())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .destination(trip.getDestination())
                .actualPrice(trip.getActualPrice())
                .odometerStart(trip.getOdometerStart())
                .odometerEnd(trip.getOdometerEnd())
                .status(trip.getStatus())
                .notes(trip.getNotes())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }

    private TripExpenseResponse toTripExpenseResponse(TripExpense expense) {
        return TripExpenseResponse.builder()
                .id(expense.getId())
                .tripId(expense.getTrip().getId())
                .expenseType(expense.getExpenseType())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .createdAt(expense.getCreatedAt())
                .build();
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .referenceNumber(payment.getReferenceNumber())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
