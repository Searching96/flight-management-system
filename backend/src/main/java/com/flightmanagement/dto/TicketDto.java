package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    
    private Integer ticketId;
    private Integer flightId;
    private Integer ticketClassId;
    private Integer bookCustomerId;
    private Integer passengerId;
    private String seatNumber;
    private Byte ticketStatus;
    private LocalDateTime paymentTime;
    private BigDecimal fare;
    private String confirmationCode;
}
