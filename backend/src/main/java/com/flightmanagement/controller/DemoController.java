package com.flightmanagement.controller;

import com.flightmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
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

    @GetMapping("/health")
    public String healthCheck() {
        return "Demo API is running!";
    }
    
    @GetMapping("/info")
    public Map<String, Object> getDemoInfo() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> testingData = new HashMap<>();
        testingData.put("parameters", parameterService.getParameterSet());
        testingData.put("ticketClasses", ticketClassService.getAllTicketClasses());
        testingData.put("airports", airportService.getAllAirports());
        testingData.put("planes", planeService.getAllPlanes());
        testingData.put("accounts", accountService.getAllAccounts());
        testingData.put("passengers", passengerService.getAllPassengers());
        testingData.put("flights", flightService.getAllFlights());
        testingData.put("flightTicketClasses", flightTicketClassService.getAllFlightTicketClasses());
        
        response.put("testing_data", testingData);
        return response;
    }
}
