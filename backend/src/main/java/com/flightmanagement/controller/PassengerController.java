package com.flightmanagement.controller;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.PassengerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@Tag(name = "Passenger", description = "Operations related to passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @Operation(summary = "Get all passengers")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PassengerDto>>> getAllPassengers() {
        List<PassengerDto> passengers = passengerService.getAllPassengers();
        ApiResponse<List<PassengerDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Passengers retrieved successfully",
                passengers,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get passenger by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerById(@PathVariable Integer id) {
        PassengerDto passenger = passengerService.getPassengerById(id);
        ApiResponse<PassengerDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Passenger retrieved successfully",
                passenger,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Create a new passenger")
    @PostMapping
    public ResponseEntity<ApiResponse<PassengerDto>> createPassenger(@RequestBody PassengerDto passengerDto) {
        PassengerDto createdPassenger = passengerService.createPassenger(passengerDto);
        ApiResponse<PassengerDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Passenger created successfully",
                createdPassenger,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "Update an existing passenger")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerDto>> updatePassenger(@PathVariable Integer id, @RequestBody PassengerDto passengerDto) {
        PassengerDto updatedPassenger = passengerService.updatePassenger(id, passengerDto);
        ApiResponse<PassengerDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Passenger updated successfully",
                updatedPassenger,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Delete a passenger")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePassenger(@PathVariable Integer id) {
        passengerService.deletePassenger(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Passenger deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @Operation(summary = "Get passenger by citizen ID")
    @GetMapping("/citizen-id/{citizenId}")
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerByCitizenId(@PathVariable String citizenId) {
        PassengerDto passenger = passengerService.getPassengerByCitizenId(citizenId);
        ApiResponse<PassengerDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Passenger retrieved successfully",
                passenger,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get passengers by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<List<PassengerDto>>> getPassengersByEmail(@PathVariable String email) {
        List<PassengerDto> passengers = passengerService.getPassengersByEmail(email);
        ApiResponse<List<PassengerDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Passengers retrieved by email successfully",
                passengers,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Search passengers by name")
    @GetMapping("/search/{name}")
    public ResponseEntity<ApiResponse<List<PassengerDto>>> searchPassengersByName(@PathVariable String name) {
        List<PassengerDto> passengers = passengerService.searchPassengersByName(name);
        ApiResponse<List<PassengerDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Passengers search completed",
                passengers,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}