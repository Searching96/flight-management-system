package com.example.sprint01.mapper;

import com.example.sprint01.dto.FlightDetailsDto;
import com.example.sprint01.entity.FlightDetails;
import com.example.sprint01.entity.composite_key.FlightDetailsId;

public class FlightDetailsMapper {
    public static FlightDetailsDto mapToDto(FlightDetails flightDetails) {
        return new FlightDetailsDto(
                flightDetails.getId().getFlightId(),
                flightDetails.getId().getMediumAirportId(),
                flightDetails.getStopTime(),
                flightDetails.getNote()
        );
    }

    public static FlightDetails mapToFlightDetails(FlightDetailsDto flightDetailsDto) {
        return new FlightDetails(
                new FlightDetailsId(flightDetailsDto.getFlightId(), flightDetailsDto.getMediumAirportId()),
                flightDetailsDto.getStopTime(),
                flightDetailsDto.getNote(),
                null,
                null
        );
    }
}
