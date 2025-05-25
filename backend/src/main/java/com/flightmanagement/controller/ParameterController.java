package com.flightmanagement.controller;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parameters")
public class ParameterController {
    
    @Autowired
    private ParameterService parameterService;
    
    @GetMapping
    public ResponseEntity<ParameterDto> getParameters() {
        ParameterDto parameters = parameterService.getParameterSet();
        return ResponseEntity.ok(parameters);
    }
    
    @PutMapping
    public ResponseEntity<ParameterDto> updateParameters(@RequestBody ParameterDto parameterDto) {
        ParameterDto updatedParameters = parameterService.updateParameters(parameterDto);
        return ResponseEntity.ok(updatedParameters);
    }
    
    @PutMapping("/max-medium-airports/{value}")
    public ResponseEntity<Void> updateMaxMediumAirports(@PathVariable int value) {
        parameterService.updateMaxMediumAirports(value);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/min-flight-duration/{value}")
    public ResponseEntity<Void> updateMinFlightDuration(@PathVariable int value) {
        parameterService.updateMinFlightDuration(value);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/max-layover-duration/{value}")
    public ResponseEntity<Void> updateMaxLayoverDuration(@PathVariable int value) {
        parameterService.updateMaxLayoverDuration(value);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/min-layover-duration/{value}")
    public ResponseEntity<Void> updateMinLayoverDuration(@PathVariable int value) {
        parameterService.updateMinLayoverDuration(value);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/min-booking-advance/{value}")
    public ResponseEntity<Void> updateMinBookingInAdvanceDuration(@PathVariable int value) {
        parameterService.updateMinBookingInAdvanceDuration(value);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/max-booking-hold/{value}")
    public ResponseEntity<Void> updateMaxBookingHoldDuration(@PathVariable int value) {
        parameterService.updateMaxBookingHoldDuration(value);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeDefaultParameters() {
        parameterService.initializeDefaultParameters();
        return ResponseEntity.noContent().build();
    }
}
