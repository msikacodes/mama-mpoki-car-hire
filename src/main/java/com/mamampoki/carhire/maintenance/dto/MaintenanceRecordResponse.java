package com.mamampoki.carhire.maintenance.dto;

import com.mamampoki.carhire.common.enums.MaintenanceType;
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
public class MaintenanceRecordResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleRegNumber;
    private LocalDate maintenanceDate;
    private MaintenanceType maintenanceType;
    private String description;
    private BigDecimal cost;
    private String garageName;
    private Integer odometer;
    private LocalDate nextServiceDate;
    private String notes;
    private LocalDateTime createdAt;
}
