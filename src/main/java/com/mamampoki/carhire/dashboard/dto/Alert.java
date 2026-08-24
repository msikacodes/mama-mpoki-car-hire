package com.mamampoki.carhire.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    private String type;
    private String severity;
    private String message;
    private Long vehicleId;
    private String vehicleRegNumber;
}
