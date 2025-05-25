package com.flightmanagement.mapper;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.Passenger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PassengerMapper implements BaseMapper<Passenger, PassengerDto> {
    
    @Override
    public PassengerDto toDto(Passenger entity) {
        if (entity == null) return null;
        
        return new PassengerDto(
            entity.getPassengerId(),
            entity.getPassengerName(),
            entity.getEmail(),
            entity.getCitizenId(),
            entity.getPhoneNumber()
        );
    }
    
    @Override
    public Passenger toEntity(PassengerDto dto) {
        if (dto == null) return null;
        
        Passenger entity = new Passenger();
        entity.setPassengerId(dto.getPassengerId());
        entity.setPassengerName(dto.getPassengerName());
        entity.setEmail(dto.getEmail());
        entity.setCitizenId(dto.getCitizenId());
        entity.setPhoneNumber(dto.getPhoneNumber());
        return entity;
    }
    
    @Override
    public List<PassengerDto> toDtoList(List<Passenger> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Passenger> toEntityList(List<PassengerDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
