package com.mamampoki.carhire.vehicle;

import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.VehicleStatus;
import com.mamampoki.carhire.common.enums.VehicleType;
import com.mamampoki.carhire.exception.BadRequestException;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.vehicle.dto.VehicleRequest;
import com.mamampoki.carhire.vehicle.dto.VehicleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Owner testOwner;
    private Vehicle testVehicle;
    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1L);
        testOwner.setUsername("mamampoki");
        testOwner.setFullName("Mama Mpoki");

        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setOwner(testOwner);
        testVehicle.setVehicleType(VehicleType.COASTER);
        testVehicle.setModuleType(ModuleType.SPECIAL_HIRE);
        testVehicle.setMake("Toyota");
        testVehicle.setModel("HiAce");
        testVehicle.setYear(2022);
        testVehicle.setRegNumber("T 123 ABC");
        testVehicle.setColor("White");
        testVehicle.setCapacity(30);
        testVehicle.setFuelType(FuelType.DIESEL);
        testVehicle.setStatus(VehicleStatus.ACTIVE);

        vehicleRequest = new VehicleRequest();
        vehicleRequest.setVehicleType(VehicleType.COASTER);
        vehicleRequest.setModuleType(ModuleType.SPECIAL_HIRE);
        vehicleRequest.setMake("Toyota");
        vehicleRequest.setModel("HiAce");
        vehicleRequest.setYear(2022);
        vehicleRequest.setRegNumber("T 456 DEF");
        vehicleRequest.setColor("Silver");
        vehicleRequest.setCapacity(16);
        vehicleRequest.setFuelType(FuelType.DIESEL);
    }

    @Test
    @DisplayName("Get Vehicles - Success")
    void getVehicles_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(testVehicle), pageable, 1);
        when(vehicleRepository.findByOwnerIdAndDeletedFalse(1L, pageable)).thenReturn(vehiclePage);

        // Act
        Page<VehicleResponse> result = vehicleService.getVehicles(1L, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("T 123 ABC", result.getContent().get(0).getRegNumber());

        verify(vehicleRepository).findByOwnerIdAndDeletedFalse(1L, pageable);
    }

    @Test
    @DisplayName("Get Vehicle By ID - Success")
    void getVehicleById_Success() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        VehicleResponse response = vehicleService.getVehicleById(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("T 123 ABC", response.getRegNumber());
        assertEquals(VehicleStatus.ACTIVE, response.getStatus());

        verify(vehicleRepository).findById(1L);
    }

    @Test
    @DisplayName("Get Vehicle By ID - Not Found")
    void getVehicleById_NotFound() {
        // Arrange
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.getVehicleById(1L, 999L));
    }

    @Test
    @DisplayName("Get Vehicle By ID - Wrong Owner")
    void getVehicleById_WrongOwner() {
        // Arrange
        Owner otherOwner = new Owner();
        otherOwner.setId(2L);
        Vehicle otherVehicle = new Vehicle();
        otherVehicle.setId(2L);
        otherVehicle.setOwner(otherOwner);
        when(vehicleRepository.findById(2L)).thenReturn(Optional.of(otherVehicle));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.getVehicleById(1L, 2L));
    }

    @Test
    @DisplayName("Create Vehicle - Success")
    void createVehicle_Success() {
        // Arrange
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(vehicleRepository.existsByRegNumberAndDeletedFalse("T 456 DEF")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.createVehicle(1L, vehicleRequest);

        // Assert
        assertNotNull(response);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Create Vehicle - Duplicate Registration")
    void createVehicle_DuplicateRegistration() {
        // Arrange
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(vehicleRepository.existsByRegNumberAndDeletedFalse("T 456 DEF")).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> vehicleService.createVehicle(1L, vehicleRequest));

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Vehicle - Success")
    void updateVehicle_Success() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.existsByRegNumberAndDeletedFalseAndIdNot("T 456 DEF", 1L)).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.updateVehicle(1L, 1L, vehicleRequest);

        // Assert
        assertNotNull(response);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Delete Vehicle - Success")
    void deleteVehicle_Success() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        vehicleService.deleteVehicle(1L, 1L);

        // Assert
        verify(vehicleRepository).save(any(Vehicle.class));
        assertTrue(testVehicle.isDeleted());
    }

    @Test
    @DisplayName("Update Vehicle Status - Success")
    void updateStatus_Success() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.updateStatus(1L, 1L, VehicleStatus.MAINTENANCE);

        // Assert
        assertNotNull(response);
        assertEquals(VehicleStatus.MAINTENANCE, testVehicle.getStatus());
    }
}
