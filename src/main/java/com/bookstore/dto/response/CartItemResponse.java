package com.bookstore.dto.response;


import lombok.Data;

@Data
public class CartItemResponse {
    private Long itemId;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
}
