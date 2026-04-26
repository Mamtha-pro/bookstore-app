package com.bookstore.controller;

import com.bookstore.dto.request.LoginRequest;
import com.bookstore.dto.request.RegisterRequest;
import com.bookstore.dto.response.AuthResponse;
import com.bookstore.dto.response.UserResponse;
import com.bookstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "🔐 Authentication", description = "Register, Login and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Creates a new USER account. Returns user details on success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "✅ User registered successfully"),
            @ApiResponse(responseCode = "400", description = "❌ Email already registered")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "name": "Sriram",
              "email": "sri@gmail.com",
              "password": "123456"
            }
            """))
    )
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login and get JWT token",
            description = """
            Login with email and password.
            Returns a JWT token — copy it and click **Authorize** 🔒 button above.
            Enter: `Bearer <your_token>`
            
            **Test credentials:**
            - Admin: `admin@bookstore.com` / `admin123`
            - User:  `user@bookstore.com`  / `user123`
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Login successful — JWT token returned"),
            @ApiResponse(responseCode = "401", description = "❌ Wrong email or password")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "Admin Login", value = """
                {
                  "email": "admin@bookstore.com",
                  "password": "admin123"
                }
                """),
                    @ExampleObject(name = "User Login", value = """
                {
                  "email": "user@bookstore.com",
                  "password": "user123"
                }
                """)
            })
    )
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}