package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.mapper.FlightTicketClassMapper;
import com.flightmanagement.repository.FlightTicketClassRepository;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.repository.TicketClassRepository;
import com.flightmanagement.service.FlightTicketClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FlightTicketClassServiceImpl implements FlightTicketClassService {
    
    @Autowired
    private FlightTicketClassRepository flightTicketClassRepository;
    
    @Autowired
    private FlightTicketClassMapper flightTicketClassMapper;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private TicketClassRepository ticketClassRepository;
    
    @Override
    public List<FlightTicketClassDto> getAllFlightTicketClasses() {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findAllActive();
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }
    
    @Override
    public FlightTicketClassDto getFlightTicketClassById(Integer flightId, Integer ticketClassId) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));
        return flightTicketClassMapper.toDto(flightTicketClass);
    }
    
    @Override
    public List<FlightTicketClassDto> getFlightTicketClassesByFlightId(Integer flightId) {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findByFlightId(flightId);
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }
    
    @Override
    public FlightTicketClassDto createFlightTicketClass(FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClass flightTicketClass = new FlightTicketClass();
        flightTicketClass.setSpecifiedFare(flightTicketClassDto.getSpecifiedFare());
        flightTicketClass.setTicketQuantity(flightTicketClassDto.getTicketQuantity());
        flightTicketClass.setRemainingTicketQuantity(flightTicketClassDto.getRemainingTicketQuantity());
        flightTicketClass.setDeletedAt(null);
        
        // Set entity relationships
        if (flightTicketClassDto.getFlightId() != null) {
            Flight flight = flightRepository.findById(flightTicketClassDto.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightTicketClassDto.getFlightId()));
            flightTicketClass.setFlight(flight);
            flightTicketClass.setFlightId(flight.getFlightId());
        }
        
        if (flightTicketClassDto.getTicketClassId() != null) {
            TicketClass ticketClass = ticketClassRepository.findById(flightTicketClassDto.getTicketClassId())
                .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + flightTicketClassDto.getTicketClassId()));
            flightTicketClass.setTicketClass(ticketClass);
            flightTicketClass.setTicketClassId(ticketClass.getTicketClassId());
        }
        
        FlightTicketClass savedFlightTicketClass = flightTicketClassRepository.save(flightTicketClass);
        return flightTicketClassMapper.toDto(savedFlightTicketClass);
    }
    
    @Override
    public FlightTicketClassDto updateFlightTicketClass(Integer flightId, Integer ticketClassId, FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClass existingFlightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));
        
        existingFlightTicketClass.setSpecifiedFare(flightTicketClassDto.getSpecifiedFare());
        existingFlightTicketClass.setTicketQuantity(flightTicketClassDto.getTicketQuantity());
        existingFlightTicketClass.setRemainingTicketQuantity(flightTicketClassDto.getRemainingTicketQuantity());
        
        FlightTicketClass updatedFlightTicketClass = flightTicketClassRepository.save(existingFlightTicketClass);
        return flightTicketClassMapper.toDto(updatedFlightTicketClass);
    }
    
    @Override
    public void deleteFlightTicketClass(Integer flightId, Integer ticketClassId) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));
        
        flightTicketClass.setDeletedAt(LocalDateTime.now());
        flightTicketClassRepository.save(flightTicketClass);
    }
    
    @Override
    public void updateRemainingTickets(Integer flightId, Integer ticketClassId, Integer quantity) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));
        
        int newRemainingQuantity = flightTicketClass.getRemainingTicketQuantity() - quantity;
        if (newRemainingQuantity < 0) {
            throw new RuntimeException("Not enough tickets available. Requested: " + quantity + 
                                     ", Available: " + flightTicketClass.getRemainingTicketQuantity());
        }
        
        flightTicketClass.setRemainingTicketQuantity(newRemainingQuantity);
        flightTicketClassRepository.save(flightTicketClass);
    }
    
    @Override
    public List<FlightTicketClassDto> getAvailableFlightTicketClasses() {
        List<FlightTicketClass> available = flightTicketClassRepository.findAvailable();
        return flightTicketClassMapper.toDtoList(available);
    }

    @Override
    public Integer calculateOccupiedSeatsByFlightIdAndTicketClassId(Integer flightId, Integer ticketClassId) {
        Integer occupiedSeats = flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(flightId, ticketClassId);
        System.out.println("Occupied seats for flightId: " + flightId + ", ticketClassId: " + ticketClassId + " is " + occupiedSeats);
        if (occupiedSeats == null) {
            throw new RuntimeException(
                    "No occupied seats found for flightId: " + flightId + " and ticketClassId: " + ticketClassId);
        }
        return occupiedSeats;
    }
}
