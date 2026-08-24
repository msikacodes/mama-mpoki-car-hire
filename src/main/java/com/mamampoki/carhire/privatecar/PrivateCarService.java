package com.mamampoki.carhire.privatecar;

import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.VehicleStatus;
import com.mamampoki.carhire.common.enums.VehicleType;
import com.mamampoki.carhire.exception.BadRequestException;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.fuel.FuelRecord;
import com.mamampoki.carhire.fuel.FuelRecordRepository;
import com.mamampoki.carhire.fuel.dto.FuelRecordRequest;
import com.mamampoki.carhire.fuel.dto.FuelRecordResponse;
import com.mamampoki.carhire.maintenance.MaintenanceRecord;
import com.mamampoki.carhire.maintenance.MaintenanceRecordRepository;
import com.mamampoki.carhire.maintenance.dto.MaintenanceRecordRequest;
import com.mamampoki.carhire.maintenance.dto.MaintenanceRecordResponse;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.privatecar.dto.PrivateCarRequest;
import com.mamampoki.carhire.privatecar.dto.PrivateCarResponse;
import com.mamampoki.carhire.vehicle.Vehicle;
import com.mamampoki.carhire.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCarService {

    private final PrivateCarRepository privateCarRepository;
    private final FuelRecordRepository fuelRecordRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;

    public Page<PrivateCarResponse> getPrivateCars(Long ownerId, Pageable pageable) {
        List<PrivateCar> privateCars = privateCarRepository.findByVehicleOwnerIdAndDeletedFalse(ownerId);
        return pageable.isUnpaged() ?
                new org.springframework.data.domain.PageImpl<>(privateCars.stream().map(this::toResponse).toList()) :
                new org.springframework.data.domain.PageImpl<>(
                        privateCars.stream().map(this::toResponse).toList(),
                        pageable,
                        privateCars.size());
    }

    public PrivateCarResponse getPrivateCarById(Long ownerId, Long privateCarId) {
        PrivateCar privateCar = findPrivateCar(ownerId, privateCarId);
        return toResponse(privateCar);
    }

    @Transactional
    public PrivateCarResponse createPrivateCar(Long ownerId, PrivateCarRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        if (vehicleRepository.existsByRegNumberAndDeletedFalse(request.getRegNumber())) {
            throw new BadRequestException("Vehicle with registration number '" +
                    request.getRegNumber() + "' already exists");
        }

        Vehicle vehicle = Vehicle.builder()
                .owner(owner)
                .vehicleType(request.getVehicleType())
                .moduleType(ModuleType.PRIVATE)
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .regNumber(request.getRegNumber())
                .color(request.getColor())
                .capacity(request.getCapacity())
                .fuelType(request.getFuelType() != null ? request.getFuelType() : FuelType.DIESEL)
                .status(VehicleStatus.ACTIVE)
                .photoUrl(request.getPhotoUrl())
                .build();

        vehicle = vehicleRepository.save(vehicle);

        PrivateCar privateCar = PrivateCar.builder()
                .vehicle(vehicle)
                .insuranceNumber(request.getInsuranceNumber())
                .insuranceProvider(request.getInsuranceProvider())
                .insuranceExpiry(request.getInsuranceExpiry())
                .registrationExpiry(request.getRegistrationExpiry())
                .inspectionDate(request.getInspectionDate())
                .lastServiceDate(request.getLastServiceDate())
                .annualMileage(request.getAnnualMileage())
                .notes(request.getNotes())
                .build();

        privateCar = privateCarRepository.save(privateCar);
        log.info("Private car created: {}", vehicle.getRegNumber());
        return toResponse(privateCar);
    }

    @Transactional
    public PrivateCarResponse updatePrivateCar(Long ownerId, Long privateCarId, PrivateCarRequest request) {
        PrivateCar privateCar = findPrivateCar(ownerId, privateCarId);
        Vehicle vehicle = privateCar.getVehicle();

        if (!vehicle.getRegNumber().equals(request.getRegNumber()) &&
            vehicleRepository.existsByRegNumberAndDeletedFalseAndIdNot(request.getRegNumber(), vehicle.getId())) {
            throw new BadRequestException("Vehicle with registration number '" +
                    request.getRegNumber() + "' already exists");
        }

        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setRegNumber(request.getRegNumber());
        vehicle.setColor(request.getColor());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setFuelType(request.getFuelType() != null ? request.getFuelType() : FuelType.DIESEL);
        vehicle.setPhotoUrl(request.getPhotoUrl());
        vehicleRepository.save(vehicle);

        privateCar.setInsuranceNumber(request.getInsuranceNumber());
        privateCar.setInsuranceProvider(request.getInsuranceProvider());
        privateCar.setInsuranceExpiry(request.getInsuranceExpiry());
        privateCar.setRegistrationExpiry(request.getRegistrationExpiry());
        privateCar.setInspectionDate(request.getInspectionDate());
        privateCar.setLastServiceDate(request.getLastServiceDate());
        privateCar.setAnnualMileage(request.getAnnualMileage());
        privateCar.setNotes(request.getNotes());

        privateCar = privateCarRepository.save(privateCar);
        log.info("Private car updated: {}", vehicle.getRegNumber());
        return toResponse(privateCar);
    }

    @Transactional
    public void deletePrivateCar(Long ownerId, Long privateCarId) {
        PrivateCar privateCar = findPrivateCar(ownerId, privateCarId);
        privateCar.softDelete();
        privateCar.getVehicle().softDelete();
        privateCarRepository.save(privateCar);
        vehicleRepository.save(privateCar.getVehicle());
        log.info("Private car soft-deleted: {}", privateCar.getVehicle().getRegNumber());
    }

    // ==================== FUEL RECORDS ====================

    public Page<FuelRecordResponse> getFuelRecords(Long ownerId, Long vehicleId, Pageable pageable) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);
        return fuelRecordRepository.findByVehicleIdAndDeletedFalse(vehicle.getId(), pageable)
                .map(this::toFuelResponse);
    }

    @Transactional
    public FuelRecordResponse addFuelRecord(Long ownerId, Long vehicleId, FuelRecordRequest request) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);

        // Validate total cost = liters × cost per liter
        BigDecimal expectedTotal = request.getLiters().multiply(request.getCostPerLiter());
        if (expectedTotal.compareTo(request.getTotalCost()) != 0) {
            throw new BadRequestException("Total cost must equal liters × cost per liter. " +
                    "Expected: " + expectedTotal + " TZS");
        }

        FuelRecord fuelRecord = FuelRecord.builder()
                .vehicle(vehicle)
                .fuelDate(request.getFuelDate())
                .liters(request.getLiters())
                .costPerLiter(request.getCostPerLiter())
                .totalCost(request.getTotalCost())
                .odometer(request.getOdometer())
                .station(request.getStation())
                .notes(request.getNotes())
                .build();

        fuelRecord = fuelRecordRepository.save(fuelRecord);
        log.info("Fuel record added: vehicle={}, liters={}, cost={}", vehicle.getRegNumber(),
                fuelRecord.getLiters(), fuelRecord.getTotalCost());
        return toFuelResponse(fuelRecord);
    }

    // ==================== MAINTENANCE RECORDS ====================

    public Page<MaintenanceRecordResponse> getMaintenanceRecords(Long ownerId, Long vehicleId, Pageable pageable) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);
        return maintenanceRecordRepository.findByVehicleIdAndDeletedFalse(vehicle.getId(), pageable)
                .map(this::toMaintenanceResponse);
    }

    @Transactional
    public MaintenanceRecordResponse addMaintenanceRecord(Long ownerId, Long vehicleId, MaintenanceRecordRequest request) {
        Vehicle vehicle = findVehicle(ownerId, vehicleId);

        MaintenanceRecord record = MaintenanceRecord.builder()
                .vehicle(vehicle)
                .maintenanceDate(request.getMaintenanceDate())
                .maintenanceType(request.getMaintenanceType())
                .description(request.getDescription())
                .cost(request.getCost())
                .garageName(request.getGarageName())
                .odometer(request.getOdometer())
                .nextServiceDate(request.getNextServiceDate())
                .notes(request.getNotes())
                .build();

        record = maintenanceRecordRepository.save(record);
        log.info("Maintenance record added: vehicle={}, type={}", vehicle.getRegNumber(),
                record.getMaintenanceType());
        return toMaintenanceResponse(record);
    }

    // ==================== DOCUMENT EXPIRY ALERTS ====================

    public List<PrivateCarResponse> getExpiringDocuments(Long ownerId, int daysAhead) {
        LocalDate cutoffDate = LocalDate.now().plusDays(daysAhead);
        List<PrivateCar> expiringInsurance = privateCarRepository.findWithExpiringInsurance(ownerId, cutoffDate);
        List<PrivateCar> expiringRegistration = privateCarRepository.findWithExpiringRegistration(ownerId, cutoffDate);

        // Combine and deduplicate
        java.util.Map<Long, PrivateCar> resultMap = new java.util.HashMap<>();
        expiringInsurance.forEach(pc -> resultMap.put(pc.getId(), pc));
        expiringRegistration.forEach(pc -> resultMap.put(pc.getId(), pc));

        return resultMap.values().stream().map(this::toResponse).toList();
    }

    // ==================== HELPERS ====================

    private PrivateCar findPrivateCar(Long ownerId, Long privateCarId) {
        PrivateCar privateCar = privateCarRepository.findById(privateCarId)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateCar", "id", privateCarId));
        if (!privateCar.getVehicle().getOwner().getId().equals(ownerId) || privateCar.isDeleted()) {
            throw new ResourceNotFoundException("PrivateCar", "id", privateCarId);
        }
        return privateCar;
    }

    private Vehicle findVehicle(Long ownerId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));
        if (!vehicle.getOwner().getId().equals(ownerId) || vehicle.isDeleted()) {
            throw new ResourceNotFoundException("Vehicle", "id", vehicleId);
        }
        return vehicle;
    }

    private PrivateCarResponse toResponse(PrivateCar pc) {
        Vehicle v = pc.getVehicle();
        BigDecimal totalFuelCost = fuelRecordRepository.sumTotalCostByVehicleId(v.getId());
        BigDecimal totalMaintenanceCost = maintenanceRecordRepository.sumCostByVehicleId(v.getId());

        return PrivateCarResponse.builder()
                .id(pc.getId())
                .vehicleId(v.getId())
                .vehicleType(v.getVehicleType())
                .make(v.getMake())
                .model(v.getModel())
                .year(v.getYear())
                .regNumber(v.getRegNumber())
                .color(v.getColor())
                .capacity(v.getCapacity())
                .fuelType(v.getFuelType())
                .photoUrl(v.getPhotoUrl())
                .insuranceNumber(pc.getInsuranceNumber())
                .insuranceProvider(pc.getInsuranceProvider())
                .insuranceExpiry(pc.getInsuranceExpiry())
                .registrationExpiry(pc.getRegistrationExpiry())
                .inspectionDate(pc.getInspectionDate())
                .lastServiceDate(pc.getLastServiceDate())
                .annualMileage(pc.getAnnualMileage())
                .notes(pc.getNotes())
                .totalFuelCost(totalFuelCost != null ? totalFuelCost : BigDecimal.ZERO)
                .totalMaintenanceCost(totalMaintenanceCost != null ? totalMaintenanceCost : BigDecimal.ZERO)
                .createdAt(pc.getCreatedAt())
                .updatedAt(pc.getUpdatedAt())
                .build();
    }

    private FuelRecordResponse toFuelResponse(FuelRecord record) {
        return FuelRecordResponse.builder()
                .id(record.getId())
                .vehicleId(record.getVehicle().getId())
                .vehicleRegNumber(record.getVehicle().getRegNumber())
                .fuelDate(record.getFuelDate())
                .liters(record.getLiters())
                .costPerLiter(record.getCostPerLiter())
                .totalCost(record.getTotalCost())
                .odometer(record.getOdometer())
                .station(record.getStation())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }

    private MaintenanceRecordResponse toMaintenanceResponse(MaintenanceRecord record) {
        return MaintenanceRecordResponse.builder()
                .id(record.getId())
                .vehicleId(record.getVehicle().getId())
                .vehicleRegNumber(record.getVehicle().getRegNumber())
                .maintenanceDate(record.getMaintenanceDate())
                .maintenanceType(record.getMaintenanceType())
                .description(record.getDescription())
                .cost(record.getCost())
                .garageName(record.getGarageName())
                .odometer(record.getOdometer())
                .nextServiceDate(record.getNextServiceDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
