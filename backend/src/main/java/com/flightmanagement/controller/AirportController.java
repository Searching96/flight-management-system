package com.flightmanagement.controller;

import com.flightmanagement.dto.AirportDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.AirportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {
    
    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AirportDto>>> getAllAirports() {
        List<AirportDto> airports = airportService.getAllAirports();
        ApiResponse<List<AirportDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched all airports",
                airports,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AirportDto>> getAirportById(@PathVariable Integer id) {
        AirportDto airport = airportService.getAirportById(id);

        ApiResponse<AirportDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched airport with id: " + id,
                airport,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<AirportDto>> createAirport(@Valid @RequestBody AirportDto airportDto) {
        AirportDto airport = airportService.createAirport(airportDto);
        ApiResponse<AirportDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Airport created successfully",
                airport,
                null
        );

        return ResponseEntity.ok(apiResponse);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AirportDto>> updateAirport(@PathVariable Integer id, @Valid @RequestBody AirportDto airportDto) {
        AirportDto updatedAirport = airportService.updateAirport(id, airportDto);

        ApiResponse<AirportDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Airport updated successfully",
                updatedAirport,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAirport(@PathVariable Integer id) {
        airportService.deleteAirport(id);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Airport deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
    
    @GetMapping("/city/{cityName}")
    public ResponseEntity<ApiResponse<List<AirportDto>>> getAirportsByCity(@PathVariable String cityName) {
        List<AirportDto> airports = airportService.getAirportsByCity(cityName);

        ApiResponse<List<AirportDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched airports in city: " + cityName,
                airports,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/country/{countryName}")
    public ResponseEntity<ApiResponse<List<AirportDto>>> getAirportsByCountry(@PathVariable String countryName) {
        List<AirportDto> airports = airportService.getAirportsByCountry(countryName);

        ApiResponse<List<AirportDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched airports in country: " + countryName,
                airports,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/search/{name}")
    public ResponseEntity<ApiResponse<List<AirportDto>>> searchAirportsByName(@PathVariable String name) {
        List<AirportDto> airports = airportService.searchAirportsByName(name);

        ApiResponse<List<AirportDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Searched airports by name: " + name,
                airports,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}