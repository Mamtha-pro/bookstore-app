package com.bookstore.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // ── API Info ──────────────────────────────────────────
                .info(new Info()
                        .title(" BookStore API")
                        .description("""
                                ## BookStore — Full Stack REST API
                                
                                A complete bookstore backend with:
                                -  JWT Authentication
                                -  Book Management
                                -  Shopping Cart
                                -  Wishlist
                                -  Order Management
                                -  Payment (Razorpay)
                                -  Reviews & Ratings
                                - 🛠 Admin Dashboard
                                
                                ### How to use:
                                1. Use **POST /api/auth/login** to get JWT token
                                2. Click **Authorize** button  above
                                3. Enter: `Bearer your_token_here`
                                4. Now all protected endpoints work!
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BookStore Team")
                                .email("admin@bookstore.com")
                                .url("http://localhost:5173"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))

                // ── Server URLs ───────────────────────────────────────
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("🖥️ Local Development Server"),
                        new Server()
                                .url("https://api.bookstore.com")
                                .description("🌐 Production Server")))

                // ── JWT Security Scheme ───────────────────────────────
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .name("Bearer Authentication")
                                        .description("Enter your JWT token. Get it from POST /api/auth/login")))

                // ── API Tags (groups in UI) ────────────────────────────
                .tags(List.of(
                        new Tag().name(" Authentication")
                                .description("Register, Login, Token management"),
                        new Tag().name(" User")
                                .description("View and update user profile"),
                        new Tag().name(" Books")
                                .description("Browse, search and filter books"),
                        new Tag().name(" Cart")
                                .description("Shopping cart operations"),
                        new Tag().name(" Wishlist")
                                .description("Save books for later"),
                        new Tag().name(" Orders")
                                .description("Place and track orders"),
                        new Tag().name(" Payments")
                                .description("Razorpay payment integration"),
                        new Tag().name(" Reviews")
                                .description("Book reviews and ratings"),
                        new Tag().name(" Admin")
                                .description("Admin-only management endpoints")));
    }
}