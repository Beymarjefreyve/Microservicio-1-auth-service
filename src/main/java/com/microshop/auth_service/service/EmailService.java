package com.microshop.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:8007/api/notifications}")
    private String notificationServiceUrl;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendVerificationEmail(String to, String name, String verificationLink) {
        logger.info("Sending verification email to {} via notification-service", to);
        Map<String, String> request = new HashMap<>();
        request.put("to", to);
        request.put("subject", "Verifica tu cuenta - MicroShop");
        request.put("body", "Hola " + name + ", verifica tu cuenta en el siguiente enlace: " + verificationLink);
        
        try {
            restTemplate.postForEntity(notificationServiceUrl + "/send-verification", request, String.class);
        } catch (Exception e) {
            logger.error("Failed to send verification email: {}", e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String name, String resetLink) {
        logger.info("Sending password reset email to {} via notification-service", to);
        Map<String, String> request = new HashMap<>();
        request.put("to", to);
        request.put("subject", "Restablece tu contraseña - MicroShop");
        request.put("body", "Hola " + name + ", restablece tu contraseña en el siguiente enlace: " + resetLink);

        try {
            restTemplate.postForEntity(notificationServiceUrl + "/send-password-reset", request, String.class);
        } catch (Exception e) {
            logger.error("Failed to send password reset email: {}", e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        logger.info("Sending HTML email to {} via notification-service", to);
        Map<String, String> request = new HashMap<>();
        request.put("to", to);
        request.put("subject", subject);
        request.put("body", htmlContent);

        try {
            restTemplate.postForEntity(notificationServiceUrl + "/send-verification", request, String.class);
        } catch (Exception e) {
            logger.error("Failed to send HTML email: {}", e.getMessage());
        }
    }
}
