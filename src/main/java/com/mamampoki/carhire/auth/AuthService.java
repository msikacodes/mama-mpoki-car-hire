package com.mamampoki.carhire.auth;

import com.mamampoki.carhire.exception.BadRequestException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerService;
import com.mamampoki.carhire.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final OwnerService ownerService;
    private final PasswordEncoder passwordEncoder;

    private static final long ACCESS_TOKEN_EXPIRY = 86400000L; // 24 hours

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Owner owner = ownerService.getOwnerByUsername(request.getUsername());

        String accessToken = tokenProvider.generateAccessToken(owner.getId(), owner.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(owner.getId());

        log.info("Owner '{}' logged in successfully", owner.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(ACCESS_TOKEN_EXPIRY)
                .owner(AuthResponse.OwnerInfo.builder()
                        .id(owner.getId())
                        .username(owner.getUsername())
                        .fullName(owner.getFullName())
                        .phone(owner.getPhone())
                        .build())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        Long ownerId = tokenProvider.getOwnerIdFromToken(refreshToken);
        Owner owner = ownerService.getOwnerById(ownerId);

        String newAccessToken = tokenProvider.generateAccessToken(owner.getId(), owner.getUsername());

        log.info("Token refreshed for owner '{}'", owner.getUsername());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(ACCESS_TOKEN_EXPIRY)
                .build();
    }

    @Transactional
    public void changePassword(Long ownerId, ChangePasswordRequest request) {
        Owner owner = ownerService.getOwnerById(ownerId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), owner.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        owner.setPassword(passwordEncoder.encode(request.getNewPassword()));
        log.info("Password changed for owner '{}'", owner.getUsername());
    }
}
