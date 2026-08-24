package com.mamampoki.carhire.daladala.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyOperationRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Route ID is required")
    private Long routeId;

    private Long driverId;

    private Long conductorId;

    @NotNull(message = "Operation date is required")
    private LocalDate operationDate;

    private LocalTime departureTime;

    private LocalTime returnTime;

    private Integer totalPassengers;

    private String notes;
}
