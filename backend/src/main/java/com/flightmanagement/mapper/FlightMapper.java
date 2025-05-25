package com.flightmanagement.mapper;

import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.entity.Flight;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper implements BaseMapper<Flight, FlightDto> {
    
    @Override
    public FlightDto toDto(Flight entity) {
        if (entity == null) return null;
        
        FlightDto dto = new FlightDto();
        dto.setFlightId(entity.getFlightId());
        dto.setFlightCode(entity.getFlightCode());
        dto.setDepartureTime(entity.getDepartureTime());
        dto.setArrivalTime(entity.getArrivalTime());
        
        if (entity.getPlane() != null) {
            dto.setPlaneId(entity.getPlane().getPlaneId());
            dto.setPlaneCode(entity.getPlane().getPlaneCode());
        }
        
        if (entity.getDepartureAirport() != null) {
            dto.setDepartureAirportId(entity.getDepartureAirport().getAirportId());
            dto.setDepartureAirportName(entity.getDepartureAirport().getAirportName());
            dto.setDepartureCityName(entity.getDepartureAirport().getCityName());
        }
        
        if (entity.getArrivalAirport() != null) {
            dto.setArrivalAirportId(entity.getArrivalAirport().getAirportId());
            dto.setArrivalAirportName(entity.getArrivalAirport().getAirportName());
            dto.setArrivalCityName(entity.getArrivalAirport().getCityName());
        }
        
        return dto;
    }
    
    @Override
    public Flight toEntity(FlightDto dto) {
        if (dto == null) return null;
        
        Flight entity = new Flight();
        entity.setFlightId(dto.getFlightId());
        entity.setFlightCode(dto.getFlightCode());
        entity.setDepartureTime(dto.getDepartureTime());
        entity.setArrivalTime(dto.getArrivalTime());
        return entity;
    }
    
    @Override
    public List<FlightDto> toDtoList(List<Flight> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Flight> toEntityList(List<FlightDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
