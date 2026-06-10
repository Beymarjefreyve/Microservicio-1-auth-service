package com.microshop.auth_service.service;

import com.microshop.auth_service.dto.AuthRequest;
import com.microshop.auth_service.dto.AuthResponse;
import com.microshop.auth_service.dto.RegisterRequest;
import com.microshop.auth_service.entity.Role;
import com.microshop.auth_service.entity.User;
import com.microshop.auth_service.repository.UserRepository;
import com.microshop.auth_service.repository.VerificationTokenRepository;
import com.microshop.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PasswordResetService passwordResetService;
    @Mock private EmailService emailService;
    @Mock private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(1L)
                .name("Juan Test")
                .email("juan@test.com")
                .password("encodedPassword")
                .roles(Set.of(Role.BUYER))
                .enabled(true)
                .build();
    }

    // ── register ────────────────────────────────────────────────────────────

    @Test
    void register_withNewEmail_shouldSaveUserAndReturnResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Test");
        request.setEmail("nuevo@test.com");
        request.setPassword("password123");
        request.setRoles(Set.of(Role.BUYER));
        request.setFrontendUrl("http://localhost:3000");

        when(userRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(verificationTokenRepository.save(any())).thenReturn(null);
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("verifica tu correo");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void register_withDuplicateEmail_shouldThrowIllegalArgumentException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("juan@test.com");
        request.setPassword("password123");
        request.setRoles(Set.of(Role.BUYER));

        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email ya está en uso");

        verify(userRepository, never()).save(any());
    }

    // ── login ────────────────────────────────────────────────────────────────

    @Test
    void login_withValidCredentials_shouldReturnTokenAndUserInfo() {
        AuthRequest request = new AuthRequest("juan@test.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(existingUser)).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getEmail()).isEqualTo("juan@test.com");
        assertThat(response.getRoles()).contains("BUYER");
    }

    @Test
    void login_withInvalidCredentials_shouldThrowException() {
        AuthRequest request = new AuthRequest("juan@test.com", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ── validateToken ────────────────────────────────────────────────────────

    @Test
    void validateToken_withValidToken_shouldReturnValidTrue() {
        when(jwtUtil.extractUsername("valid.token")).thenReturn("juan@test.com");
        when(userRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.isTokenValid("valid.token", existingUser)).thenReturn(true);

        var result = authService.validateToken("valid.token");

        assertThat(result.isValid()).isTrue();
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnValidFalse() {
        when(jwtUtil.extractUsername("bad.token")).thenThrow(new RuntimeException("Invalid JWT"));

        var result = authService.validateToken("bad.token");

        assertThat(result.isValid()).isFalse();
    }
}
