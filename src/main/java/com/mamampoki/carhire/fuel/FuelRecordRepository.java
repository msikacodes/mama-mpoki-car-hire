package com.mamampoki.carhire.fuel;

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
public interface FuelRecordRepository extends JpaRepository<FuelRecord, Long> {

    Page<FuelRecord> findByVehicleIdAndDeletedFalse(Long vehicleId, Pageable pageable);

    List<FuelRecord> findByVehicleIdAndDeletedFalse(Long vehicleId);

    List<FuelRecord> findByVehicleOwnerIdAndDeletedFalse(Long ownerId);

    Page<FuelRecord> findByVehicleOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(f.totalCost), 0) FROM FuelRecord f WHERE f.vehicle.id = :vehicleId AND f.deleted = false")
    BigDecimal sumTotalCostByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT COALESCE(SUM(f.totalCost), 0) FROM FuelRecord f WHERE f.vehicle.owner.id = :ownerId AND f.deleted = false")
    BigDecimal sumTotalCostByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COALESCE(SUM(f.liters), 0) FROM FuelRecord f WHERE f.vehicle.id = :vehicleId AND f.deleted = false")
    BigDecimal sumLitersByVehicleId(@Param("vehicleId") Long vehicleId);
}
