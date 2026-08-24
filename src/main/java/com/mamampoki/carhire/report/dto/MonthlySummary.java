package com.mamampoki.carhire.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummary {

    private int year;
    private int month;
    private ModuleSummary specialHire;
    private ModuleSummary daladala;
    private ModuleSummary privateCars;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private String currency;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleSummary {
        private BigDecimal revenue;
        private BigDecimal expenses;
        private BigDecimal profit;
    }
}
