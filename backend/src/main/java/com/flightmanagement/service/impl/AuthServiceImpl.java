package com.flightmanagement.service.impl;

import com.flightmanagement.dto.LoginRequestDto;
import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.mapper.AuthMapper;
import com.flightmanagement.security.CustomUserDetails;
import com.flightmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    public AuthResponse authenticate(LoginRequestDto request) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // Get user details from authentication principal
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Build the response
        return createAuthResponse(userDetails);
    }

    private AuthResponse createAuthResponse(CustomUserDetails userDetails) {
        return new AuthResponse(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails.getEmail()),
                "Bearer",
                Instant.now().plusMillis(jwtService.getJwtExpirationMs()),
                authMapper.toUserDetailsDto(userDetails));
    }
}
