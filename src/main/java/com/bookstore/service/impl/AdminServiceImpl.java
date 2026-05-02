package com.bookstore.service.impl;

import com.bookstore.constants.OrderStatus;
import com.bookstore.dto.response.DashboardResponse;
import com.bookstore.repository.*;
import com.bookstore.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository    userRepository;
    private final BookRepository    bookRepository;
    private final OrderRepository   orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public DashboardResponse getDashboardStats() {
        long totalUsers    = userRepository.count();
        long totalBooks    = bookRepository.count();
        long totalOrders   = orderRepository.count();
        long totalPayments = paymentRepository.count();

        double totalRevenue = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getAmount() != null)
                .mapToDouble(p -> p.getAmount())
                .sum();

        long pendingOrders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count();

        long deliveredOrders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalBooks(totalBooks)
                .totalOrders(totalOrders)
                .totalPayments(totalPayments)
                .totalRevenue(totalRevenue)
                .pendingOrders(pendingOrders)
                .deliveredOrders(deliveredOrders)
                .build();
    }
}