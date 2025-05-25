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
public class FlightDetailDto {
    
    private Integer flightId;
    private Integer mediumAirportId;
    private String mediumAirportName;
    private String mediumCityName;
    private LocalDateTime arrivalTime;
    private Integer layoverDuration;
}
