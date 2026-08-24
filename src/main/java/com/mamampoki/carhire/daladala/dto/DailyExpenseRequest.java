package com.mamampoki.carhire.daladala.dto;

import com.mamampoki.carhire.common.enums.DailyExpenseType;
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
public class DailyExpenseRequest {

    @NotNull(message = "Expense type is required")
    private DailyExpenseType expenseType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;
}
