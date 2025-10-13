package com.flightmanagement.controller;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.FlightDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flight-details")
@Tag(name = "FlightDetail", description = "Operations related to flight details")
public class FlightDetailController {
    
    private final FlightDetailService flightDetailService;

    public FlightDetailController(FlightDetailService flightDetailService) {
        this.flightDetailService = flightDetailService;
    }
    
    @Operation(summary = "Get all flight details")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightDetailDto>>> getAllFlightDetails() {
        List<FlightDetailDto> flightDetails = flightDetailService.getAllFlightDetails();
        ApiResponse<List<FlightDetailDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight details retrieved successfully",
                flightDetails,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Create a new flight detail")
    @PostMapping("/")
    public ResponseEntity<ApiResponse<FlightDetailDto>> createFlightDetail(@RequestBody FlightDetailDto flightDetailDto) {
        FlightDetailDto createdFlightDetail = flightDetailService.createFlightDetail(flightDetailDto);
        ApiResponse<FlightDetailDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Flight detail created successfully",
                createdFlightDetail,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @Operation(summary = "Update a flight detail")
    @PutMapping("/{flightId}/{mediumAirportId}")
    public ResponseEntity<ApiResponse<FlightDetailDto>> updateFlightDetail(@PathVariable Integer flightId,
                                                                           @PathVariable Integer mediumAirportId,
                                                                           @RequestBody FlightDetailDto flightDetailDto) {
        FlightDetailDto updatedFlightDetail = flightDetailService.updateFlightDetail(flightId, mediumAirportId, flightDetailDto);
        ApiResponse<FlightDetailDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight detail updated successfully",
                updatedFlightDetail,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Delete a flight detail")
    @DeleteMapping("/{flightId}/{mediumAirportId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlightDetail(@PathVariable Integer flightId, @PathVariable Integer mediumAirportId) {
        flightDetailService.deleteFlightDetail(flightId, mediumAirportId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Flight detail deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
    
    @Operation(summary = "Get flight details by flight ID")
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<ApiResponse<List<FlightDetailDto>>> getFlightDetailsByFlightId(@PathVariable Integer flightId) {
        List<FlightDetailDto> flightDetails = flightDetailService.getFlightDetailsByFlightId(flightId);
        ApiResponse<List<FlightDetailDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight details for flight retrieved successfully",
                flightDetails,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Get flight details by airport ID")
    @GetMapping("/airport/{airportId}")
    public ResponseEntity<ApiResponse<List<FlightDetailDto>>> getFlightDetailsByAirportId(@PathVariable Integer airportId) {
        List<FlightDetailDto> flightDetails = flightDetailService.getFlightDetailsByAirportId(airportId);
        ApiResponse<List<FlightDetailDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight details for airport retrieved successfully",
                flightDetails,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}