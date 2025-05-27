package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.FlightSearchCriteria;
import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.mapper.FlightMapper;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.service.FlightService;
import com.flightmanagement.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private FlightMapper flightMapper;
    
    @Autowired
    private ParameterService parameterService;
    
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
    public FlightDto createFlight(FlightDto flightDto) {
        validateFlightData(flightDto);
        validateFlightRoute(flightDto);
        Flight flight = flightMapper.toEntity(flightDto);
        flight.setDeletedAt(null);
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toDto(savedFlight);
    }
    
    @Override
    public FlightDto updateFlight(Integer id, FlightDto flightDto) {
        Flight existingFlight = flightRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
        
        existingFlight.setFlightCode(flightDto.getFlightCode());
        existingFlight.setDepartureTime(flightDto.getDepartureTime());
        existingFlight.setArrivalTime(flightDto.getArrivalTime());
        
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
        System.out.println("FlightService.searchFlights called with criteria: " + criteria);
        
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
            
            System.out.println("Found " + flights.size() + " flights in database");
            return flightMapper.toDtoList(flights);
            
        } catch (Exception e) {
            System.err.println("Error in searchFlights: " + e.getMessage());
            e.printStackTrace();
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
    
    private void validateFlightData(FlightDto flightDto) {
        if (flightDto.getDepartureTime() == null || flightDto.getArrivalTime() == null) {
            throw new IllegalArgumentException("Departure and arrival times are required");
        }
        
        if (flightDto.getArrivalTime().isBefore(flightDto.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }
        
        if (flightDto.getDepartureAirportId().equals(flightDto.getArrivalAirportId())) {
            throw new IllegalArgumentException("Departure and arrival airports cannot be the same");
        }
        
        // Check minimum flight duration from parameters
        ParameterDto parameters = parameterService.getParameterSet();
        long durationMinutes = java.time.Duration.between(flightDto.getDepartureTime(), flightDto.getArrivalTime()).toMinutes();
        
        if (durationMinutes < parameters.getMinFlightDuration()) {
            throw new IllegalArgumentException("Flight duration must be at least " + parameters.getMinFlightDuration() + " minutes");
        }
    }
    
    private void validateFlightRoute(FlightDto flightDto) {
        // Check if flight code is unique
        try {
            getFlightByCode(flightDto.getFlightCode());
            throw new IllegalArgumentException("Flight code already exists: " + flightDto.getFlightCode());
        } catch (RuntimeException e) {
            // Flight code doesn't exist, which is good
        }
    }
    
    private boolean hasAvailableSeats(Integer flightId, Integer ticketClassId, Integer passengerCount) {
        // Check if flight has enough available seats for the specified class
        return flightRepository.checkSeatAvailability(flightId, ticketClassId, passengerCount);
    }
    
    @Override
    public List<FlightTicketClassDto> checkFlightAvailability(Integer flightId) {
        // Get flight ticket class information for the given flight
        try {
            Flight flight = flightRepository.findActiveById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
            
            // Return the flight ticket class information - this would be implemented 
            // once we have the FlightTicketClassService properly set up
            // For now, return empty list to allow compilation
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Error checking flight availability: " + e.getMessage());
        }
    }
}
