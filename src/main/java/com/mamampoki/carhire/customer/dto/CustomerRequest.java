package com.mamampoki.carhire.customer.dto;

import com.mamampoki.carhire.common.enums.CustomerIdType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String email;

    private String address;

    private CustomerIdType idType;

    private String idNumber;

    private String notes;
}
