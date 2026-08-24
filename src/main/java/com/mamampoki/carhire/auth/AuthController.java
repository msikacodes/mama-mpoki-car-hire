package com.mamampoki.carhire.auth;

import com.mamampoki.carhire.common.ApiResponse;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerService;
import com.mamampoki.carhire.security.OwnerDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Owner login and token management")
public class AuthController {

    private final AuthService authService;
    private final OwnerService ownerService;

    @Operation(summary = "Login", description = "Authenticate owner and get JWT tokens")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @Operation(summary = "Refresh Token", description = "Get new access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @Operation(summary = "Change Password", description = "Change owner password")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal OwnerDetails ownerDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(ownerDetails.getOwner().getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @Operation(summary = "Get Current Owner", description = "Get current authenticated owner info")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.OwnerInfo>> getCurrentOwner(
            @AuthenticationPrincipal OwnerDetails ownerDetails) {
        Owner owner = ownerDetails.getOwner();
        AuthResponse.OwnerInfo info = AuthResponse.OwnerInfo.builder()
                .id(owner.getId())
                .username(owner.getUsername())
                .fullName(owner.getFullName())
                .phone(owner.getPhone())
                .build();
        return ResponseEntity.ok(ApiResponse.success(info));
    }
}
