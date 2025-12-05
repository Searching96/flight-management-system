package com.flightmanagement.controller;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Operations related to authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login to the system")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponse authResponse = authService.authenticate(request);
        ApiResponse<AuthResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Login successful",
                authResponse,
                null
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register a new customer")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCustomer(@Valid @RequestBody RegisterDto request) {
        request.setEmployeeType(null); // Ensure employeeType is null for customers
        request.setAccountType(1); // Set account type to customer

        AuthResponse authResponse = authService.register(request);
        ApiResponse<AuthResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Registration successful",
                authResponse,
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create a new employee account")
    @PostMapping("/create-employee")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<AuthResponse>> registerEmployee(@Valid @RequestBody RegisterDto request) {
        request.setAccountType(2); // Set account type to employee
        AuthResponse authResponse = authService.register(request);

        ApiResponse<AuthResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Employee account created successfully",
                authResponse,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Send password reset email")
    @PostMapping("/forget-password") // Yet to assign role
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody PasswordForgetRequest request) {
        authService.processForgotPassword(request.getEmail(), request.getPhoneNumber());

        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "Password reset email sent",
                null,
                null
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<AuthResponse>> resetPassword(@RequestBody PasswordResetRequest request) {
        AuthResponse authResponse = authService.processPasswordReset(request.getToken(), request.getNewPassword());
        ApiResponse<AuthResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Password has been reset successfully",
                authResponse,
                null
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh authentication token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody TokenRequest request) throws AccessDeniedException {
        AuthResponse authResponse = authService.refreshToken(request.getToken());
        if (authResponse == null) {
            throw new AccessDeniedException("Invalid or expired token");
        }
        ApiResponse<AuthResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Token refreshed successfully",
                authResponse,
                null
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate password reset token")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validatePasswordResetToken(@RequestBody TokenRequest request) {
        Boolean isValid = authService.validatePasswordResetToken(request.getToken());
        ApiResponse<Boolean> response = new ApiResponse<>(
                HttpStatus.OK,
                "Token validation completed",
                isValid,
                null
        );
        return ResponseEntity.ok(response);
    }
}