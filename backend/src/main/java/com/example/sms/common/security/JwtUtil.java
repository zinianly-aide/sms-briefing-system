package com.example.sms.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:sms-briefing-secret-key-must-be-at-least-32-characters}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                "jwt.secret must be at least 32 characters. Set JWT_SECRET environment variable.");
        }
        log.info("JWT secret validated (length={})", secret.length());
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
            .subject(username)
            .claims(Map.of("role", role != null ? role : "user"))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getKey())
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
