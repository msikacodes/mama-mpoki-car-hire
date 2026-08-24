package com.mamampoki.carhire.vehicle;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.FuelType;
import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.common.enums.VehicleStatus;
import com.mamampoki.carhire.common.enums.VehicleType;
import com.mamampoki.carhire.owner.Owner;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "vehicle_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(name = "module_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;

    @Column(name = "make", length = 50)
    private String make;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "model_year")
    private Integer year;

    @Column(name = "reg_number", unique = true, nullable = false, length = 20)
    private String regNumber;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "fuel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType = FuelType.DIESEL;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleStatus status = VehicleStatus.ACTIVE;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "notes")
    private String notes;
}
