package com.flightmanagement.controller;

import com.flightmanagement.dto.EmailBookingRequest;
import com.flightmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/booking-confirmation")
    public ResponseEntity<String> sendBookingConfirmation(@RequestBody EmailBookingRequest request) {
        try {
            emailService.sendBookingConfirmationEmail(request);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }
}
