package com.flightmanagement.service.impl;

import com.flightmanagement.dto.AirportDto;
import com.flightmanagement.entity.Airport;
import com.flightmanagement.mapper.AirportMapper;
import com.flightmanagement.repository.AirportRepository;
import com.flightmanagement.service.AirportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AirportServiceImpl implements AirportService {
    
    private final AirportRepository airportRepository;
    
    private final AirportMapper airportMapper;

    public AirportServiceImpl(AirportRepository airportRepository, AirportMapper airportMapper) {
        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
    }
    
    @Override
    public List<AirportDto> getAllAirports() {
        List<Airport> airports = airportRepository.findAllActive();
        return airportMapper.toDtoList(airports);
    }
    
    @Override
    public AirportDto getAirportById(Integer id) {
        Airport airport = airportRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
        return airportMapper.toDto(airport);
    }
    
    @Override
    public AirportDto createAirport(AirportDto airportDto) {
        Airport airport = airportMapper.toEntity(airportDto);
        airport.setDeletedAt(null);
        Airport savedAirport = airportRepository.save(airport);
        return airportMapper.toDto(savedAirport);
    }
    
    @Override
    public AirportDto updateAirport(Integer id, AirportDto airportDto) {
        Airport existingAirport = airportRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
        
        existingAirport.setAirportName(airportDto.getAirportName());
        existingAirport.setCityName(airportDto.getCityName());
        existingAirport.setCountryName(airportDto.getCountryName());
        
        Airport updatedAirport = airportRepository.save(existingAirport);
        return airportMapper.toDto(updatedAirport);
    }
    
    @Override
    public void deleteAirport(Integer id) {
        Airport airport = airportRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
        
        airport.setDeletedAt(LocalDateTime.now());
        airportRepository.save(airport);
    }
    
    @Override
    public List<AirportDto> getAirportsByCity(String cityName) {
        List<Airport> airports = airportRepository.findByCityName(cityName);
        return airportMapper.toDtoList(airports);
    }
    
    @Override
    public List<AirportDto> getAirportsByCountry(String countryName) {
        List<Airport> airports = airportRepository.findByCountryName(countryName);
        return airportMapper.toDtoList(airports);
    }
    
    @Override
    public List<AirportDto> searchAirportsByName(String airportName) {
        List<Airport> airports = airportRepository.findByAirportNameContaining(airportName);
        return airportMapper.toDtoList(airports);
    }
}
