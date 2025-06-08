package com.flightmanagement.service;

import com.flightmanagement.dto.FlightTicketClassDto;

import java.util.List;

public interface FlightTicketClassService {
    List<FlightTicketClassDto> getAllFlightTicketClasses();
    FlightTicketClassDto getFlightTicketClassById(Integer flightId, Integer ticketClassId);
    List<FlightTicketClassDto> getFlightTicketClassesByFlightId(Integer flightId);
    FlightTicketClassDto createFlightTicketClass(FlightTicketClassDto flightTicketClassDto);
    FlightTicketClassDto updateFlightTicketClass(Integer flightId, Integer ticketClassId, FlightTicketClassDto flightTicketClassDto);
    void deleteFlightTicketClass(Integer flightId, Integer ticketClassId);
    void updateRemainingTickets(Integer flightId, Integer ticketClassId, Integer quantity);
    List<FlightTicketClassDto> getAvailableFlightTicketClasses();
    Integer calculateOccupiedSeatsByFlightIdAndTicketClassId(Integer flightId, Integer ticketClassId);
}
