package com.microshop.auth_service.repository;

import com.microshop.auth_service.entity.User;
import com.microshop.auth_service.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
    void deleteByToken(String token);
}
