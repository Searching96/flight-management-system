package com.flightmanagement.service.impl;

import com.flightmanagement.dto.PlaneDto;
import com.flightmanagement.entity.Plane;
import com.flightmanagement.mapper.PlaneMapper;
import com.flightmanagement.repository.PlaneRepository;
import com.flightmanagement.service.PlaneService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlaneServiceImpl implements PlaneService {
    
    private final PlaneRepository planeRepository;
    
    private final PlaneMapper planeMapper;

    public PlaneServiceImpl(PlaneRepository planeRepository, PlaneMapper planeMapper) {
        this.planeRepository = planeRepository;
        this.planeMapper = planeMapper;
    }
    
    @Override
    public List<PlaneDto> getAllPlanes() {
        List<Plane> planes = planeRepository.findAllActive();
        return planeMapper.toDtoList(planes);
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
        return planeMapper.toDto(savedPlane);
    }
    
    @Override
    public PlaneDto updatePlane(Integer id, PlaneDto planeDto) {
        Plane existingPlane = planeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Plane not found with id: " + id));
        
        existingPlane.setPlaneCode(planeDto.getPlaneCode());
        existingPlane.setPlaneType(planeDto.getPlaneType());
        existingPlane.setSeatQuantity(planeDto.getSeatQuantity());
        
        Plane updatedPlane = planeRepository.save(existingPlane);
        return planeMapper.toDto(updatedPlane);
    }
    
    @Override
    public void deletePlane(Integer id) {
        Plane plane = planeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Plane not found with id: " + id));
        
        plane.setDeletedAt(LocalDateTime.now());
        planeRepository.save(plane);
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
