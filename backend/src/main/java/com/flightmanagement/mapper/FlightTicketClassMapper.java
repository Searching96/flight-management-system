package com.flightmanagement.mapper;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.FlightTicketClass;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightTicketClassMapper {
    
    public FlightTicketClassDto toDto(FlightTicketClass entity) {
        if (entity == null) {
            return null;
        }
        
        FlightTicketClassDto dto = new FlightTicketClassDto();
        dto.setFlightId(entity.getFlightId());
        dto.setTicketClassId(entity.getTicketClassId());
        dto.setTicketQuantity(entity.getTicketQuantity());
        dto.setRemainingTicketQuantity(entity.getRemainingTicketQuantity());
        dto.setSpecifiedFare(entity.getSpecifiedFare());
        
        // Include related entity information
        if (entity.getTicketClass() != null) {
            dto.setTicketClassName(entity.getTicketClass().getTicketClassName());
            dto.setColor(entity.getTicketClass().getColor());
        }
        
        if (entity.getFlight() != null) {
            dto.setFlightCode(entity.getFlight().getFlightCode());
        }
        
        // Set availability flag
        dto.setIsAvailable(entity.getRemainingTicketQuantity() > 0);
        
        return dto;
    }
    
    public FlightTicketClass toEntity(FlightTicketClassDto dto) {
        if (dto == null) {
            return null;
        }
        
        FlightTicketClass entity = new FlightTicketClass();
        entity.setFlightId(dto.getFlightId());
        entity.setTicketClassId(dto.getTicketClassId());
        entity.setTicketQuantity(dto.getTicketQuantity());
        entity.setRemainingTicketQuantity(dto.getRemainingTicketQuantity());
        entity.setSpecifiedFare(dto.getSpecifiedFare());
        
        return entity;
    }
    
    public List<FlightTicketClassDto> toDtoList(List<FlightTicketClass> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<FlightTicketClass> toEntityList(List<FlightTicketClassDto> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}