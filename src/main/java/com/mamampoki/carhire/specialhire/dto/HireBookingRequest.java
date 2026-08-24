package com.mamampoki.carhire.specialhire.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HireBookingRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    private Long customerId;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    private LocalDate endDate;

    private String destination;

    private String tripPurpose;

    @NotNull(message = "Agreed price is required")
    @Positive(message = "Agreed price must be positive")
    private BigDecimal agreedPrice;

    @Positive(message = "Deposit must be positive")
    private BigDecimal depositPaid;

    private String notes;
}
