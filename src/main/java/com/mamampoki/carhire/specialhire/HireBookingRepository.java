package com.mamampoki.carhire.specialhire;

import com.mamampoki.carhire.common.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HireBookingRepository extends JpaRepository<HireBooking, Long> {

    Page<HireBooking> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    Page<HireBooking> findByOwnerIdAndStatusAndDeletedFalse(Long ownerId, BookingStatus status, Pageable pageable);

    List<HireBooking> findByVehicleIdAndDeletedFalseAndStatusNotInAndHireDateLessThanEqualAndEndDateGreaterThanEqual(
            Long vehicleId, List<BookingStatus> excludedStatuses, LocalDate startDate, LocalDate endDate);

    long countByOwnerIdAndDeletedFalse(Long ownerId);

    long countByOwnerIdAndStatusAndDeletedFalse(Long ownerId, BookingStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.booking.id = :bookingId AND p.deleted = false")
    java.math.BigDecimal sumPaymentsByBookingId(@Param("bookingId") Long bookingId);
}
