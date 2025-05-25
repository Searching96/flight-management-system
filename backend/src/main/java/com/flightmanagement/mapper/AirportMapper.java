package com.flightmanagement.mapper;

import com.flightmanagement.dto.AirportDto;
import com.flightmanagement.entity.Airport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AirportMapper implements BaseMapper<Airport, AirportDto> {
    
    @Override
    public AirportDto toDto(Airport entity) {
        if (entity == null) return null;
        
        return new AirportDto(
            entity.getAirportId(),
            entity.getAirportName(),
            entity.getCityName(),
            entity.getCountryName()
        );
    }
    
    @Override
    public Airport toEntity(AirportDto dto) {
        if (dto == null) return null;
        
        Airport entity = new Airport();
        entity.setAirportId(dto.getAirportId());
        entity.setAirportName(dto.getAirportName());
        entity.setCityName(dto.getCityName());
        entity.setCountryName(dto.getCountryName());
        return entity;
    }
    
    @Override
    public List<AirportDto> toDtoList(List<Airport> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Airport> toEntityList(List<AirportDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
