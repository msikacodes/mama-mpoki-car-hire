package com.mamampoki.carhire.daladala.dto;

import com.mamampoki.carhire.common.enums.RouteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {

    private Long id;
    private String name;
    private String startPoint;
    private String endPoint;
    private BigDecimal distanceKm;
    private BigDecimal fareAmount;
    private RouteStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
