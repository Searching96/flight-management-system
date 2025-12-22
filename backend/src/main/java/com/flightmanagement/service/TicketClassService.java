package com.flightmanagement.service;

import com.flightmanagement.dto.TicketClassDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketClassService {
    
    List<TicketClassDto> getAllTicketClasses();

    Page<TicketClassDto> getAllTicketClassesPaged(Pageable pageable);
    
    TicketClassDto getTicketClassById(Integer id);
    
    TicketClassDto createTicketClass(TicketClassDto ticketClassDto);
    
    TicketClassDto updateTicketClass(Integer id, TicketClassDto ticketClassDto);
    
    void deleteTicketClass(Integer id);
    
    TicketClassDto getTicketClassByName(String ticketClassName);

}
