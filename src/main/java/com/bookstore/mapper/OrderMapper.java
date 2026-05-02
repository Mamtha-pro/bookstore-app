package com.bookstore.mapper;

import com.bookstore.dto.response.OrderItemResponse;
import com.bookstore.dto.response.OrderResponse;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> items = new ArrayList<>();
        if (order.getOrderItems() != null) {
            items = order.getOrderItems()
                    .stream()
                    .map(OrderMapper::toItemResponse)
                    .collect(Collectors.toList());
        }

        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setStatus(order.getStatus().name());
        r.setTotalAmount(order.getTotalAmount());
        r.setAddress(order.getAddress());
        r.setOrderedAt(order.getOrderedAt());
        r.setItems(items);
        return r;
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse r = new OrderItemResponse();
        r.setBookId(item.getBook().getId());
        r.setBookTitle(item.getBook().getTitle());
        r.setQuantity(item.getQuantity());
        r.setPrice(item.getPrice());
        return r;
    }
}
