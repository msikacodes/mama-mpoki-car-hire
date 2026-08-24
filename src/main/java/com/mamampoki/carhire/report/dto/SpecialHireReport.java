package com.mamampoki.carhire.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialHireReport {

    private LocalDate fromDate;
    private LocalDate toDate;
    private long totalBookings;
    private long completedTrips;
    private long cancelledBookings;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal totalProfit;
    private BigDecimal profitMargin;
    private String topVehicleRegNumber;
    private long topVehicleTrips;
    private String topDestination;
    private BigDecimal averageBookingValue;
    private String currency;
}
