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
        // This will delete old records and create a new one
        ParameterDto updatedParameters = parameterService.updateParameters(parameterDto);
        return ResponseEntity.ok(updatedParameters);
    }
    
    @PutMapping("/max-medium-airports/{value}")
    public ResponseEntity<ParameterDto> updateMaxMediumAirports(@PathVariable int value) {
        parameterService.updateMaxMediumAirports(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @PutMapping("/min-flight-duration/{value}")
    public ResponseEntity<ParameterDto> updateMinFlightDuration(@PathVariable int value) {
        parameterService.updateMinFlightDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @PutMapping("/max-layover-duration/{value}")
    public ResponseEntity<ParameterDto> updateMaxLayoverDuration(@PathVariable int value) {
        parameterService.updateMaxLayoverDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @PutMapping("/min-layover-duration/{value}")
    public ResponseEntity<ParameterDto> updateMinLayoverDuration(@PathVariable int value) {
        parameterService.updateMinLayoverDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @PutMapping("/min-booking-advance/{value}")
    public ResponseEntity<ParameterDto> updateMinBookingInAdvanceDuration(@PathVariable int value) {
        parameterService.updateMinBookingInAdvanceDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @PutMapping("/max-booking-hold/{value}")
    public ResponseEntity<ParameterDto> updateMaxBookingHoldDuration(@PathVariable int value) {
        parameterService.updateMaxBookingHoldDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<ParameterDto> initializeDefaultParameters() {
        parameterService.initializeDefaultParameters();
        // Return the newly created parameters
        ParameterDto newParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(newParameters);
    }
}
