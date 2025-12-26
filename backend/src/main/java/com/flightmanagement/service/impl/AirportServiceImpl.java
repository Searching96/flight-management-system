package com.flightmanagement.service.impl;

import com.flightmanagement.dto.AirportDto;
import com.flightmanagement.entity.Airport;
import com.flightmanagement.mapper.AirportMapper;
import com.flightmanagement.repository.AirportRepository;
import com.flightmanagement.service.AirportService;
import com.flightmanagement.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AirportServiceImpl implements AirportService {
    
    private final AirportRepository airportRepository;
    
    private final AirportMapper airportMapper;

    private final AuditLogService auditLogService;

    public AirportServiceImpl(AirportRepository airportRepository, AirportMapper airportMapper, AuditLogService auditLogService) {
        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
        this.auditLogService = auditLogService;
    }
    
    @Override
    public List<AirportDto> getAllAirports() {
        List<Airport> airports = airportRepository.findAllActive();
        return airportMapper.toDtoList(airports);
    }

    @Override
    public Page<AirportDto> getAllAirportsPaged(Pageable pageable) {
        Page<Airport> page = airportRepository.findByDeletedAtIsNull(pageable);
        return page.map(airportMapper::toDto);
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
        auditLogService.saveAuditLog("Airport", savedAirport.getAirportId().toString(), "CREATE", "airport", null, savedAirport.getAirportName() + " (" + savedAirport.getCityName() + ")", "system");
        return airportMapper.toDto(savedAirport);
    }
    
    @Override
    public AirportDto updateAirport(Integer id, AirportDto airportDto) {
        Airport existingAirport = airportRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
        
        // Store old values
        String oldAirportName = existingAirport.getAirportName();
        String oldCityName = existingAirport.getCityName();
        String oldCountryName = existingAirport.getCountryName();
        
        // Update fields
        existingAirport.setAirportName(airportDto.getAirportName());
        existingAirport.setCityName(airportDto.getCityName());
        existingAirport.setCountryName(airportDto.getCountryName());
        
        Airport updatedAirport = airportRepository.save(existingAirport);
        
        // Audit log each changed field
        if (!oldAirportName.equals(airportDto.getAirportName())) {
            auditLogService.saveAuditLog("Airport", id.toString(), "UPDATE", "airportName", oldAirportName, airportDto.getAirportName(), "system");
        }
        if (!oldCityName.equals(airportDto.getCityName())) {
            auditLogService.saveAuditLog("Airport", id.toString(), "UPDATE", "cityName", oldCityName, airportDto.getCityName(), "system");
        }
        if (!oldCountryName.equals(airportDto.getCountryName())) {
            auditLogService.saveAuditLog("Airport", id.toString(), "UPDATE", "countryName", oldCountryName, airportDto.getCountryName(), "system");
        }
        
        return airportMapper.toDto(updatedAirport);
    }
    
    @Override
    public void deleteAirport(Integer id) {
        Airport airport = airportRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
        
        airport.setDeletedAt(LocalDateTime.now());
        airportRepository.save(airport);
        auditLogService.saveAuditLog("Airport", id.toString(), "DELETE", "airport", airport.getAirportName() + " (" + airport.getCityName() + ")", null, "system");
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
