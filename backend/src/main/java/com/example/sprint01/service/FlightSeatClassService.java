package com.example.sprint01.service;

import com.example.sprint01.dto.FlightSeatClassDto;

import java.util.List;

public interface FlightSeatClassService {
    FlightSeatClassDto createFlightSeatClass(FlightSeatClassDto FlightSeatClassDto);

    FlightSeatClassDto getFlightSeatClassById(Long flightId, Long mediumAirportId);

    List<FlightSeatClassDto> getFlightSeatClassByFlightId(Long flightId);

    FlightSeatClassDto updateFlightSeatClass(Long flightId, Long mediumAirportId, FlightSeatClassDto updatedFlightSeatClass);

    void deleteFlightSeatClass(Long flightId, Long mediumAirportId);

    void deleteFlightSeatClassByFlightId(Long flightId);
}
