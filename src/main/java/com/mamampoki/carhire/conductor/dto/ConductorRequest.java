package com.mamampoki.carhire.conductor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConductorRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    private String nationalId;

    private String address;

    private BigDecimal dailyRate;

    private String notes;
}
