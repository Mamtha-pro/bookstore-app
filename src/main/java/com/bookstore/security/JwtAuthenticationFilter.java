package com.bookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider          jwtTokenProvider;
    private final CustomUserDetailsService  userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path   = request.getRequestURI();
        String method = request.getMethod();

        // Skip OPTIONS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // ✅ Debug log — you will see this in IntelliJ console
        log.info("Request: " + method + " " + path
                + " | Auth header: " + (authHeader != null
                ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..."
                : "MISSING"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No JWT token for: " + method + " " + path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token    = authHeader.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(token);

            log.info("Token valid: " + isValid + " for: " + path);

            if (isValid) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null,
                                userDetails.getAuthorities());
                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("Auth set for user: " + username
                        + " roles: " + userDetails.getAuthorities());
            }

        } catch (Exception e) {
            log.error("JWT filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}