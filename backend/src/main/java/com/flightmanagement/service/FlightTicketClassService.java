package com.flightmanagement.service;

import com.flightmanagement.dto.FlightTicketClassDto;

import java.util.List;

public interface FlightTicketClassService {
    
    List<FlightTicketClassDto> getAllFlightTicketClasses();
    
    FlightTicketClassDto getFlightTicketClassById(Integer flightId, Integer ticketClassId);
    
    FlightTicketClassDto createFlightTicketClass(FlightTicketClassDto flightTicketClassDto);
    
    FlightTicketClassDto updateFlightTicketClass(Integer flightId, Integer ticketClassId, FlightTicketClassDto flightTicketClassDto);
    
    void deleteFlightTicketClass(Integer flightId, Integer ticketClassId);
    
    List<FlightTicketClassDto> getFlightTicketClassesByFlightId(Integer flightId);
    
    List<FlightTicketClassDto> getFlightTicketClassesByTicketClassId(Integer ticketClassId);
    
    List<FlightTicketClassDto> getAvailableFlightTicketClasses();
    
    void updateRemainingTickets(Integer flightId, Integer ticketClassId, Integer quantity);
}
