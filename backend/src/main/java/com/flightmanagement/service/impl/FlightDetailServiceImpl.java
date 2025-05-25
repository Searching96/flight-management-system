package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.entity.composite.FlightDetailId;
import com.flightmanagement.mapper.FlightDetailMapper;
import com.flightmanagement.repository.FlightDetailRepository;
import com.flightmanagement.service.FlightDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightDetailServiceImpl implements FlightDetailService {
    
    @Autowired
    private FlightDetailRepository flightDetailRepository;
    
    @Autowired
    private FlightDetailMapper flightDetailMapper;
    
    @Override
    public List<FlightDetailDto> getAllFlightDetails() {
        List<FlightDetail> flightDetails = flightDetailRepository.findAllActive();
        return flightDetailMapper.toDtoList(flightDetails);
    }
    
    @Override
    public List<FlightDetailDto> getFlightDetailsByFlightId(Integer flightId) {
        List<FlightDetail> flightDetails = flightDetailRepository.findByFlightId(flightId);
        return flightDetailMapper.toDtoList(flightDetails);
    }
    
    @Override
    public List<FlightDetailDto> getFlightDetailsByAirportId(Integer airportId) {
        List<FlightDetail> flightDetails = flightDetailRepository.findByMediumAirportId(airportId);
        return flightDetailMapper.toDtoList(flightDetails);
    }
    
    @Override
    public FlightDetailDto createFlightDetail(FlightDetailDto flightDetailDto) {
        FlightDetail flightDetail = flightDetailMapper.toEntity(flightDetailDto);
        flightDetail.setDeletedAt(null);
        FlightDetail savedFlightDetail = flightDetailRepository.save(flightDetail);
        return flightDetailMapper.toDto(savedFlightDetail);
    }
    
    @Override
    public FlightDetailDto updateFlightDetail(Integer flightId, Integer mediumAirportId, FlightDetailDto flightDetailDto) {
        FlightDetailId id = new FlightDetailId(flightId, mediumAirportId);
        FlightDetail existingFlightDetail = flightDetailRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FlightDetail not found"));
        
        existingFlightDetail.setArrivalTime(flightDetailDto.getArrivalTime());
        existingFlightDetail.setLayoverDuration(flightDetailDto.getLayoverDuration());
        
        FlightDetail updatedFlightDetail = flightDetailRepository.save(existingFlightDetail);
        return flightDetailMapper.toDto(updatedFlightDetail);
    }
    
    @Override
    public void deleteFlightDetail(Integer flightId, Integer mediumAirportId) {
        FlightDetailId id = new FlightDetailId(flightId, mediumAirportId);
        FlightDetail flightDetail = flightDetailRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FlightDetail not found"));
        
        flightDetail.setDeletedAt(LocalDateTime.now());
        flightDetailRepository.save(flightDetail);
    }
}
