package com.smart_interview_backend.service;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart_interview_backend.dto.AuthResponse;
import com.smart_interview_backend.dto.LoginRequest;
import com.smart_interview_backend.dto.RegisterRequest;
import com.smart_interview_backend.entity.Role;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.UserRepository;
import com.smart_interview_backend.security.JwtService;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}

    public User register(RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(
                Role.valueOf(request.getRole().toUpperCase())
        );

        return userRepository.save(user);
    }


    public AuthResponse login(LoginRequest request) {
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getRole().name()
        );
    }


}
