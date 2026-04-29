package com.microshop.auth_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public VerificationToken() {}

    public VerificationToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public static VerificationTokenBuilder builder() {
        return new VerificationTokenBuilder();
    }

    public static class VerificationTokenBuilder {
        private String token;
        private User user;
        private LocalDateTime expiryDate;

        public VerificationTokenBuilder token(String token) { this.token = token; return this; }
        public VerificationTokenBuilder user(User user) { this.user = user; return this; }
        public VerificationTokenBuilder expiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; return this; }
        public VerificationToken build() {
            return new VerificationToken(token, user, expiryDate);
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}
