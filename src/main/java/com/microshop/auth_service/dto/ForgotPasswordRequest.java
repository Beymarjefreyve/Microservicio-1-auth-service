package com.microshop.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    private String frontendUrl;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String email, String frontendUrl) {
        this.email = email;
        this.frontendUrl = frontendUrl;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFrontendUrl() { return frontendUrl; }
    public void setFrontendUrl(String frontendUrl) { this.frontendUrl = frontendUrl; }
}
