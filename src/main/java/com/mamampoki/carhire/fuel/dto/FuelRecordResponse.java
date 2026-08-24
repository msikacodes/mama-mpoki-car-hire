package com.mamampoki.carhire.fuel.dto;

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
public class FuelRecordResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleRegNumber;
    private LocalDate fuelDate;
    private BigDecimal liters;
    private BigDecimal costPerLiter;
    private BigDecimal totalCost;
    private Integer odometer;
    private String station;
    private String notes;
    private LocalDateTime createdAt;
}
