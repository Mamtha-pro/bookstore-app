package com.bookstore.dto.response;



import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String status;
    private Double totalAmount;
    private String address;
    private LocalDateTime orderedAt;
    private List<OrderItemResponse> items;
}
