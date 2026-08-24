package com.mamampoki.carhire.conductor.dto;

import com.mamampoki.carhire.common.enums.StaffStatus;
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
public class ConductorResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String nationalId;
    private String address;
    private BigDecimal dailyRate;
    private StaffStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
