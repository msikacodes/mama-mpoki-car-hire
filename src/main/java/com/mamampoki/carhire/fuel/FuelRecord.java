package com.mamampoki.carhire.fuel;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fuel_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FuelRecord extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "fuel_date", nullable = false)
    private LocalDate fuelDate;

    @Column(name = "liters", nullable = false, precision = 8, scale = 2)
    private BigDecimal liters;

    @Column(name = "cost_per_liter", nullable = false, precision = 8, scale = 2)
    private BigDecimal costPerLiter;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "odometer")
    private Integer odometer;

    @Column(name = "station", length = 100)
    private String station;

    @Column(name = "notes")
    private String notes;
}
