package com.bookstore.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishlistResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private Double bookPrice;
    private String bookImageUrl;
    private LocalDateTime addedAt;
}