package com.bookstore.service;



import com.bookstore.dto.request.PlaceOrderRequest;
import com.bookstore.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(String email, PlaceOrderRequest request);
    List<OrderResponse> getMyOrders(String email);
    OrderResponse getOrderById(String email, Long orderId);
    OrderResponse cancelOrder(String email, Long orderId);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long orderId, String status);
}
