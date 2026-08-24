package com.mamampoki.carhire.driver;

import com.mamampoki.carhire.common.enums.StaffStatus;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.driver.dto.DriverRequest;
import com.mamampoki.carhire.driver.dto.DriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final OwnerRepository ownerRepository;

    public Page<DriverResponse> getDrivers(Long ownerId, StaffStatus status, Pageable pageable) {
        Page<Driver> drivers;

        if (status != null) {
            drivers = driverRepository.findByOwnerIdAndStatusAndDeletedFalse(ownerId, status, pageable);
        } else {
            drivers = driverRepository.findByOwnerIdAndDeletedFalse(ownerId, pageable);
        }

        return drivers.map(this::toResponse);
    }

    public DriverResponse getDriverById(Long ownerId, Long driverId) {
        Driver driver = findDriver(ownerId, driverId);
        return toResponse(driver);
    }

    @Transactional
    public DriverResponse createDriver(Long ownerId, DriverRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        Driver driver = Driver.builder()
                .owner(owner)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .licenseExpiry(request.getLicenseExpiry())
                .nationalId(request.getNationalId())
                .address(request.getAddress())
                .dailyRate(request.getDailyRate())
                .status(StaffStatus.ACTIVE)
                .notes(request.getNotes())
                .build();

        driver = driverRepository.save(driver);
        log.info("Driver created: {} ({})", driver.getFullName(), driver.getPhone());
        return toResponse(driver);
    }

    @Transactional
    public DriverResponse updateDriver(Long ownerId, Long driverId, DriverRequest request) {
        Driver driver = findDriver(ownerId, driverId);

        driver.setFullName(request.getFullName());
        driver.setPhone(request.getPhone());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setNationalId(request.getNationalId());
        driver.setAddress(request.getAddress());
        driver.setDailyRate(request.getDailyRate());
        driver.setNotes(request.getNotes());

        driver = driverRepository.save(driver);
        log.info("Driver updated: {}", driver.getFullName());
        return toResponse(driver);
    }

    @Transactional
    public void deleteDriver(Long ownerId, Long driverId) {
        Driver driver = findDriver(ownerId, driverId);
        driver.softDelete();
        driverRepository.save(driver);
        log.info("Driver soft-deleted: {}", driver.getFullName());
    }

    @Transactional
    public DriverResponse updateStatus(Long ownerId, Long driverId, StaffStatus status) {
        Driver driver = findDriver(ownerId, driverId);
        driver.setStatus(status);
        driver = driverRepository.save(driver);
        log.info("Driver status updated: {} -> {}", driver.getFullName(), status);
        return toResponse(driver);
    }

    private Driver findDriver(Long ownerId, Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        if (!driver.getOwner().getId().equals(ownerId) || driver.isDeleted()) {
            throw new ResourceNotFoundException("Driver", "id", driverId);
        }

        return driver;
    }

    private DriverResponse toResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .phone(driver.getPhone())
                .licenseNumber(driver.getLicenseNumber())
                .licenseExpiry(driver.getLicenseExpiry())
                .nationalId(driver.getNationalId())
                .address(driver.getAddress())
                .dailyRate(driver.getDailyRate())
                .status(driver.getStatus())
                .notes(driver.getNotes())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
