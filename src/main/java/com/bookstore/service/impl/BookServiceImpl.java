package com.bookstore.service.impl;

import com.bookstore.dto.request.BookRequest;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.entity.Book;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final Logger log =
            LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookMapper::toResponse);
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found: " + id));
        return BookMapper.toResponse(book);
    }

    @Override
    public Page<BookResponse> searchBooks(String query, Pageable pageable) {
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        query, query, pageable)
                .map(BookMapper::toResponse);
    }

    @Override
    public Page<BookResponse> getBooksByCategory(String category,
                                                 Pageable pageable) {
        return bookRepository
                .findByCategoryIgnoreCase(category, pageable)
                .map(BookMapper::toResponse);
    }

    @Override
    @Transactional
    public BookResponse createBook(BookRequest request) {
        Book book = BookMapper.toEntity(request);
        Book saved = bookRepository.save(book);
        log.info("Book created: " + saved.getId());
        return BookMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found: " + id));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setCategory(request.getCategory());
        book.setStock(request.getStock());
        book.setDescription(request.getDescription());
        book.setImageUrl(request.getImageUrl());

        log.info("Book updated: " + id);
        return BookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found: " + id));
        bookRepository.deleteById(id);
        log.info("Book deleted: " + id);
    }

    @Override
    @Transactional
    public BookResponse updateStock(Long id, Integer stock) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found: " + id));
        book.setStock(stock);
        return BookMapper.toResponse(bookRepository.save(book));
    }
}