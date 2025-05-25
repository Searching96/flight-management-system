package com.flightmanagement.mapper;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightDetailMapper implements BaseMapper<FlightDetail, FlightDetailDto> {
    
    @Override
    public FlightDetailDto toDto(FlightDetail entity) {
        if (entity == null) return null;
        
        FlightDetailDto dto = new FlightDetailDto();
        dto.setFlightId(entity.getFlightId());
        dto.setMediumAirportId(entity.getMediumAirportId());
        dto.setArrivalTime(entity.getArrivalTime());
        dto.setLayoverDuration(entity.getLayoverDuration());
        
        if (entity.getMediumAirport() != null) {
            dto.setMediumAirportName(entity.getMediumAirport().getAirportName());
            dto.setMediumCityName(entity.getMediumAirport().getCityName());
        }
        
        return dto;
    }
    
    @Override
    public FlightDetail toEntity(FlightDetailDto dto) {
        if (dto == null) return null;
        
        FlightDetail entity = new FlightDetail();
        entity.setFlightId(dto.getFlightId());
        entity.setMediumAirportId(dto.getMediumAirportId());
        entity.setArrivalTime(dto.getArrivalTime());
        entity.setLayoverDuration(dto.getLayoverDuration());
        return entity;
    }
    
    @Override
    public List<FlightDetailDto> toDtoList(List<FlightDetail> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<FlightDetail> toEntityList(List<FlightDetailDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
