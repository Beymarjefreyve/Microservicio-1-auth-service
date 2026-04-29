package com.microshop.auth_service.dto;

public class TokenValidationResponse {
    private boolean valid;
    private String email;
    private String role;
    private String name;

    public TokenValidationResponse() {}

    public TokenValidationResponse(boolean valid, String email, String role, String name) {
        this.valid = valid;
        this.email = email;
        this.role = role;
        this.name = name;
    }

    public static TokenValidationResponseBuilder builder() {
        return new TokenValidationResponseBuilder();
    }

    public static class TokenValidationResponseBuilder {
        private boolean valid;
        private String email;
        private String role;
        private String name;

        public TokenValidationResponseBuilder valid(boolean valid) { this.valid = valid; return this; }
        public TokenValidationResponseBuilder email(String email) { this.email = email; return this; }
        public TokenValidationResponseBuilder role(String role) { this.role = role; return this; }
        public TokenValidationResponseBuilder name(String name) { this.name = name; return this; }
        public TokenValidationResponse build() {
            return new TokenValidationResponse(valid, email, role, name);
        }
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
