package com.mamampoki.carhire.specialhire.dto;

import com.mamampoki.carhire.common.enums.BookingStatus;
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
public class HireBookingResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleRegNumber;
    private Long customerId;
    private String customerName;
    private LocalDate hireDate;
    private LocalDate endDate;
    private String destination;
    private String tripPurpose;
    private BigDecimal agreedPrice;
    private BigDecimal depositPaid;
    private BigDecimal totalPaid;
    private BigDecimal outstandingBalance;
    private BookingStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
