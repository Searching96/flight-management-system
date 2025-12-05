package com.flightmanagement.controller;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<ParameterDto>> getLatestParameter() {
        ParameterDto parameter = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Parameters retrieved successfully",
                parameter,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update system parameters")
    @PutMapping
    public ResponseEntity<ApiResponse<ParameterDto>> updateParameters(@RequestBody ParameterDto parameterDto) {
        // This will delete old records and create a new one
        ParameterDto updatedParameters = parameterService.updateParameters(parameterDto);

        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Parameters updated successfully",
                updatedParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update maximum medium airports")
    @PutMapping("/max-medium-airports/{value}")
    public ResponseEntity<ApiResponse<ParameterDto>> updateMaxMediumAirports(@PathVariable int value) {
        parameterService.updateMaxMediumAirports(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Maximum medium airports updated successfully",
                latestParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update minimum flight duration")
    @PutMapping("/min-flight-duration/{value}")
    public ResponseEntity<ApiResponse<ParameterDto>> updateMinFlightDuration(@PathVariable int value) {
        parameterService.updateMinFlightDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Minimum flight duration updated successfully",
                latestParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update maximum layover duration")
    @PutMapping("/max-layover-duration/{value}")
    public ResponseEntity<ApiResponse<ParameterDto>> updateMaxLayoverDuration(@PathVariable int value) {
        parameterService.updateMaxLayoverDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Maximum layover duration updated successfully",
                latestParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update minimum layover duration")
    @PutMapping("/min-layover-duration/{value}")
    public ResponseEntity<ApiResponse<ParameterDto>> updateMinLayoverDuration(@PathVariable int value) {
        parameterService.updateMinLayoverDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Minimum layover duration updated successfully",
                latestParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update minimum booking in advance duration")
    @PutMapping("/min-booking-advance/{value}")
    public ResponseEntity<ApiResponse<ParameterDto>> updateMinBookingInAdvanceDuration(@PathVariable int value) {
        parameterService.updateMinBookingInAdvanceDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Minimum booking advance duration updated successfully",
                latestParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update maximum booking hold duration")
    @PutMapping("/max-booking-hold/{value}")
    public ResponseEntity<ApiResponse<ParameterDto>> updateMaxBookingHoldDuration(@PathVariable int value) {
        parameterService.updateMaxBookingHoldDuration(value);
        // Return the latest parameters after update
        ParameterDto latestParameters = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Maximum booking hold duration updated successfully",
                latestParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Initialize default parameters")
    @PostMapping("/initialize")
    public ResponseEntity<ApiResponse<ParameterDto>> initializeDefaultParameters() {
        parameterService.initializeDefaultParameters();
        // Return the newly created parameters
        ParameterDto newParameters = parameterService.getLatestParameter();
        
        ApiResponse<ParameterDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Default parameters initialized successfully",
                newParameters,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}