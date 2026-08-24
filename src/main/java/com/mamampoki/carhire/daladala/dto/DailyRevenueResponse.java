package com.mamampoki.carhire.daladala.dto;

import com.mamampoki.carhire.common.enums.DailyRevenueSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueResponse {

    private Long id;
    private Long operationId;
    private DailyRevenueSource source;
    private BigDecimal amount;
    private String description;
    private LocalDate revenueDate;
    private LocalDateTime createdAt;
}
