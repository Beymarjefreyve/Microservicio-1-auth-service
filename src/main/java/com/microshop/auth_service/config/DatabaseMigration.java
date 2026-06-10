package com.microshop.auth_service.config;

import com.microshop.auth_service.entity.Role;
import com.microshop.auth_service.entity.User;
import com.microshop.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DatabaseMigration implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigration.class);
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Credenciales del administrador ──────────────────────────────────
    private static final String ADMIN_EMAIL    = "admin@microshop.com";
    private static final String ADMIN_PASSWORD = "Admin@Micro2025!";
    private static final String ADMIN_NAME     = "Administrador MicroShop";
    // ────────────────────────────────────────────────────────────────────

    public DatabaseMigration(JdbcTemplate jdbcTemplate,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.jdbcTemplate  = jdbcTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Migración: eliminar columna role antigua si existe
        try {
            jdbcTemplate.execute("ALTER TABLE users DROP COLUMN IF EXISTS role");
            logger.info("Migration: Dropped old 'role' column from 'users' table successfully.");
        } catch (Exception e) {
            logger.warn("Migration: Could not drop 'role' column (may already be removed): {}", e.getMessage());
        }

        // 2. Seed: crear administrador si no existe
        boolean adminExists = userRepository.findByEmail(ADMIN_EMAIL).isPresent();
        if (!adminExists) {
            User admin = User.builder()
                    .name(ADMIN_NAME)
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .roles(Set.of(Role.ADMIN))
                    .enabled(true)  // habilitado directamente, sin verificación de correo
                    .build();
            userRepository.save(admin);
            logger.info("Seed: Cuenta de administrador creada -> {}", ADMIN_EMAIL);
        } else {
            logger.info("Seed: La cuenta de administrador ya existe -> {}", ADMIN_EMAIL);
        }
    }
}
