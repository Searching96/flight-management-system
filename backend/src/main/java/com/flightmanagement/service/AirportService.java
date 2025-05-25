package com.flightmanagement.service;

import com.flightmanagement.dto.AirportDto;

import java.util.List;

public interface AirportService {
    
    List<AirportDto> getAllAirports();
    
    AirportDto getAirportById(Integer id);
    
    AirportDto createAirport(AirportDto airportDto);
    
    AirportDto updateAirport(Integer id, AirportDto airportDto);
    
    void deleteAirport(Integer id);
    
    List<AirportDto> getAirportsByCity(String cityName);
    
    List<AirportDto> getAirportsByCountry(String countryName);
    
    List<AirportDto> searchAirportsByName(String airportName);
}
