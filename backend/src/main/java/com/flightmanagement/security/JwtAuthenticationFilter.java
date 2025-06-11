package com.flightmanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter with CORS Preflight Support
 * Last updated: 2025-06-11 08:14:55 UTC by thinh0704hcm
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Handle CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Handling CORS preflight request at 2025-06-11 08:14:55 UTC by thinh0704hcm");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();

        System.out.println("Processing request: " + method + " " + requestURI + " at 2025-06-11 08:14:55 UTC by thinh0704hcm");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted username from JWT: " + username + " at 2025-06-11 08:14:55 UTC by thinh0704hcm");
            } catch (Exception e) {
                System.err.println("JWT token validation failed at 2025-06-11 08:14:55 UTC by thinh0704hcm: " + e.getMessage());
                logger.error("JWT token validation failed", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("Authentication set for user: " + username + " at 2025-06-11 08:14:55 UTC by thinh0704hcm");
                } else {
                    System.err.println("JWT token validation failed for user: " + username + " at 2025-06-11 08:14:55 UTC by thinh0704hcm");
                }
            } catch (Exception e) {
                System.err.println("Error loading user details for: " + username + " at 2025-06-11 08:14:55 UTC by thinh0704hcm");
                logger.error("Error loading user details", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}