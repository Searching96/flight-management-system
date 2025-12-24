package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.mapper.FlightDetailMapper;
import com.flightmanagement.repository.FlightDetailRepository;
import com.flightmanagement.service.AuditLogService;
import com.flightmanagement.service.FlightDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightDetailServiceImpl implements FlightDetailService {
    
    private final FlightDetailRepository flightDetailRepository;
    
    private final FlightDetailMapper flightDetailMapper;

    private final AuditLogService auditLogService;

    public FlightDetailServiceImpl(FlightDetailRepository flightDetailRepository, FlightDetailMapper flightDetailMapper, AuditLogService auditLogService) {
        this.flightDetailRepository = flightDetailRepository;
        this.flightDetailMapper = flightDetailMapper;
        this.auditLogService = auditLogService;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlightDetailDto> getAllFlightDetails() {
        List<FlightDetail> flightDetails = flightDetailRepository.findAllActive();
        return flightDetailMapper.toDtoList(flightDetails);
    }

    @Override
    public Page<FlightDetailDto> getAllFlightDetailsPaged(Pageable pageable) {
        Page<FlightDetail> page = flightDetailRepository.findByDeletedAtIsNull(pageable);
        return page.map(flightDetailMapper::toDto);
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
        
        // Audit log for CREATE
        String entityId = savedFlightDetail.getFlightId() + "-" + savedFlightDetail.getMediumAirportId();
        auditLogService.saveAuditLog("FlightDetail", entityId, "CREATE", "flightDetail", null, "FlightDetail", "system");
        
        return flightDetailMapper.toDto(savedFlightDetail);
    }
    
    @Override
    @Transactional
    public FlightDetailDto updateFlightDetail(Integer flightId, Integer mediumAirportId, FlightDetailDto flightDetailDto) {
        FlightDetail existingFlightDetail = flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId)
            .orElseThrow(() -> new RuntimeException("FlightDetail not found for flight: " + flightId + " and airport: " + mediumAirportId));
        
        // Store old values for audit logging
        String oldArrivalTime = existingFlightDetail.getArrivalTime() != null ? existingFlightDetail.getArrivalTime().toString() : null;
        String oldLayoverDuration = existingFlightDetail.getLayoverDuration() != null ? existingFlightDetail.getLayoverDuration().toString() : null;
        
        existingFlightDetail.setArrivalTime(flightDetailDto.getArrivalTime());
        existingFlightDetail.setLayoverDuration(flightDetailDto.getLayoverDuration());
        
        FlightDetail updatedFlightDetail = flightDetailRepository.save(existingFlightDetail);
        
        // Audit log for changed fields
        String entityId = flightId + "-" + mediumAirportId;
        String newArrivalTime = updatedFlightDetail.getArrivalTime() != null ? updatedFlightDetail.getArrivalTime().toString() : null;
        if ((oldArrivalTime == null && newArrivalTime != null) || (oldArrivalTime != null && !oldArrivalTime.equals(newArrivalTime))) {
            auditLogService.saveAuditLog("FlightDetail", entityId, "UPDATE", "arrivalTime", oldArrivalTime, newArrivalTime, "system");
        }
        
        String newLayoverDuration = updatedFlightDetail.getLayoverDuration() != null ? updatedFlightDetail.getLayoverDuration().toString() : null;
        if ((oldLayoverDuration == null && newLayoverDuration != null) || (oldLayoverDuration != null && !oldLayoverDuration.equals(newLayoverDuration))) {
            auditLogService.saveAuditLog("FlightDetail", entityId, "UPDATE", "layoverDuration", oldLayoverDuration, newLayoverDuration, "system");
        }
        
        return flightDetailMapper.toDto(updatedFlightDetail);
    }
    
    @Override
    @Transactional
    public void deleteFlightDetail(Integer flightId, Integer mediumAirportId) {
        FlightDetail flightDetail = flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId)
            .orElseThrow(() -> new RuntimeException("FlightDetail not found for flight: " + flightId + " and airport: " + mediumAirportId));
        
        // Capture entity info before delete
        String entityId = flightId + "-" + mediumAirportId;
        
        flightDetail.setDeletedAt(LocalDateTime.now());
        flightDetailRepository.save(flightDetail);
        
        // Audit log for DELETE
        auditLogService.saveAuditLog("FlightDetail", entityId, "DELETE", "flightDetail", "FlightDetail", null, "system");
    }
}
