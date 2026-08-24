package com.mamampoki.carhire.specialhire.dto;

import com.mamampoki.carhire.common.enums.TripStatus;
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
public class TripResponse {

    private Long id;
    private Long bookingId;
    private Long driverId;
    private String driverName;
    private Long vehicleId;
    private String vehicleRegNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String destination;
    private BigDecimal actualPrice;
    private Integer odometerStart;
    private Integer odometerEnd;
    private TripStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
