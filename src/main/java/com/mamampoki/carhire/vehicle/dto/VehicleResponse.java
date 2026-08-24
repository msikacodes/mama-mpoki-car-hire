package com.mamampoki.carhire.vehicle.dto;

import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.VehicleStatus;
import com.mamampoki.carhire.common.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private Long id;
    private VehicleType vehicleType;
    private ModuleType moduleType;
    private String make;
    private String model;
    private Integer year;
    private String regNumber;
    private String color;
    private Integer capacity;
    private FuelType fuelType;
    private VehicleStatus status;
    private String photoUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
