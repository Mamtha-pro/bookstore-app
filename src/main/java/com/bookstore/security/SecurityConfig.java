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
    private final CorsConfig corsConfig;

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
                // ✅ Enable CORS with our config
                .cors(cors -> cors.configurationSource(
                        corsConfig.corsConfigurationSource()))

                // ✅ Disable CSRF (REST API — stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ Stateless sessions (JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Route permissions
                .authorizeHttpRequests(auth -> auth

                        // Swagger UI — public
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Auth — public
                        .requestMatchers("/api/auth/**").permitAll()

                        // Books GET — public
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books/**"
                        ).permitAll()

                        // Reviews GET — public
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/book/**"
                        ).permitAll()

                        // AI chat — public
                        .requestMatchers("/api/ai/**").permitAll()

                        // OPTIONS preflight — always allow
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Everything else needs login
                        .anyRequest().authenticated()
                )

                //  Add JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}