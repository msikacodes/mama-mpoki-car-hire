package com.mamampoki.carhire.vehicle;

import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Page<Vehicle> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    List<Vehicle> findByOwnerIdAndDeletedFalse(Long ownerId);

    Page<Vehicle> findByOwnerIdAndModuleTypeAndDeletedFalse(Long ownerId, ModuleType moduleType, Pageable pageable);

    Page<Vehicle> findByOwnerIdAndStatusAndDeletedFalse(Long ownerId, VehicleStatus status, Pageable pageable);

    Page<Vehicle> findByOwnerIdAndModuleTypeAndStatusAndDeletedFalse(
            Long ownerId, ModuleType moduleType, VehicleStatus status, Pageable pageable);

    boolean existsByRegNumberAndDeletedFalse(String regNumber);

    boolean existsByRegNumberAndDeletedFalseAndIdNot(String regNumber, Long id);

    long countByOwnerIdAndDeletedFalse(Long ownerId);

    long countByOwnerIdAndModuleTypeAndDeletedFalse(Long ownerId, ModuleType moduleType);

    long countByOwnerIdAndStatusAndDeletedFalse(Long ownerId, VehicleStatus status);
}
