package com.microshop.auth_service.security;

import com.microshop.auth_service.entity.Role;
import com.microshop.auth_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject values that @Value would normally provide
        ReflectionTestUtils.setField(jwtUtil, "secretKey",
                "test-secret-key-at-least-32-characters-long");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L);

        testUser = User.builder()
                .id(1L)
                .name("Juan Test")
                .email("juan@test.com")
                .password("encodedPassword")
                .roles(Set.of(Role.BUYER))
                .enabled(true)
                .build();
    }

    @Test
    void generateToken_shouldReturnNonBlankToken() {
        String token = jwtUtil.generateToken(testUser);
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnUserEmail() {
        String token = jwtUtil.generateToken(testUser);
        String username = jwtUtil.extractUsername(token);
        assertThat(username).isEqualTo("juan@test.com");
    }

    @Test
    void isTokenValid_withCorrectUser_shouldReturnTrue() {
        String token = jwtUtil.generateToken(testUser);
        assertThat(jwtUtil.isTokenValid(token, testUser)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_shouldReturnFalse() {
        String token = jwtUtil.generateToken(testUser);

        User otherUser = User.builder()
                .id(2L)
                .email("otro@test.com")
                .password("pass")
                .roles(Set.of(Role.BUYER))
                .enabled(true)
                .build();

        assertThat(jwtUtil.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", -3600000L); // 1 hour in the past
        String token = jwtUtil.generateToken(testUser);
        assertThat(jwtUtil.isTokenValid(token, testUser)).isFalse();
    }
}
