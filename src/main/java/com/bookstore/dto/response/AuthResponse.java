package com.bookstore.dto.response;



import lombok.*;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String role;

    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
}
