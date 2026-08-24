package com.mamampoki.carhire.auth;

import com.mamampoki.carhire.exception.BadRequestException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerService;
import com.mamampoki.carhire.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private OwnerService ownerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Owner testOwner;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1L);
        testOwner.setUsername("mamampoki");
        testOwner.setPassword("$2a$12$encodedpassword");
        testOwner.setFullName("Mama Mpoki");
        testOwner.setPhone("+255712345678");
        testOwner.setEmail("info@mamampoki.co.tz");

        authRequest = new AuthRequest("mamampoki", "MamaMpoki2026!");
    }

    @Test
    @DisplayName("Login - Success")
    void login_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(ownerService.getOwnerByUsername("mamampoki")).thenReturn(testOwner);
        when(tokenProvider.generateAccessToken(1L, "mamampoki")).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(1L)).thenReturn("refresh-token");

        // Act
        AuthResponse response = authService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("mamampoki", response.getOwner().getUsername());
        assertEquals("Mama Mpoki", response.getOwner().getFullName());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(ownerService).getOwnerByUsername("mamampoki");
        verify(tokenProvider).generateAccessToken(1L, "mamampoki");
        verify(tokenProvider).generateRefreshToken(1L);
    }

    @Test
    @DisplayName("Login - Invalid Credentials")
    void login_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(ownerService, never()).getOwnerByUsername(anyString());
    }

    @Test
    @DisplayName("Refresh Token - Success")
    void refreshToken_Success() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        when(tokenProvider.validateToken("refresh-token")).thenReturn(true);
        when(tokenProvider.isRefreshToken("refresh-token")).thenReturn(true);
        when(tokenProvider.getOwnerIdFromToken("refresh-token")).thenReturn(1L);
        when(ownerService.getOwnerById(1L)).thenReturn(testOwner);
        when(tokenProvider.generateAccessToken(1L, "mamampoki")).thenReturn("new-access-token");

        // Act
        AuthResponse response = authService.refreshToken(request);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertNull(response.getRefreshToken());
    }

    @Test
    @DisplayName("Refresh Token - Invalid Token")
    void refreshToken_InvalidToken() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(tokenProvider.validateToken("invalid-token")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(request));
    }

    @Test
    @DisplayName("Refresh Token - Not a Refresh Token")
    void refreshToken_NotRefreshToken() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("access-token");
        when(tokenProvider.validateToken("access-token")).thenReturn(true);
        when(tokenProvider.isRefreshToken("access-token")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(request));
    }

    @Test
    @DisplayName("Change Password - Success")
    void changePassword_Success() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword123!");
        when(ownerService.getOwnerById(1L)).thenReturn(testOwner);
        when(passwordEncoder.matches("oldPassword", "$2a$12$encodedpassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123!")).thenReturn("$2a$12$newencoded");

        // Act
        authService.changePassword(1L, request);

        // Assert
        verify(ownerService).getOwnerById(1L);
        verify(passwordEncoder).matches("oldPassword", "$2a$12$encodedpassword");
        verify(passwordEncoder).encode("newPassword123!");
    }

    @Test
    @DisplayName("Change Password - Wrong Current Password")
    void changePassword_WrongPassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword123!");
        when(ownerService.getOwnerById(1L)).thenReturn(testOwner);
        when(passwordEncoder.matches("wrongPassword", testOwner.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.changePassword(1L, request));

        verify(passwordEncoder, never()).encode(anyString());
    }
}
