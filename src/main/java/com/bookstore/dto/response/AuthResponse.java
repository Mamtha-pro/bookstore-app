package com.bookstore.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private String role;
    private String email;
    private String name;

    public AuthResponse(String token, String role, String email, String name) {
        this.token     = token;
        this.tokenType = "Bearer";
        this.role      = role;
        this.email     = email;
        this.name      = name;
    }
}