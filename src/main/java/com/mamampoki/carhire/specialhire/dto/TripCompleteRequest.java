package com.mamampoki.carhire.specialhire.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripCompleteRequest {

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Odometer end reading is required")
    private Integer odometerEnd;

    private BigDecimal actualPrice;
}
