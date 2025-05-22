package com.example.sprint01.service;

import com.example.sprint01.dto.FlightDetailsDto;

import java.util.List;

public interface FlightDetailsService {
    FlightDetailsDto createFlightDetails(FlightDetailsDto flightDetailsDto);

    FlightDetailsDto getFlightDetailsById(Long flightId, Long mediumAirportId);

    List<FlightDetailsDto> getFlightDetailsByFlightId(Long flightId);

    FlightDetailsDto updateFlightDetails(Long flightId, Long mediumAirportId, FlightDetailsDto updatedFlightDetails);

    void deleteFlightDetails(Long flightId, Long mediumAirportId);

    void deleteFlightDetailsByFlightId(Long flightId);
}
