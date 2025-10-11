package com.flightmanagement.controller;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

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
    // ...existing code...
}