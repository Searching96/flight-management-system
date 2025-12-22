package com.flightmanagement.service;

import com.flightmanagement.dto.PassengerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PassengerService {
    List<PassengerDto> getAllPassengers();
    Page<PassengerDto> getAllPassengersPaged(Pageable pageable);
    PassengerDto getPassengerById(Integer id);
    PassengerDto getPassengerByCitizenId(String citizenId);
    PassengerDto createPassenger(PassengerDto passengerDto);
    PassengerDto updatePassenger(Integer id, PassengerDto passengerDto);
    void deletePassenger(Integer id);
    PassengerDto getPassengersByEmail(String email);
    List<PassengerDto> searchPassengersByName(String name);
}
