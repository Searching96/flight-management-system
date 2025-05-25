package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.mapper.FlightTicketClassMapper;
import com.flightmanagement.repository.FlightTicketClassRepository;
import com.flightmanagement.service.FlightTicketClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightTicketClassServiceImpl implements FlightTicketClassService {
    
    @Autowired
    private FlightTicketClassRepository flightTicketClassRepository;
    
    @Autowired
    private FlightTicketClassMapper flightTicketClassMapper;
    
    @Override
    public List<FlightTicketClassDto> getAllFlightTicketClasses() {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findAllActive();
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }
    
    @Override
    public FlightTicketClassDto getFlightTicketClassById(Integer flightId, Integer ticketClassId) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found"));
        return flightTicketClassMapper.toDto(flightTicketClass);
    }
    
    @Override
    public FlightTicketClassDto createFlightTicketClass(FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClass flightTicketClass = flightTicketClassMapper.toEntity(flightTicketClassDto);
        flightTicketClass.setDeletedAt(null);
        FlightTicketClass savedFlightTicketClass = flightTicketClassRepository.save(flightTicketClass);
        return flightTicketClassMapper.toDto(savedFlightTicketClass);
    }
    
    @Override
    public FlightTicketClassDto updateFlightTicketClass(Integer flightId, Integer ticketClassId, FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClass existingFlightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found"));
        
        existingFlightTicketClass.setTicketQuantity(flightTicketClassDto.getTicketQuantity());
        existingFlightTicketClass.setRemainingTicketQuantity(flightTicketClassDto.getRemainingTicketQuantity());
        existingFlightTicketClass.setSpecifiedFare(flightTicketClassDto.getSpecifiedFare());
        
        FlightTicketClass updatedFlightTicketClass = flightTicketClassRepository.save(existingFlightTicketClass);
        return flightTicketClassMapper.toDto(updatedFlightTicketClass);
    }
    
    @Override
    public void deleteFlightTicketClass(Integer flightId, Integer ticketClassId) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found"));
        
        flightTicketClass.setDeletedAt(LocalDateTime.now());
        flightTicketClassRepository.save(flightTicketClass);
    }
    
    @Override
    public List<FlightTicketClassDto> getFlightTicketClassesByFlightId(Integer flightId) {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findByFlightId(flightId);
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }
    
    @Override
    public List<FlightTicketClassDto> getFlightTicketClassesByTicketClassId(Integer ticketClassId) {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findByTicketClassId(ticketClassId);
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }
    
    @Override
    public List<FlightTicketClassDto> getAvailableFlightTicketClasses() {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findAvailableTickets();
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }
    
    @Override
    public void updateRemainingTickets(Integer flightId, Integer ticketClassId, Integer quantity) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
            .orElseThrow(() -> new RuntimeException("FlightTicketClass not found"));
        
        int newRemaining = flightTicketClass.getRemainingTicketQuantity() - quantity;
        if (newRemaining < 0) {
            throw new RuntimeException("Not enough tickets available");
        }
        
        flightTicketClass.setRemainingTicketQuantity(newRemaining);
        flightTicketClassRepository.save(flightTicketClass);
    }
}
