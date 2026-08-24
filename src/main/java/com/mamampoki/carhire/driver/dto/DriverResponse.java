package com.mamampoki.carhire.driver.dto;

import com.mamampoki.carhire.common.enums.StaffStatus;
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
public class DriverResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String nationalId;
    private String address;
    private BigDecimal dailyRate;
    private StaffStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
