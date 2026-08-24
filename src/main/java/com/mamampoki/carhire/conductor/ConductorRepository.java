package com.mamampoki.carhire.conductor;

import com.mamampoki.carhire.common.enums.StaffStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Long> {

    Page<Conductor> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    List<Conductor> findByOwnerIdAndDeletedFalse(Long ownerId);

    Page<Conductor> findByOwnerIdAndStatusAndDeletedFalse(Long ownerId, StaffStatus status, Pageable pageable);

    long countByOwnerIdAndDeletedFalse(Long ownerId);
}
