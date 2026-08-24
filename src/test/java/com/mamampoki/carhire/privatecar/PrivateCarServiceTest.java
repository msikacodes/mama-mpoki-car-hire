package com.mamampoki.carhire.privatecar;

import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.MaintenanceType;
import com.mamampoki.carhire.common.enums.VehicleStatus;
import com.mamampoki.carhire.common.enums.VehicleType;
import com.mamampoki.carhire.exception.BadRequestException;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.fuel.FuelRecord;
import com.mamampoki.carhire.fuel.FuelRecordRepository;
import com.mamampoki.carhire.fuel.dto.FuelRecordRequest;
import com.mamampoki.carhire.maintenance.MaintenanceRecord;
import com.mamampoki.carhire.maintenance.MaintenanceRecordRepository;
import com.mamampoki.carhire.maintenance.dto.MaintenanceRecordRequest;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.privatecar.dto.PrivateCarRequest;
import com.mamampoki.carhire.privatecar.dto.PrivateCarResponse;
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
@DisplayName("PrivateCarService Tests")
class PrivateCarServiceTest {

    @Mock
    private PrivateCarRepository privateCarRepository;

    @Mock
    private FuelRecordRepository fuelRecordRepository;

    @Mock
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private PrivateCarService privateCarService;

    private Owner testOwner;
    private Vehicle testVehicle;
    private PrivateCar testPrivateCar;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1L);
        testOwner.setUsername("mamampoki");

        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setOwner(testOwner);
        testVehicle.setVehicleType(VehicleType.PRIVATE_CAR);
        testVehicle.setModuleType(ModuleType.PRIVATE);
        testVehicle.setMake("Toyota");
        testVehicle.setModel("Land Cruiser");
        testVehicle.setYear(2023);
        testVehicle.setRegNumber("T 789 STU");
        testVehicle.setColor("Black");
        testVehicle.setCapacity(7);
        testVehicle.setFuelType(FuelType.DIESEL);
        testVehicle.setStatus(VehicleStatus.ACTIVE);

        testPrivateCar = new PrivateCar();
        testPrivateCar.setId(1L);
        testPrivateCar.setVehicle(testVehicle);
        testPrivateCar.setInsuranceNumber("INS-2026-001");
        testPrivateCar.setInsuranceProvider("APA Insurance");
        testPrivateCar.setInsuranceExpiry(LocalDate.of(2027, 3, 31));
        testPrivateCar.setRegistrationExpiry(LocalDate.of(2027, 6, 30));
        testPrivateCar.setLastServiceDate(LocalDate.of(2026, 8, 1));
    }

    @Test
    @DisplayName("Create Private Car - Success")
    void createPrivateCar_Success() {
        // Arrange
        PrivateCarRequest request = new PrivateCarRequest();
        request.setVehicleType(VehicleType.PRIVATE_CAR);
        request.setMake("Toyota");
        request.setModel("Land Cruiser");
        request.setYear(2023);
        request.setRegNumber("T 789 STU");
        request.setColor("Black");
        request.setCapacity(7);
        request.setFuelType(FuelType.DIESEL);
        request.setInsuranceNumber("INS-2026-001");
        request.setInsuranceProvider("APA Insurance");
        request.setInsuranceExpiry(LocalDate.of(2027, 3, 31));

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(vehicleRepository.existsByRegNumberAndDeletedFalse("T 789 STU")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(privateCarRepository.save(any(PrivateCar.class))).thenReturn(testPrivateCar);
        when(fuelRecordRepository.sumTotalCostByVehicleId(1L)).thenReturn(BigDecimal.ZERO);
        when(maintenanceRecordRepository.sumCostByVehicleId(1L)).thenReturn(BigDecimal.ZERO);

        // Act
        PrivateCarResponse response = privateCarService.createPrivateCar(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("T 789 STU", response.getRegNumber());
        assertEquals("INS-2026-001", response.getInsuranceNumber());

        verify(vehicleRepository).save(any(Vehicle.class));
        verify(privateCarRepository).save(any(PrivateCar.class));
    }

    @Test
    @DisplayName("Create Private Car - Duplicate Registration")
    void createPrivateCar_DuplicateRegistration() {
        // Arrange
        PrivateCarRequest request = new PrivateCarRequest();
        request.setRegNumber("T 789 STU");

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(vehicleRepository.existsByRegNumberAndDeletedFalse("T 789 STU")).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> privateCarService.createPrivateCar(1L, request));

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get Private Car By ID - Success")
    void getPrivateCarById_Success() {
        // Arrange
        when(privateCarRepository.findById(1L)).thenReturn(Optional.of(testPrivateCar));
        when(fuelRecordRepository.sumTotalCostByVehicleId(1L)).thenReturn(new BigDecimal("538750"));
        when(maintenanceRecordRepository.sumCostByVehicleId(1L)).thenReturn(new BigDecimal("470000"));

        // Act
        PrivateCarResponse response = privateCarService.getPrivateCarById(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals("T 789 STU", response.getRegNumber());
        assertEquals(new BigDecimal("538750"), response.getTotalFuelCost());
        assertEquals(new BigDecimal("470000"), response.getTotalMaintenanceCost());
    }

    @Test
    @DisplayName("Add Fuel Record - Success")
    void addFuelRecord_Success() {
        // Arrange
        FuelRecordRequest request = new FuelRecordRequest();
        request.setVehicleId(1L);
        request.setFuelDate(LocalDate.of(2026, 8, 24));
        request.setLiters(new BigDecimal("80"));
        request.setCostPerLiter(new BigDecimal("3500"));
        request.setTotalCost(new BigDecimal("280000"));
        request.setOdometer(45000);
        request.setStation("Total Dodoma");

        FuelRecord fuelRecord = new FuelRecord();
        fuelRecord.setId(1L);
        fuelRecord.setVehicle(testVehicle);
        fuelRecord.setLiters(new BigDecimal("80"));
        fuelRecord.setCostPerLiter(new BigDecimal("3500"));
        fuelRecord.setTotalCost(new BigDecimal("280000"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(fuelRecordRepository.save(any(FuelRecord.class))).thenReturn(fuelRecord);

        // Act
        var response = privateCarService.addFuelRecord(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("280000"), response.getTotalCost());

        verify(fuelRecordRepository).save(any(FuelRecord.class));
    }

    @Test
    @DisplayName("Add Fuel Record - Validation Error")
    void addFuelRecord_ValidationError() {
        // Arrange
        FuelRecordRequest request = new FuelRecordRequest();
        request.setVehicleId(1L);
        request.setLiters(new BigDecimal("80"));
        request.setCostPerLiter(new BigDecimal("3500"));
        request.setTotalCost(new BigDecimal("999999")); // Wrong total

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> privateCarService.addFuelRecord(1L, 1L, request));

        verify(fuelRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Add Maintenance Record - Success")
    void addMaintenanceRecord_Success() {
        // Arrange
        MaintenanceRecordRequest request = new MaintenanceRecordRequest();
        request.setVehicleId(1L);
        request.setMaintenanceDate(LocalDate.of(2026, 8, 1));
        request.setMaintenanceType(MaintenanceType.SERVICE);
        request.setDescription("Regular service");
        request.setCost(new BigDecimal("150000"));
        request.setGarageName("Dodoma Auto Garage");
        request.setOdometer(43000);
        request.setNextServiceDate(LocalDate.of(2026, 12, 1));

        MaintenanceRecord record = new MaintenanceRecord();
        record.setId(1L);
        record.setVehicle(testVehicle);
        record.setMaintenanceType(MaintenanceType.SERVICE);
        record.setCost(new BigDecimal("150000"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(maintenanceRecordRepository.save(any(MaintenanceRecord.class))).thenReturn(record);

        // Act
        var response = privateCarService.addMaintenanceRecord(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(MaintenanceType.SERVICE, response.getMaintenanceType());
        assertEquals(new BigDecimal("150000"), response.getCost());

        verify(maintenanceRecordRepository).save(any(MaintenanceRecord.class));
    }

    @Test
    @DisplayName("Delete Private Car - Success")
    void deletePrivateCar_Success() {
        // Arrange
        when(privateCarRepository.findById(1L)).thenReturn(Optional.of(testPrivateCar));
        when(privateCarRepository.save(any(PrivateCar.class))).thenReturn(testPrivateCar);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        privateCarService.deletePrivateCar(1L, 1L);

        // Assert
        assertTrue(testPrivateCar.isDeleted());
        assertTrue(testVehicle.isDeleted());

        verify(privateCarRepository).save(any(PrivateCar.class));
        verify(vehicleRepository).save(any(Vehicle.class));
    }
}
