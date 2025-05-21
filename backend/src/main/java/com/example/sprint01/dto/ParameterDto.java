package com.example.sprint01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto {
    // Getters and setters
    private Integer maxMediumAirport;
    private Integer minFlightDuration;
    private Integer maxFlightDuration;
    private Integer maxStopDuration;

}