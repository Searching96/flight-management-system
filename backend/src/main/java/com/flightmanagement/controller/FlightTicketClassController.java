package com.flightmanagement.controller;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.service.FlightTicketClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flight-ticket-classes")
public class FlightTicketClassController {
    
    @Autowired
    private FlightTicketClassService flightTicketClassService;
    
    @GetMapping
    public ResponseEntity<List<FlightTicketClassDto>> getAllFlightTicketClasses() {
        List<FlightTicketClassDto> flightTicketClasses = flightTicketClassService.getAllFlightTicketClasses();
        return ResponseEntity.ok(flightTicketClasses);
    }
    
    @GetMapping("/{flightId}/{ticketClassId}")
    public ResponseEntity<FlightTicketClassDto> getFlightTicketClassById(@PathVariable Integer flightId, 
                                                                        @PathVariable Integer ticketClassId) {
        FlightTicketClassDto flightTicketClass = flightTicketClassService.getFlightTicketClassById(flightId, ticketClassId);
        return ResponseEntity.ok(flightTicketClass);
    }
    
    @PostMapping
    public ResponseEntity<FlightTicketClassDto> createFlightTicketClass(@RequestBody FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClassDto createdFlightTicketClass = flightTicketClassService.createFlightTicketClass(flightTicketClassDto);
        return new ResponseEntity<>(createdFlightTicketClass, HttpStatus.CREATED);
    }
    
    @PutMapping("/{flightId}/{ticketClassId}")
    public ResponseEntity<FlightTicketClassDto> updateFlightTicketClass(@PathVariable Integer flightId, 
                                                                       @PathVariable Integer ticketClassId, 
                                                                       @RequestBody FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClassDto updatedFlightTicketClass = flightTicketClassService.updateFlightTicketClass(flightId, ticketClassId, flightTicketClassDto);
        return ResponseEntity.ok(updatedFlightTicketClass);
    }
    
    @DeleteMapping("/{flightId}/{ticketClassId}")
    public ResponseEntity<Void> deleteFlightTicketClass(@PathVariable Integer flightId, @PathVariable Integer ticketClassId) {
        flightTicketClassService.deleteFlightTicketClass(flightId, ticketClassId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<FlightTicketClassDto>> getFlightTicketClassesByFlightId(@PathVariable Integer flightId) {
        List<FlightTicketClassDto> flightTicketClasses = flightTicketClassService.getFlightTicketClassesByFlightId(flightId);
        return ResponseEntity.ok(flightTicketClasses);
    }
    
    @GetMapping("/ticket-class/{ticketClassId}")
    public ResponseEntity<List<FlightTicketClassDto>> getFlightTicketClassesByTicketClassId(@PathVariable Integer ticketClassId) {
        List<FlightTicketClassDto> flightTicketClasses = flightTicketClassService.getFlightTicketClassesByTicketClassId(ticketClassId);
        return ResponseEntity.ok(flightTicketClasses);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<FlightTicketClassDto>> getAvailableFlightTicketClasses() {
        List<FlightTicketClassDto> flightTicketClasses = flightTicketClassService.getAvailableFlightTicketClasses();
        return ResponseEntity.ok(flightTicketClasses);
    }
    
    @PutMapping("/{flightId}/{ticketClassId}/update-remaining")
    public ResponseEntity<Void> updateRemainingTickets(@PathVariable Integer flightId, 
                                                       @PathVariable Integer ticketClassId, 
                                                       @RequestParam Integer quantity) {
        flightTicketClassService.updateRemainingTickets(flightId, ticketClassId, quantity);
        return ResponseEntity.noContent().build();
    }
}
