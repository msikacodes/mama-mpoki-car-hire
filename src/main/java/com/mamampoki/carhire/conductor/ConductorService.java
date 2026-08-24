package com.mamampoki.carhire.conductor;

import com.mamampoki.carhire.common.enums.StaffStatus;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.conductor.dto.ConductorRequest;
import com.mamampoki.carhire.conductor.dto.ConductorResponse;
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
public class ConductorService {

    private final ConductorRepository conductorRepository;
    private final OwnerRepository ownerRepository;

    public Page<ConductorResponse> getConductors(Long ownerId, StaffStatus status, Pageable pageable) {
        Page<Conductor> conductors;

        if (status != null) {
            conductors = conductorRepository.findByOwnerIdAndStatusAndDeletedFalse(ownerId, status, pageable);
        } else {
            conductors = conductorRepository.findByOwnerIdAndDeletedFalse(ownerId, pageable);
        }

        return conductors.map(this::toResponse);
    }

    public ConductorResponse getConductorById(Long ownerId, Long conductorId) {
        Conductor conductor = findConductor(ownerId, conductorId);
        return toResponse(conductor);
    }

    @Transactional
    public ConductorResponse createConductor(Long ownerId, ConductorRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        Conductor conductor = Conductor.builder()
                .owner(owner)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .nationalId(request.getNationalId())
                .address(request.getAddress())
                .dailyRate(request.getDailyRate())
                .status(StaffStatus.ACTIVE)
                .notes(request.getNotes())
                .build();

        conductor = conductorRepository.save(conductor);
        log.info("Conductor created: {} ({})", conductor.getFullName(), conductor.getPhone());
        return toResponse(conductor);
    }

    @Transactional
    public ConductorResponse updateConductor(Long ownerId, Long conductorId, ConductorRequest request) {
        Conductor conductor = findConductor(ownerId, conductorId);

        conductor.setFullName(request.getFullName());
        conductor.setPhone(request.getPhone());
        conductor.setNationalId(request.getNationalId());
        conductor.setAddress(request.getAddress());
        conductor.setDailyRate(request.getDailyRate());
        conductor.setNotes(request.getNotes());

        conductor = conductorRepository.save(conductor);
        log.info("Conductor updated: {}", conductor.getFullName());
        return toResponse(conductor);
    }

    @Transactional
    public void deleteConductor(Long ownerId, Long conductorId) {
        Conductor conductor = findConductor(ownerId, conductorId);
        conductor.softDelete();
        conductorRepository.save(conductor);
        log.info("Conductor soft-deleted: {}", conductor.getFullName());
    }

    @Transactional
    public ConductorResponse updateStatus(Long ownerId, Long conductorId, StaffStatus status) {
        Conductor conductor = findConductor(ownerId, conductorId);
        conductor.setStatus(status);
        conductor = conductorRepository.save(conductor);
        log.info("Conductor status updated: {} -> {}", conductor.getFullName(), status);
        return toResponse(conductor);
    }

    private Conductor findConductor(Long ownerId, Long conductorId) {
        Conductor conductor = conductorRepository.findById(conductorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor", "id", conductorId));

        if (!conductor.getOwner().getId().equals(ownerId) || conductor.isDeleted()) {
            throw new ResourceNotFoundException("Conductor", "id", conductorId);
        }

        return conductor;
    }

    private ConductorResponse toResponse(Conductor conductor) {
        return ConductorResponse.builder()
                .id(conductor.getId())
                .fullName(conductor.getFullName())
                .phone(conductor.getPhone())
                .nationalId(conductor.getNationalId())
                .address(conductor.getAddress())
                .dailyRate(conductor.getDailyRate())
                .status(conductor.getStatus())
                .notes(conductor.getNotes())
                .createdAt(conductor.getCreatedAt())
                .updatedAt(conductor.getUpdatedAt())
                .build();
    }
}
