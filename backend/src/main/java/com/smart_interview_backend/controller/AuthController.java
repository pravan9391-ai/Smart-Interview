package com.smart_interview_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smart_interview_backend.dto.AuthResponse;
import com.smart_interview_backend.dto.LoginRequest;
import com.smart_interview_backend.dto.RegisterRequest;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RequestBody LoginRequest request) {

    AuthResponse response = authService.login(request);

    return ResponseEntity.ok(response);
}
}
