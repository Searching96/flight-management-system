package com.example.sprint01.service;

import com.example.sprint01.dto.SeatClassDto;

import java.util.List;

public interface SeatClassService {
    SeatClassDto createAirport(SeatClassDto airportDto);

    SeatClassDto getAirportById(Long id);

    List<SeatClassDto> getAllAirports();

    SeatClassDto updateAirport(Long id, SeatClassDto updatedAirport);

    void deleteAirport(Long id);
}
