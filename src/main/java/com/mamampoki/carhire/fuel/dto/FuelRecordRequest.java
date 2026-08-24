package com.mamampoki.carhire.fuel.dto;

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
public class FuelRecordRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Fuel date is required")
    private LocalDate fuelDate;

    @NotNull(message = "Liters is required")
    @Positive(message = "Liters must be positive")
    private BigDecimal liters;

    @NotNull(message = "Cost per liter is required")
    @Positive(message = "Cost per liter must be positive")
    private BigDecimal costPerLiter;

    @NotNull(message = "Total cost is required")
    @Positive(message = "Total cost must be positive")
    private BigDecimal totalCost;

    private Integer odometer;

    private String station;

    private String notes;
}
