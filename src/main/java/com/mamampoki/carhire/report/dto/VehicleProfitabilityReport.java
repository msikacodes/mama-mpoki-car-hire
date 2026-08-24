package com.mamampoki.carhire.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleProfitabilityReport {

    private List<VehicleProfitability> vehicles;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal totalProfit;
    private String currency;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleProfitability {
        private Long vehicleId;
        private String regNumber;
        private String make;
        private String model;
        private String moduleType;
        private BigDecimal revenue;
        private BigDecimal fuelCost;
        private BigDecimal maintenanceCost;
        private BigDecimal totalExpenses;
        private BigDecimal profit;
        private BigDecimal profitMargin;
    }
}
