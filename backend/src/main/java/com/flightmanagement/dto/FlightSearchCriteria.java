package com.flightmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightSearchCriteria {
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private Integer passengerCount;
    private Integer ticketClassId;
}
