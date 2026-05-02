package com.bookstore.controller;

import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.request.ChangePasswordRequest;
import com.bookstore.dto.request.UpdateProfileRequest;
import com.bookstore.dto.response.UserResponse;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "👤 User", description = "User profile management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/me")
    @Operation(summary = "Get my profile",
            description = "Returns the logged-in user's profile details.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Profile returned")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails user) {
        UserResponse profile = userService.getMyProfile(user.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success(profile, "Profile retrieved successfully", 200));
    }

    @PutMapping("/api/users/me")
    @Operation(summary = "Update my profile",
            description = "Update name and/or email.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "name": "Sriram Updated",
              "email": "sri.new@gmail.com"
            }
            """))
    )
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse updated = userService.updateProfile(user.getUsername(), request);
        return ResponseEntity.ok(
                ApiResponse.success(updated, "Profile updated successfully", 200));
    }

    @PutMapping("/api/users/me/password")
    @Operation(summary = "Change password",
            description = "Change password — requires current password for verification.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "currentPassword": "user123",
              "newPassword": "newpass456"
            }
            """))
    )
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user.getUsername(), request);
        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", "Password updated", 200));
    }

    @GetMapping("/api/admin/users")
    @Operation(summary = "Get ALL users (Admin)",
            description = "Admin only — list all registered users.")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.success(users, "Users retrieved successfully", 200));
    }

    @GetMapping("/api/admin/users/{id}")
    @Operation(summary = "Get user by ID (Admin)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.success(user, "User retrieved successfully", 200));
    }

    @DeleteMapping("/api/admin/users/{id}")
    @Operation(summary = "Delete user (Admin)",
            description = "Admin only — permanently delete a user account.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "User deleted")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "User deleted successfully", 200));
    }
}