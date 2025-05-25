package com.flightmanagement.controller;

import com.flightmanagement.dto.AirportDto;
import com.flightmanagement.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {
    
    @Autowired
    private AirportService airportService;
    
    @GetMapping
    public ResponseEntity<List<AirportDto>> getAllAirports() {
        try {
            List<AirportDto> airports = airportService.getAllAirports();
            return ResponseEntity.ok(airports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AirportDto> getAirportById(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            AirportDto airport = airportService.getAirportById(id);
            return ResponseEntity.ok(airport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<AirportDto> createAirport(@Valid @RequestBody AirportDto airportDto) {
        try {
            if (airportDto == null) {
                return ResponseEntity.badRequest().build();
            }
            AirportDto createdAirport = airportService.createAirport(airportDto);
            return new ResponseEntity<>(createdAirport, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AirportDto> updateAirport(@PathVariable Integer id, @Valid @RequestBody AirportDto airportDto) {
        try {
            if (id == null || id <= 0 || airportDto == null) {
                return ResponseEntity.badRequest().build();
            }
            AirportDto updatedAirport = airportService.updateAirport(id, airportDto);
            return ResponseEntity.ok(updatedAirport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            airportService.deleteAirport(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<AirportDto>> getAirportsByCity(@PathVariable String cityName) {
        try {
            if (cityName == null || cityName.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<AirportDto> airports = airportService.getAirportsByCity(cityName);
            return ResponseEntity.ok(airports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/country/{countryName}")
    public ResponseEntity<List<AirportDto>> getAirportsByCountry(@PathVariable String countryName) {
        try {
            if (countryName == null || countryName.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<AirportDto> airports = airportService.getAirportsByCountry(countryName);
            return ResponseEntity.ok(airports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search/{name}")
    public ResponseEntity<List<AirportDto>> searchAirportsByName(@PathVariable String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<AirportDto> airports = airportService.searchAirportsByName(name);
            return ResponseEntity.ok(airports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
