package com.mamampoki.carhire.daladala.dto;

import com.mamampoki.carhire.common.enums.DailyRevenueSource;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueRequest {

    @NotNull(message = "Revenue source is required")
    private DailyRevenueSource source;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Revenue date is required")
    private LocalDate revenueDate;
}
