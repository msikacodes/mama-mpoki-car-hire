package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.RouteStatus;
import com.mamampoki.carhire.owner.Owner;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "route")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Route extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "start_point", nullable = false, length = 100)
    private String startPoint;

    @Column(name = "end_point", nullable = false, length = 100)
    private String endPoint;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "fare_amount", nullable = false, precision = 8, scale = 2)
    private BigDecimal fareAmount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RouteStatus status = RouteStatus.ACTIVE;

    @Column(name = "notes")
    private String notes;
}
