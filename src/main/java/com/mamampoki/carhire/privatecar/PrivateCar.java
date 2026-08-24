package com.mamampoki.carhire.privatecar;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "private_car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PrivateCar extends SoftDeletableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", unique = true, nullable = false)
    private Vehicle vehicle;

    @Column(name = "insurance_number", length = 50)
    private String insuranceNumber;

    @Column(name = "insurance_provider", length = 100)
    private String insuranceProvider;

    @Column(name = "insurance_expiry")
    private LocalDate insuranceExpiry;

    @Column(name = "registration_expiry")
    private LocalDate registrationExpiry;

    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;

    @Column(name = "annual_mileage")
    private Integer annualMileage;

    @Column(name = "notes")
    private String notes;
}
