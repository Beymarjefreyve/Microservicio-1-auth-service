package com.microshop.auth_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigration implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigration.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        // Remove the old 'role' column from 'users' table (replaced by 'user_roles' table)
        try {
            jdbcTemplate.execute("ALTER TABLE users DROP COLUMN IF EXISTS role");
            logger.info("Migration: Dropped old 'role' column from 'users' table successfully.");
        } catch (Exception e) {
            logger.warn("Migration: Could not drop 'role' column (may already be removed): {}", e.getMessage());
        }
    }
}
