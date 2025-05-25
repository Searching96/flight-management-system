package com.flightmanagement.mapper;

import com.flightmanagement.dto.PlaneDto;
import com.flightmanagement.entity.Plane;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaneMapper implements BaseMapper<Plane, PlaneDto> {
    
    @Override
    public PlaneDto toDto(Plane entity) {
        if (entity == null) return null;
        
        return new PlaneDto(
            entity.getPlaneId(),
            entity.getPlaneCode(),
            entity.getPlaneType(),
            entity.getSeatQuantity()
        );
    }
    
    @Override
    public Plane toEntity(PlaneDto dto) {
        if (dto == null) return null;
        
        Plane entity = new Plane();
        entity.setPlaneId(dto.getPlaneId());
        entity.setPlaneCode(dto.getPlaneCode());
        entity.setPlaneType(dto.getPlaneType());
        entity.setSeatQuantity(dto.getSeatQuantity());
        return entity;
    }
    
    @Override
    public List<PlaneDto> toDtoList(List<Plane> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Plane> toEntityList(List<PlaneDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
