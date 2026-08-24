package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DailyOperationRepository extends JpaRepository<DailyOperation, Long> {

    Page<DailyOperation> findByVehicleOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    Page<DailyOperation> findByVehicleOwnerIdAndStatusAndDeletedFalse(Long ownerId, TripStatus status, Pageable pageable);

    Page<DailyOperation> findByVehicleOwnerIdAndOperationDateBetweenAndDeletedFalse(
            Long ownerId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<DailyOperation> findByRouteOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    long countByVehicleOwnerIdAndDeletedFalse(Long ownerId);

    long countByVehicleOwnerIdAndOperationDateAndDeletedFalse(Long ownerId, LocalDate date);

    @Query("SELECT COALESCE(SUM(dr.amount), 0) FROM DailyRevenue dr WHERE dr.operation.id = :operationId AND dr.deleted = false")
    java.math.BigDecimal sumRevenuesByOperationId(@Param("operationId") Long operationId);

    @Query("SELECT COALESCE(SUM(de.amount), 0) FROM DailyExpense de WHERE de.operation.id = :operationId AND de.deleted = false")
    java.math.BigDecimal sumExpensesByOperationId(@Param("operationId") Long operationId);
}
