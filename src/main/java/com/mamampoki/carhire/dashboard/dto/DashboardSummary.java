package com.mamampoki.carhire.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {

    private FleetSummary fleet;
    private SpecialHireSummary specialHire;
    private DaladalaSummary daladala;
    private java.util.List<Alert> alerts;
    private String currency;
}
