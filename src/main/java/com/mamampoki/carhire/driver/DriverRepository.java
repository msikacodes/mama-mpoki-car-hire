package com.mamampoki.carhire.driver;

import com.mamampoki.carhire.common.enums.StaffStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Page<Driver> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    List<Driver> findByOwnerIdAndDeletedFalse(Long ownerId);

    Page<Driver> findByOwnerIdAndStatusAndDeletedFalse(Long ownerId, StaffStatus status, Pageable pageable);

    long countByOwnerIdAndDeletedFalse(Long ownerId);

    long countByOwnerIdAndStatusAndDeletedFalse(Long ownerId, StaffStatus status);
}
