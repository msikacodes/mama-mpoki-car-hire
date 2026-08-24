package com.mamampoki.carhire.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaladalaReport {

    private LocalDate fromDate;
    private LocalDate toDate;
    private long totalOperations;
    private long completedOperations;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal totalProfit;
    private BigDecimal profitMargin;
    private BigDecimal averageDailyRevenue;
    private BigDecimal averageDailyExpenses;
    private long totalPassengers;
    private List<RoutePerformance> routePerformance;
    private List<VehiclePerformance> vehiclePerformance;
    private String currency;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutePerformance {
        private Long routeId;
        private String routeName;
        private long operations;
        private BigDecimal revenue;
        private BigDecimal expenses;
        private BigDecimal profit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclePerformance {
        private Long vehicleId;
        private String regNumber;
        private long operations;
        private BigDecimal revenue;
    }
}
