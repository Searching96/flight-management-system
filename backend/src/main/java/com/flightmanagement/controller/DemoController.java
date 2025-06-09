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

    @Autowired
    private ParameterService parameterService;
    
    @Autowired
    private TicketClassService ticketClassService;
    
    @Autowired
    private AirportService airportService;
    
    @Autowired
    private PlaneService planeService;
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private PassengerService passengerService;
    
    @Autowired
    private FlightService flightService;
    
    @Autowired
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
