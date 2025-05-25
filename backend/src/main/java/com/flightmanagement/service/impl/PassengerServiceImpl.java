package com.flightmanagement.service.impl;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.Passenger;
import com.flightmanagement.mapper.PassengerMapper;
import com.flightmanagement.repository.PassengerRepository;
import com.flightmanagement.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private PassengerMapper passengerMapper;
    
    @Override
    public List<PassengerDto> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        return passengerMapper.toDtoList(passengers);
    }
    
    @Override
    public PassengerDto getPassengerById(Integer id) {
        Passenger passenger = passengerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        return passengerMapper.toDto(passenger);
    }
    
    @Override
    public PassengerDto createPassenger(PassengerDto passengerDto) {
        Passenger passenger = passengerMapper.toEntity(passengerDto);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toDto(savedPassenger);
    }
    
    @Override
    public PassengerDto updatePassenger(Integer id, PassengerDto passengerDto) {
        Passenger existingPassenger = passengerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        
        existingPassenger.setPassengerName(passengerDto.getPassengerName());
        existingPassenger.setEmail(passengerDto.getEmail());
        existingPassenger.setCitizenId(passengerDto.getCitizenId());
        existingPassenger.setPhoneNumber(passengerDto.getPhoneNumber());
        
        Passenger updatedPassenger = passengerRepository.save(existingPassenger);
        return passengerMapper.toDto(updatedPassenger);
    }
    
    @Override
    public void deletePassenger(Integer id) {
        Passenger passenger = passengerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        
        passengerRepository.delete(passenger);
    }
    
    @Override
    public PassengerDto getPassengerByCitizenId(String citizenId) {
        Passenger passenger = passengerRepository.findByCitizenId(citizenId)
            .orElseThrow(() -> new RuntimeException("Passenger not found with citizen ID: " + citizenId));
        return passengerMapper.toDto(passenger);
    }
    
    @Override
    public List<PassengerDto> getPassengersByEmail(String email) {
        List<Passenger> passengers = passengerRepository.findByEmail(email);
        return passengerMapper.toDtoList(passengers);
    }
    
    @Override
    public List<PassengerDto> searchPassengersByName(String passengerName) {
        List<Passenger> passengers = passengerRepository.findByPassengerNameContaining(passengerName);
        return passengerMapper.toDtoList(passengers);
    }
}
