package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.enums.DailyExpenseType;
import com.mamampoki.carhire.common.enums.DailyRevenueSource;
import com.mamampoki.carhire.common.enums.RouteStatus;
import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.conductor.Conductor;
import com.mamampoki.carhire.conductor.ConductorRepository;
import com.mamampoki.carhire.driver.Driver;
import com.mamampoki.carhire.driver.DriverRepository;
import com.mamampoki.carhire.daladala.dto.*;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
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
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DaladalaService Tests")
class DaladalaServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private DailyOperationRepository operationRepository;

    @Mock
    private DailyRevenueRepository revenueRepository;

    @Mock
    private DailyExpenseRepository expenseRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private DaladalaService daladalaService;

    private Owner testOwner;
    private Route testRoute;
    private Vehicle testVehicle;
    private Driver testDriver;
    private Conductor testConductor;
    private DailyOperation testOperation;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1L);
        testOwner.setUsername("mamampoki");

        testRoute = new Route();
        testRoute.setId(1L);
        testRoute.setOwner(testOwner);
        testRoute.setName("Dodoma Town - Ihumwa");
        testRoute.setStartPoint("Dodoma Town Centre");
        testRoute.setEndPoint("Ihumwa");
        testRoute.setDistanceKm(new BigDecimal("25.5"));
        testRoute.setFareAmount(new BigDecimal("1500"));
        testRoute.setStatus(RouteStatus.ACTIVE);

        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setOwner(testOwner);
        testVehicle.setRegNumber("T 888 DEF");

        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setOwner(testOwner);
        testDriver.setFullName("Peter Kimaro");

        testConductor = new Conductor();
        testConductor.setId(1L);
        testConductor.setOwner(testOwner);
        testConductor.setFullName("Amina Rashid");

        testOperation = new DailyOperation();
        testOperation.setId(1L);
        testOperation.setVehicle(testVehicle);
        testOperation.setRoute(testRoute);
        testOperation.setDriver(testDriver);
        testOperation.setConductor(testConductor);
        testOperation.setOperationDate(LocalDate.of(2026, 8, 24));
        testOperation.setDepartureTime(LocalTime.of(6, 30));
        testOperation.setTotalPassengers(45);
        testOperation.setStatus(TripStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Create Route - Success")
    void createRoute_Success() {
        // Arrange
        RouteRequest request = new RouteRequest();
        request.setName("Dodoma - Kondoa");
        request.setStartPoint("Dodoma");
        request.setEndPoint("Kondoa");
        request.setDistanceKm(new BigDecimal("85"));
        request.setFareAmount(new BigDecimal("5000"));

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // Act
        RouteResponse response = daladalaService.createRoute(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("Dodoma Town - Ihumwa", response.getName());

        verify(routeRepository).save(any(Route.class));
    }

    @Test
    @DisplayName("Get Route By ID - Success")
    void getRouteById_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));

        // Act
        RouteResponse response = daladalaService.getRouteById(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals("Dodoma Town - Ihumwa", response.getName());
        assertEquals(new BigDecimal("1500"), response.getFareAmount());
    }

    @Test
    @DisplayName("Get Route By ID - Not Found")
    void getRouteById_NotFound() {
        // Arrange
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> daladalaService.getRouteById(1L, 999L));
    }

    @Test
    @DisplayName("Create Daily Operation - Success")
    void createOperation_Success() {
        // Arrange
        DailyOperationRequest request = new DailyOperationRequest();
        request.setVehicleId(1L);
        request.setRouteId(1L);
        request.setDriverId(1L);
        request.setConductorId(1L);
        request.setOperationDate(LocalDate.of(2026, 8, 24));
        request.setDepartureTime(LocalTime.of(6, 30));
        request.setTotalPassengers(45);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(testConductor));
        when(operationRepository.save(any(DailyOperation.class))).thenReturn(testOperation);
        when(operationRepository.sumRevenuesByOperationId(1L)).thenReturn(BigDecimal.ZERO);
        when(operationRepository.sumExpensesByOperationId(1L)).thenReturn(BigDecimal.ZERO);

        // Act
        DailyOperationResponse response = daladalaService.createOperation(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(TripStatus.SCHEDULED, response.getStatus());
        assertEquals("Dodoma Town - Ihumwa", response.getRouteName());
        assertEquals("Peter Kimaro", response.getDriverName());
        assertEquals("Amina Rashid", response.getConductorName());

        verify(operationRepository).save(any(DailyOperation.class));
    }

    @Test
    @DisplayName("Complete Operation - Success")
    void completeOperation_Success() {
        // Arrange
        when(operationRepository.findById(1L)).thenReturn(Optional.of(testOperation));
        when(operationRepository.save(any(DailyOperation.class))).thenReturn(testOperation);
        when(operationRepository.sumRevenuesByOperationId(1L)).thenReturn(new BigDecimal("67500"));
        when(operationRepository.sumExpensesByOperationId(1L)).thenReturn(new BigDecimal("55000"));

        // Act
        DailyOperationResponse response = daladalaService.completeOperation(
                1L, 1L, 45, LocalTime.of(17, 0));

        // Assert
        assertNotNull(response);
        assertEquals(TripStatus.COMPLETED, testOperation.getStatus());
        assertEquals(45, testOperation.getTotalPassengers());
        assertEquals(LocalTime.of(17, 0), testOperation.getReturnTime());
    }

    @Test
    @DisplayName("Add Revenue - Success")
    void addRevenue_Success() {
        // Arrange
        DailyRevenueRequest request = new DailyRevenueRequest();
        request.setSource(DailyRevenueSource.FARE);
        request.setAmount(new BigDecimal("67500"));
        request.setRevenueDate(LocalDate.of(2026, 8, 24));

        DailyRevenue revenue = new DailyRevenue();
        revenue.setId(1L);
        revenue.setOperation(testOperation);
        revenue.setSource(DailyRevenueSource.FARE);
        revenue.setAmount(new BigDecimal("67500"));

        when(operationRepository.findById(1L)).thenReturn(Optional.of(testOperation));
        when(revenueRepository.save(any(DailyRevenue.class))).thenReturn(revenue);

        // Act
        DailyRevenueResponse response = daladalaService.addRevenue(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("67500"), response.getAmount());
        assertEquals(DailyRevenueSource.FARE, response.getSource());

        verify(revenueRepository).save(any(DailyRevenue.class));
    }

    @Test
    @DisplayName("Add Expense - Success")
    void addExpense_Success() {
        // Arrange
        DailyExpenseRequest request = new DailyExpenseRequest();
        request.setExpenseType(DailyExpenseType.FUEL);
        request.setAmount(new BigDecimal("35000"));
        request.setExpenseDate(LocalDate.of(2026, 8, 24));

        DailyExpense expense = new DailyExpense();
        expense.setId(1L);
        expense.setOperation(testOperation);
        expense.setExpenseType(DailyExpenseType.FUEL);
        expense.setAmount(new BigDecimal("35000"));

        when(operationRepository.findById(1L)).thenReturn(Optional.of(testOperation));
        when(expenseRepository.save(any(DailyExpense.class))).thenReturn(expense);

        // Act
        DailyExpenseResponse response = daladalaService.addExpense(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("35000"), response.getAmount());
        assertEquals(DailyExpenseType.FUEL, response.getExpenseType());

        verify(expenseRepository).save(any(DailyExpense.class));
    }

    @Test
    @DisplayName("Delete Route - Success")
    void deleteRoute_Success() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // Act
        daladalaService.deleteRoute(1L, 1L);

        // Assert
        assertTrue(testRoute.isDeleted());
        verify(routeRepository).save(any(Route.class));
    }
}
