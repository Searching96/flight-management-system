package com.flightmanagement.service;

import com.flightmanagement.dto.EmailBookingRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface EmailService {
    void sendPasswordResetEmail(String to, String resetToken);

    void sendSingleTicketConfirmation(String to, String customerName, String passengerName,
                                      String confirmationCode, String flightCode, String departureCity,
                                      String arrivalCity, String departureTime, String seatNumber,
                                      BigDecimal fare, boolean needsPayment);

    void sendCustomerWelcomeEmail(String to, String customerName);

    void sendPassengerPaymentNotification(String to, String passengerName, String confirmationCode,
                                          String flightCode, String departureCity, String arrivalCity,
                                          String departureTime, String seatNumber, BigDecimal fare);

    void sendEmployeeCredentialsEmail(String to, String employeeName, String accountName, String employeeTypeName,
                                      String tempPassword);
}