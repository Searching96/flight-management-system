package com.flightmanagement.service.impl;

import com.flightmanagement.dto.TicketClassDto;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.mapper.TicketClassMapper;
import com.flightmanagement.repository.TicketClassRepository;
import com.flightmanagement.service.AuditLogService;
import com.flightmanagement.service.TicketClassService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketClassServiceImpl implements TicketClassService {
    
    private final TicketClassRepository ticketClassRepository;
    
    private final TicketClassMapper ticketClassMapper;

    private final AuditLogService auditLogService;

    public TicketClassServiceImpl(TicketClassRepository ticketClassRepository, TicketClassMapper ticketClassMapper, AuditLogService auditLogService) {
        this.ticketClassRepository = ticketClassRepository;
        this.ticketClassMapper = ticketClassMapper;
        this.auditLogService = auditLogService;
    }
    
    @Override
    public List<TicketClassDto> getAllTicketClasses() {
        List<TicketClass> ticketClasses = ticketClassRepository.findAllActive();
        return ticketClassMapper.toDtoList(ticketClasses);
    }

    @Override
    public Page<TicketClassDto> getAllTicketClassesPaged(Pageable pageable) {
        Page<TicketClass> page = ticketClassRepository.findByDeletedAtIsNull(pageable);
        return page.map(ticketClassMapper::toDto);
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
        
        // Audit log for CREATE
        auditLogService.saveAuditLog("TicketClass", savedTicketClass.getTicketClassId().toString(), "CREATE", "ticketClass", null, savedTicketClass.getTicketClassName(), "system");
        
        return ticketClassMapper.toDto(savedTicketClass);
    }
    
    @Override
    public TicketClassDto updateTicketClass(Integer id, TicketClassDto ticketClassDto) {
        TicketClass existingTicketClass = ticketClassRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + id));
        
        // Store old values for audit logging
        String oldTicketClassName = existingTicketClass.getTicketClassName();
        String oldColor = existingTicketClass.getColor();
        
        existingTicketClass.setTicketClassName(ticketClassDto.getTicketClassName());
        existingTicketClass.setColor(ticketClassDto.getColor());
        
        TicketClass updatedTicketClass = ticketClassRepository.save(existingTicketClass);
        
        // Audit log for changed fields
        if ((oldTicketClassName == null && updatedTicketClass.getTicketClassName() != null) || (oldTicketClassName != null && !oldTicketClassName.equals(updatedTicketClass.getTicketClassName()))) {
            auditLogService.saveAuditLog("TicketClass", id.toString(), "UPDATE", "ticketClassName", oldTicketClassName, updatedTicketClass.getTicketClassName(), "system");
        }
        if ((oldColor == null && updatedTicketClass.getColor() != null) || (oldColor != null && !oldColor.equals(updatedTicketClass.getColor()))) {
            auditLogService.saveAuditLog("TicketClass", id.toString(), "UPDATE", "color", oldColor, updatedTicketClass.getColor(), "system");
        }
        
        return ticketClassMapper.toDto(updatedTicketClass);
    }
    
    @Override
    public void deleteTicketClass(Integer id) {
        TicketClass ticketClass = ticketClassRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + id));
        
        // Capture entity info before delete
        String ticketClassDescription = ticketClass.getTicketClassName();
        
        ticketClass.setDeletedAt(LocalDateTime.now());
        ticketClassRepository.save(ticketClass);
        
        // Audit log for DELETE
        auditLogService.saveAuditLog("TicketClass", id.toString(), "DELETE", "ticketClass", ticketClassDescription, null, "system");
    }
    
    @Override
    public TicketClassDto getTicketClassByName(String ticketClassName) {
        TicketClass ticketClass = ticketClassRepository.findByTicketClassName(ticketClassName)
            .orElseThrow(() -> new RuntimeException("TicketClass not found with name: " + ticketClassName));
        return ticketClassMapper.toDto(ticketClass);
    }
}