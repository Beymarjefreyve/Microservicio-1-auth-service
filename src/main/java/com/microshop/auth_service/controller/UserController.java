package com.microshop.auth_service.controller;

import com.microshop.auth_service.dto.UserProfileResponse;
import com.microshop.auth_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getProfile(email));
    }
}
