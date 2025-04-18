package com.example.sprint01.service;

import com.example.sprint01.dto.AirportDto;

//import java.util.List;

public interface AirportService {
    AirportDto createAirport(AirportDto airportDto);
    AirportDto getAirportById(Long id);
//    AirportDto getAirportById(Long id);
//    List<AirportDto> getAllAirports();
//    AirportDto updateAirport(Long id, AirportDto airportDto);
//    void deleteAirport(Long id);
}
