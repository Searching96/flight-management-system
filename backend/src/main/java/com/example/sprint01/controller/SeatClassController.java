package com.example.sprint01.controller;

import com.example.sprint01.dto.SeatClassDto;
import com.example.sprint01.service.SeatClassService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("api/seat-classes")
public class SeatClassController {
    private SeatClassService seatClassService;

    // Build Add Seat Class REST API
    @PostMapping
    public ResponseEntity<SeatClassDto> createSeatClass(@RequestBody SeatClassDto seatClassDto) {
        SeatClassDto savedSeatClass = seatClassService.createAirport(seatClassDto);
        return new ResponseEntity<>(savedSeatClass, HttpStatus.CREATED);
    }

    // Build Get Seat Class by ID REST API
    @GetMapping("{id}")
    public ResponseEntity<SeatClassDto> getSeatClassById(@PathVariable("id") Long id) {
        SeatClassDto seatClassDto = seatClassService.getAirportById(id);
        return ResponseEntity.ok(seatClassDto);
    }

    // Build Get All Seat Classes REST API
    @GetMapping
    public ResponseEntity<List<SeatClassDto>> getAllSeatClasses() {
        List<SeatClassDto> seatClasses = seatClassService.getAllAirports();
        return ResponseEntity.ok(seatClasses);
    }

    // Build Update Seat Class REST API
    @PutMapping("{id}")
    public ResponseEntity<SeatClassDto> updateSeatClass(@PathVariable("id") Long id,
                                                        @RequestBody SeatClassDto updatedSeatClass) {
        SeatClassDto updatedSeatClassDto = seatClassService.updateAirport(id, updatedSeatClass);
        return ResponseEntity.ok(updatedSeatClassDto);
    }

    // Build Delete Seat Class REST API
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteSeatClass(@PathVariable("id") Long id) {
        seatClassService.deleteAirport(id);
        return ResponseEntity.ok("Seat class deleted successfully, id: " + id);
    }
}