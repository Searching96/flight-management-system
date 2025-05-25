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
public class FlightDto {
    
    private Integer flightId;
    private String flightCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer planeId;
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    
    // Additional fields for display purposes
    private String planeCode;
    private String departureAirportName;
    private String departureCityName;
    private String arrivalAirportName;
    private String arrivalCityName;
}
