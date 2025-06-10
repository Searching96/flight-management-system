package com.flightmanagement.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetToken);
}
