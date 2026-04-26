package com.bookstore.controller;

import com.bookstore.dto.response.DashboardResponse;
import com.bookstore.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "🛠️ Admin", description = "Admin dashboard and management")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics",
            description = """
                   Returns summary of the entire store:
                   - Total users, books, orders, payments
                   - Total revenue
                   - Pending vs delivered orders
                   """)
    @ApiResponse(responseCode = "200", description = "✅ Dashboard stats returned")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}