package com.flightmanagement.config;

import com.flightmanagement.dto.*;
import com.flightmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
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
    
    @Override
    public void run(String... args) {
        System.out.println("🚀 Initializing Flight Management System Demo Data...");
        initializeParameters();
        initializeTicketClasses();
        initializeAirports();
        initializePlanes();
        initializeDemoAccounts();
        initializeDemoFlights();
        System.out.println("✅ Demo data initialization completed!");
    }
    
    private void initializeParameters() {
        try {
            parameterService.getParameterSet();
        } catch (RuntimeException e) {
            // No parameters exist, create default ones
            parameterService.initializeDefaultParameters();
            System.out.println("✓ Default parameters initialized");
        }
    }
    
    private void initializeTicketClasses() {
        try {
            if (ticketClassService.getAllTicketClasses().isEmpty()) {
                // Create default ticket classes
                ticketClassService.createTicketClass(new TicketClassDto(null, "Economy", "#3498db"));
                ticketClassService.createTicketClass(new TicketClassDto(null, "Business", "#f39c12"));
                ticketClassService.createTicketClass(new TicketClassDto(null, "First Class", "#e74c3c"));
                System.out.println("✓ Default ticket classes initialized");
            }
        } catch (Exception e) {
            System.err.println("Error initializing ticket classes: " + e.getMessage());
        }
    }
    
    private void initializeAirports() {
        try {
            if (airportService.getAllAirports().isEmpty()) {
                // Create major Vietnamese airports
                airportService.createAirport(new AirportDto(null, "Tan Son Nhat International Airport", "Ho Chi Minh City", "Vietnam"));
                airportService.createAirport(new AirportDto(null, "Noi Bai International Airport", "Hanoi", "Vietnam"));
                airportService.createAirport(new AirportDto(null, "Da Nang International Airport", "Da Nang", "Vietnam"));
                airportService.createAirport(new AirportDto(null, "Cam Ranh International Airport", "Nha Trang", "Vietnam"));
                airportService.createAirport(new AirportDto(null, "Phu Quoc International Airport", "Phu Quoc", "Vietnam"));
                System.out.println("✓ Default airports initialized");
            }
        } catch (Exception e) {
            System.err.println("Error initializing airports: " + e.getMessage());
        }
    }
    
    private void initializePlanes() {
        try {
            if (planeService.getAllPlanes().isEmpty()) {
                // Create sample aircraft
                planeService.createPlane(new PlaneDto(null, "VN-A001", "Airbus A321", 184));
                planeService.createPlane(new PlaneDto(null, "VN-A002", "Boeing 787-9", 294));
                planeService.createPlane(new PlaneDto(null, "VN-A003", "Airbus A350", 305));
                planeService.createPlane(new PlaneDto(null, "VN-A004", "Boeing 737-800", 162));
                planeService.createPlane(new PlaneDto(null, "VN-A005", "ATR 72", 70));
                System.out.println("✓ Default planes initialized");
            }
        } catch (Exception e) {
            System.err.println("Error initializing planes: " + e.getMessage());
        }
    }
    
    private void initializeDemoAccounts() {
        try {
            if (accountService.getAllAccounts().isEmpty()) {
                // Create admin account
                RegisterDto admin = new RegisterDto();
                admin.setAccountName("System Administrator");
                admin.setEmail("admin@flightmanagement.com");
                admin.setPassword("admin123");
                admin.setCitizenId("999999999");
                admin.setPhoneNumber("0123456789");
                admin.setAccountType(1); // Admin
                accountService.createAccount(admin);
                
                // Create demo customer
                RegisterDto customer = new RegisterDto();
                customer.setAccountName("John Doe");
                customer.setEmail("customer@demo.com");
                customer.setPassword("demo123");
                customer.setCitizenId("123456789");
                customer.setPhoneNumber("0987654321");
                customer.setAccountType(2); // Customer
                accountService.createAccount(customer);
                
                System.out.println("✓ Demo accounts created (admin@flightmanagement.com / customer@demo.com)");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo accounts: " + e.getMessage());
        }
    }
    
    private void initializeDemoFlights() {
        try {
            if (flightService.getAllFlights().isEmpty()) {
                LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
                
                // Create sample flights
                FlightDto flight1 = new FlightDto();
                flight1.setFlightCode("VN001");
                flight1.setPlaneId(1);
                flight1.setDepartureAirportId(1); // SGN
                flight1.setArrivalAirportId(2);   // HAN
                flight1.setDepartureTime(tomorrow.withHour(8).withMinute(0));
                flight1.setArrivalTime(tomorrow.withHour(10).withMinute(30));
                flightService.createFlight(flight1);
                
                FlightDto flight2 = new FlightDto();
                flight2.setFlightCode("VN002");
                flight2.setPlaneId(2);
                flight2.setDepartureAirportId(2); // HAN
                flight2.setArrivalAirportId(3);   // DAD
                flight2.setDepartureTime(tomorrow.withHour(14).withMinute(0));
                flight2.setArrivalTime(tomorrow.withHour(15).withMinute(45));
                flightService.createFlight(flight2);
                
                // Add flight ticket classes
                initializeFlightTicketClasses();
                
                System.out.println("✓ Demo flights created");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo flights: " + e.getMessage());
        }
    }
    
    private void initializeFlightTicketClasses() {
        try {
            // Add ticket classes for flight 1
            FlightTicketClassDto ftc1 = new FlightTicketClassDto();
            ftc1.setFlightId(1);
            ftc1.setTicketClassId(1); // Economy
            ftc1.setTicketQuantity(100);
            ftc1.setRemainingTicketQuantity(100);
            ftc1.setSpecifiedFare(new BigDecimal("1500000")); // 1.5M VND
            flightTicketClassService.createFlightTicketClass(ftc1);
            
            FlightTicketClassDto ftc2 = new FlightTicketClassDto();
            ftc2.setFlightId(1);
            ftc2.setTicketClassId(2); // Business
            ftc2.setTicketQuantity(20);
            ftc2.setRemainingTicketQuantity(20);
            ftc2.setSpecifiedFare(new BigDecimal("3000000")); // 3M VND
            flightTicketClassService.createFlightTicketClass(ftc2);
            
            System.out.println("✓ Flight ticket classes created");
        } catch (Exception e) {
            System.err.println("⚠️ Error creating flight ticket classes: " + e.getMessage());
        }
    }
}
