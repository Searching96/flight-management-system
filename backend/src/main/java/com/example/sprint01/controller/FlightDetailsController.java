package com.example.sprint01.controller;

import com.example.sprint01.dto.FlightDetailsDto;
import com.example.sprint01.service.FlightDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("api/flight-details")
public class FlightDetailsController {
    private FlightDetailsService flightDetailsService;

    @PostMapping
    public ResponseEntity<FlightDetailsDto> createFlightDetails(@RequestBody FlightDetailsDto flightDetailsDto) {
        FlightDetailsDto savedFlightDetails = flightDetailsService.createFlightDetails(flightDetailsDto);
        return new ResponseEntity<>(savedFlightDetails, HttpStatus.CREATED);
    }

    @GetMapping("{flightId}")
    public ResponseEntity<List<FlightDetailsDto>> getFlightDetailsByFlightId(@PathVariable Long flightId) {
        List<FlightDetailsDto> flightDetails = flightDetailsService.getFlightDetailsByFlightId(flightId);
        return ResponseEntity.ok(flightDetails);
    }

    @GetMapping("{flightId}/{mediumAirportId}")
    public ResponseEntity<FlightDetailsDto> getFlightDetailsById(@PathVariable Long flightId, @PathVariable Long mediumAirportId) {
        FlightDetailsDto flightDetails = flightDetailsService.getFlightDetailsById(flightId, mediumAirportId);
        return ResponseEntity.ok(flightDetails);
    }

    @PutMapping("{flightId}/{mediumAirportId}")
    public ResponseEntity<FlightDetailsDto> updateFlightDetails(@PathVariable Long flightId, @PathVariable Long mediumAirportId,
                                                                @RequestBody FlightDetailsDto updatedFlightDetails) {
        FlightDetailsDto updatedDetails = flightDetailsService.updateFlightDetails(flightId, mediumAirportId, updatedFlightDetails);
        return ResponseEntity.ok(updatedDetails);
    }

    @DeleteMapping("{flightId}/{mediumAirportId}")
    public ResponseEntity<String> deleteFlightDetails(@PathVariable Long flightId, @PathVariable Long mediumAirportId) {
        flightDetailsService.deleteFlightDetails(flightId, mediumAirportId);
        return ResponseEntity.ok("Flight details deleted successfully for flightId: " + flightId + " and mediumAirportId: " + mediumAirportId);
    }
}