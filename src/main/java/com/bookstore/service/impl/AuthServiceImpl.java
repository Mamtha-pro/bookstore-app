package com.bookstore.service.impl;

import com.bookstore.constants.Role;
import com.bookstore.dto.request.LoginRequest;
import com.bookstore.dto.request.RegisterRequest;
import com.bookstore.dto.response.AuthResponse;
import com.bookstore.dto.response.UserResponse;
import com.bookstore.entity.User;
import com.bookstore.exception.BadRequestException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtTokenProvider;
import com.bookstore.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;

    // ── Register ──────────────────────────────────────────────────────
    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        log.info("User registered: " + saved.getEmail());
        return UserMapper.toResponse(saved);
    }

    // ── Login ─────────────────────────────────────────────────────────
    @Override
    public AuthResponse login(LoginRequest request) {
        // Step 1: Authenticate
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // Step 2: Generate token
        String token = jwtTokenProvider.generateToken(auth);

        // Step 3: Extract role
        String role = auth.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        // Step 4: Get user details for name and email ✅ ADDED
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        log.info("User logged in: " + request.getEmail() + " role: " + role);

        // Step 5: Return token + role + email + name ✅ CHANGED
        return new AuthResponse(token, role, user.getEmail(), user.getName());
    }
}