package com.bookstore.service;

import com.bookstore.dto.request.UpdateProfileRequest;
import com.bookstore.dto.request.ChangePasswordRequest;
import com.bookstore.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse getMyProfile(String email);
    UserResponse updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    void deleteUser(Long id);
}