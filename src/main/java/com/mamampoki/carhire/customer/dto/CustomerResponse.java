package com.mamampoki.carhire.customer.dto;

import com.mamampoki.carhire.common.enums.CustomerIdType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private CustomerIdType idType;
    private String idNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
