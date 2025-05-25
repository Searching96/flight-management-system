package com.flightmanagement.mapper;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.entity.Parameter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParameterMapper implements BaseMapper<Parameter, ParameterDto> {
    
    @Override
    public ParameterDto toDto(Parameter entity) {
        if (entity == null) return null;
        
        return new ParameterDto(
            entity.getId(),
            entity.getMaxMediumAirport(),
            entity.getMinFlightDuration(),
            entity.getMinLayoverDuration(),
            entity.getMaxLayoverDuration(),
            entity.getMinBookingInAdvanceDuration(),
            entity.getMaxBookingHoldDuration()
        );
    }
    
    @Override
    public Parameter toEntity(ParameterDto dto) {
        if (dto == null) return null;
        
        Parameter entity = new Parameter();
        entity.setId(dto.getId());
        entity.setMaxMediumAirport(dto.getMaxMediumAirport());
        entity.setMinFlightDuration(dto.getMinFlightDuration());
        entity.setMinLayoverDuration(dto.getMinLayoverDuration());
        entity.setMaxLayoverDuration(dto.getMaxLayoverDuration());
        entity.setMinBookingInAdvanceDuration(dto.getMinBookingInAdvanceDuration());
        entity.setMaxBookingHoldDuration(dto.getMaxBookingHoldDuration());
        return entity;
    }
    
    @Override
    public List<ParameterDto> toDtoList(List<Parameter> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Parameter> toEntityList(List<ParameterDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
