package com.flightmanagement.service;

import com.flightmanagement.dto.BookingDto;
import com.flightmanagement.dto.TicketDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TicketService {
    
    List<TicketDto> getAllTickets();
    
    TicketDto getTicketById(Integer id);
    
    TicketDto createTicket(TicketDto ticketDto);
    
    TicketDto updateTicket(Integer id, TicketDto ticketDto);
    
    void    deleteTicket(Integer id);

    List<TicketDto> getTicketsByFlightId(Integer flightId);
    
    List<TicketDto> getTicketsByCustomerId(Integer customerId);
    
    List<TicketDto> getTicketsByPassengerId(Integer passengerId);
    
    List<TicketDto> getTicketsByStatus(Byte ticketStatus);
    
    List<TicketDto> bookTickets(BookingDto bookingDto);
    
    TicketDto payTicket(Integer ticketId, String orderId);
    
    void cancelTicket(Integer ticketId);
    
    boolean isSeatAvailable(Integer flightId, String seatNumber);

    String generateConfirmationCode();

    List<TicketDto> getTicketsOnConfirmationCode(String code);
}
