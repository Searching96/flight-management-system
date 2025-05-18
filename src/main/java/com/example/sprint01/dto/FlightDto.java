package com.example.sprint01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {
    private Long id;
    private Long departureAirportId;
    private Long arrivalAirportId;
    private LocalDate flightDate;
    private LocalTime flightTime;
    private int duration; // Duration in minutes
}
