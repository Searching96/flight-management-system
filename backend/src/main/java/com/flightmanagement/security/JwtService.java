package com.flightmanagement.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    // Add these new fields to your JwtService
    @Value("${jwt.password-reset.secret}")
    private String passwordResetSecret;

    @Value("${jwt.password-reset.expiration-min}")
    private int passwordResetExpirationMin;

    @Getter
    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpirationMs;

    // Update password reset token generation
    public String generatePasswordResetToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(passwordResetExpirationMin, ChronoUnit.MINUTES)))
                .signWith(getPasswordResetSigningKey())
                .compact();
    }

    public String getEmailFromPasswordResetToken(String token) {
        return Jwts.parser()
                .verifyWith(getPasswordResetSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    // Modern JJWT validation for password reset tokens
    public boolean validatePasswordResetToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getPasswordResetSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Dedicated signing key for password resets
    private SecretKey getPasswordResetSigningKey() {
        return Keys.hmacShaKeyFor(
                passwordResetSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        return buildToken(userDetails, jwtExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(refreshExpirationMs, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }

    private String buildToken(CustomUserDetails userDetails, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getEmail())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public long getJwtExpirationInSeconds() {
        return jwtExpirationMs / 1000L;
    }
}
