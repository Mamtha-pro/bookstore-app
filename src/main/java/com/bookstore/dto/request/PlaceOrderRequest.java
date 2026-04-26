package com.bookstore.dto.request;



import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotBlank private String address;
}
