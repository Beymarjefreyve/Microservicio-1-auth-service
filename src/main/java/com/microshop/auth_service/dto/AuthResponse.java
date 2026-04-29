package com.microshop.auth_service.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private List<String> roles;
    private String name;
    private String email;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, List<String> roles, String name, String email, String message) {
        this.token = token;
        this.roles = roles;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private List<String> roles;
        private String name;
        private String email;
        private String message;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder roles(List<String> roles) { this.roles = roles; return this; }
        public AuthResponseBuilder name(String name) { this.name = name; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder message(String message) { this.message = message; return this; }
        public AuthResponse build() {
            return new AuthResponse(token, roles, name, email, message);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
