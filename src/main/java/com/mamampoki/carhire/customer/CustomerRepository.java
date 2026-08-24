package com.mamampoki.carhire.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    List<Customer> findByOwnerIdAndDeletedFalse(Long ownerId);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false AND c.owner.id = :ownerId " +
           "AND (LOWER(c.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR c.phone LIKE CONCAT('%', :query, '%'))")
    List<Customer> searchByNameOrPhone(@Param("ownerId") Long ownerId, @Param("query") String query);

    long countByOwnerIdAndDeletedFalse(Long ownerId);
}
