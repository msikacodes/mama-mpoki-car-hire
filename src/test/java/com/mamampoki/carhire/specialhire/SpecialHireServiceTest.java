package com.mamampoki.carhire.specialhire;

import com.mamampoki.carhire.common.enums.BookingStatus;
import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.customer.Customer;
import com.mamampoki.carhire.customer.CustomerRepository;
import com.mamampoki.carhire.driver.Driver;
import com.mamampoki.carhire.driver.DriverRepository;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.specialhire.dto.*;
import com.mamampoki.carhire.vehicle.Vehicle;
import com.mamampoki.carhire.vehicle.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpecialHireService Tests")
class SpecialHireServiceTest {

    @Mock
    private HireBookingRepository bookingRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripExpenseRepository tripExpenseRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private SpecialHireService specialHireService;

    private Owner testOwner;
    private Vehicle testVehicle;
    private Customer testCustomer;
    private Driver testDriver;
    private HireBooking testBooking;
    private Trip testTrip;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1L);
        testOwner.setUsername("mamampoki");

        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setOwner(testOwner);
        testVehicle.setRegNumber("T 123 ABC");

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setOwner(testOwner);
        testCustomer.setFullName("Amina Hassan");
        testCustomer.setPhone("+255789012345");

        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setOwner(testOwner);
        testDriver.setFullName("John Mwakasege");

        testBooking = new HireBooking();
        testBooking.setId(1L);
        testBooking.setOwner(testOwner);
        testBooking.setVehicle(testVehicle);
        testBooking.setCustomer(testCustomer);
        testBooking.setHireDate(LocalDate.of(2026, 9, 1));
        testBooking.setEndDate(LocalDate.of(2026, 9, 3));
        testBooking.setDestination("Dar es Salaam");
        testBooking.setTripPurpose("Corporate event");
        testBooking.setAgreedPrice(new BigDecimal("850000"));
        testBooking.setDepositPaid(new BigDecimal("200000"));
        testBooking.setStatus(BookingStatus.PENDING);

        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setBooking(testBooking);
        testTrip.setDriver(testDriver);
        testTrip.setVehicle(testVehicle);
        testTrip.setStartDate(LocalDate.of(2026, 9, 1));
        testTrip.setOdometerStart(125000);
        testTrip.setStatus(TripStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Create Booking - Success")
    void createBooking_Success() {
        // Arrange
        HireBookingRequest request = new HireBookingRequest();
        request.setVehicleId(1L);
        request.setCustomerId(1L);
        request.setHireDate(LocalDate.of(2026, 9, 1));
        request.setEndDate(LocalDate.of(2026, 9, 3));
        request.setDestination("Dar es Salaam");
        request.setAgreedPrice(new BigDecimal("850000"));
        request.setDepositPaid(new BigDecimal("200000"));

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(bookingRepository.save(any(HireBooking.class))).thenReturn(testBooking);
        when(bookingRepository.sumPaymentsByBookingId(1L)).thenReturn(BigDecimal.ZERO);

        // Act
        HireBookingResponse response = specialHireService.createBooking(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(BookingStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("850000"), response.getAgreedPrice());

        verify(bookingRepository).save(any(HireBooking.class));
    }

    @Test
    @DisplayName("Create Booking - Vehicle Not Found")
    void createBooking_VehicleNotFound() {
        // Arrange
        HireBookingRequest request = new HireBookingRequest();
        request.setVehicleId(999L);
        request.setHireDate(LocalDate.of(2026, 9, 1));
        request.setAgreedPrice(new BigDecimal("850000"));

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> specialHireService.createBooking(1L, request));
    }

    @Test
    @DisplayName("Update Booking Status - Success")
    void updateBookingStatus_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(HireBooking.class))).thenReturn(testBooking);
        when(bookingRepository.sumPaymentsByBookingId(1L)).thenReturn(BigDecimal.ZERO);

        // Act
        HireBookingResponse response = specialHireService.updateBookingStatus(1L, 1L, BookingStatus.CONFIRMED);

        // Assert
        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED, testBooking.getStatus());
    }

    @Test
    @DisplayName("Create Trip - Success")
    void createTrip_Success() {
        // Arrange
        TripRequest request = new TripRequest();
        request.setBookingId(1L);
        request.setDriverId(1L);
        request.setStartDate(LocalDate.of(2026, 9, 1));
        request.setOdometerStart(125000);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        // Act
        TripResponse response = specialHireService.createTrip(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(TripStatus.SCHEDULED, response.getStatus());
        assertEquals("John Mwakasege", response.getDriverName());

        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    @DisplayName("Complete Trip - Success")
    void completeTrip_Success() {
        // Arrange
        TripCompleteRequest request = new TripCompleteRequest();
        request.setEndDate(LocalDate.of(2026, 9, 3));
        request.setOdometerEnd(126500);
        request.setActualPrice(new BigDecimal("850000"));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        // Act
        TripResponse response = specialHireService.completeTrip(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(TripStatus.COMPLETED, testTrip.getStatus());
        assertEquals(126500, testTrip.getOdometerEnd());
    }

    @Test
    @DisplayName("Add Payment - Success")
    void addPayment_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("650000"));
        request.setPaymentMethod(com.mamampoki.carhire.common.enums.PaymentMethod.MOBILE_MONEY);
        request.setPaymentDate(LocalDate.of(2026, 9, 1));
        request.setReferenceNumber("MPESA-12345");

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBooking(testBooking);
        payment.setAmount(new BigDecimal("650000"));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponse response = specialHireService.addPayment(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("650000"), response.getAmount());

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Get Booking Financials - Success")
    void getBookingFinancials_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.sumPaymentsByBookingId(1L)).thenReturn(new BigDecimal("650000"));

        // Act
        BookingFinancialSummary summary = specialHireService.getBookingFinancials(1L, 1L);

        // Assert
        assertNotNull(summary);
        assertEquals(new BigDecimal("850000"), summary.getAgreedPrice());
        assertEquals(new BigDecimal("650000"), summary.getTotalPaid());
        assertEquals(new BigDecimal("200000"), summary.getOutstandingBalance());
        assertEquals("PARTIALLY_PAID", summary.getPaymentStatus());
    }

    @Test
    @DisplayName("Add Trip Expense - Success")
    void addTripExpense_Success() {
        // Arrange
        TripExpenseRequest request = new TripExpenseRequest();
        request.setExpenseType(com.mamampoki.carhire.common.enums.TripExpenseType.FUEL);
        request.setAmount(new BigDecimal("180000"));
        request.setExpenseDate(LocalDate.of(2026, 9, 1));

        TripExpense expense = new TripExpense();
        expense.setId(1L);
        expense.setTrip(testTrip);
        expense.setExpenseType(com.mamampoki.carhire.common.enums.TripExpenseType.FUEL);
        expense.setAmount(new BigDecimal("180000"));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripExpenseRepository.save(any(TripExpense.class))).thenReturn(expense);

        // Act
        TripExpenseResponse response = specialHireService.addTripExpense(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("180000"), response.getAmount());

        verify(tripExpenseRepository).save(any(TripExpense.class));
    }
}
