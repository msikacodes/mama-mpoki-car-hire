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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;

    public Page<VehicleResponse> getVehicles(Long ownerId, ModuleType moduleType,
                                              VehicleStatus status, Pageable pageable) {
        Page<Vehicle> vehicles;

        if (moduleType != null && status != null) {
            vehicles = vehicleRepository.findByOwnerIdAndModuleTypeAndStatusAndDeletedFalse(
                    ownerId, moduleType, status, pageable);
        } else if (moduleType != null) {
            vehicles = vehicleRepository.findByOwnerIdAndModuleTypeAndDeletedFalse(
                    ownerId, moduleType, pageable);
        } else if (status != null) {
            vehicles = vehicleRepository.findByOwnerIdAndStatusAndDeletedFalse(
                    ownerId, status, pageable);
        } else {
            vehicles = vehicleRepository.findByOwnerIdAndDeletedFalse(ownerId, pageable);
        }

        return vehicles.map(this::toResponse);
    }

    public VehicleResponse getVehicleById(Long ownerId, Long vehicleId) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);
        return toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse createVehicle(Long ownerId, VehicleRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        if (vehicleRepository.existsByRegNumberAndDeletedFalse(request.getRegNumber())) {
            throw new BadRequestException("Vehicle with registration number '" +
                    request.getRegNumber() + "' already exists");
        }

        Vehicle vehicle = Vehicle.builder()
                .owner(owner)
                .vehicleType(request.getVehicleType())
                .moduleType(request.getModuleType())
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .regNumber(request.getRegNumber())
                .color(request.getColor())
                .capacity(request.getCapacity())
                .fuelType(request.getFuelType() != null ? request.getFuelType() : FuelType.DIESEL)
                .status(VehicleStatus.ACTIVE)
                .photoUrl(request.getPhotoUrl())
                .notes(request.getNotes())
                .build();

        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created: {} ({})", vehicle.getRegNumber(), vehicle.getModuleType());
        return toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse updateVehicle(Long ownerId, Long vehicleId, VehicleRequest request) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);

        if (!vehicle.getRegNumber().equals(request.getRegNumber()) &&
            vehicleRepository.existsByRegNumberAndDeletedFalseAndIdNot(request.getRegNumber(), vehicleId)) {
            throw new BadRequestException("Vehicle with registration number '" +
                    request.getRegNumber() + "' already exists");
        }

        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setModuleType(request.getModuleType());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setRegNumber(request.getRegNumber());
        vehicle.setColor(request.getColor());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setFuelType(request.getFuelType() != null ? request.getFuelType() : FuelType.DIESEL);
        vehicle.setPhotoUrl(request.getPhotoUrl());
        vehicle.setNotes(request.getNotes());

        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated: {}", vehicle.getRegNumber());
        return toResponse(vehicle);
    }

    @Transactional
    public void deleteVehicle(Long ownerId, Long vehicleId) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);
        vehicle.softDelete();
        vehicleRepository.save(vehicle);
        log.info("Vehicle soft-deleted: {}", vehicle.getRegNumber());
    }

    @Transactional
    public VehicleResponse updateStatus(Long ownerId, Long vehicleId, VehicleStatus status) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);
        vehicle.setStatus(status);
        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle status updated: {} -> {}", vehicle.getRegNumber(), status);
        return toResponse(vehicle);
    }

    public List<VehicleResponse> getAvailableVehicles(Long ownerId, LocalDate startDate, LocalDate endDate) {
        // TODO: Implement when HireBooking entity is created
        return vehicleRepository.findByOwnerIdAndModuleTypeAndStatusAndDeletedFalse(
                ownerId, ModuleType.SPECIAL_HIRE, VehicleStatus.ACTIVE, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getFleetSummary(Long ownerId) {
        long total = vehicleRepository.countByOwnerIdAndDeletedFalse(ownerId);
        long specialHire = vehicleRepository.countByOwnerIdAndModuleTypeAndDeletedFalse(ownerId, ModuleType.SPECIAL_HIRE);
        long daladala = vehicleRepository.countByOwnerIdAndModuleTypeAndDeletedFalse(ownerId, ModuleType.DALADALA);
        long privateCars = vehicleRepository.countByOwnerIdAndModuleTypeAndDeletedFalse(ownerId, ModuleType.PRIVATE);
        long active = vehicleRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, VehicleStatus.ACTIVE);
        long maintenance = vehicleRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, VehicleStatus.MAINTENANCE);
        long inactive = vehicleRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, VehicleStatus.INACTIVE);

        return Map.of(
                "total", total,
                "specialHire", specialHire,
                "daladala", daladala,
                "privateCars", privateCars,
                "active", active,
                "maintenance", maintenance,
                "inactive", inactive
        );
    }

    private Vehicle findVehicle(Long ownerId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        if (!vehicle.getOwner().getId().equals(ownerId)) {
            throw new ResourceNotFoundException("Vehicle", "id", vehicleId);
        }

        if (vehicle.isDeleted()) {
            throw new ResourceNotFoundException("Vehicle", "id", vehicleId);
        }

        return vehicle;
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleType(vehicle.getVehicleType())
                .moduleType(vehicle.getModuleType())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .regNumber(vehicle.getRegNumber())
                .color(vehicle.getColor())
                .capacity(vehicle.getCapacity())
                .fuelType(vehicle.getFuelType())
                .status(vehicle.getStatus())
                .photoUrl(vehicle.getPhotoUrl())
                .notes(vehicle.getNotes())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
