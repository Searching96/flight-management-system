package com.flightmanagement.controller;

import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Demo", description = "Demo operations for testing and development")
public class DemoController {

    private final ParameterService parameterService;
    
    private final TicketClassService ticketClassService;
    
    private final AirportService airportService;
    
    private final PlaneService planeService;
    
    private final AccountService accountService;

    private final PassengerService passengerService;
    
    private final FlightService flightService;

    public DemoController(ParameterService parameterService, TicketClassService ticketClassService,
                          AirportService airportService, PlaneService planeService,
                          AccountService accountService, PassengerService passengerService,
                          FlightService flightService) {
        this.parameterService = parameterService;
        this.ticketClassService = ticketClassService;
        this.airportService = airportService;
        this.planeService = planeService;
        this.accountService = accountService;
        this.passengerService = passengerService;
        this.flightService = flightService;
    }
    
    private FlightTicketClassService flightTicketClassService;

    @Operation(summary = "Health check for demo API")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        ApiResponse<String> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Health check successful",
                "Demo API is running!",
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Get demo information")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDemoInfo() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> testingData = new HashMap<>();
        testingData.put("parameters", parameterService.getLatestParameter());
        testingData.put("ticketClasses", ticketClassService.getAllTicketClasses());
        testingData.put("airports", airportService.getAllAirports());
        testingData.put("planes", planeService.getAllPlanes());
        testingData.put("accounts", accountService.getAllAccounts());
        testingData.put("passengers", passengerService.getAllPassengers());
        testingData.put("flights", flightService.getAllFlights());
        testingData.put("flightTicketClasses", flightTicketClassService.getAllFlightTicketClasses());
        
        response.put("testing_data", testingData);
        
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Demo information retrieved successfully",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
