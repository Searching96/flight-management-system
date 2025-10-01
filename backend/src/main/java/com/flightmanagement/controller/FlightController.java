package com.flightmanagement.controller;

import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.FlightSearchCriteria;
import com.flightmanagement.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        List<FlightDto> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDto> getFlightById(@PathVariable Integer id) {
        FlightDto flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @PostMapping
    public ResponseEntity<FlightDto> createFlight(@RequestBody FlightDto flightDto) {
        FlightDto createdFlight = flightService.createFlight(flightDto);
        return new ResponseEntity<>(createdFlight, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDto> updateFlight(@PathVariable Integer id, @RequestBody FlightDto flightDto) {
        FlightDto updatedFlight = flightService.updateFlight(id, flightDto);
        return ResponseEntity.ok(updatedFlight);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<FlightDto> getFlightByCode(@PathVariable String code) {
        FlightDto flight = flightService.getFlightByCode(code);
        return ResponseEntity.ok(flight);
    }

    // Change from POST to GET and use @RequestParam instead of @RequestBody
    @GetMapping("/search")
    public ResponseEntity<List<FlightDto>> searchFlights(
            @RequestParam Integer departureAirportId,
            @RequestParam Integer arrivalAirportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDate,
            @RequestParam Integer passengerCount,
            @RequestParam(required = false) Integer ticketClassId) {
        
        try {
            // Log the search request for debugging
            System.out.println("Flight search request received:");
            System.out.println("Departure Airport ID: " + departureAirportId);
            System.out.println("Arrival Airport ID: " + arrivalAirportId);
            System.out.println("Departure Date: " + departureDate);
            System.out.println("Return Date: " + returnDate);
            System.out.println("Passenger Count: " + passengerCount);
            System.out.println("Ticket Class ID: " + ticketClassId);
            
            // Create search criteria
            FlightSearchCriteria criteria = new FlightSearchCriteria();
            criteria.setDepartureAirportId(departureAirportId);
            criteria.setArrivalAirportId(arrivalAirportId);
            criteria.setDepartureDate(departureDate);
            criteria.setReturnDate(returnDate);
            criteria.setPassengerCount(passengerCount);
            criteria.setTicketClassId(ticketClassId);
            
            List<FlightDto> flights = flightService.searchFlights(criteria);
            System.out.println("Found " + flights.size() + " flights");
            
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            System.err.println("Error in flight search: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Flight search failed: " + e.getMessage(), e);
        }
    }

    @GetMapping("/route")
    public ResponseEntity<List<FlightDto>> getFlightsByRoute(
            @RequestParam Integer departureAirportId,
            @RequestParam Integer arrivalAirportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate) {
        List<FlightDto> flights = flightService.getFlightsByRoute(departureAirportId, arrivalAirportId, departureDate);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<FlightDto>> getFlightsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<FlightDto> flights = flightService.getFlightsByDateRange(startDate, endDate);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/search/date")
    public ResponseEntity<List<FlightDto>> searchFlightsByDate(@RequestParam String departureDate) {
        try {
            if (departureDate == null || departureDate.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<FlightDto> flights = flightService.searchFlightsByDate(departureDate);
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
