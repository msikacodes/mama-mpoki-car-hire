package com.mamampoki.carhire.specialhire.dto;

import com.mamampoki.carhire.common.enums.TripExpenseType;
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
public class TripExpenseResponse {

    private Long id;
    private Long tripId;
    private TripExpenseType expenseType;
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
}
