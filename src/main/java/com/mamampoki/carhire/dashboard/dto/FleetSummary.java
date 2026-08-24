package com.mamampoki.carhire.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetSummary {

    private long totalVehicles;
    private long activeVehicles;
    private long inMaintenance;
    private long inactive;
    private long specialHire;
    private long daladala;
    private long privateCars;
}
