package com.flightmanagement.service.impl;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.mapper.FlightMapper;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.service.FlightService;
import com.flightmanagement.service.FlightTicketClassService;
import com.flightmanagement.service.ParameterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {
    
    private final FlightRepository flightRepository;

    private final FlightTicketClassService flightTicketClassService;
    
    private final FlightMapper flightMapper;
    
    private final ParameterService parameterService;

    public FlightServiceImpl(FlightRepository flightRepository,
                             FlightMapper flightMapper,
                             ParameterService parameterService,
                             FlightTicketClassService flightTicketClassService) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
        this.parameterService = parameterService;
        this.flightTicketClassService = flightTicketClassService;
    }
    
    @Override
    public List<FlightDto> getAllFlights() {
        List<Flight> flights = flightRepository.findAllActive();
        return flightMapper.toDtoList(flights);
    }
    
    @Override
    public FlightDto getFlightById(Integer id) {
        Flight flight = flightRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
        return flightMapper.toDto(flight);
    }

    @Override
    public FlightDto createFlight(FlightRequest request) {
        validateFlightData(request);

        Flight flight = flightMapper.toEntityFromCreateRequest(request);
        flight.setDeletedAt(null);
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toDto(savedFlight);
    }
    
    @Override
    public FlightDto updateFlight(Integer id, FlightRequest updateRequest) {
        Flight existingFlight = flightRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
        
        validateFlightDataForUpdate(id, updateRequest);
        existingFlight.setFlightCode(updateRequest.getFlightCode());
        existingFlight.setDepartureTime(updateRequest.getDepartureTime());
        existingFlight.setArrivalTime(updateRequest.getArrivalTime());
        
        Flight updatedFlight = flightRepository.save(existingFlight);
        return flightMapper.toDto(updatedFlight);
    }
    
    @Override
    public void deleteFlight(Integer id) {
        Flight flight = flightRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
        
        flight.setDeletedAt(LocalDateTime.now());
        flightRepository.save(flight);
    }
    
    @Override
    public FlightDto getFlightByCode(String flightCode) {
        Flight flight = flightRepository.findByFlightCode(flightCode)
            .orElseThrow(() -> new RuntimeException("Flight not found with code: " + flightCode));
        return flightMapper.toDto(flight);
    }
    
    @Override
    public List<FlightDto> searchFlights(FlightSearchCriteria criteria) {
        try {
            List<Flight> flights;
            
            if (criteria.getTicketClassId() != null && criteria.getTicketClassId() > 0) {
                // Search flights with specific ticket class availability
                flights = flightRepository.findFlightsWithTicketClass(
                    criteria.getDepartureAirportId(),
                    criteria.getArrivalAirportId(),
                    criteria.getDepartureDate(),
                    criteria.getTicketClassId(),
                    criteria.getPassengerCount()
                );
            } else {
                // Search all flights on route regardless of ticket class
                flights = flightRepository.findFlightsByRoute(
                    criteria.getDepartureAirportId(),
                    criteria.getArrivalAirportId(),
                    criteria.getDepartureDate()
                );
            }
            
            return flightMapper.toDtoList(flights);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to search flights", e);
        }
    }
    
    @Override
    public List<FlightDto> getFlightsByRoute(Integer departureAirportId, Integer arrivalAirportId, LocalDateTime departureDate) {
        List<Flight> flights = flightRepository.findFlights(departureAirportId, arrivalAirportId, departureDate);
        return flightMapper.toDtoList(flights);
    }
    
    @Override
    public List<FlightDto> getFlightsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Flight> flights = flightRepository.findByDepartureDateRange(startDate, endDate);
        return flightMapper.toDtoList(flights);
    }
    
    @Override
    public List<FlightDto> searchFlightsByDate(String departureDate) {
        try {
            LocalDate date = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<Flight> flights = flightRepository.findByDepartureDate(date);
            return flightMapper.toDtoList(flights);
        } catch (Exception e) {
            throw new RuntimeException("Error searching flights by date: " + e.getMessage());
        }
    }
    
    private void validateFlightData(FlightRequest request) {
        if (flightRepository.existsByFlightCode(request.getFlightCode())) {
            throw new IllegalArgumentException("Flight code already exists: " + request.getFlightCode());
        }

        if (request.getArrivalTime().isBefore(request.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }
        
        if (request.getDepartureAirportId().equals(request.getArrivalAirportId())) {
            throw new IllegalArgumentException("Departure and arrival airports cannot be the same");
        }
        
        // Check minimum flight duration from parameters
        ParameterDto parameters = parameterService.getLatestParameter();
        long durationMinutes = Duration.between(request.getDepartureTime(), request.getArrivalTime()).toMinutes();
        
        if (durationMinutes < parameters.getMinFlightDuration()) {
            throw new IllegalArgumentException("Flight duration must be at least " + parameters.getMinFlightDuration() + " minutes");
        }
    }

    private void validateFlightDataForUpdate(Integer flightId, FlightRequest request) {
        // Check duplicate code - exclude current flight
        Flight existingFlightWithCode = flightRepository.findByFlightCode(request.getFlightCode()).orElse(null);
        if (existingFlightWithCode != null && !existingFlightWithCode.getFlightId().equals(flightId)) {
            throw new IllegalArgumentException("Flight code already exists: " + request.getFlightCode());
        }

        if (request.getArrivalTime().isBefore(request.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }
        
        if (request.getDepartureAirportId().equals(request.getArrivalAirportId())) {
            throw new IllegalArgumentException("Departure and arrival airports cannot be the same");
        }
        
        // Check minimum flight duration from parameters
        ParameterDto parameters = parameterService.getLatestParameter();
        long durationMinutes = Duration.between(request.getDepartureTime(), request.getArrivalTime()).toMinutes();
        
        if (durationMinutes < parameters.getMinFlightDuration()) {
            throw new IllegalArgumentException("Flight duration must be at least " + parameters.getMinFlightDuration() + " minutes");
        }
    }
    
    @Override
    public List<FlightTicketClassDto> checkFlightAvailability(Integer flightId) {
        // Get flight ticket class information for the given flight
        try {
            Flight flight = flightRepository.findActiveById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
            
            return flightTicketClassService.getFlightTicketClassesByFlightId(flight.getFlightId());
        } catch (Exception e) {
            throw new RuntimeException("Error checking flight availability: " + e.getMessage());
        }
    }
}
