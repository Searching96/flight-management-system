package com.flightmanagement.controller;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    
    @Autowired
    private PassengerService passengerService;
    
    @GetMapping
    public ResponseEntity<List<PassengerDto>> getAllPassengers() {
        List<PassengerDto> passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PassengerDto> getPassengerById(@PathVariable Integer id) {
        PassengerDto passenger = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passenger);
    }
    
    @PostMapping
    public ResponseEntity<PassengerDto> createPassenger(@RequestBody PassengerDto passengerDto) {
        PassengerDto createdPassenger = passengerService.createPassenger(passengerDto);
        return new ResponseEntity<>(createdPassenger, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PassengerDto> updatePassenger(@PathVariable Integer id, @RequestBody PassengerDto passengerDto) {
        PassengerDto updatedPassenger = passengerService.updatePassenger(id, passengerDto);
        return ResponseEntity.ok(updatedPassenger);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Integer id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/citizen-id/{citizenId}")
    public ResponseEntity<PassengerDto> getPassengerByCitizenId(@PathVariable String citizenId) {
        PassengerDto passenger = passengerService.getPassengerByCitizenId(citizenId);
        return ResponseEntity.ok(passenger);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<List<PassengerDto>> getPassengersByEmail(@PathVariable String email) {
        List<PassengerDto> passengers = passengerService.getPassengersByEmail(email);
        return ResponseEntity.ok(passengers);
    }
    
    @GetMapping("/search/{name}")
    public ResponseEntity<List<PassengerDto>> searchPassengersByName(@PathVariable String name) {
        List<PassengerDto> passengers = passengerService.searchPassengersByName(name);
        return ResponseEntity.ok(passengers);
    }
}
