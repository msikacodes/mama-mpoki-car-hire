package com.mamampoki.carhire.specialhire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFinancialSummary {

    private Long bookingId;
    private BigDecimal agreedPrice;
    private BigDecimal totalPaid;
    private BigDecimal outstandingBalance;
    private String paymentStatus;
    private String currency;
}
