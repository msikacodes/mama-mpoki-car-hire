package com.mamampoki.carhire.conductor;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.StaffStatus;
import com.mamampoki.carhire.owner.Owner;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "conductor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Conductor extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "national_id", length = 50)
    private String nationalId;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StaffStatus status = StaffStatus.ACTIVE;

    @Column(name = "notes")
    private String notes;
}
