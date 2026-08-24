package com.mamampoki.carhire.daladala;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyRevenueRepository extends JpaRepository<DailyRevenue, Long> {

    List<DailyRevenue> findByOperationIdAndDeletedFalse(Long operationId);
}
