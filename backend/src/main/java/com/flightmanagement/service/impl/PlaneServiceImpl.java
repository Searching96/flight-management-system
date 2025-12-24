package com.flightmanagement.service.impl;

import com.flightmanagement.dto.PlaneDto;
import com.flightmanagement.entity.Plane;
import com.flightmanagement.mapper.PlaneMapper;
import com.flightmanagement.repository.PlaneRepository;
import com.flightmanagement.service.PlaneService;
import com.flightmanagement.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlaneServiceImpl implements PlaneService {
    
    private final PlaneRepository planeRepository;
    
    private final PlaneMapper planeMapper;

    private final AuditLogService auditLogService;

    public PlaneServiceImpl(PlaneRepository planeRepository, PlaneMapper planeMapper, AuditLogService auditLogService) {
        this.planeRepository = planeRepository;
        this.planeMapper = planeMapper;
        this.auditLogService = auditLogService;
    }
    
    @Override
    public List<PlaneDto> getAllPlanes() {
        List<Plane> planes = planeRepository.findAllActive();
        return planeMapper.toDtoList(planes);
    }

    @Override
    public Page<PlaneDto> getAllPlanesPaged(Pageable pageable) {
        Page<Plane> page = planeRepository.findByDeletedAtIsNull(pageable);
        return page.map(planeMapper::toDto);
    }
    
    @Override
    public PlaneDto getPlaneById(Integer id) {
        Plane plane = planeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Plane not found with id: " + id));
        return planeMapper.toDto(plane);
    }
    
    @Override
    public PlaneDto createPlane(PlaneDto planeDto) {
        Plane plane = planeMapper.toEntity(planeDto);
        plane.setDeletedAt(null);
        Plane savedPlane = planeRepository.save(plane);
        auditLogService.saveAuditLog("Plane", savedPlane.getPlaneId().toString(), "CREATE", "plane", null, savedPlane.getPlaneCode() + " (" + savedPlane.getPlaneType() + ")", "system");
        return planeMapper.toDto(savedPlane);
    }
    
    @Override
    public PlaneDto updatePlane(Integer id, PlaneDto planeDto) {
        Plane existingPlane = planeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Plane not found with id: " + id));
        
        // Store old values
        String oldPlaneCode = existingPlane.getPlaneCode();
        String oldPlaneType = existingPlane.getPlaneType();
        Integer oldSeatQuantity = existingPlane.getSeatQuantity();
        
        // Update fields
        existingPlane.setPlaneCode(planeDto.getPlaneCode());
        existingPlane.setPlaneType(planeDto.getPlaneType());
        existingPlane.setSeatQuantity(planeDto.getSeatQuantity());
        
        Plane updatedPlane = planeRepository.save(existingPlane);
        
        // Audit log each changed field
        if (!oldPlaneCode.equals(planeDto.getPlaneCode())) {
            auditLogService.saveAuditLog("Plane", id.toString(), "UPDATE", "planeCode", oldPlaneCode, planeDto.getPlaneCode(), "system");
        }
        if (!oldPlaneType.equals(planeDto.getPlaneType())) {
            auditLogService.saveAuditLog("Plane", id.toString(), "UPDATE", "planeType", oldPlaneType, planeDto.getPlaneType(), "system");
        }
        if (!oldSeatQuantity.equals(planeDto.getSeatQuantity())) {
            auditLogService.saveAuditLog("Plane", id.toString(), "UPDATE", "seatQuantity", oldSeatQuantity.toString(), planeDto.getSeatQuantity().toString(), "system");
        }
        
        return planeMapper.toDto(updatedPlane);
    }
    
    @Override
    public void deletePlane(Integer id) {
        Plane plane = planeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Plane not found with id: " + id));
        
        plane.setDeletedAt(LocalDateTime.now());
        planeRepository.save(plane);
        auditLogService.saveAuditLog("Plane", id.toString(), "DELETE", "plane", plane.getPlaneCode() + " (" + plane.getPlaneType() + ")", null, "system");
    }
    
    @Override
    public PlaneDto getPlaneByCode(String planeCode) {
        Plane plane = planeRepository.findByPlaneCode(planeCode)
            .orElseThrow(() -> new RuntimeException("Plane not found with code: " + planeCode));
        return planeMapper.toDto(plane);
    }
    
    @Override
    public List<PlaneDto> getPlanesByType(String planeType) {
        List<Plane> planes = planeRepository.findByPlaneType(planeType);
        return planeMapper.toDtoList(planes);
    }
}
