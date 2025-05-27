package com.flightmanagement.mapper;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.Passenger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PassengerMapper {
    
    public PassengerDto toDto(Passenger entity) {
        if (entity == null) {
            return null;
        }
        
        PassengerDto dto = new PassengerDto();
        dto.setPassengerId(entity.getPassengerId());
        dto.setPassengerName(entity.getPassengerName());
        dto.setEmail(entity.getEmail());
        dto.setCitizenId(entity.getCitizenId());
        
        return dto;
    }
    
    public Passenger toEntity(PassengerDto dto) {
        if (dto == null) {
            return null;
        }
        
        Passenger entity = new Passenger();
        entity.setPassengerId(dto.getPassengerId());
        entity.setPassengerName(dto.getPassengerName());
        entity.setEmail(dto.getEmail());
        entity.setCitizenId(dto.getCitizenId());
        entity.setPhoneNumber(dto.getPhoneNumber());
        return entity;
    }
    
    public List<PassengerDto> toDtoList(List<Passenger> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<Passenger> toEntityList(List<PassengerDto> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
