package com.flightmanagement.service;

import com.flightmanagement.dto.EmailBookingRequest;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendPasswordResetEmail(String to, String resetToken);

    void sendBookingConfirmationEmail(EmailBookingRequest request);
}