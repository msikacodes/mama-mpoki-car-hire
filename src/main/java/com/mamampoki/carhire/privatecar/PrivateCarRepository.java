package com.mamampoki.carhire.privatecar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateCarRepository extends JpaRepository<PrivateCar, Long> {

    Optional<PrivateCar> findByVehicleIdAndDeletedFalse(Long vehicleId);

    List<PrivateCar> findByVehicleOwnerIdAndDeletedFalse(Long ownerId);

    @Query("SELECT pc FROM PrivateCar pc WHERE pc.deleted = false AND pc.vehicle.owner.id = :ownerId " +
           "AND pc.insuranceExpiry IS NOT NULL AND pc.insuranceExpiry <= :date")
    List<PrivateCar> findWithExpiringInsurance(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);

    @Query("SELECT pc FROM PrivateCar pc WHERE pc.deleted = false AND pc.vehicle.owner.id = :ownerId " +
           "AND pc.registrationExpiry IS NOT NULL AND pc.registrationExpiry <= :date")
    List<PrivateCar> findWithExpiringRegistration(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);
}
