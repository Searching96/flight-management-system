package com.example.sprint01.service.implementation;

import com.example.sprint01.dto.FlightDto;
import com.example.sprint01.entity.Airport;
import com.example.sprint01.entity.Flight;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.mapper.FlightMapper;
import com.example.sprint01.repository.AirportRepository;
import com.example.sprint01.repository.FlightRepository;
import com.example.sprint01.service.FlightService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FlightServiceImpl implements FlightService {
    private FlightRepository flightRepository;
    private AirportRepository airportRepository;

    @Override
    public FlightDto createFlight(FlightDto flightDto) {
        Airport departureAirport = airportRepository.findById(flightDto.getDepartureAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Departure airport not found with id: " + flightDto.getDepartureAirportId()));
        Airport arrivalAirport = airportRepository.findById(flightDto.getArrivalAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Arrival airport not found with id: " + flightDto.getArrivalAirportId()));

        Flight flight = FlightMapper.mapToFlight(flightDto, departureAirport, arrivalAirport);
        Flight savedFlight = flightRepository.save(flight);

        return FlightMapper.mapToDto(savedFlight);
    }

    @Override
    public FlightDto updateFlight(Long id, FlightDto updatedFlight) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        Airport departureAirport = airportRepository.findById(updatedFlight.getDepartureAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Departure airport not found with id: " + updatedFlight.getDepartureAirportId()));
        Airport arrivalAirport = airportRepository.findById(updatedFlight.getArrivalAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Arrival airport not found with id: " + updatedFlight.getArrivalAirportId()));

        existingFlight.setDepartureAirport(departureAirport);
        existingFlight.setArrivalAirport(arrivalAirport);
        existingFlight.setFlightDate(updatedFlight.getFlightDate());
        existingFlight.setFlightTime(updatedFlight.getFlightTime());
        existingFlight.setDuration(updatedFlight.getDuration());

        Flight updatedFlightObj = flightRepository.save(existingFlight);
        return FlightMapper.mapToDto(updatedFlightObj);
    }

    @Override
    public void deleteFlight(Long id) {
        Flight existingFlight = flightRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        // Use the current timestamp
        existingFlight.setDeletedAt(LocalDateTime.now());
        flightRepository.save(existingFlight);
    }

    @Override
    public FlightDto getFlightById(Long id) {
        Flight flight = flightRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        return FlightMapper.mapToDto(flight);
    }

    @Override
    public List<FlightDto> getAllFlights() {
        List<Flight> flights = flightRepository.findAllActive();
        return flights.stream()
                .map(FlightMapper::mapToDto)
                .toList();
    }
}
