package com.mamampoki.carhire.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReport {

    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalExpenses;
    private Map<String, BigDecimal> byCategory;
    private List<ExpenseDetail> topExpenses;
    private String currency;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseDetail {
        private String category;
        private BigDecimal amount;
        private long count;
    }
}
