package com.flightmanagement.controller;

import com.flightmanagement.dto.*;
import com.flightmanagement.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

// AuthController.java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerCustomer(@Valid @RequestBody RegisterDto request) {
        request.setEmployeeType(null);
        request.setAccountType(1); // Default to customer account type
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/create-employee")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<AuthResponse> registerEmployee(@Valid @RequestBody RegisterDto request) {
        request.setAccountType(2); // Set account type to employee
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordForgetRequest request) {
        if (!authService.validateEmail(request.getEmail()))
            return ResponseEntity.badRequest().body("Cannot find account with this email");
        authService.processForgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(authService.processPasswordReset(request.getToken(), request.getNewPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRequest request) throws AccessDeniedException {
        return ResponseEntity.ok(authService.refreshToken(request.getToken()));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Boolean> validatePasswordResetToken(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(authService.validatePasswordResetToken(request.getToken()));
    }
}
