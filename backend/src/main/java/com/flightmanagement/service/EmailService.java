package com.flightmanagement.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public interface EmailService {
    void sendPasswordResetEmail(String to, String resetToken);

    void sendSingleTicketConfirmation(String to, String customerName, String passengerName,
                                      String confirmationCode, String flightCode, String departureCity,
                                      String arrivalCity, String departureTime, String seatNumber,
                                      BigDecimal fare, boolean needsPayment);

    void sendMultiPassengerBookingConfirmation(String to, String customerName, String confirmationCode,
                                               String flightCode, String departureCity, String arrivalCity,
                                               String departureTime, List<PassengerTicketInfo> passengers,
                                               BigDecimal totalFare, boolean needsPayment);

    void sendCustomerWelcomeEmail(String to, String customerName);

    void sendPassengerPaymentNotification(String to, String passengerName, String confirmationCode,
                                          String flightCode, String departureCity, String arrivalCity,
                                          String departureTime, String seatNumber, BigDecimal fare);

    void sendEmployeeCredentialsEmail(String to, String employeeName, String accountName, String employeeTypeName,
                                      String tempPassword);

    // Inner class for passenger ticket information
    class PassengerTicketInfo {
        private String passengerName;
        private String seatNumber;
        private BigDecimal fare;

        public PassengerTicketInfo(String passengerName, String seatNumber, BigDecimal fare) {
            this.passengerName = passengerName;
            this.seatNumber = seatNumber;
            this.fare = fare;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public BigDecimal getFare() {
            return fare;
        }
    }
}
