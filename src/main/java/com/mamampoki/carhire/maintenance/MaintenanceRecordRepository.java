package com.mamampoki.carhire.maintenance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    Page<MaintenanceRecord> findByVehicleIdAndDeletedFalse(Long vehicleId, Pageable pageable);

    List<MaintenanceRecord> findByVehicleIdAndDeletedFalse(Long vehicleId);

    List<MaintenanceRecord> findByVehicleOwnerIdAndDeletedFalse(Long ownerId);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM MaintenanceRecord m WHERE m.vehicle.id = :vehicleId AND m.deleted = false")
    BigDecimal sumCostByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM MaintenanceRecord m WHERE m.vehicle.owner.id = :ownerId AND m.deleted = false")
    BigDecimal sumCostByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT m FROM MaintenanceRecord m WHERE m.deleted = false AND m.vehicle.owner.id = :ownerId " +
           "AND m.nextServiceDate IS NOT NULL AND m.nextServiceDate <= :date")
    List<MaintenanceRecord> findUpcomingMaintenance(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);
}
