package com.bookstore.controller;

import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.request.LoginRequest;
import com.bookstore.dto.request.RegisterRequest;
import com.bookstore.dto.response.AuthResponse;
import com.bookstore.dto.response.UserResponse;
import com.bookstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = " Authentication", description = "Register, Login and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Creates a new USER account. Returns user details on success.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email already registered")
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
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully", 201));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token",
            description = """
            Login with email and password.
            Returns a JWT token — copy it and click **Authorize** button above.
            Enter: `Bearer <your_token>`
            
            **Test credentials:**
            - Admin: `admin@bookstore.com` / `admin123`
            - User:  `user@bookstore.com`  / `user123`
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful — JWT token returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Wrong email or password")
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
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success(auth, "Login successful", 200));
    }
}