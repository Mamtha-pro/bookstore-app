package com.bookstore.dto.response;


import lombok.Data;

@Data
public class OrderItemResponse {
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private Double price;
}
