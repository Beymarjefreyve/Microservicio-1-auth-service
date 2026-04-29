package com.microshop.auth_service.dto;

import java.time.LocalDateTime;

public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String name, String email, String role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
    }

    public static class UserProfileResponseBuilder {
        private Long id;
        private String name;
        private String email;
        private String role;
        private LocalDateTime createdAt;

        public UserProfileResponseBuilder id(Long id) { this.id = id; return this; }
        public UserProfileResponseBuilder name(String name) { this.name = name; return this; }
        public UserProfileResponseBuilder email(String email) { this.email = email; return this; }
        public UserProfileResponseBuilder role(String role) { this.role = role; return this; }
        public UserProfileResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserProfileResponse build() {
            return new UserProfileResponse(id, name, email, role, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
