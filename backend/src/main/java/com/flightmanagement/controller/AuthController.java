package com.flightmanagement.controller;

import com.flightmanagement.dto.LoginRequestDto;
import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.entity.Account;
import com.flightmanagement.mapper.AuthMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.security.CustomUserDetails;
import com.flightmanagement.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Map;

// AuthController.java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AccountRepository accountRepo;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDto request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(createAuthResponse(userDetails));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request)
            throws AccessDeniedException {

        String refreshToken = request.get("refreshToken");
        if (!jwtService.validateToken(refreshToken)) {
            throw new AccessDeniedException("Invalid refresh token");
        }

        String email = jwtService.getEmailFromToken(refreshToken);
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(createAuthResponse(CustomUserDetails.create(account)));
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
