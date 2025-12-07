package com.flightmanagement.service;

import com.flightmanagement.dto.TicketDto;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendPasswordResetEmail(String to, String resetToken);

    void sendBookingConfirmation(TicketDto ticketDto);

    void sendCustomerWelcomeEmail(String to, String customerName);

    void sendEmployeeCredentialsEmail(String to, String employeeName, String accountName, String employeeTypeName, String tempPassword);
}
