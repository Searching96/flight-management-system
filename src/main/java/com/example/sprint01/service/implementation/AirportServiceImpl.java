package com.example.sprint01.service.implementation;

import com.example.sprint01.dto.AirportDto;
import com.example.sprint01.entity.Airport;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.mapper.AirportMapper;
import com.example.sprint01.repository.AirportRepository;
import com.example.sprint01.service.AirportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AirportServiceImpl implements AirportService {
    private AirportRepository airportRepository;

    @Override
    public AirportDto createAirport(AirportDto airportDto) {
        Airport airport = AirportMapper.mapToAirport(airportDto);
        Airport savedAirport = airportRepository.save(airport);
        return AirportMapper.mapToDto(savedAirport);
    }

    @Override
    public AirportDto getAirportById(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found with id: " + id));
        return AirportMapper.mapToDto(airport);
    }

    @Override
    public List<AirportDto> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(AirportMapper::mapToDto)
                .toList();
    }
}
