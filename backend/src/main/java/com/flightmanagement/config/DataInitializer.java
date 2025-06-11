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
    private FlightDetailService flightDetailService;

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
                // Create admin account
                RegisterDto admin = new RegisterDto();
                admin.setAccountName("System Administrator");
                admin.setEmail("admin@flightms.com");
                admin.setPassword("123Abc");
                admin.setCitizenId("999999999");
                admin.setPhoneNumber("0123456789");
                admin.setAccountType(2); // Admin
                admin.setEmployeeType(5); // Quán lý dịch vụ (làm hết)
                accountService.createAccount(admin);

                //Nhân viên bán vé
                RegisterDto ticketingEmployee = new RegisterDto();
                ticketingEmployee.setAccountName("Ticketing Employee");
                ticketingEmployee.setEmail("ticketing@flightms.com");
                ticketingEmployee.setPassword("123Abc");
                ticketingEmployee.setCitizenId("666666666");
                ticketingEmployee.setPhoneNumber("0123456786");
                ticketingEmployee.setAccountType(2); // Employee
                ticketingEmployee.setEmployeeType(2); // Nhân viên bán vé
                accountService.createAccount(ticketingEmployee);

                //Nhân viên kế toán
                RegisterDto accountingEmployee = new RegisterDto();
                accountingEmployee.setAccountName("Accounting Employee");
                accountingEmployee.setEmail("accounting@flightms.com");
                accountingEmployee.setPassword("123Abc");
                accountingEmployee.setCitizenId("555555555");
                accountingEmployee.setPhoneNumber("0123456785");
                accountingEmployee.setAccountType(2); // Employee
                accountingEmployee.setEmployeeType(4); // Nhân viên kế toán
                accountService.createAccount(accountingEmployee);

                // Nhân viên quản lý nhân sự
                RegisterDto hrEmployee = new RegisterDto();
                hrEmployee.setAccountName("HR Employee");
                hrEmployee.setEmail("hr@flightms.com");
                hrEmployee.setPassword("123Abc");
                hrEmployee.setCitizenId("444444444");
                hrEmployee.setPhoneNumber("0123456784");
                hrEmployee.setAccountType(2); // Employee
                hrEmployee.setEmployeeType(6); // Nhân viên quản lý nhân sự
                accountService.createAccount(hrEmployee);

                // Create employee account
                RegisterDto employee = new RegisterDto();
                employee.setAccountName("Flight Operations Employee");
                employee.setEmail("employee@flightms.com");
                employee.setPassword("123Abc");
                employee.setCitizenId("888888888");
                employee.setPhoneNumber("0123456788");
                employee.setAccountType(2); // Employee
                employee.setEmployeeType(1); // Nhân viên khai thác bay
                accountService.createAccount(employee);


                // Customer account
                RegisterDto supportEmployee = new RegisterDto();
                supportEmployee.setAccountName("Support Employee");
                supportEmployee.setEmail("support@flightms.com");
                supportEmployee.setPassword("123Abc");
                supportEmployee.setCitizenId("777777777");
                supportEmployee.setPhoneNumber("0123456787");
                supportEmployee.setAccountType(2); // Employee
                supportEmployee.setEmployeeType(3); // Nhân viên hỗ trợ khách hàng
                accountService.createAccount(supportEmployee);


                // Create demo customer
                RegisterDto customer = new RegisterDto();
                customer.setAccountName("John Doe");
                customer.setEmail("customer@flightms.com");
                customer.setPassword("123Abc");
                customer.setCitizenId("123456789");
                customer.setPhoneNumber("0987654321");
                customer.setAccountType(1); // Customer
                accountService.createAccount(customer);

                // Create additional test customer
                RegisterDto testCustomer = new RegisterDto();
                testCustomer.setAccountName("Jane Smith");
                testCustomer.setEmail("john.doe@email.com");
                testCustomer.setPassword("123Abc");
                testCustomer.setCitizenId("987654321");
                testCustomer.setPhoneNumber("0987654322");
                testCustomer.setAccountType(1); // Customer
                accountService.createAccount(testCustomer);

                System.out.println("✓ Demo accounts created with correct email addresses");
                System.out.println("  - Admin: admin@flightms.com / 123Abc");
                System.out.println("  - Employee: employee@flightms.com / 123Abc");
                System.out.println("  - Support: support@flightms.com / 123Abc");
                System.out.println("  - Customer: customer@flightms.com / 123Abc");
                System.out.println("  - Test Customer: john.doe@email.com / 123Abc");
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
            System.err.println("⚠️ Error creating demo flight details: " + e.getMessage());
            e.printStackTrace();
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
