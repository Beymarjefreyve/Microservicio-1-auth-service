package com.microshop.auth_service.service;

import com.microshop.auth_service.dto.*;
import com.microshop.auth_service.entity.PasswordResetToken;
import com.microshop.auth_service.entity.User;
import com.microshop.auth_service.entity.VerificationToken;
import com.microshop.auth_service.repository.UserRepository;
import com.microshop.auth_service.repository.VerificationTokenRepository;
import com.microshop.auth_service.security.JwtUtil;
import java.util.UUID;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager, PasswordResetService passwordResetService,
                       EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false) // Dehabilitado hasta verificar
                .build();
        
        userRepository.save(user);

        // Crear token de verificación
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        verificationTokenRepository.save(verificationToken);

        // Enviar correo de verificación (placeholder)
        String baseUrl = request.getFrontendUrl();
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:3000";
        }
        String verificationLink = baseUrl + "/verify-email?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), verificationLink);

        return AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Registro exitoso. Por favor verifica tu correo.")
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = passwordResetService.createToken(user);
            
            String baseUrl = request.getFrontendUrl();
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "http://localhost:3000"; // Fallback
            }
            String resetLink = baseUrl + "/reset-password?token=" + token;

            try {
                String htmlContent = generateResetPasswordEmail(user.getName(), resetLink);
                emailService.sendHtmlEmail(user.getEmail(), "Restablece tu contraseña - MicroShop", htmlContent);
                logger.info("Password reset email sent to: {}", user.getEmail());
            } catch (Exception e) {
                logger.error("Error sending recovery email to {}: {}", user.getEmail(), e.getMessage());
            }
        });
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        String jwtToken = jwtUtil.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private String generateResetPasswordEmail(String name, String resetLink) {
        return "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; background-color: #ffffff;\">" +
                "  <div style=\"background-color: #2563eb; padding: 30px; text-align: center;\">" +
                "    <h1 style=\"color: #ffffff; margin: 0; font-size: 28px; letter-spacing: -0.5px;\">MicroShop</h1>" +
                "  </div>" +
                "  <div style=\"padding: 40px;\">" +
                "    <h2 style=\"color: #111827; margin-top: 0; margin-bottom: 20px; font-size: 24px;\">¡Hola, " + name + "!</h2>" +
                "    <p style=\"color: #4b5563; line-height: 1.6; font-size: 16px; margin-bottom: 30px;\">" +
                "      Has solicitado restablecer tu contraseña en MicroShop. Haz clic en el siguiente botón para continuar con el proceso:" +
                "    </p>" +
                "    <div style=\"text-align: center; margin-bottom: 35px;\">" +
                "      <a href=\"" + resetLink + "\" style=\"background-color: #2563eb; color: #ffffff; padding: 16px 32px; text-decoration: none; border-radius: 8px; font-weight: 700; font-size: 16px; display: inline-block; box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2);\">" +
                "        Restablecer Contraseña" +
                "      </a>" +
                "    </div>" +
                "    <p style=\"color: #6b7280; font-size: 14px; margin-bottom: 10px;\">" +
                "      O copia y pega este enlace en tu navegador:" +
                "    </p>" +
                "    <p style=\"color: #2563eb; font-size: 13px; word-break: break-all; margin-bottom: 30px; line-height: 1.4;\">" + resetLink + "</p>" +
                "    <hr style=\"border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;\">" +
                "    <p style=\"color: #9ca3af; font-size: 12px; line-height: 1.5; text-align: center;\">" +
                "      Este enlace expirará pronto por seguridad. Si no solicitaste este cambio, puedes ignorar este correo.<br><br>" +
                "      © 2026 MicroShop. Todos los derechos reservados." +
                "    </p>" +
                "  </div>" +
                "</div>";
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetService.validateToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetService.deleteToken(request.getToken());
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de verificación inválido"));

        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("El token de verificación ha expirado");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            boolean isValid = jwtUtil.isTokenValid(token, user);

            return TokenValidationResponse.builder()
                    .valid(isValid)
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .name(user.getName())
                    .build();
        } catch (Exception e) {
            return TokenValidationResponse.builder().valid(false).build();
        }
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
