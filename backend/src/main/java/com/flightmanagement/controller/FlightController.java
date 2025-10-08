package com.flightmanagement.controller;

import com.flightmanagement.dto.FlightRequest;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.FlightSearchCriteria;
import com.flightmanagement.service.FlightService;
import com.flightmanagement.entity.ApiResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<List<FlightDto>>> getAllFlights() {
        List<FlightDto> flights = flightService.getAllFlights();
        ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flights retrieved successfully",
                flights,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> getFlightById(@PathVariable Integer id) {
        FlightDto flight = flightService.getFlightById(id);
        ApiResponse<FlightDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight retrieved successfully",
                flight,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FlightDto>> createFlight(@Valid @RequestBody FlightRequest request) {
        FlightDto createdFlight = flightService.createFlight(request);
        ApiResponse<FlightDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Flight created successfully",
                createdFlight,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlight(@PathVariable Integer id, @RequestBody FlightRequest updateRequest) {
        FlightDto updatedFlight = flightService.updateFlight(id, updateRequest);
        ApiResponse<FlightDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight updated successfully",
                updatedFlight,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Flight deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<FlightDto>> getFlightByCode(@PathVariable String code) {
        FlightDto flight = flightService.getFlightByCode(code);
        ApiResponse<FlightDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flight retrieved by code successfully",
                flight,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    // Change from POST to GET and use @RequestParam instead of @RequestBody
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightDto>>> searchFlights(
            @RequestParam Integer departureAirportId,
            @RequestParam Integer arrivalAirportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDate,
            @RequestParam Integer passengerCount,
            @RequestParam(required = false) Integer ticketClassId) {
        
        try {
            // Create search criteria
            FlightSearchCriteria criteria = new FlightSearchCriteria();
            criteria.setDepartureAirportId(departureAirportId);
            criteria.setArrivalAirportId(arrivalAirportId);
            criteria.setDepartureDate(departureDate);
            criteria.setReturnDate(returnDate);
            criteria.setPassengerCount(passengerCount);
            criteria.setTicketClassId(ticketClassId);
            
            List<FlightDto> flights = flightService.searchFlights(criteria);

            ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Flight search completed",
                    flights,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Flight search failed: " + e.getMessage(),
                    null,
                    "INTERNAL_SERVER_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/route")
    public ResponseEntity<ApiResponse<List<FlightDto>>> getFlightsByRoute(
            @RequestParam Integer departureAirportId,
            @RequestParam Integer arrivalAirportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate) {
        List<FlightDto> flights = flightService.getFlightsByRoute(departureAirportId, arrivalAirportId, departureDate);
        ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flights retrieved by route successfully",
                flights,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<FlightDto>>> getFlightsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<FlightDto> flights = flightService.getFlightsByDateRange(startDate, endDate);
        ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Flights retrieved by date range successfully",
                flights,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search/date")
    public ResponseEntity<ApiResponse<List<FlightDto>>> searchFlightsByDate(@RequestParam String departureDate) {
        try {
            if (departureDate == null || departureDate.trim().isEmpty()) {
                ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                        HttpStatus.BAD_REQUEST,
                        "departureDate is required",
                        null,
                        "BAD_REQUEST"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            }
            List<FlightDto> flights = flightService.searchFlightsByDate(departureDate);
            ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Flights retrieved for date successfully",
                    flights,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<List<FlightDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to search flights by date: " + e.getMessage(),
                    null,
                    "INTERNAL_SERVER_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
    // ...existing code...
}