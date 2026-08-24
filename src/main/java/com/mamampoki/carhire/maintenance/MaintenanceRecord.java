package com.mamampoki.carhire.maintenance;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.MaintenanceType;
import com.mamampoki.carhire.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MaintenanceRecord extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "maintenance_date", nullable = false)
    private LocalDate maintenanceDate;

    @Column(name = "maintenance_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MaintenanceType maintenanceType;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "garage_name", length = 100)
    private String garageName;

    @Column(name = "odometer")
    private Integer odometer;

    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;

    @Column(name = "notes")
    private String notes;
}
