package com.taskapp.common.security;

import com.taskapp.common.dto.TokenClaims;
import com.taskapp.common.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtUtil(String secret, long expirationSeconds) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 characters long"
            );
        }

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expirationSeconds = expirationSeconds;
    }

    public String generate(Long userId, String username, Role role) {
        Date now = new Date();
        Date exp = new Date(
                now.getTime() + expirationSeconds * 1000L
        );

        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of(
                        "userId", userId,
                        "username", username,
                        "role", role.name()
                ))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenClaims parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = claims.get("userId", Number.class).longValue();
        String username = claims.get("username", String.class);
        Role role = Role.valueOf(
                claims.get("role", String.class)
        );

        return new TokenClaims(userId, username, role);
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}