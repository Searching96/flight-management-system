package com.flightmanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility Service for token generation, validation, and extraction
 * Handles JWT operations for authentication and authorization
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * Generate JWT token for authenticated user
     * @param userDetails Spring Security UserDetails
     * @param accountId User account ID
     * @param accountType User account type (1=Customer, 2=Employee)
     * @param employeeType Employee type if applicable (1-5)
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails, Integer accountId, Integer accountType, Integer employeeType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", accountId);
        claims.put("accountType", accountType);
        if (employeeType != null) {
            claims.put("employeeType", employeeType);
        }
        return createToken(claims, userDetails.getUsername());
    }
    
    /**
     * Create JWT token with claims and subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        Instant expirationInstant = now.plus(expiration, ChronoUnit.MILLIS);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationInstant))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Validate JWT token
     * @param token JWT token
     * @param userDetails User details for validation
     * @return true if token is valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Extract username from JWT token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * Extract account ID from JWT token
     */
    public Integer getAccountIdFromToken(String token) {
        return getClaimFromToken(token, claims -> (Integer) claims.get("accountId"));
    }
    
    /**
     * Extract account type from JWT token
     */
    public Integer getAccountTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> (Integer) claims.get("accountType"));
    }
    
    /**
     * Extract employee type from JWT token
     */
    public Integer getEmployeeTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> (Integer) claims.get("employeeType"));
    }
    
    /**
     * Extract expiration date from JWT token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim from JWT token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from JWT token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Check if JWT token is expired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * Validate token format and signature
     */
    public boolean isTokenValid(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
