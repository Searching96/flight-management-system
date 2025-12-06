package com.flightmanagement.service;

import com.flightmanagement.dto.TicketDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public interface EmailService {
    void sendPasswordResetEmail(String to, String resetToken);

    void sendBookingConfirmation(TicketDto ticketDto);

    void sendCustomerWelcomeEmail(String to, String customerName);

    void sendPassengerPaymentNotification(String to, String passengerName, String confirmationCode,
                                          String flightCode, String departureCity, String arrivalCity,
                                          String departureTime, String seatNumber, BigDecimal fare);

    void sendEmployeeCredentialsEmail(String to, String employeeName, String accountName, String employeeTypeName,
                                      String tempPassword);
}
