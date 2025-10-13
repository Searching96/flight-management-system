package com.flightmanagement.controller;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.FlightTicketClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flight-ticket-classes")
@Tag(name = "FlightTicketClass", description = "Operations related to flight ticket classes")
public class FlightTicketClassController {
    
    private final FlightTicketClassService flightTicketClassService;

    public FlightTicketClassController(FlightTicketClassService flightTicketClassService) {
        this.flightTicketClassService = flightTicketClassService;
    }
    
    @Operation(summary = "Get all flight ticket classes")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightTicketClassDto>>> getAllFlightTicketClasses() {
        List<FlightTicketClassDto> flightTicketClasses = flightTicketClassService.getAllFlightTicketClasses();
        ApiResponse<List<FlightTicketClassDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight ticket classes retrieved successfully",
                flightTicketClasses,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Get flight ticket class by ID")
    @GetMapping("/{flightId}/{ticketClassId}")
    public ResponseEntity<ApiResponse<FlightTicketClassDto>> getFlightTicketClassById(
            @PathVariable Integer flightId, 
            @PathVariable Integer ticketClassId) {
        FlightTicketClassDto flightTicketClass = flightTicketClassService.getFlightTicketClassById(flightId, ticketClassId);
        ApiResponse<FlightTicketClassDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight ticket class retrieved successfully",
                flightTicketClass,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Get flight ticket classes by flight ID")
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<ApiResponse<List<FlightTicketClassDto>>> getFlightTicketClassesByFlightId(@PathVariable Integer flightId) {
        List<FlightTicketClassDto> flightTicketClasses = flightTicketClassService.getFlightTicketClassesByFlightId(flightId);
        ApiResponse<List<FlightTicketClassDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight ticket classes for flight retrieved successfully",
                flightTicketClasses,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Create a new flight ticket class")
    @PostMapping
    public ResponseEntity<ApiResponse<FlightTicketClassDto>> createFlightTicketClass(@RequestBody FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClassDto createdFlightTicketClass = flightTicketClassService.createFlightTicketClass(flightTicketClassDto);
        ApiResponse<FlightTicketClassDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Flight ticket class created successfully",
                createdFlightTicketClass,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @Operation(summary = "Update an existing flight ticket class")
    @PutMapping("/{flightId}/{ticketClassId}")
    public ResponseEntity<ApiResponse<FlightTicketClassDto>> updateFlightTicketClass(
            @PathVariable Integer flightId,
            @PathVariable Integer ticketClassId,
            @RequestBody FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClassDto updatedFlightTicketClass = flightTicketClassService.updateFlightTicketClass(flightId, ticketClassId, flightTicketClassDto);
        ApiResponse<FlightTicketClassDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight ticket class updated successfully",
                updatedFlightTicketClass,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Delete a flight ticket class")
    @DeleteMapping("/{flightId}/{ticketClassId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlightTicketClass(@PathVariable Integer flightId, @PathVariable Integer ticketClassId) {
        flightTicketClassService.deleteFlightTicketClass(flightId, ticketClassId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Flight ticket class deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
    
    @Operation(summary = "Get available flight ticket classes")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<FlightTicketClassDto>>> getAvailableFlightTicketClasses() {
        List<FlightTicketClassDto> availableFlightTicketClasses = flightTicketClassService.getAvailableFlightTicketClasses();
        ApiResponse<List<FlightTicketClassDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Available flight ticket classes retrieved successfully",
                availableFlightTicketClasses,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Calculate occupied seats by flight ID and ticket class ID")
    @GetMapping("/occupied-seats/{flightId}/{ticketClassId}")
    public ResponseEntity<ApiResponse<Integer>> calculateOccupiedSeatsByFlightIdAndTicketClassId(
            @PathVariable Integer flightId,
            @PathVariable Integer ticketClassId) {
        Integer occupiedSeats = flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(flightId, ticketClassId);
        ApiResponse<Integer> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Occupied seats calculated successfully",
                occupiedSeats,
                null
        );
        System.out.println("Occupied seats for flightId: " + flightId + ", ticketClassId: " + ticketClassId + " is " + occupiedSeats);
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Update remaining tickets for a flight ticket class")
    @PutMapping("/{flightId}/{ticketClassId}/update-remaining")
    public ResponseEntity<ApiResponse<Void>> updateRemainingTickets(
            @PathVariable Integer flightId,
            @PathVariable Integer ticketClassId,
            @RequestParam Integer quantity) {
        flightTicketClassService.updateRemainingTickets(flightId, ticketClassId, quantity);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Remaining tickets updated successfully",
                null,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}