package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.DailyRevenueSource;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_revenue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class DailyRevenue extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private DailyOperation operation;

    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    private DailyRevenueSource source;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "revenue_date", nullable = false)
    private LocalDate revenueDate;
}
