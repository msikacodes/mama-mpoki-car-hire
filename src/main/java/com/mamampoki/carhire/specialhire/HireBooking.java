package com.mamampoki.carhire.specialhire;

import com.mamampoki.carhire.common.SoftDeletableEntity;
import com.mamampoki.carhire.common.enums.BookingStatus;
import com.mamampoki.carhire.customer.Customer;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hire_booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HireBooking extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "destination", length = 200)
    private String destination;

    @Column(name = "trip_purpose", length = 200)
    private String tripPurpose;

    @Column(name = "agreed_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal agreedPrice;

    @Column(name = "deposit_paid", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal depositPaid = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "notes")
    private String notes;

    // Helper method to get customer ID
    public Long getCustomerIdValue() {
        return customer != null ? customer.getId() : null;
    }

    // Helper method to get customer name
    public String getCustomerNameValue() {
        return customer != null ? customer.getFullName() : null;
    }
}
