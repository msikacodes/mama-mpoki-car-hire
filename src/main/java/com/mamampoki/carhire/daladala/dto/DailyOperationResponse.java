package com.mamampoki.carhire.daladala.dto;

import com.mamampoki.carhire.common.enums.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyOperationResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleRegNumber;
    private Long routeId;
    private String routeName;
    private Long driverId;
    private String driverName;
    private Long conductorId;
    private String conductorName;
    private LocalDate operationDate;
    private LocalTime departureTime;
    private LocalTime returnTime;
    private Integer totalPassengers;
    private TripStatus status;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal profit;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
