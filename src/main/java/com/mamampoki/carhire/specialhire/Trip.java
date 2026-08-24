package com.mamampoki.carhire.specialhire;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.driver.Driver;
import com.mamampoki.carhire.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Trip extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private HireBooking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "destination", length = 200)
    private String destination;

    @Column(name = "actual_price", precision = 12, scale = 2)
    private BigDecimal actualPrice;

    @Column(name = "odometer_start")
    private Integer odometerStart;

    @Column(name = "odometer_end")
    private Integer odometerEnd;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TripStatus status = TripStatus.SCHEDULED;

    @Column(name = "notes")
    private String notes;
}
