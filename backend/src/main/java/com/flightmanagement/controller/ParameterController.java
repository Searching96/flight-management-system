package com.flightmanagement.controller;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parameters")
@Tag(name = "Parameter", description = "Operations related to system parameters")
public class ParameterController {
    
    private final ParameterService parameterService;

    public ParameterController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    @Operation(summary = "Get system parameters")
    @GetMapping
    public ResponseEntity<ParameterDto> getParameters() {
        ParameterDto parameters = parameterService.getParameterSet();
        return ResponseEntity.ok(parameters);
    }
    
    @Operation(summary = "Update system parameters")
    @PutMapping
    public ResponseEntity<ParameterDto> updateParameters(@RequestBody ParameterDto parameterDto) {
        // This will delete old records and create a new one
        ParameterDto updatedParameters = parameterService.updateParameters(parameterDto);
        return ResponseEntity.ok(updatedParameters);
    }
    
    @Operation(summary = "Update maximum medium airports")
    @PutMapping("/max-medium-airports/{value}")
    public ResponseEntity<ParameterDto> updateMaxMediumAirports(@PathVariable int value) {
        parameterService.updateMaxMediumAirports(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @Operation(summary = "Update minimum flight duration")
    @PutMapping("/min-flight-duration/{value}")
    public ResponseEntity<ParameterDto> updateMinFlightDuration(@PathVariable int value) {
        parameterService.updateMinFlightDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @Operation(summary = "Update maximum layover duration")
    @PutMapping("/max-layover-duration/{value}")
    public ResponseEntity<ParameterDto> updateMaxLayoverDuration(@PathVariable int value) {
        parameterService.updateMaxLayoverDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @Operation(summary = "Update minimum layover duration")
    @PutMapping("/min-layover-duration/{value}")
    public ResponseEntity<ParameterDto> updateMinLayoverDuration(@PathVariable int value) {
        parameterService.updateMinLayoverDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @Operation(summary = "Update minimum booking in advance duration")
    @PutMapping("/min-booking-advance/{value}")
    public ResponseEntity<ParameterDto> updateMinBookingInAdvanceDuration(@PathVariable int value) {
        parameterService.updateMinBookingInAdvanceDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @Operation(summary = "Update maximum booking hold duration")
    @PutMapping("/max-booking-hold/{value}")
    public ResponseEntity<ParameterDto> updateMaxBookingHoldDuration(@PathVariable int value) {
        parameterService.updateMaxBookingHoldDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(latestParameters);
    }
    
    @Operation(summary = "Initialize default parameters")
    @PostMapping("/initialize")
    public ResponseEntity<ParameterDto> initializeDefaultParameters() {
        parameterService.initializeDefaultParameters();
        // Return the newly created parameters
        ParameterDto newParameters = parameterService.getParameterSet();
        return ResponseEntity.ok(newParameters);
    }
}