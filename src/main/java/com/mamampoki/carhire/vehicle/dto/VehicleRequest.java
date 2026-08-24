package com.mamampoki.carhire.vehicle.dto;

import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Module type is required")
    private ModuleType moduleType;

    private String make;

    private String model;

    private Integer year;

    @NotBlank(message = "Registration number is required")
    private String regNumber;

    private String color;

    private Integer capacity;

    private FuelType fuelType;

    private String photoUrl;

    private String notes;
}
