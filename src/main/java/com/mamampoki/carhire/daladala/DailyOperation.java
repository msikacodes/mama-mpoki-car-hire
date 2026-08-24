package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.conductor.Conductor;
import com.mamampoki.carhire.driver.Driver;
import com.mamampoki.carhire.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "daily_operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class DailyOperation extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_id")
    private Conductor conductor;

    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "return_time")
    private LocalTime returnTime;

    @Column(name = "total_passengers")
    @Builder.Default
    private Integer totalPassengers = 0;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TripStatus status = TripStatus.SCHEDULED;

    @Column(name = "notes")
    private String notes;
}
