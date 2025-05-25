package com.flightmanagement.controller;

import com.flightmanagement.dto.PlaneDto;
import com.flightmanagement.service.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlaneController {
    
    @Autowired
    private PlaneService planeService;
    
    @GetMapping
    public ResponseEntity<List<PlaneDto>> getAllPlanes() {
        List<PlaneDto> planes = planeService.getAllPlanes();
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PlaneDto> getPlaneById(@PathVariable Integer id) {
        PlaneDto plane = planeService.getPlaneById(id);
        return ResponseEntity.ok(plane);
    }
    
    @PostMapping
    public ResponseEntity<PlaneDto> createPlane(@RequestBody PlaneDto planeDto) {
        PlaneDto createdPlane = planeService.createPlane(planeDto);
        return new ResponseEntity<>(createdPlane, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PlaneDto> updatePlane(@PathVariable Integer id, @RequestBody PlaneDto planeDto) {
        PlaneDto updatedPlane = planeService.updatePlane(id, planeDto);
        return ResponseEntity.ok(updatedPlane);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlane(@PathVariable Integer id) {
        planeService.deletePlane(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<PlaneDto> getPlaneByCode(@PathVariable String code) {
        PlaneDto plane = planeService.getPlaneByCode(code);
        return ResponseEntity.ok(plane);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PlaneDto>> getPlanesByType(@PathVariable String type) {
        List<PlaneDto> planes = planeService.getPlanesByType(type);
        return ResponseEntity.ok(planes);
    }
}
