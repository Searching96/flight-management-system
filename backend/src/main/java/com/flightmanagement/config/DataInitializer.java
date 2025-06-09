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
    @Autowired
    private EmployeeService employeeService;

    @Override
    public void run(String... args) {
        System.out.println("🚀 Initializing Flight Management System Demo Data...");
        initializeParameters();
        initializeTicketClasses();
        initializeAirports();
        initializePlanes();
        initializeDemoAccounts();
        initializeDemoFlights();
        initializeDemoPassengers();
        System.out.println("✅ Demo data initialization completed!");
    }

    private void initializeParameters() {
        try {
            parameterService.getParameterSet();
        } catch (RuntimeException e) {
            parameterService.initializeDefaultParameters();
            System.out.println("✓ Default parameters initialized");
        }
    }

    private void initializeTicketClasses() {
        try {
            if (ticketClassService.getAllTicketClasses().isEmpty()) {
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
                // Admin account
                RegisterDto admin = new RegisterDto();
                admin.setAccountName("System Administrator");
                admin.setEmail("admin@flightms.com");
                admin.setPassword("admin123");
                admin.setCitizenId("999999999");
                admin.setPhoneNumber("0123456789");
                admin.setAccountType(2); // Admin
                accountService.createAccount(admin);

                AccountDto result = accountService.getAccountByEmail(admin.getEmail());

                // Employee account
                RegisterDto employee = new RegisterDto();
                employee.setAccountName("Flight Operations Employee");
                employee.setEmail("employee@flightms.com");
                employee.setPassword("employee123");
                employee.setCitizenId("888888888");
                employee.setPhoneNumber("0123456788");
                employee.setAccountType(2); // Employee
                accountService.createAccount(employee);

                // Customer account
                RegisterDto customer = new RegisterDto();
                customer.setAccountName("John Doe");
                customer.setEmail("customer@flightms.com");
                customer.setPassword("customer123");
                customer.setCitizenId("123456789");
                customer.setPhoneNumber("0987654321");
                customer.setAccountType(1); // Customer
                accountService.createAccount(customer);

                // Test customer
                RegisterDto testCustomer = new RegisterDto();
                testCustomer.setAccountName("Jane Smith");
                testCustomer.setEmail("john.doe@email.com");
                testCustomer.setPassword("password123");
                testCustomer.setCitizenId("987654321");
                testCustomer.setPhoneNumber("0987654322");
                testCustomer.setAccountType(1); // Customer
                accountService.createAccount(testCustomer);

                System.out.println("✓ Demo accounts created with correct email addresses");
                System.out.println("  - Admin: admin@flightms.com / admin123");
                System.out.println("  - Employee: employee@flightms.com / employee123");
                System.out.println("  - Customer: customer@flightms.com / customer123");
                System.out.println("  - Test Customer: john.doe@email.com / password123");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo accounts: " + e.getMessage());
        }
    }

    private void initializeDemoFlights() {
        try {
            if (flightService.getAllFlights().isEmpty()) {
                LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
                LocalDateTime dayAfter = LocalDateTime.now().plusDays(2);

                // Flight 1: SGN -> HAN
                FlightDto flight1 = new FlightDto();
                flight1.setFlightCode("VN001");
                flight1.setPlaneId(1);
                flight1.setDepartureAirportId(1);
                flight1.setArrivalAirportId(2);
                flight1.setDepartureTime(tomorrow.withHour(8).withMinute(0));
                flight1.setArrivalTime(tomorrow.withHour(10).withMinute(30));
                flightService.createFlight(flight1);

                // Flight 2: HAN -> DAD
                FlightDto flight2 = new FlightDto();
                flight2.setFlightCode("VN002");
                flight2.setPlaneId(2);
                flight2.setDepartureAirportId(2);
                flight2.setArrivalAirportId(3);
                flight2.setDepartureTime(tomorrow.withHour(14).withMinute(0));
                flight2.setArrivalTime(tomorrow.withHour(15).withMinute(45));
                flightService.createFlight(flight2);

                // Flight 3: DAD -> CXR
                FlightDto flight3 = new FlightDto();
                flight3.setFlightCode("VN003");
                flight3.setPlaneId(3);
                flight3.setDepartureAirportId(3);
                flight3.setArrivalAirportId(4);
                flight3.setDepartureTime(tomorrow.withHour(16).withMinute(30));
                flight3.setArrivalTime(tomorrow.withHour(17).withMinute(45));
                flightService.createFlight(flight3);

                // Flight 4: SGN -> PQC
                FlightDto flight4 = new FlightDto();
                flight4.setFlightCode("VN004");
                flight4.setPlaneId(4);
                flight4.setDepartureAirportId(1);
                flight4.setArrivalAirportId(5);
                flight4.setDepartureTime(dayAfter.withHour(9).withMinute(0));
                flight4.setArrivalTime(dayAfter.withHour(10).withMinute(15));
                flightService.createFlight(flight4);

                // Flight 5: PQC -> SGN
                FlightDto flight5 = new FlightDto();
                flight5.setFlightCode("VN005");
                flight5.setPlaneId(5);
                flight5.setDepartureAirportId(5);
                flight5.setArrivalAirportId(1);
                flight5.setDepartureTime(dayAfter.withHour(18).withMinute(0));
                flight5.setArrivalTime(dayAfter.withHour(19).withMinute(15));
                flightService.createFlight(flight5);

                initializeAllFlightTicketClasses();
                System.out.println("✓ Demo flights and ticket classes created");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo flights: " + e.getMessage());
        }
    }

    private void initializeAllFlightTicketClasses() {
        try {
            // Flight 1 (SGN -> HAN)
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(1, 1, 100, new BigDecimal("1500000"))); // Economy
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(1, 2, 20, new BigDecimal("3000000")));  // Business
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(1, 3, 10, new BigDecimal("5000000")));  // First Class

            // Flight 2 (HAN -> DAD)
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(2, 1, 150, new BigDecimal("1200000"))); // Economy
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(2, 2, 30, new BigDecimal("2500000")));  // Business

            // Flight 3 (DAD -> CXR)
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(3, 1, 80, new BigDecimal("800000")));   // Economy
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(3, 2, 15, new BigDecimal("1800000")));  // Business

            // Flight 4 (SGN -> PQC)
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(4, 1, 120, new BigDecimal("1000000"))); // Economy
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(4, 2, 25, new BigDecimal("2200000")));  // Business
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(4, 3, 8, new BigDecimal("3500000")));   // First Class

            // Flight 5 (PQC -> SGN)
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(5, 1, 60, new BigDecimal("950000")));   // Economy
            flightTicketClassService.createFlightTicketClass(createFlightTicketClass(5, 2, 10, new BigDecimal("2100000")));  // Business

            System.out.println("✓ Flight ticket classes created for all flights");
        } catch (Exception e) {
            System.err.println("⚠️ Error creating flight ticket classes: " + e.getMessage());
        }
    }

    private void initializeDemoPassengers() {
        try {
            if (passengerService.getAllPassengers().isEmpty()) {
                passengerService.createPassenger(new PassengerDto(null, "Nguyen Van A", "nguyenvana@email.com", "123456789", "0987654321"));
                passengerService.createPassenger(new PassengerDto(null, "Tran Thi B", "tranthib@email.com", "987654321", "0123456789"));
                passengerService.createPassenger(new PassengerDto(null, "Le Van C", "levanc@email.com", "456789123", "0369852147"));
                passengerService.createPassenger(new PassengerDto(null, "Pham Thi D", "phamthid@email.com", "789123456", "0147258369"));
                passengerService.createPassenger(new PassengerDto(null, "Hoang Van E", "hoangvane@email.com", "321654987", "0258147963"));
                System.out.println("✓ Demo passengers created");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo passengers: " + e.getMessage());
        }
    }

    private FlightTicketClassDto createFlightTicketClass(int flightId, int ticketClassId, int quantity, BigDecimal fare) {
        FlightTicketClassDto ftc = new FlightTicketClassDto();
        ftc.setFlightId(flightId);
        ftc.setTicketClassId(ticketClassId);
        ftc.setTicketQuantity(quantity);
        ftc.setRemainingTicketQuantity(quantity);
        ftc.setSpecifiedFare(fare);
        return ftc;
    }
}
