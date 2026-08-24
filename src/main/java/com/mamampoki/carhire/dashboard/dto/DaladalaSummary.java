package com.mamampoki.carhire.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaladalaSummary {

    private long totalRoutes;
    private long activeRoutes;
    private long todayOperations;
    private long monthlyOperations;
    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyExpenses;
    private BigDecimal monthlyProfit;
}
