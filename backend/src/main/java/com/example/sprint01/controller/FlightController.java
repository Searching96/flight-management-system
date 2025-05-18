package com.example.sprint01.controller;

import com.example.sprint01.dto.FlightDto;
import com.example.sprint01.service.FlightService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("api/flights")
public class FlightController {
    private FlightService flightService;

    @PostMapping
    public ResponseEntity<FlightDto> createFlight(@RequestBody FlightDto flightDto) {
        FlightDto savedFlight = flightService.createFlight(flightDto);
        return new ResponseEntity<>(savedFlight, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<FlightDto> getFlightById(@PathVariable("id") Long id) {
        FlightDto flightDto = flightService.getFlightById(id);
        return ResponseEntity.ok(flightDto);
    }

    @GetMapping
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        List<FlightDto> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @PutMapping("{id}")
    public ResponseEntity<FlightDto> updateFlight(@PathVariable("id") Long id,
                                                  @RequestBody FlightDto updatedFlight) {
        FlightDto updatedFlightDto = flightService.updateFlight(id, updatedFlight);
        return ResponseEntity.ok(updatedFlightDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteFlight(@PathVariable("id") Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok("Flight deleted successfully, id: " + id);
    }
}