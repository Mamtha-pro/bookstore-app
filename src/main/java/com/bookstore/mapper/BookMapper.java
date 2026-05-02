package com.bookstore.mapper;

import com.bookstore.dto.request.BookRequest;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.entity.Book;

public class BookMapper {

    private BookMapper() {}

    public static BookResponse toResponse(Book book) {
        if (book == null) return null;

        BookResponse r = new BookResponse();
        r.setId(book.getId());
        r.setTitle(book.getTitle());
        r.setAuthor(book.getAuthor());
        r.setIsbn(book.getIsbn());
        r.setPrice(book.getPrice());
        r.setCategory(book.getCategory());
        r.setStock(book.getStock());
        r.setDescription(book.getDescription());
        r.setImageUrl(book.getImageUrl());
        return r;
    }

    public static Book toEntity(BookRequest request) {
        if (request == null) return null;

        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();
    }
}