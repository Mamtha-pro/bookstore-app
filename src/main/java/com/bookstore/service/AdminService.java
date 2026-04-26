package com.bookstore.service;

import com.bookstore.dto.response.DashboardResponse;

public interface AdminService {
    DashboardResponse getDashboardStats();
}