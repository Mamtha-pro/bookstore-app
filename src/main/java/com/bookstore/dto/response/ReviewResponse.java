package com.bookstore.dto.response;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Long bookId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
