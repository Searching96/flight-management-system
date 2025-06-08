package com.flightmanagement.controller;

import com.flightmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
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
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("message", "Flight Management System is running");
        status.put("version", "1.0.0-DEMO");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Flight Management System");
        info.put("description", "Comprehensive flight booking and management system");
        info.put("version", "1.0.0-DEMO");
        info.put("features", new String[]{
                "Flight Search & Booking",
                "Airport Management",
                "Plane Fleet Management",
                "Customer Management",
                "Real-time Chat Support",
                "System Parameters Configuration"
        });

        Map<String, Object> testingData = new HashMap<>();
        testingData.put("parameters", parameterService.getParameterSet());
        testingData.put("ticketClasses", ticketClassService.getAllTicketClasses());
        testingData.put("airports", airportService.getAllAirports());
        testingData.put("planes", planeService.getAllPlanes());
        testingData.put("accounts", accountService.getAllAccounts());
        testingData.put("passengers", passengerService.getAllPassengers());
        testingData.put("flights", flightService.getAllFlights());
        testingData.put("flightTicketClasses", flightTicketClassService.getAllFlightTicketClasses());
        info.put("testing_data", testingData);

        // Replace the System.out.println statements with a nested map
        Map<String, String> testAccounts = new HashMap<>();
        testAccounts.put("Admin", "admin@flightms.com / admin123");
        testAccounts.put("Employee", "employee@flightms.com / employee123");
        testAccounts.put("Customer", "customer@flightms.com / customer123");
        testAccounts.put("Test Customer", "john.doe@email.com / password123");

        info.put("test_accounts", testAccounts);

        return ResponseEntity.ok(info);
    }
}
