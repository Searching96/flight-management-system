package com.example.sprint01.mapper;

import com.example.sprint01.dto.FlightDto;
import com.example.sprint01.entity.Airport;
import com.example.sprint01.entity.Flight;

public class FlightMapper {
    public static Flight mapToFlight(FlightDto flightDto, Airport departureAirport, Airport arrivalAirport) {
        return new Flight(
                flightDto.getId(),
                departureAirport,
                arrivalAirport,
                flightDto.getFlightDate(),
                flightDto.getFlightTime(),
                flightDto.getDuration()
        );
    }

    public static FlightDto mapToDto(Flight flight) {
        return new FlightDto(
                flight.getId(),
                flight.getDepartureAirport().getId(),
                flight.getArrivalAirport().getId(),
                flight.getFlightDate(),
                flight.getFlightTime(),
                flight.getDuration()
        );
    }
}
