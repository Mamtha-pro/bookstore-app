package com.bookstore.security;

import com.bookstore.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfig              corsConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(
                        corsConfig.corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ── Always allow OPTIONS preflight ─────────────────
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // ── Swagger UI ─────────────────────────────────────
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs",
                                "/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**")
                        .permitAll()

                        // ── Auth (register, login) ─────────────────────────
                        .requestMatchers("/api/auth/**")
                        .permitAll()

//                        // ── AI Assistant — public ──────────────────────────
//                        .requestMatchers("/api/ai/**")        // ✅ ADDED
//                        .permitAll()

                        // ── Books public browsing ──────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/books")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**")
                        .permitAll()

                        // ── Reviews public read ────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/reviews/book/**")
                        .permitAll()

                        // ── Admin only ─────────────────────────────────────
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // ── Everything else needs login ────────────────────
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}