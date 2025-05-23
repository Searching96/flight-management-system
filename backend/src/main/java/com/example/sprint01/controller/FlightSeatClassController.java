package com.example.sprint01.controller;

import com.example.sprint01.dto.FlightSeatClassDto;
import com.example.sprint01.service.FlightSeatClassService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("api/flight-details")
public class FlightSeatClassController {
    private FlightSeatClassService FlightSeatClassService;

    @PostMapping
    public ResponseEntity<FlightSeatClassDto> createFlightSeatClass(@RequestBody FlightSeatClassDto FlightSeatClassDto) {
        FlightSeatClassDto savedFlightSeatClass = FlightSeatClassService.createFlightSeatClass(FlightSeatClassDto);
        return new ResponseEntity<>(savedFlightSeatClass, HttpStatus.CREATED);
    }

    @GetMapping("{flightId}")
    public ResponseEntity<List<FlightSeatClassDto>> getFlightSeatClassByFlightId(@PathVariable Long flightId) {
        List<FlightSeatClassDto> FlightSeatClass = FlightSeatClassService.getFlightSeatClassByFlightId(flightId);
        return ResponseEntity.ok(FlightSeatClass);
    }

    @GetMapping("{flightId}/{seatClassId}")
    public ResponseEntity<FlightSeatClassDto> getFlightSeatClassById(@PathVariable Long flightId, @PathVariable Long seatClassId) {
        FlightSeatClassDto FlightSeatClass = FlightSeatClassService.getFlightSeatClassById(flightId, seatClassId);
        return ResponseEntity.ok(FlightSeatClass);
    }

    @PutMapping("{flightId}/{seatClassId}")
    public ResponseEntity<FlightSeatClassDto> updateFlightSeatClass(@PathVariable Long flightId, @PathVariable Long seatClassId,
                                                                @RequestBody FlightSeatClassDto updatedFlightSeatClass) {
        FlightSeatClassDto updatedDetails = FlightSeatClassService.updateFlightSeatClass(flightId, seatClassId, updatedFlightSeatClass);
        return ResponseEntity.ok(updatedDetails);
    }

    @DeleteMapping("{flightId}/{seatClassId}")
    public ResponseEntity<String> deleteFlightSeatClass(@PathVariable Long flightId, @PathVariable Long seatClassId) {
        FlightSeatClassService.deleteFlightSeatClass(flightId, seatClassId);
        return ResponseEntity.ok("Flight seat class deleted successfully for flightId: " + flightId + " and seatClassId: " + seatClassId);
    }
}