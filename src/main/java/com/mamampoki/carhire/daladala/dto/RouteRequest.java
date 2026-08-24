package com.mamampoki.carhire.daladala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {

    @NotBlank(message = "Route name is required")
    private String name;

    @NotBlank(message = "Start point is required")
    private String startPoint;

    @NotBlank(message = "End point is required")
    private String endPoint;

    private BigDecimal distanceKm;

    @NotNull(message = "Fare amount is required")
    @Positive(message = "Fare must be positive")
    private BigDecimal fareAmount;

    private String notes;
}
