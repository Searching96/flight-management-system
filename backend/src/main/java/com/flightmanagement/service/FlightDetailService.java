package com.flightmanagement.service;

import com.flightmanagement.dto.FlightDetailDto;

import java.util.List;

public interface FlightDetailService {
    
    List<FlightDetailDto> getAllFlightDetails();
    
    List<FlightDetailDto> getFlightDetailsByFlightId(Integer flightId);
    
    List<FlightDetailDto> getFlightDetailsByAirportId(Integer airportId);
    
    FlightDetailDto createFlightDetail(FlightDetailDto flightDetailDto);
    
    FlightDetailDto updateFlightDetail(Integer flightId, Integer mediumAirportId, FlightDetailDto flightDetailDto);
    
    void deleteFlightDetail(Integer flightId, Integer mediumAirportId);
}
