package com.example.sprint01.mapper;

import com.example.sprint01.dto.AirportDto;
import com.example.sprint01.entity.Airport;

public class AirportMapper {
    public static AirportDto mapToDto(Airport airport) {
        return new AirportDto(
                airport.getId(),
                airport.getName()
        );
    }

    public static Airport mapToAirport(AirportDto airportDto) {
        return new Airport(
                airportDto.getId(),
                airportDto.getName()
        );
    }
}
