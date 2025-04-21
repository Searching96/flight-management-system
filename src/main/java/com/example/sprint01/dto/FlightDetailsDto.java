package com.example.sprint01.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDetailsDto {
    private Long flightId;
    private Long mediumAirportId;
    private int stopTime;
    private String note;
}
