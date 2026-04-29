package com.microshop.auth_service.dto;

import java.util.List;

public class TokenValidationResponse {
    private boolean valid;
    private String email;
    private List<String> roles;
    private String name;

    public TokenValidationResponse() {}

    public TokenValidationResponse(boolean valid, String email, List<String> roles, String name) {
        this.valid = valid;
        this.email = email;
        this.roles = roles;
        this.name = name;
    }

    public static TokenValidationResponseBuilder builder() {
        return new TokenValidationResponseBuilder();
    }

    public static class TokenValidationResponseBuilder {
        private boolean valid;
        private String email;
        private List<String> roles;
        private String name;

        public TokenValidationResponseBuilder valid(boolean valid) { this.valid = valid; return this; }
        public TokenValidationResponseBuilder email(String email) { this.email = email; return this; }
        public TokenValidationResponseBuilder roles(List<String> roles) { this.roles = roles; return this; }
        public TokenValidationResponseBuilder name(String name) { this.name = name; return this; }
        public TokenValidationResponse build() {
            return new TokenValidationResponse(valid, email, roles, name);
        }
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
