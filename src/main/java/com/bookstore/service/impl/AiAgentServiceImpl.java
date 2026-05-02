package com.bookstore.service.impl;

import com.bookstore.dto.request.AiChatRequest;
import com.bookstore.dto.response.AiChatResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.AiAgentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAgentServiceImpl implements AiAgentService {

    private static final Logger log =
            LoggerFactory.getLogger(AiAgentServiceImpl.class);

    private final BookRepository  bookRepository;
    private final OrderRepository orderRepository;
    private final UserRepository  userRepository;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        String msg   = request.getMessage();
        String lower = msg.toLowerCase().trim();

        // ── Greetings ─────────────────────────────────────────────────
        if (lower.matches(".*(hi|hello|hey|hii|helo|namaste).*")) {
            return reply(
                    "👋 Hello! Welcome to BookStore!\n\n"
                            + "I can help you with:\n"
                            + "📚 Find books by price — say 'books under ₹500'\n"
                            + "🎯 Recommendations — say 'recommend programming books'\n"
                            + "📦 Track orders — say 'show my orders'\n"
                            + "🔍 Search — say 'find books on java'\n\n"
                            + "What would you like?",
                    "greeting");
        }

        // ── Price Search ──────────────────────────────────────────────
        if (lower.contains("under") || lower.contains("below")
                || lower.contains("cheap") || lower.contains("less than")
                || lower.contains("budget")) {
            return handlePriceSearch(lower);
        }

        // ── Order Tracking ────────────────────────────────────────────
        if (lower.contains("order") || lower.contains("track")
                || lower.contains("delivery") || lower.contains("status")
                || lower.contains("bought") || lower.contains("purchase")) {
            return handleOrders(request.getUserEmail());
        }

        // ── Category Search ───────────────────────────────────────────
        if (lower.contains("programming") || lower.contains("coding")
                || lower.contains("java") || lower.contains("python")
                || lower.contains("javascript") || lower.contains("developer")) {
            return handleCategory("Programming");
        }

        if (lower.contains("fiction") || lower.contains("novel")
                || lower.contains("story") || lower.contains("thriller")
                || lower.contains("mystery")) {
            return handleCategory("Fiction");
        }

        if (lower.contains("self help") || lower.contains("self-help")
                || lower.contains("motivation") || lower.contains("habit")
                || lower.contains("success") || lower.contains("mindset")) {
            return handleCategory("Self Help");
        }

        if (lower.contains("science") || lower.contains("physics")
                || lower.contains("biology") || lower.contains("cosmos")
                || lower.contains("hawking") || lower.contains("sagan")) {
            return handleCategory("Science");
        }

        if (lower.contains("business") || lower.contains("startup")
                || lower.contains("entrepreneur") || lower.contains("finance")
                || lower.contains("money") || lower.contains("invest")) {
            return handleCategory("Business");
        }

        if (lower.contains("spiritual") || lower.contains("yoga")
                || lower.contains("meditation") || lower.contains("gita")
                || lower.contains("peace") || lower.contains("monk")) {
            return handleCategory("Spirituality");
        }

        if (lower.contains("design") || lower.contains("ux")
                || lower.contains("ui") || lower.contains("product")) {
            return handleCategory("Design");
        }

        // ── Recommend ─────────────────────────────────────────────────
        if (lower.contains("recommend") || lower.contains("suggest")
                || lower.contains("best") || lower.contains("top")
                || lower.contains("popular") || lower.contains("good")) {
            return handleRecommend();
        }

        // ── Search by keyword ─────────────────────────────────────────
        if (lower.contains("find") || lower.contains("search")
                || lower.contains("show") || lower.contains("list")
                || lower.contains("all books")) {
            return handleSearch(msg);
        }

        // ── How many books ─────────────────────────────────────────────
        if (lower.contains("how many") || lower.contains("count")
                || lower.contains("total books")) {
            long count = bookRepository.count();
            return reply(
                    "📚 We have " + count + " books in our store!\n"
                            + "Categories: Programming, Fiction, Self Help, "
                            + "Science, Business, Spirituality, Design.\n\n"
                            + "Which category interests you?",
                    "info");
        }

        // ── Help ──────────────────────────────────────────────────────
        if (lower.contains("help") || lower.contains("what can")
                || lower.contains("how to") || lower.contains("?")) {
            return reply(
                    "🤖 I can help you with:\n\n"
                            + "1️⃣ 'books under ₹300' — find affordable books\n"
                            + "2️⃣ 'recommend fiction books' — get suggestions\n"
                            + "3️⃣ 'show my orders' — track your orders\n"
                            + "4️⃣ 'find java books' — search by topic\n"
                            + "5️⃣ 'best programming books' — top picks\n\n"
                            + "Try any of these!",
                    "help");
        }

        // ── Thank you ─────────────────────────────────────────────────
        if (lower.contains("thank") || lower.contains("thanks")
                || lower.contains("great") || lower.contains("awesome")) {
            return reply(
                    "😊 You're welcome! Happy reading!\n"
                            + "Let me know if you need anything else.",
                    "general");
        }

        // ── Default response ──────────────────────────────────────────
        return reply(
                "🤔 I didn't quite get that. Here's what I can do:\n\n"
                        + "📚 'books under ₹500' — find by price\n"
                        + "🎯 'recommend programming books' — suggestions\n"
                        + "📦 'track my order' — order status\n"
                        + "🔍 'find books on python' — search\n\n"
                        + "Try one of these!",
                "general");
    }

    // ── Price Handler ─────────────────────────────────────────────────
    private AiChatResponse handlePriceSearch(String message) {
        double maxPrice = 500.0;

        // Extract number from message
        String[] words = message.split("\\s+");
        for (String word : words) {
            String cleaned = word.replaceAll("[^0-9]", "");
            if (!cleaned.isEmpty() && cleaned.length() <= 5) {
                try {
                    maxPrice = Double.parseDouble(cleaned);
                    break;
                } catch (NumberFormatException ignored) {}
            }
        }

        final double limit = maxPrice;
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(b -> b.getPrice() <= limit && b.getStock() > 0)
                .sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
                .limit(6)
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            return reply(
                    "😔 No books found under ₹" + (int) limit + ".\n"
                            + "Try 'books under ₹500' or 'books under ₹1000'.",
                    "book_search");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📚 Found ").append(books.size())
                .append(" books under ₹").append((int) limit).append(":\n\n");

        books.forEach(b ->
                sb.append("• ").append(b.getTitle())
                        .append("\n  by ").append(b.getAuthor())
                        .append(" — ₹").append(b.getPrice())
                        .append(" [").append(b.getCategory()).append("]\n\n"));

        return reply(sb.toString(), "book_search");
    }

    // ── Category Handler ──────────────────────────────────────────────
    private AiChatResponse handleCategory(String category) {
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(b -> category.equalsIgnoreCase(b.getCategory())
                        && b.getStock() > 0)
                .limit(5)
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            return reply(
                    "😔 No " + category + " books found right now.",
                    "recommendation");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🎯 Top ").append(category).append(" books:\n\n");

        books.forEach(b ->
                sb.append("📖 ").append(b.getTitle())
                        .append("\n   by ").append(b.getAuthor())
                        .append(" — ₹").append(b.getPrice()).append("\n\n"));

        sb.append("Click any book to view details and add to cart!");
        return reply(sb.toString(), "recommendation");
    }

    // ── Recommend Handler ─────────────────────────────────────────────
    private AiChatResponse handleRecommend() {
        // Pick top books across categories
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(b -> b.getStock() > 0)
                .limit(6)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder(
                "⭐ Our Top Picks for You:\n\n");

        books.forEach(b ->
                sb.append("📚 ").append(b.getTitle())
                        .append("\n   by ").append(b.getAuthor())
                        .append(" — ₹").append(b.getPrice())
                        .append(" [").append(b.getCategory()).append("]\n\n"));

        sb.append("Type a category like 'fiction' or 'programming' "
                + "for specific recommendations!");
        return reply(sb.toString(), "recommendation");
    }

    // ── Search Handler ────────────────────────────────────────────────
    private AiChatResponse handleSearch(String message) {
        // Extract search keyword
        String keyword = message
                .toLowerCase()
                .replace("find", "")
                .replace("search", "")
                .replace("show", "")
                .replace("books on", "")
                .replace("books about", "")
                .replace("book on", "")
                .trim();

        if (keyword.isEmpty()) {
            return handleRecommend();
        }

        final String kw = keyword;
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(b -> b.getStock() > 0
                        && (b.getTitle().toLowerCase().contains(kw)
                        ||  b.getAuthor().toLowerCase().contains(kw)
                        ||  b.getCategory().toLowerCase().contains(kw)
                        ||  (b.getDescription() != null
                        && b.getDescription().toLowerCase().contains(kw))))
                .limit(5)
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            return reply(
                    "🔍 No books found for '" + keyword + "'.\n\n"
                            + "Try: 'programming books', 'fiction books', "
                            + "'books under ₹500'",
                    "book_search");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🔍 Found ").append(books.size())
                .append(" books for '").append(keyword).append("':\n\n");

        books.forEach(b ->
                sb.append("📖 ").append(b.getTitle())
                        .append("\n   by ").append(b.getAuthor())
                        .append(" — ₹").append(b.getPrice()).append("\n\n"));

        return reply(sb.toString(), "book_search");
    }

    // ── Order Handler ─────────────────────────────────────────────────
    private AiChatResponse handleOrders(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return reply(
                    "🔐 Please login to see your orders!\n\n"
                            + "Go to Login page and sign in "
                            + "to track your orders.",
                    "order_track");
        }

        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElse(null);

            if (user == null) {
                return reply("Please login again to see orders.",
                        "order_track");
            }

            List<Order> orders =
                    orderRepository.findByUserOrderByOrderedAtDesc(user);

            if (orders.isEmpty()) {
                return reply(
                        "📦 You haven't placed any orders yet!\n\n"
                                + "Browse our books and place your first order. "
                                + "We have 100+ books available!",
                        "order_track");
            }

            StringBuilder sb = new StringBuilder(
                    "📦 Your Recent Orders:\n\n");

            orders.stream().limit(5).forEach(o ->
                    sb.append("Order #").append(o.getId()).append("\n")
                            .append("Status: ").append(getStatusEmoji(
                                    o.getStatus().name()))
                            .append(" ").append(o.getStatus().name()).append("\n")
                            .append("Amount: ₹").append(o.getTotalAmount())
                            .append("\n")
                            .append("Date: ").append(
                                    o.getOrderedAt().toLocalDate()).append("\n\n"));

            sb.append("Go to Orders page for full details!");
            return reply(sb.toString(), "order_track");

        } catch (Exception e) {
            log.error("Order fetch error: " + e.getMessage());
            return reply(
                    "😅 Could not fetch orders right now.\n"
                            + "Please visit the Orders page directly.",
                    "order_track");
        }
    }

    // ── Helper: Status Emoji ──────────────────────────────────────────
    private String getStatusEmoji(String status) {
        switch (status) {
            case "PENDING":   return "⏳";
            case "CONFIRMED": return "✅";
            case "SHIPPED":   return "🚚";
            case "DELIVERED": return "📦";
            case "CANCELLED": return "❌";
            default:          return "📋";
        }
    }

    // ── Helper: Build Response ────────────────────────────────────────
    private AiChatResponse reply(String text, String type) {
        return new AiChatResponse(text, type);
    }
}