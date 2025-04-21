package com.example.sprint01.service;

import com.example.sprint01.dto.FlightDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FlightService {
    FlightDto createFlight(FlightDto flightDto);

    FlightDto getFlightById(Long id);

    List<FlightDto> getAllFlights();

    FlightDto updateFlight(Long id, FlightDto updatedFlight);

    void deleteFlight(Long id);
}
