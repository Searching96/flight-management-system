package com.flightmanagement.service.impl;

import com.flightmanagement.dto.BookingDto;
import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.entity.Ticket;
import com.flightmanagement.mapper.TicketMapper;
import com.flightmanagement.repository.TicketRepository;
import com.flightmanagement.service.FlightTicketClassService;
import com.flightmanagement.service.PassengerService;
import com.flightmanagement.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketMapper ticketMapper;
    
    @Autowired
    private FlightTicketClassService flightTicketClassService;
    
    @Autowired
    private PassengerService passengerService;
    
    @Override
    public List<TicketDto> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAllActive();
        return ticketMapper.toDtoList(tickets);
    }
    
    @Override
    public TicketDto getTicketById(Integer id) {
        Ticket ticket = ticketRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        return ticketMapper.toDto(ticket);
    }
    
    @Override
    public TicketDto createTicket(TicketDto ticketDto) {
        Ticket ticket = ticketMapper.toEntity(ticketDto);
        ticket.setDeletedAt(null);
        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }
    
    @Override
    public TicketDto updateTicket(Integer id, TicketDto ticketDto) {
        Ticket existingTicket = ticketRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        
        existingTicket.setSeatNumber(ticketDto.getSeatNumber());
        existingTicket.setTicketStatus(ticketDto.getTicketStatus());
        existingTicket.setFare(ticketDto.getFare());
        
        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return ticketMapper.toDto(updatedTicket);
    }
    
    @Override
    public void deleteTicket(Integer id) {
        Ticket ticket = ticketRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        
        ticket.setDeletedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }
    
    @Override
    public List<TicketDto> getTicketsByFlightId(Integer flightId) {
        List<Ticket> tickets = ticketRepository.findByFlightId(flightId);
        return ticketMapper.toDtoList(tickets);
    }
    
    @Override
    public List<TicketDto> getTicketsByCustomerId(Integer customerId) {
        List<Ticket> tickets = ticketRepository.findByCustomerId(customerId);
        return ticketMapper.toDtoList(tickets);
    }
    
    @Override
    public List<TicketDto> getTicketsByPassengerId(Integer passengerId) {
        List<Ticket> tickets = ticketRepository.findByPassengerId(passengerId);
        return ticketMapper.toDtoList(tickets);
    }
    
    @Override
    public List<TicketDto> getTicketsByStatus(Byte ticketStatus) {
        List<Ticket> tickets = ticketRepository.findByTicketStatus(ticketStatus);
        return ticketMapper.toDtoList(tickets);
    }
    
    @Override
    @Transactional
    public List<TicketDto> bookTickets(BookingDto bookingDto) {
        validateBookingRequest(bookingDto);
        
        List<TicketDto> bookedTickets = new ArrayList<>();
        
        // Check availability first
        if (!isFlightAvailable(bookingDto.getFlightId(), bookingDto.getTicketClassId(), bookingDto.getPassengers().size())) {
            throw new RuntimeException("Not enough tickets available for this flight and class");
        }
        
        // Create tickets for each passenger
        for (int i = 0; i < bookingDto.getPassengers().size(); i++) {
            PassengerDto passengerDto = bookingDto.getPassengers().get(i);
            String seatNumber = bookingDto.getSeatNumbers().get(i);
            
            // Ensure passenger exists or create new one
            PassengerDto existingPassenger = getOrCreatePassenger(passengerDto);
            
            // Create ticket
            TicketDto ticketDto = new TicketDto();
            ticketDto.setFlightId(bookingDto.getFlightId());
            ticketDto.setTicketClassId(bookingDto.getTicketClassId());
            ticketDto.setBookCustomerId(bookingDto.getCustomerId());
            ticketDto.setPassengerId(existingPassenger.getPassengerId());
            ticketDto.setSeatNumber(seatNumber);
            ticketDto.setFare(calculateFare(bookingDto.getFlightId(), bookingDto.getTicketClassId()));
            ticketDto.setTicketStatus((byte) 0); // 0: booked, not paid
            
            TicketDto createdTicket = createTicket(ticketDto);
            bookedTickets.add(createdTicket);
        }
        
        // Update remaining ticket quantity
        flightTicketClassService.updateRemainingTickets(
            bookingDto.getFlightId(), 
            bookingDto.getTicketClassId(), 
            bookingDto.getPassengers().size()
        );
        
        return bookedTickets;
    }
    
    private void validateBookingRequest(BookingDto bookingDto) {
        if (bookingDto.getPassengers() == null || bookingDto.getPassengers().isEmpty()) {
            throw new IllegalArgumentException("At least one passenger is required");
        }
        
        if (bookingDto.getSeatNumbers() == null || 
            bookingDto.getSeatNumbers().size() != bookingDto.getPassengers().size()) {
            throw new IllegalArgumentException("Number of seat numbers must match number of passengers");
        }
        
        // Check seat availability
        for (String seatNumber : bookingDto.getSeatNumbers()) {
            if (!isSeatAvailable(bookingDto.getFlightId(), seatNumber)) {
                throw new IllegalArgumentException("Seat " + seatNumber + " is already taken");
            }
        }
    }
    
    private boolean isFlightAvailable(Integer flightId, Integer ticketClassId, int passengerCount) {
        try {
            FlightTicketClassDto flightTicketClass = flightTicketClassService.getFlightTicketClassById(flightId, ticketClassId);
            return flightTicketClass.getRemainingTicketQuantity() >= passengerCount;
        } catch (RuntimeException e) {
            return false;
        }
    }
    
    private PassengerDto getOrCreatePassenger(PassengerDto passengerDto) {
        try {
            return passengerService.getPassengerByCitizenId(passengerDto.getCitizenId());
        } catch (RuntimeException e) {
            return passengerService.createPassenger(passengerDto);
        }
    }
    
    private BigDecimal calculateFare(Integer flightId, Integer ticketClassId) {
        FlightTicketClassDto flightTicketClass = flightTicketClassService.getFlightTicketClassById(flightId, ticketClassId);
        return flightTicketClass.getSpecifiedFare();
    }
    
    @Override
    public TicketDto payTicket(Integer ticketId) {
        Ticket ticket = ticketRepository.findActiveById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        
        ticket.setTicketStatus((byte) 1); // 1: paid
        ticket.setPaymentTime(LocalDateTime.now());
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }
    
    @Override
    public void cancelTicket(Integer ticketId) {
        Ticket ticket = ticketRepository.findActiveById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        
        ticket.setTicketStatus((byte) 3); // 3: canceled
        ticketRepository.save(ticket);
    }
    
    @Override
    public boolean isSeatAvailable(Integer flightId, String seatNumber) {
        return ticketRepository.findByFlightIdAndSeatNumber(flightId, seatNumber).isEmpty();
    }
}
