package com.flightmanagement.service;

import com.flightmanagement.dto.FlightRequest;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.FlightSearchCriteria;
import com.flightmanagement.dto.FlightTicketClassDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {
    
    List<FlightDto> getAllFlights();
    
    FlightDto getFlightById(Integer id);

    FlightDto createFlight(FlightRequest request);
    
    FlightDto updateFlight(Integer id, FlightRequest request);

    void deleteFlight(Integer id);
    
    FlightDto getFlightByCode(String flightCode);
    
    List<FlightDto> searchFlights(FlightSearchCriteria searchDto);
    
    List<FlightDto> getFlightsByRoute(Integer departureAirportId, Integer arrivalAirportId, LocalDateTime departureDate);
    
    List<FlightDto> getFlightsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<FlightDto> searchFlightsByDate(String departureDate);
    
    List<FlightTicketClassDto> checkFlightAvailability(Integer flightId);
}
