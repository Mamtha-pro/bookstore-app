package com.bookstore.controller;

import com.bookstore.dto.request.BookRequest;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "📚 Books", description = "Browse, search and manage books")
public class BookController {

    private final BookService bookService;

    // ── Public endpoints ──────────────────────────────────────────────
    @GetMapping("/api/books")
    @Operation(summary = "Get all books (paginated)",
            description = "Returns all books with pagination. Public — no login needed.")
    @ApiResponse(responseCode = "200", description = "✅ List of books returned")
    public Page<BookResponse> getAllBooks(
            @PageableDefault(size = 12) Pageable pageable) {
        return bookService.getAllBooks(pageable);
    }

    @GetMapping("/api/books/{id}")
    @Operation(summary = "Get single book by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Book found"),
            @ApiResponse(responseCode = "404", description = "❌ Book not found")
    })
    public BookResponse getBook(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/api/books/search")
    @Operation(summary = "Search books by title or author",
            description = "Search across title and author fields. Public.")
    public Page<BookResponse> searchBooks(
            @Parameter(description = "Search query", example = "Clean Code")
            @RequestParam String q,
            @PageableDefault(size = 12) Pageable pageable) {
        return bookService.searchBooks(q, pageable);
    }

    @GetMapping("/api/books/category/{name}")
    @Operation(summary = "Get books by category",
            description = "Filter books by category name. Public.")
    public Page<BookResponse> getByCategory(
            @Parameter(description = "Category name",
                    example = "Programming")
            @PathVariable String name,
            @PageableDefault(size = 12) Pageable pageable) {
        return bookService.getBooksByCategory(name, pageable);
    }

    // ── Admin endpoints ────────────────────────────────────────────────
    @PostMapping("/api/admin/books")
    @Operation(summary = "Add new book",
            description = "Admin only — add a new book to the catalogue.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "✅ Book created"),
            @ApiResponse(responseCode = "403", description = "❌ Admin access required")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "title": "Clean Code",
              "author": "Robert C. Martin",
              "isbn": "9780132350884",
              "price": 499.00,
              "category": "Programming",
              "stock": 50,
              "description": "A handbook of agile software craftsmanship"
            }
            """))
    )
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(request));
    }

    @PutMapping("/api/admin/books/{id}")
    @Operation(summary = "Update book",
            description = "Admin only — update existing book details.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public BookResponse updateBook(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/api/admin/books/{id}")
    @Operation(summary = "Delete book",
            description = "Admin only — permanently delete a book.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "✅ Book deleted"),
            @ApiResponse(responseCode = "404", description = "❌ Book not found")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/admin/books/{id}/stock")
    @Operation(summary = "Update book stock",
            description = "Admin only — update stock quantity.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public BookResponse updateStock(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New stock quantity", example = "100")
            @RequestParam Integer stock) {
        return bookService.updateStock(id, stock);
    }
}