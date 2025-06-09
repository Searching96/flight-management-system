package com.flightmanagement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchCriteria {
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private Integer passengerCount;
    private Integer ticketClassId;
}
