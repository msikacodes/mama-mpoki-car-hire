package com.mamampoki.carhire.dashboard;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.dashboard.dto.DashboardSummary;
import com.mamampoki.carhire.security.OwnerDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard summary and alerts")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get Dashboard Summary", description = "Get fleet overview, module stats, and system alerts")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummary>> getDashboardSummary(
            @AuthenticationPrincipal OwnerDetails ownerDetails) {
        DashboardSummary summary = dashboardService.getDashboardSummary(ownerDetails.getOwner().getId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
