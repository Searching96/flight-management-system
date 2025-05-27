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
public class FlightSearchDto {
    
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private Integer passengerCount;
    private Integer ticketClassId;
    private Boolean isRoundTrip;
}