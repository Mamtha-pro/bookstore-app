package com.bookstore.service;

import com.bookstore.dto.request.*;
import com.bookstore.dto.response.*;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
