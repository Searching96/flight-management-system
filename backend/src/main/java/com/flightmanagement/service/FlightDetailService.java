package com.flightmanagement.service;

import com.flightmanagement.dto.FlightDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FlightDetailService {
    
    List<FlightDetailDto> getAllFlightDetails();

    Page<FlightDetailDto> getAllFlightDetailsPaged(Pageable pageable);
    
    List<FlightDetailDto> getFlightDetailsByFlightId(Integer flightId);
    
    List<FlightDetailDto> getFlightDetailsByAirportId(Integer airportId);
    
    FlightDetailDto createFlightDetail(FlightDetailDto flightDetailDto);
    
    FlightDetailDto updateFlightDetail(Integer flightId, Integer mediumAirportId, FlightDetailDto flightDetailDto);
    
    void deleteFlightDetail(Integer flightId, Integer mediumAirportId);
}
