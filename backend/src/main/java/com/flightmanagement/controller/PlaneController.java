package com.flightmanagement.controller;

import com.flightmanagement.dto.PlaneDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlaneController {
    
    private final PlaneService planeService;

    public PlaneController(PlaneService planeService) {
        this.planeService = planeService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaneDto>>> getAllPlanes() {
        List<PlaneDto> planes = planeService.getAllPlanes();
        ApiResponse<List<PlaneDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Planes retrieved successfully",
                planes,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaneDto>> getPlaneById(@PathVariable Integer id) {
        PlaneDto plane = planeService.getPlaneById(id);
        ApiResponse<PlaneDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Plane retrieved successfully",
                plane,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<PlaneDto>> createPlane(@RequestBody PlaneDto planeDto) {
        PlaneDto createdPlane = planeService.createPlane(planeDto);
        ApiResponse<PlaneDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Plane created successfully",
                createdPlane,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaneDto>> updatePlane(@PathVariable Integer id, @RequestBody PlaneDto planeDto) {
        PlaneDto updatedPlane = planeService.updatePlane(id, planeDto);
        ApiResponse<PlaneDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Plane updated successfully",
                updatedPlane,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlane(@PathVariable Integer id) {
        planeService.deletePlane(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Plane deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PlaneDto>> getPlaneByCode(@PathVariable String code) {
        PlaneDto plane = planeService.getPlaneByCode(code);
        ApiResponse<PlaneDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Plane retrieved by code successfully",
                plane,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<PlaneDto>>> getPlanesByType(@PathVariable String type) {
        List<PlaneDto> planes = planeService.getPlanesByType(type);
        ApiResponse<List<PlaneDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Planes retrieved by type successfully",
                planes,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
