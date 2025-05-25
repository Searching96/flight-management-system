package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto {
    
    private Integer id;
    private Integer maxMediumAirport;
    private Integer minFlightDuration;
    private Integer minLayoverDuration;
    private Integer maxLayoverDuration;
    private Integer minBookingInAdvanceDuration;
    private Integer maxBookingHoldDuration;
}
