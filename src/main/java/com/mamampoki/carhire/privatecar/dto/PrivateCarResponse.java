package com.mamampoki.carhire.privatecar.dto;

import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.VehicleType;
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
public class PrivateCarResponse {

    private Long id;
    private Long vehicleId;

    // Vehicle fields
    private VehicleType vehicleType;
    private String make;
    private String model;
    private Integer year;
    private String regNumber;
    private String color;
    private Integer capacity;
    private FuelType fuelType;
    private String photoUrl;

    // Private car specific fields
    private String insuranceNumber;
    private String insuranceProvider;
    private LocalDate insuranceExpiry;
    private LocalDate registrationExpiry;
    private LocalDate inspectionDate;
    private LocalDate lastServiceDate;
    private Integer annualMileage;
    private String notes;

    // Financial summary
    private BigDecimal totalFuelCost;
    private BigDecimal totalMaintenanceCost;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
