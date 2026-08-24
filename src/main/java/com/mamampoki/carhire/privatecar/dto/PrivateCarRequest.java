package com.mamampoki.carhire.privatecar.dto;

import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateCarRequest {

    // Vehicle fields
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private String make;

    private String model;

    private Integer year;

    @NotBlank(message = "Registration number is required")
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
}
