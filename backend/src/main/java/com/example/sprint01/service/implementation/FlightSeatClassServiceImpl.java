package com.example.sprint01.service.implementation;

import com.example.sprint01.dto.FlightSeatClassDto;
import com.example.sprint01.entity.Flight;
import com.example.sprint01.entity.FlightSeatClass;
import com.example.sprint01.entity.SeatClass;
import com.example.sprint01.entity.composite_key.FlightSeatClassId;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.mapper.FlightSeatClassMapper;
import com.example.sprint01.repository.FlightSeatClassRepository;
import com.example.sprint01.repository.FlightRepository;
import com.example.sprint01.repository.SeatClassRepository;
import com.example.sprint01.service.FlightSeatClassService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FlightSeatClassServiceImpl implements FlightSeatClassService {
    private final SeatClassRepository seatClassRepository;
    private FlightSeatClassRepository FlightSeatClassRepository;
    private FlightRepository flightRepository;

    @Override
    public FlightSeatClassDto createFlightSeatClass(FlightSeatClassDto FlightSeatClassDto) {
        Flight flight = flightRepository.findById(FlightSeatClassDto.getFlightId()).orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + FlightSeatClassDto.getFlightId()));

        SeatClass seatClass = seatClassRepository.findById(FlightSeatClassDto.getSeatClassId()).orElseThrow(() -> new ResourceNotFoundException("Seat class not found with id: " + FlightSeatClassDto.getSeatClassId()));

        FlightSeatClass FlightSeatClass = FlightSeatClassMapper.mapToFlightSeatClass(FlightSeatClassDto);

        FlightSeatClass.setFlight(flight);
        FlightSeatClass.setSeatClass(seatClass);

        FlightSeatClass savedFlightSeatClass = FlightSeatClassRepository.save(FlightSeatClass);
        return FlightSeatClassMapper.mapToDto(savedFlightSeatClass);
    }

    @Override
    public FlightSeatClassDto updateFlightSeatClass(Long flightId, Long seatClassId, FlightSeatClassDto updatedFlightSeatClassDto) {
        FlightSeatClassId FlightSeatClassId = new FlightSeatClassId(flightId, seatClassId);
        FlightSeatClass existingFlightSeatClass = FlightSeatClassRepository.findById(FlightSeatClassId).orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + FlightSeatClassId));

        existingFlightSeatClass.setTotalTickets(updatedFlightSeatClassDto.getTotalTickets());
        existingFlightSeatClass.setRemainingTickets(updatedFlightSeatClassDto.getRemainingTickets());
        existingFlightSeatClass.setCurrentPrice(updatedFlightSeatClassDto.getCurrentPrice());

        FlightSeatClass updatedFlightSeatClass = FlightSeatClassRepository.save(existingFlightSeatClass);
        return FlightSeatClassMapper.mapToDto(updatedFlightSeatClass);
    }

    @Override
    public void deleteFlightSeatClass(Long flightId, Long seatClassId) {
        FlightSeatClassId FlightSeatClassId = new FlightSeatClassId(flightId, seatClassId);
        FlightSeatClass existingFlightSeatClass = FlightSeatClassRepository.findById(FlightSeatClassId).orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + FlightSeatClassId));

        // Use the current timestamp
        existingFlightSeatClass.setDeletedAt(LocalDateTime.now());
        FlightSeatClassRepository.save(existingFlightSeatClass);
    }

    @Override
    public FlightSeatClassDto getFlightSeatClassById(Long flightId, Long seatClassId) {
        FlightSeatClassId FlightSeatClassId = new FlightSeatClassId(flightId, seatClassId);
        FlightSeatClass FlightSeatClass = FlightSeatClassRepository.findActiveById(flightId, seatClassId).orElseThrow(() -> new ResourceNotFoundException("Flight details not found with id: " + FlightSeatClassId));
        return FlightSeatClassMapper.mapToDto(FlightSeatClass);
    }

    @Override
    public void deleteFlightSeatClassByFlightId(Long flightId) {
        List<FlightSeatClass> detailsList = FlightSeatClassRepository.findAllActiveByFlightId(flightId);
        for (FlightSeatClass details : detailsList) {
            details.setDeletedAt(LocalDateTime.now());
            FlightSeatClassRepository.save(details);
        }
    }

    @Override
    public List<FlightSeatClassDto> getFlightSeatClassByFlightId(Long flightId) {
        List<FlightSeatClass> FlightSeatClassList = FlightSeatClassRepository.findAllActiveByFlightId(flightId);
        if (FlightSeatClassList.isEmpty()) {
            throw new ResourceNotFoundException("No flight details found for flightId: " + flightId);
        }
        return FlightSeatClassList.stream()
                .map(FlightSeatClassMapper::mapToDto)
                .toList();
    }
}
