package com.flightmanagement.service;

import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.dto.LoginRequestDto;

public interface AuthService {

    AuthResponse authenticate(LoginRequestDto request);

    AuthResponse debugLoginByName(String accountName);
}
