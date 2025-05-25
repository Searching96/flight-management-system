package com.flightmanagement.service;

import com.flightmanagement.dto.TicketClassDto;

import java.util.List;

public interface TicketClassService {
    
    List<TicketClassDto> getAllTicketClasses();
    
    TicketClassDto getTicketClassById(Integer id);
    
    TicketClassDto createTicketClass(TicketClassDto ticketClassDto);
    
    TicketClassDto updateTicketClass(Integer id, TicketClassDto ticketClassDto);
    
    void deleteTicketClass(Integer id);
    
    TicketClassDto getTicketClassByName(String ticketClassName);
}
