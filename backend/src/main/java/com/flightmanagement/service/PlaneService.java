package com.flightmanagement.service;

import com.flightmanagement.dto.PlaneDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaneService {
    
    List<PlaneDto> getAllPlanes();

    Page<PlaneDto> getAllPlanesPaged(Pageable pageable);
    
    PlaneDto getPlaneById(Integer id);
    
    PlaneDto createPlane(PlaneDto planeDto);
    
    PlaneDto updatePlane(Integer id, PlaneDto planeDto);
    
    void deletePlane(Integer id);
    
    PlaneDto getPlaneByCode(String planeCode);
    
    List<PlaneDto> getPlanesByType(String planeType);
}
