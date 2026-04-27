package com.bookstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long   totalUsers;
    private long   totalBooks;
    private long   totalOrders;
    private long   totalPayments;
    private double totalRevenue;
    private long   pendingOrders;
    private long   deliveredOrders;
}