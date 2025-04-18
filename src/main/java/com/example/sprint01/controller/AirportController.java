package com.example.sprint01.controller;

import com.example.sprint01.dto.AirportDto;
import com.example.sprint01.service.AirportService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/airports")
public class AirportController {
    private AirportService airportService;

    // Build Add Airport REST API
    @PostMapping
    // RequestBody annotation is used to bind the HTTP request body (JSON) to DTO object
    public ResponseEntity<AirportDto> createAirport(@RequestBody AirportDto airportDto) {
        AirportDto savedAirport = airportService.createAirport(airportDto);
        return new ResponseEntity<>(savedAirport, HttpStatus.CREATED);
    }

    // Build Get Airport by ID REST API
    @GetMapping("{id}")
    public ResponseEntity<AirportDto> getAirportById(@PathVariable("id") Long id) {
        AirportDto airportDto = airportService.getAirportById(id);
        return ResponseEntity.ok(airportDto);
    }

    // Build Get All Airports REST API
    @GetMapping
    public ResponseEntity<List<AirportDto>> getAllAirports() {
        List<AirportDto> airports = airportService.getAllAirports();
        return ResponseEntity.ok(airports);
    }
}
