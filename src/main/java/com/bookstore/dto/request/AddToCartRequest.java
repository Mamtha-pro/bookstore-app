package com.bookstore.dto.request;



import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull private Long bookId;
    @NotNull @Min(1) private Integer quantity;
}
