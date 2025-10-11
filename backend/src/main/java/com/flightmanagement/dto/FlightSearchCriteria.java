package com.flightmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchCriteria {

    @NotNull(message = "Departure airport ID is required")
    private Integer departureAirportId;

    @NotNull(message = "Arrival airport ID is required")
    private Integer arrivalAirportId;

    @NotNull(message = "Departure date is required")
    private LocalDateTime departureDate;

    @NotNull(message = "Passenger count is required")
    private Integer passengerCount;

    private Integer ticketClassId;
}
