package com.flightmanagement.config;

import com.flightmanagement.dto.*;
import com.flightmanagement.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${app.data-initializer.enabled:true}")
    private boolean enabled;

    private final ParameterService parameterService;

    private final TicketClassService ticketClassService;

    private final AirportService airportService;

    private final PlaneService planeService;

    private final AccountService accountService;

    private final PassengerService passengerService;

    private final FlightService flightService;

    private final FlightDetailService flightDetailService;

    private final FlightTicketClassService flightTicketClassService;

    public DataInitializer(ParameterService parameterService, TicketClassService ticketClassService,
                           AirportService airportService, PlaneService planeService,
                           AccountService accountService, PassengerService passengerService,
                           FlightService flightService, FlightDetailService flightDetailService,
                           FlightTicketClassService flightTicketClassService) {
        this.parameterService = parameterService;
        this.ticketClassService = ticketClassService;
        this.airportService = airportService;
        this.planeService = planeService;
        this.accountService = accountService;
        this.passengerService = passengerService;
        this.flightService = flightService;
        this.flightDetailService = flightDetailService;
        this.flightTicketClassService = flightTicketClassService;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            System.out.println("ℹ️  Data initializer is disabled");
            return;
        }
        System.out.println("🚀 Initializing Flight Management System Demo Data...");
        initializeParameters();
        initializeTicketClasses();
        initializeAirports();
        initializePlanes();
        initializeDemoAccounts();
        initializeDemoFlights();
        initializeDemoFlightDetails();
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
                System.out.println("Creating demo accounts at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // 1. Flight Scheduling Employee (Type 1)
                RegisterDto schedulingEmployee = new RegisterDto();
                schedulingEmployee.setAccountName("Hoang Van A");
                schedulingEmployee.setEmail("scheduling@flightms.com");
                schedulingEmployee.setPassword("123Abc");
                schedulingEmployee.setCitizenId("555555555");
                schedulingEmployee.setPhoneNumber("0123456785");
                schedulingEmployee.setAccountType(2); // Employee
                schedulingEmployee.setEmployeeType(1); // Flight Scheduling
                accountService.createAccount(schedulingEmployee);
                System.out.println("Created Flight Scheduling employee at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // 2. Ticketing Employee (Type 2)
                RegisterDto ticketingEmployee = new RegisterDto();
                ticketingEmployee.setAccountName("Tran Thi B");
                ticketingEmployee.setEmail("ticketing@flightms.com");
                ticketingEmployee.setPassword("123Abc");
                ticketingEmployee.setCitizenId("222222222");
                ticketingEmployee.setPhoneNumber("0123456782");
                ticketingEmployee.setAccountType(2); // Employee
                ticketingEmployee.setEmployeeType(2); // Ticketing
                accountService.createAccount(ticketingEmployee);
                System.out.println("Created Ticketing employee at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // 3. Support Employee (Type 3)
                RegisterDto supportEmployee = new RegisterDto();
                supportEmployee.setAccountName("Le Van C");
                supportEmployee.setEmail("support@flightms.com");
                supportEmployee.setPassword("123Abc");
                supportEmployee.setCitizenId("333333333");
                supportEmployee.setPhoneNumber("0123456783");
                supportEmployee.setAccountType(2); // Employee
                supportEmployee.setEmployeeType(3); // Support
                accountService.createAccount(supportEmployee);
                System.out.println("Created Customer Support employee at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // 4. Accounting Employee (Type 4)
                RegisterDto accountingEmployee = new RegisterDto();
                accountingEmployee.setAccountName("Pham Thi D");
                accountingEmployee.setEmail("accounting@flightms.com");
                    accountingEmployee.setPassword("123Abc");
                accountingEmployee.setCitizenId("444444444");
                accountingEmployee.setPhoneNumber("0123456784");
                accountingEmployee.setAccountType(2); // Employee
                accountingEmployee.setEmployeeType(4); // Accounting
                accountService.createAccount(accountingEmployee);
                System.out.println("Created Accounting employee at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // 5. Flight Operations Employee (Type 5)
                RegisterDto flightOpsEmployee = new RegisterDto();
                flightOpsEmployee.setAccountName("Nguyen Van E");
                flightOpsEmployee.setEmail("flightops@flightms.com");
                flightOpsEmployee.setPassword("123Abc");
                flightOpsEmployee.setCitizenId("111111111");
                flightOpsEmployee.setPhoneNumber("0123456781");
                flightOpsEmployee.setAccountType(2); // Employee
                flightOpsEmployee.setEmployeeType(5); // Flight Operations
                accountService.createAccount(flightOpsEmployee);
                System.out.println("Created Flight Operations employee at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // 6. Human Resources Employee (Type 6)
                RegisterDto hrEmployee = new RegisterDto();
                hrEmployee.setAccountName("Vu Thi F");
                hrEmployee.setEmail("hr@flightms.com");
                hrEmployee.setPassword("123Abc");
                hrEmployee.setCitizenId("666666666");
                hrEmployee.setPhoneNumber("0123456786");
                hrEmployee.setAccountType(2); // Employee
                hrEmployee.setEmployeeType(6); // Human Resources
                accountService.createAccount(hrEmployee);
                System.out.println("Created Human Resources employee at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // 7. Administrator Employee (Type 7)
                RegisterDto adminEmployee = new RegisterDto();
                adminEmployee.setAccountName("Dao Van G");
                adminEmployee.setEmail("admin@flightms.com");
                adminEmployee.setPassword("123Abc");
                adminEmployee.setCitizenId("777777777");
                adminEmployee.setPhoneNumber("0123456787");
                adminEmployee.setAccountType(2); // Employee
                adminEmployee.setEmployeeType(7); // Administrator
                accountService.createAccount(adminEmployee);
                System.out.println("Created System Administrator employee at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // Additional demo accounts for variety

                // Senior Flight Operations Manager
                RegisterDto seniorFlightOps = new RegisterDto();
                seniorFlightOps.setAccountName("Bui Thi H");
                seniorFlightOps.setEmail("seniorflightops@flightms.com");
                seniorFlightOps.setPassword("123Abc");
                seniorFlightOps.setCitizenId("888888888");
                seniorFlightOps.setPhoneNumber("0123456788");
                seniorFlightOps.setAccountType(2); // Employee
                seniorFlightOps.setEmployeeType(5); // Flight Operations
                accountService.createAccount(seniorFlightOps);
                System.out.println("Created Senior Flight Operations Manager at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // Customer Service Supervisor
                RegisterDto customerServiceSup = new RegisterDto();
                customerServiceSup.setAccountName("Ngo Van I");
                customerServiceSup.setEmail("cssupervisor@flightms.com");
                customerServiceSup.setPassword("123Abc");
                customerServiceSup.setCitizenId("999999999");
                customerServiceSup.setPhoneNumber("0123456789");
                customerServiceSup.setAccountType(2); // Employee
                customerServiceSup.setEmployeeType(3); // Support
                accountService.createAccount(customerServiceSup);
                System.out.println("Created Customer Service Supervisor at 2025-06-11 08:30:03 UTC by thinh0704hcm");

                // Demo Customer Account
                RegisterDto customer = new RegisterDto();
                customer.setAccountName("Khach Hang Demo");
                customer.setEmail("customer@flightms.com");
                customer.setPassword("123Abc");
                customer.setCitizenId("000000000");
                customer.setPhoneNumber("0123456780");
                customer.setAccountType(1); // Customer
                customer.setEmployeeType(null); // Not applicable for customers
                accountService.createAccount(customer);
                System.out.println("Created demo customer account at 2025-06-11 08:30:03 UTC by thinh0704hcm");


                // Create demo customer
                customer = new RegisterDto();
                customer.setAccountName("John Doe");
                customer.setEmail("john.doe@flightms.com");
                customer.setPassword("123Abc");
                customer.setCitizenId("123456789");
                customer.setPhoneNumber("0987654321");
                customer.setAccountType(1); // Customer
                accountService.createAccount(customer);

                // Create additional test customer
                RegisterDto testCustomer = new RegisterDto();
                testCustomer.setAccountName("Jane Smith");
                testCustomer.setEmail("jane.smith@email.com");
                testCustomer.setPassword("123Abc");
                testCustomer.setCitizenId("987654321");
                testCustomer.setPhoneNumber("0987654322");
                testCustomer.setAccountType(1); // Customer
                accountService.createAccount(testCustomer);

                System.out.println("DEMO ACCOUNTS SUMMARY:");
                System.out.println("=".repeat(80));
                System.out.println("1. Flight Scheduling   : scheduling@flightms.com / 123Abc");
                System.out.println("2. Ticketing           : ticketing@flightms.com / 123Abc");
                System.out.println("3. Customer Support    : support@flightms.com / 123Abc");
                System.out.println("4. Accounting          : accounting@flightms.com / 123Abc");
                System.out.println("5. Flight Operations   : flightops@flightms.com / 123Abc");
                System.out.println("6. Human Resources     : hr@flightms.com / 123Abc");
                System.out.println("7. Administrator       : admin@flightms.com / 123Abc");
                System.out.println("8. Senior Flight Ops   : seniorflightops@flightms.com / 123Abc");
                System.out.println("9. CS Supervisor       : cssupervisor@flightms.com / 123Abc");
                System.out.println("10. Demo Customer      : customer@flightms.com / 123Abc");
                System.out.println("11. John Doe           : john.doe@email.com / 123Abc");
                System.out.println("12. Jane Smith         : jane.smith@email.com / 123Abc");
                System.out.println("=".repeat(80));
                System.out.println("Demo accounts initialization completed at 2025-06-11 08:30:03 UTC by thinh0704hcm");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDemoFlights() {
        try {
            if (flightService.getAllFlights().isEmpty()) {
                LocalDateTime tomorrow = LocalDateTime.now().plusDays(2);
                LocalDateTime dayAfter = LocalDateTime.now().plusDays(3);

                // Flight 1: SGN -> HAN
                FlightRequest request1 = new FlightRequest();
                request1.setFlightCode("VN001");
                request1.setPlaneId(1);
                request1.setDepartureAirportId(1);
                request1.setArrivalAirportId(2);
                request1.setDepartureTime(tomorrow.withHour(8).withMinute(0));
                request1.setArrivalTime(tomorrow.withHour(10).withMinute(30));
                flightService.createFlight(request1);

                // Flight 2: HAN -> DAD
                FlightRequest request2 = new FlightRequest();
                request2.setFlightCode("VN002");
                request2.setPlaneId(2);
                request2.setDepartureAirportId(2);
                request2.setArrivalAirportId(3);
                request2.setDepartureTime(tomorrow.withHour(14).withMinute(0));
                request2.setArrivalTime(tomorrow.withHour(15).withMinute(45));
                flightService.createFlight(request2);

                // Flight 3: DAD -> CXR
                FlightRequest request3 = new FlightRequest();
                request3.setFlightCode("VN003");
                request3.setPlaneId(3);
                request3.setDepartureAirportId(3);
                request3.setArrivalAirportId(4);
                request3.setDepartureTime(tomorrow.withHour(16).withMinute(30));
                request3.setArrivalTime(tomorrow.withHour(17).withMinute(45));
                flightService.createFlight(request3);

                // Flight 4: SGN -> PQC
                FlightRequest request4 = new FlightRequest();
                request4.setFlightCode("VN004");
                request4.setPlaneId(4);
                request4.setDepartureAirportId(1);
                request4.setArrivalAirportId(5);
                request4.setDepartureTime(dayAfter.withHour(9).withMinute(0));
                request4.setArrivalTime(dayAfter.withHour(10).withMinute(15));
                flightService.createFlight(request4);

                // Flight 5: PQC -> SGN
                FlightRequest request5 = new FlightRequest();
                request5.setFlightCode("VN005");
                request5.setPlaneId(5);
                request5.setDepartureAirportId(5);
                request5.setArrivalAirportId(1);
                request5.setDepartureTime(dayAfter.withHour(18).withMinute(0));
                request5.setArrivalTime(dayAfter.withHour(19).withMinute(15));
                flightService.createFlight(request5);

                initializeAllFlightTicketClasses();
                System.out.println("✓ Demo flights and ticket classes created");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo flights: " + e.getMessage());
        }
    }

    private void initializeDemoFlightDetails() {
        try {
            if (flightDetailService.getAllFlightDetails().isEmpty()) {
                LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

                // Flight 1 (SGN -> HAN) with 2 stopovers: Da Nang and Nha Trang
                FlightDetailDto detail1_1 = new FlightDetailDto();
                detail1_1.setFlightId(1);
                detail1_1.setMediumAirportId(3); // Da Nang airport
                detail1_1.setArrivalTime(tomorrow.withHour(8).withMinute(45));
                detail1_1.setLayoverDuration(30); // 30 minutes layover
                flightDetailService.createFlightDetail(detail1_1);

                FlightDetailDto detail1_2 = new FlightDetailDto();
                detail1_2.setFlightId(1);
                detail1_2.setMediumAirportId(4); // Cam Ranh airport (Nha Trang)
                detail1_2.setArrivalTime(tomorrow.withHour(9).withMinute(30));
                detail1_2.setLayoverDuration(25); // 25 minutes layover
                flightDetailService.createFlightDetail(detail1_2);

                // Flight 2 (HAN -> DAD) with 2 stopovers: Ho Chi Minh City and Nha Trang
                FlightDetailDto detail2_1 = new FlightDetailDto();
                detail2_1.setFlightId(2);
                detail2_1.setMediumAirportId(1); // Tan Son Nhat airport (Ho Chi Minh City)
                detail2_1.setArrivalTime(tomorrow.withHour(14).withMinute(30));
                detail2_1.setLayoverDuration(35); // 35 minutes layover
                flightDetailService.createFlightDetail(detail2_1);

                FlightDetailDto detail2_2 = new FlightDetailDto();
                detail2_2.setFlightId(2);
                detail2_2.setMediumAirportId(4); // Cam Ranh airport (Nha Trang)
                detail2_2.setArrivalTime(tomorrow.withHour(15).withMinute(15));
                detail2_2.setLayoverDuration(20); // 20 minutes layover
                flightDetailService.createFlightDetail(detail2_2);

                System.out.println("✓ Demo flight details (stopovers) created");
                System.out.println("  - Flight 1 (SGN -> HAN): 2 stopovers at Da Nang and Nha Trang");
                System.out.println("  - Flight 2 (HAN -> DAD): 2 stopovers at Ho Chi Minh City and Nha Trang");
            }
        } catch (Exception e) {
//            System.err.println("⚠️ Error creating demo flight details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("⚠️ Error creating demo flight details: " + e.getMessage());
        }
    }

    private void initializeAllFlightTicketClasses() {
        try {
            // Flight 1 (SGN -> HAN)
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(1, 1, 100, new BigDecimal("1500000"))); // Economy
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(1, 2, 20, new BigDecimal("3000000"))); // Business
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(1, 3, 10, new BigDecimal("5000000"))); // First
                                                                                                            // Class

            // Flight 2 (HAN -> DAD)
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(2, 1, 150, new BigDecimal("1200000"))); // Economy
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(2, 2, 30, new BigDecimal("2500000"))); // Business

            // Flight 3 (DAD -> CXR)
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(3, 1, 80, new BigDecimal("800000"))); // Economy
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(3, 2, 15, new BigDecimal("1800000"))); // Business

            // Flight 4 (SGN -> PQC)
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(4, 1, 120, new BigDecimal("1000000"))); // Economy
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(4, 2, 25, new BigDecimal("2200000"))); // Business
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(4, 3, 8, new BigDecimal("3500000"))); // First
                                                                                                           // Class

            // Flight 5 (PQC -> SGN)
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(5, 1, 60, new BigDecimal("950000"))); // Economy
            flightTicketClassService
                    .createFlightTicketClass(createFlightTicketClass(5, 2, 10, new BigDecimal("2100000"))); // Business

            System.out.println("✓ Flight ticket classes created for all flights");
        } catch (Exception e) {
            System.err.println("⚠️ Error creating flight ticket classes: " + e.getMessage());
        }
    }

    private void initializeDemoPassengers() {
        try {
            if (passengerService.getAllPassengers().isEmpty()) {
                passengerService.createPassenger(
                        new PassengerDto(null, "Nguyen Van A", "nguyenvana@email.com", "123456789", "0987654321"));
                passengerService.createPassenger(
                        new PassengerDto(null, "Tran Thi B", "tranthib@email.com", "987654321", "0123456789"));
                passengerService.createPassenger(
                        new PassengerDto(null, "Le Van C", "levanc@email.com", "456789123", "0369852147"));
                passengerService.createPassenger(
                        new PassengerDto(null, "Pham Thi D", "phamthid@email.com", "789123456", "0147258369"));
                passengerService.createPassenger(
                        new PassengerDto(null, "Hoang Van E", "hoangvane@email.com", "321654987", "0258147963"));
                System.out.println("✓ Demo passengers created");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating demo passengers: " + e.getMessage());
        }
    }

    private FlightTicketClassDto createFlightTicketClass(int flightId, int ticketClassId, int quantity,
            BigDecimal fare) {
        FlightTicketClassDto ftc = new FlightTicketClassDto();
        ftc.setFlightId(flightId);
        ftc.setTicketClassId(ticketClassId);
        ftc.setTicketQuantity(quantity);
        ftc.setRemainingTicketQuantity(quantity);
        ftc.setSpecifiedFare(fare);
        return ftc;
    }
}