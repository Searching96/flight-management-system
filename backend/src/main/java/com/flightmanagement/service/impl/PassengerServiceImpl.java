package com.flightmanagement.service.impl;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.Passenger;
import com.flightmanagement.mapper.PassengerMapper;
import com.flightmanagement.repository.PassengerRepository;
import com.flightmanagement.service.PassengerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PassengerServiceImpl implements PassengerService {
    
    private final PassengerRepository passengerRepository;
    
    private final PassengerMapper passengerMapper;

    public PassengerServiceImpl(PassengerRepository passengerRepository, PassengerMapper passengerMapper) {
        this.passengerRepository = passengerRepository;
        this.passengerMapper = passengerMapper;
    }
    
    @Override
    public List<PassengerDto> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAllActive();
        return passengerMapper.toDtoList(passengers);
    }

    @Override
    public Page<PassengerDto> getAllPassengersPaged(Pageable pageable) {
        Page<Passenger> page = passengerRepository.findByDeletedAtIsNull(pageable);
        return page.map(passengerMapper::toDto);
    }
    
    @Override
    public PassengerDto getPassengerById(Integer id) {
        Passenger passenger = passengerRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        return passengerMapper.toDto(passenger);
    }
    
    @Override
    public PassengerDto getPassengerByCitizenId(String citizenId) {
        return passengerRepository.findByCitizenId(citizenId)
            .map(passengerMapper::toDto)
            .orElse(null);
    }
    
    @Override
    public PassengerDto createPassenger(PassengerDto passengerDto) {
        // Check if passenger already exists with this citizen ID
        if (passengerRepository.findByCitizenId(passengerDto.getCitizenId()).isPresent()) {
            throw new RuntimeException("Passenger already exists with citizen ID: " + passengerDto.getCitizenId());
        }
        
        Passenger passenger = new Passenger();
        passenger.setPassengerName(passengerDto.getPassengerName());
        passenger.setEmail(passengerDto.getEmail());
        passenger.setCitizenId(passengerDto.getCitizenId());
        passenger.setPhoneNumber(passengerDto.getPhoneNumber());
        passenger.setDeletedAt(null);
        
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toDto(savedPassenger);
    }
    
    @Override
    public PassengerDto updatePassenger(Integer id, PassengerDto passengerDto) {
        Passenger existingPassenger = passengerRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        
        existingPassenger.setPassengerName(passengerDto.getPassengerName());
        existingPassenger.setEmail(passengerDto.getEmail());
        existingPassenger.setPhoneNumber(passengerDto.getPhoneNumber());
        
        Passenger updatedPassenger = passengerRepository.save(existingPassenger);
        return passengerMapper.toDto(updatedPassenger);
    }
    
    @Override
    public void deletePassenger(Integer id) {
        Passenger passenger = passengerRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        
        passenger.setDeletedAt(LocalDateTime.now());
        passengerRepository.save(passenger);
    }
    
    @Override
    public PassengerDto getPassengersByEmail(String email) {
        Optional<Passenger> passengers = passengerRepository.findByEmail(email);
        return passengerMapper.toDto(passengers.orElse(null));
    }
    
    @Override
    public List<PassengerDto> searchPassengersByName(String name) {
        List<Passenger> passengers = passengerRepository.findByPassengerNameContainingIgnoreCase(name);
        return passengerMapper.toDtoList(passengers);
    }
}
