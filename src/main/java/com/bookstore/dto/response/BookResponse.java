package com.bookstore.dto.response;



import lombok.Data;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private String category;
    private Integer stock;
    private String description;
    private String imageUrl;
}
