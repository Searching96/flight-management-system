package com.flightmanagement.controller;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.service.FlightDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flight-details")
public class FlightDetailController {
    
    @Autowired
    private FlightDetailService flightDetailService;
    
    @GetMapping
    public ResponseEntity<List<FlightDetailDto>> getAllFlightDetails() {
        List<FlightDetailDto> flightDetails = flightDetailService.getAllFlightDetails();
        return ResponseEntity.ok(flightDetails);
    }
    
    @PostMapping
    public ResponseEntity<FlightDetailDto> createFlightDetail(@RequestBody FlightDetailDto flightDetailDto) {
        FlightDetailDto createdFlightDetail = flightDetailService.createFlightDetail(flightDetailDto);
        return new ResponseEntity<>(createdFlightDetail, HttpStatus.CREATED);
    }
    
    @PutMapping("/{flightId}/{mediumAirportId}")
    public ResponseEntity<FlightDetailDto> updateFlightDetail(@PathVariable Integer flightId, 
                                                             @PathVariable Integer mediumAirportId, 
                                                             @RequestBody FlightDetailDto flightDetailDto) {
        FlightDetailDto updatedFlightDetail = flightDetailService.updateFlightDetail(flightId, mediumAirportId, flightDetailDto);
        return ResponseEntity.ok(updatedFlightDetail);
    }
    
    @DeleteMapping("/{flightId}/{mediumAirportId}")
    public ResponseEntity<Void> deleteFlightDetail(@PathVariable Integer flightId, @PathVariable Integer mediumAirportId) {
        flightDetailService.deleteFlightDetail(flightId, mediumAirportId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<FlightDetailDto>> getFlightDetailsByFlightId(@PathVariable Integer flightId) {
        List<FlightDetailDto> flightDetails = flightDetailService.getFlightDetailsByFlightId(flightId);
        return ResponseEntity.ok(flightDetails);
    }
    
    @GetMapping("/airport/{airportId}")
    public ResponseEntity<List<FlightDetailDto>> getFlightDetailsByAirportId(@PathVariable Integer airportId) {
        List<FlightDetailDto> flightDetails = flightDetailService.getFlightDetailsByAirportId(airportId);
        return ResponseEntity.ok(flightDetails);
    }
}
