package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailBookingRequest {
        private String email;
        private String confirmationCode;
        private Object bookingData; // The full booking confirmation data
        private String paymentUrl; // URL for payment if not paid
        private String printedBy;
        private LocalDateTime printedAt;
        private boolean includePaymentButton;
}
