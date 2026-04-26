package com.bookstore.dto.request;



import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank private String title;
    @NotBlank private String author;
    private String isbn;
    @NotNull @Positive private Double price;
    private String category;
    @NotNull @Min(0) private Integer stock;
    private String description;
    private String imageUrl;
}
