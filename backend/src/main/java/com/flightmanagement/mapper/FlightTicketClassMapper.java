package com.flightmanagement.mapper;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.FlightTicketClass;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightTicketClassMapper implements BaseMapper<FlightTicketClass, FlightTicketClassDto> {
    
    @Override
    public FlightTicketClassDto toDto(FlightTicketClass entity) {
        if (entity == null) return null;
        
        FlightTicketClassDto dto = new FlightTicketClassDto();
        dto.setFlightId(entity.getFlightId());
        dto.setTicketClassId(entity.getTicketClassId());
        dto.setTicketQuantity(entity.getTicketQuantity());
        dto.setRemainingTicketQuantity(entity.getRemainingTicketQuantity());
        dto.setSpecifiedFare(entity.getSpecifiedFare());
        dto.setIsAvailable(entity.getRemainingTicketQuantity() > 0);
        
        if (entity.getTicketClass() != null) {
            dto.setTicketClassName(entity.getTicketClass().getTicketClassName());
            dto.setColor(entity.getTicketClass().getColor());
        }
        
        return dto;
    }

    @Override
    public FlightTicketClass toEntity(FlightTicketClassDto dto) {
        if (dto == null) return null;
        
        FlightTicketClass entity = new FlightTicketClass();
        entity.setFlightId(dto.getFlightId());
        entity.setTicketClassId(dto.getTicketClassId());
        entity.setTicketQuantity(dto.getTicketQuantity());
        entity.setRemainingTicketQuantity(dto.getRemainingTicketQuantity());
        entity.setSpecifiedFare(dto.getSpecifiedFare());
        return entity;
    }
    
    @Override
    public List<FlightTicketClassDto> toDtoList(List<FlightTicketClass> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<FlightTicketClass> toEntityList(List<FlightTicketClassDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}