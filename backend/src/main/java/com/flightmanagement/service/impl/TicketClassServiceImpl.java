package com.flightmanagement.service.impl;

import com.flightmanagement.dto.TicketClassDto;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.mapper.TicketClassMapper;
import com.flightmanagement.repository.TicketClassRepository;
import com.flightmanagement.service.TicketClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketClassServiceImpl implements TicketClassService {
    
    @Autowired
    private TicketClassRepository ticketClassRepository;
    
    @Autowired
    private TicketClassMapper ticketClassMapper;
    
    @Override
    public List<TicketClassDto> getAllTicketClasses() {
        List<TicketClass> ticketClasses = ticketClassRepository.findAllActive();
        return ticketClassMapper.toDtoList(ticketClasses);
    }
    
    @Override
    public TicketClassDto getTicketClassById(Integer id) {
        TicketClass ticketClass = ticketClassRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + id));
        return ticketClassMapper.toDto(ticketClass);
    }
    
    @Override
    public TicketClassDto createTicketClass(TicketClassDto ticketClassDto) {
        TicketClass ticketClass = ticketClassMapper.toEntity(ticketClassDto);
        ticketClass.setDeletedAt(null);
        TicketClass savedTicketClass = ticketClassRepository.save(ticketClass);
        return ticketClassMapper.toDto(savedTicketClass);
    }
    
    @Override
    public TicketClassDto updateTicketClass(Integer id, TicketClassDto ticketClassDto) {
        TicketClass existingTicketClass = ticketClassRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + id));
        
        existingTicketClass.setTicketClassName(ticketClassDto.getTicketClassName());
        existingTicketClass.setColor(ticketClassDto.getColor());
        
        TicketClass updatedTicketClass = ticketClassRepository.save(existingTicketClass);
        return ticketClassMapper.toDto(updatedTicketClass);
    }
    
    @Override
    public void deleteTicketClass(Integer id) {
        TicketClass ticketClass = ticketClassRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + id));
        
        ticketClass.setDeletedAt(LocalDateTime.now());
        ticketClassRepository.save(ticketClass);
    }
    
    @Override
    public TicketClassDto getTicketClassByName(String ticketClassName) {
        TicketClass ticketClass = ticketClassRepository.findByTicketClassName(ticketClassName)
            .orElseThrow(() -> new RuntimeException("TicketClass not found with name: " + ticketClassName));
        return ticketClassMapper.toDto(ticketClass);
    }

    
}
