package com.bookstore.service;



import com.bookstore.dto.request.BookRequest;
import com.bookstore.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookResponse> getAllBooks(Pageable pageable);
    BookResponse getBookById(Long id);
    Page<BookResponse> searchBooks(String query, Pageable pageable);
    Page<BookResponse> getBooksByCategory(String category, Pageable pageable);
    BookResponse createBook(BookRequest request);
    BookResponse updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
    BookResponse updateStock(Long id, Integer stock);
}
