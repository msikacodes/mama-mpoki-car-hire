package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.enums.RouteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Page<Route> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    List<Route> findByOwnerIdAndDeletedFalse(Long ownerId);

    List<Route> findByOwnerIdAndStatusAndDeletedFalse(Long ownerId, RouteStatus status);

    long countByOwnerIdAndDeletedFalse(Long ownerId);

    long countByOwnerIdAndStatusAndDeletedFalse(Long ownerId, RouteStatus status);
}
