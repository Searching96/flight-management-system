package com.flightmanagement.service;

import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.dto.LoginRequestDto;
import com.flightmanagement.dto.RegisterDto;

public interface AuthService {

    AuthResponse authenticate(LoginRequestDto request);

    AuthResponse register(RegisterDto request);

    AuthResponse refreshToken(String token);

    AuthResponse debugLoginByName(String accountName);

    AuthResponse processPasswordReset(String token, String newPassword);

    void processForgotPassword(String email);

    boolean validatePasswordResetToken(String token);

    boolean validateEmail(String email);
}
