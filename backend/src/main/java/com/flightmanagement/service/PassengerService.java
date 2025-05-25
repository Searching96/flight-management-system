package com.flightmanagement.service;

import com.flightmanagement.dto.PassengerDto;

import java.util.List;

public interface PassengerService {
    
    List<PassengerDto> getAllPassengers();
    
    PassengerDto getPassengerById(Integer id);
    
    PassengerDto createPassenger(PassengerDto passengerDto);
    
    PassengerDto updatePassenger(Integer id, PassengerDto passengerDto);
    
    void deletePassenger(Integer id);
    
    PassengerDto getPassengerByCitizenId(String citizenId);
    
    List<PassengerDto> getPassengersByEmail(String email);
    
    List<PassengerDto> searchPassengersByName(String passengerName);
}
