package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    
    private Integer flightId;
    private Integer customerId;
    private Integer ticketClassId;
    private List<PassengerDto> passengers;
    private BigDecimal totalFare;
    private List<String> seatNumbers;
}
