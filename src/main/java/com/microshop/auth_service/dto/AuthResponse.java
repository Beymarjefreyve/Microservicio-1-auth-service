package com.microshop.auth_service.dto;

public class AuthResponse {
    private String token;
    private String role;
    private String name;
    private String email;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String role, String name, String email, String message) {
        this.token = token;
        this.role = role;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String role;
        private String name;
        private String email;
        private String message;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder role(String role) { this.role = role; return this; }
        public AuthResponseBuilder name(String name) { this.name = name; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder message(String message) { this.message = message; return this; }
        public AuthResponse build() {
            return new AuthResponse(token, role, name, email, message);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
