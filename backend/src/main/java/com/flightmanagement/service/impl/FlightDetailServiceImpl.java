package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.mapper.FlightDetailMapper;
import com.flightmanagement.repository.FlightDetailRepository;
import com.flightmanagement.service.FlightDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightDetailServiceImpl implements FlightDetailService {
    
    private final FlightDetailRepository flightDetailRepository;
    
    private final FlightDetailMapper flightDetailMapper;

    public FlightDetailServiceImpl(FlightDetailRepository flightDetailRepository, FlightDetailMapper flightDetailMapper) {
        this.flightDetailRepository = flightDetailRepository;
        this.flightDetailMapper = flightDetailMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlightDetailDto> getAllFlightDetails() {
        List<FlightDetail> flightDetails = flightDetailRepository.findAllActive();
        return flightDetailMapper.toDtoList(flightDetails);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlightDetailDto> getFlightDetailsByFlightId(Integer flightId) {
        List<FlightDetail> flightDetails = flightDetailRepository.findByFlightId(flightId);
        return flightDetailMapper.toDtoList(flightDetails);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlightDetailDto> getFlightDetailsByAirportId(Integer airportId) {
        List<FlightDetail> flightDetails = flightDetailRepository.findByMediumAirportId(airportId);
        return flightDetailMapper.toDtoList(flightDetails);
    }
    
    @Override
    @Transactional
    public FlightDetailDto createFlightDetail(FlightDetailDto flightDetailDto) {
        if (flightDetailDto == null
            || flightDetailDto.getFlightId() == null
            || flightDetailDto.getMediumAirportId() == null
            || flightDetailDto.getArrivalTime() == null
            || flightDetailDto.getLayoverDuration() == null) {
            throw new IllegalArgumentException("FlightDetail payload is missing required fields");
        }

        FlightDetail flightDetail = flightDetailMapper.toEntity(flightDetailDto);

        flightDetail.setDeletedAt(null);
        FlightDetail savedFlightDetail = flightDetailRepository.save(flightDetail);
        return flightDetailMapper.toDto(savedFlightDetail);
    }
    
    @Override
    @Transactional
    public FlightDetailDto updateFlightDetail(Integer flightId, Integer mediumAirportId, FlightDetailDto flightDetailDto) {
        FlightDetail existingFlightDetail = flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId)
            .orElseThrow(() -> new RuntimeException("FlightDetail not found for flight: " + flightId + " and airport: " + mediumAirportId));
        
        existingFlightDetail.setArrivalTime(flightDetailDto.getArrivalTime());
        existingFlightDetail.setLayoverDuration(flightDetailDto.getLayoverDuration());
        
        FlightDetail updatedFlightDetail = flightDetailRepository.save(existingFlightDetail);
        return flightDetailMapper.toDto(updatedFlightDetail);
    }
    
    @Override
    @Transactional
    public void deleteFlightDetail(Integer flightId, Integer mediumAirportId) {
        FlightDetail flightDetail = flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId)
            .orElseThrow(() -> new RuntimeException("FlightDetail not found for flight: " + flightId + " and airport: " + mediumAirportId));
        
        flightDetail.setDeletedAt(LocalDateTime.now());
        flightDetailRepository.save(flightDetail);
    }
}
