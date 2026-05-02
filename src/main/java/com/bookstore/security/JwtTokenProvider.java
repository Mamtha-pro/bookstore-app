package com.bookstore.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log =
            LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ── Generate Token ────────────────────────────────────────────────
    public String generateToken(Authentication authentication) {
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Token generated for: " + userDetails.getUsername());
        return token;
    }

    // ── Get Username from Token ───────────────────────────────────────
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ── Validate Token ────────────────────────────────────────────────
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: " + e.getMessage());
        } catch (JwtException e) {
            log.warn("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Token validation error: " + e.getMessage());
        }
        return false;
    }
}