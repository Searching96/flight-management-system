package com.example.sprint01.service.implementation;

import com.example.sprint01.dto.FlightDetailsDto;
import com.example.sprint01.entity.Airport;
import com.example.sprint01.entity.Flight;
import com.example.sprint01.entity.FlightDetails;
import com.example.sprint01.entity.composite_key.FlightDetailsId;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.mapper.FlightDetailsMapper;
import com.example.sprint01.repository.AirportRepository;
import com.example.sprint01.repository.FlightDetailsRepository;
import com.example.sprint01.repository.FlightRepository;
import com.example.sprint01.service.FlightDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlightDetailsServiceImpl implements FlightDetailsService {
    private FlightDetailsRepository flightDetailsRepository;
    private FlightRepository flightRepository;
    private AirportRepository airportRepository;

    @Override
    public FlightDetailsDto createFlightDetails(FlightDetailsDto flightDetailsDto) {
        Flight flight = flightRepository.findById(flightDetailsDto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + flightDetailsDto.getFlightId()));

        Airport mediumAirport = airportRepository.findById(flightDetailsDto.getMediumAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Medium airport not found with id: " + flightDetailsDto.getMediumAirportId()));

        FlightDetails flightDetails = FlightDetailsMapper.mapToFlightDetails(flightDetailsDto);

        flightDetails.setFlight(flight);
        flightDetails.setMediumAirport(mediumAirport);

        FlightDetails savedFlightDetails = flightDetailsRepository.save(flightDetails);
        return FlightDetailsMapper.mapToDto(savedFlightDetails);
    }

    @Override
    public FlightDetailsDto getFlightDetailsById(Long flightId, Long mediumAirportId) {
        FlightDetailsId flightDetailsId = new FlightDetailsId(flightId, mediumAirportId);
        FlightDetails flightDetails = flightDetailsRepository.findById(flightDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + flightDetailsId));
        return FlightDetailsMapper.mapToDto(flightDetails);
    }

    @Override
    public FlightDetailsDto updateFlightDetails(Long flightId, Long mediumAirportId, FlightDetailsDto updatedFlightDetailsDto) {
        FlightDetailsId flightDetailsId = new FlightDetailsId(flightId, mediumAirportId);
        FlightDetails existingFlightDetails = flightDetailsRepository.findById(flightDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + flightDetailsId));

        existingFlightDetails.setStopTime(updatedFlightDetailsDto.getStopTime());
        existingFlightDetails.setNote(updatedFlightDetailsDto.getNote());

        FlightDetails updatedFlightDetails = flightDetailsRepository.save(existingFlightDetails);
        return FlightDetailsMapper.mapToDto(updatedFlightDetails);
    }

    @Override
    public void deleteFlightDetails(Long flightId, Long mediumAirportId) {
        FlightDetailsId flightDetailsId = new FlightDetailsId(flightId, mediumAirportId);
        FlightDetails existingFlightDetails = flightDetailsRepository.findById(flightDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + flightDetailsId));
        flightDetailsRepository.deleteById(flightDetailsId);
    }
}
