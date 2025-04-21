package com.example.sprint01.service.implementation;

import com.example.sprint01.dto.FlightDetailsDto;
import com.example.sprint01.entity.FlightDetails;
import com.example.sprint01.entity.composite_key.FlightDetailsId;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.mapper.FlightDetailsMapper;
import com.example.sprint01.repository.FlightDetailsRepository;
import com.example.sprint01.service.FlightDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlightDetailsServiceImpl implements FlightDetailsService {
    private FlightDetailsRepository flightDetailsRepository;

    @Override
    public FlightDetailsDto createFlightDetails(FlightDetailsDto flightDetailsDto) {
        FlightDetails flightDetails = FlightDetailsMapper.mapToFlightDetails(flightDetailsDto);
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
    public FlightDetailsDto updateFlightDetails(Long flightId, Long mediumAirportId, FlightDetailsDto updatedFlightDetails) {
        FlightDetailsId flightDetailsId = new FlightDetailsId(flightId, mediumAirportId);
        FlightDetails existingFlightDetails = flightDetailsRepository.findById(flightDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + flightDetailsId));

        existingFlightDetails.setStopTime(updatedFlightDetails.getStopTime());
        existingFlightDetails.setNote(updatedFlightDetails.getNote());

        FlightDetails updatedFlightDetailsObj = flightDetailsRepository.save(existingFlightDetails);
        return FlightDetailsMapper.mapToDto(updatedFlightDetailsObj);
    }

    @Override
    public void deleteFlightDetails(Long flightId, Long mediumAirportId) {
        FlightDetailsId flightDetailsId = new FlightDetailsId(flightId, mediumAirportId);
        FlightDetails existingFlightDetails = flightDetailsRepository.findById(flightDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + flightDetailsId));
        flightDetailsRepository.deleteById(flightDetailsId);
    }
}
