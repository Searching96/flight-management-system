package com.flightmanagement.mapper;

import com.flightmanagement.dto.TicketClassDto;
import com.flightmanagement.entity.TicketClass;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketClassMapper implements BaseMapper<TicketClass, TicketClassDto> {
    
    @Override
    public TicketClassDto toDto(TicketClass entity) {
        if (entity == null) return null;
        
        return new TicketClassDto(
            entity.getTicketClassId(),
            entity.getTicketClassName(),
            entity.getColor()
        );
    }
    
    @Override
    public TicketClass toEntity(TicketClassDto dto) {
        if (dto == null) return null;
        
        TicketClass entity = new TicketClass();
        entity.setTicketClassId(dto.getTicketClassId());
        entity.setTicketClassName(dto.getTicketClassName());
        entity.setColor(dto.getColor());
        return entity;
    }
    
    @Override
    public List<TicketClassDto> toDtoList(List<TicketClass> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<TicketClass> toEntityList(List<TicketClassDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
