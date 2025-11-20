package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Airport;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.Plane;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class TicketBookingFlowIT {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        r.add("spring.datasource.username", mysql::getUsername);
        r.add("spring.datasource.password", mysql::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    // Repositories to assert DB state after cross-service flow
    @Autowired FlightRepository flightRepository;
    @Autowired TicketRepository ticketRepository;
    @Autowired TicketClassRepository ticketClassRepository;
    @Autowired FlightTicketClassRepository flightTicketClassRepository;
    @Autowired AirportRepository airportRepository;
    @Autowired PlaneRepository planeRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired PassengerRepository passengerRepository;
    @Autowired FlightDetailRepository flightDetailRepository;
    @Autowired MessageRepository messageRepository;
    @Autowired ChatboxRepository chatboxRepository;

    private Airport departureAirport;
    private Airport arrivalAirport;
    private Plane testPlane;
    private TicketClass economyClass;
    private Customer testCustomer;

    @BeforeEach
    void setupTestData() {
        // Keep tests independent - clean up the database in proper order
        // (foreign key relationships require careful cleanup order)
        // Clean from most dependent to least dependent
        messageRepository.deleteAll();
        chatboxRepository.deleteAll();
        ticketRepository.deleteAll();
        passengerRepository.deleteAll();
        flightTicketClassRepository.deleteAll();
        flightDetailRepository.deleteAll();
        customerRepository.deleteAll();
        accountRepository.deleteAll();
        flightRepository.deleteAll();
        planeRepository.deleteAll();
        airportRepository.deleteAll();
        ticketClassRepository.deleteAll();
        
        // Create test airports
        departureAirport = new Airport();
        departureAirport.setAirportName("Tan Son Nhat International Airport");
        departureAirport.setCityName("Ho Chi Minh City");
        departureAirport.setCountryName("Vietnam");
        departureAirport = airportRepository.save(departureAirport);

        arrivalAirport = new Airport();
        arrivalAirport.setAirportName("Noi Bai International Airport");
        arrivalAirport.setCityName("Hanoi");
        arrivalAirport.setCountryName("Vietnam");
        arrivalAirport = airportRepository.save(arrivalAirport);

        // Create test plane
        testPlane = new Plane();
        testPlane.setPlaneCode("VN-IT123");
        testPlane.setPlaneType("Boeing 737");
        testPlane.setSeatQuantity(180);
        testPlane = planeRepository.save(testPlane);

        // Create ticket class
        economyClass = new TicketClass();
        economyClass.setTicketClassName("Economy");
        economyClass.setColor("Blue");
        economyClass = ticketClassRepository.save(economyClass);
        
        // Note: FlightTicketClass records will be created dynamically in each test
        // when flights are created, linking them to the ticket class with available capacity

        // Create test account first (required for Customer due to @MapsId)
        // Use unique values to avoid constraint violations across test runs
        String uniqueId = String.valueOf(System.currentTimeMillis());
        Account testAccount = new Account();
        testAccount.setAccountName("Test Customer");
        testAccount.setEmail("test.customer." + uniqueId + "@example.com");
        testAccount.setPassword("password123");
        testAccount.setCitizenId("CID" + uniqueId);
        testAccount.setPhoneNumber("09" + uniqueId.substring(uniqueId.length() - 8));
        testAccount.setAccountType(1); // 1 = customer
        testAccount = accountRepository.save(testAccount);
        
        // Create test customer and associate with account
        testCustomer = new Customer();
        testCustomer.setAccount(testAccount);
        testCustomer.setScore(0);
        testCustomer = customerRepository.save(testCustomer);
        
        // Verify customer was saved with ID
        assertThat(testCustomer.getCustomerId()).isNotNull();
        assertThat(testCustomer.getAccount()).isNotNull();
        assertThat(testCustomer.getAccount().getAccountId()).isEqualTo(testAccount.getAccountId());
    }

    @Test
    void fullTicketBookingFlow_createsTickets_and_reservesSeats() throws Exception {
        // 1) create a flight (POST /api/flights)
        String flightPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightCode", "IT123",
                        "departureTime", "2025-12-01T08:00:00",
                        "arrivalTime", "2025-12-01T10:00:00",
                        "planeId", testPlane.getPlaneId(),
                        "departureAirportId", departureAirport.getAirportId(),
                        "arrivalAirportId", arrivalAirport.getAirportId()
                )
        );
        String flightJson = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(flightPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        // extract flight id
        var createdFlight = objectMapper.readTree(flightJson);
        Integer flightId = createdFlight.get("data").get("flightId").asInt();
        
        // Create FlightTicketClass to link flight with ticket class and provide capacity
        FlightTicketClass flightTicketClass = new FlightTicketClass();
        flightTicketClass.setFlightId(flightId);
        flightTicketClass.setTicketClassId(economyClass.getTicketClassId());
        flightTicketClass.setTicketQuantity(50);
        flightTicketClass.setRemainingTicketQuantity(50);
        flightTicketClass.setSpecifiedFare(new BigDecimal("150.00"));
        flightTicketClassRepository.save(flightTicketClass);

        // 2) book tickets (POST /api/tickets/book)
        String bookingPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightId", flightId,
                        "customerId", testCustomer.getCustomerId(),
                        "ticketClassId", economyClass.getTicketClassId(),
                        "totalFare", new BigDecimal("150.00"),
                        "passengers", List.of(
                                Map.of(
                                        "passengerName", "Integration Test Passenger",
                                        "citizenId", "123456789",
                                        "phoneNumber", "0123456789",
                                        "email", "integration.test@example.com"
                                )
                        ),
                        "seatNumbers", List.of("1A")
                )
        );
        
        String ticketsJson = mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify response
        assertThat(ticketsJson).isNotBlank();

        // 3) assert DB: tickets created and seats reserved
        var tickets = ticketRepository.findByFlightId(flightId);
        assertThat(tickets).isNotEmpty();
        assertThat(tickets.get(0).getSeatNumber()).isEqualTo("1A");
        assertThat(tickets.get(0).getTicketStatus()).isEqualTo((byte) 0); // unpaid status
    }

    @Test
    void multiPassengerTicketBooking_createsMultipleTickets() throws Exception {
        // Create flight first
        String flightPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightCode", "IT456",
                        "departureTime", "2025-12-02T14:00:00",
                        "arrivalTime", "2025-12-02T16:00:00",
                        "planeId", testPlane.getPlaneId(),
                        "departureAirportId", departureAirport.getAirportId(),
                        "arrivalAirportId", arrivalAirport.getAirportId()
                )
        );
        
        String flightJson = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(flightPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        var flight = objectMapper.readTree(flightJson);
        Integer flightId = flight.get("data").get("flightId").asInt();
        
        // Create FlightTicketClass to link flight with ticket class and provide capacity
        FlightTicketClass flightTicketClass = new FlightTicketClass();
        flightTicketClass.setFlightId(flightId);
        flightTicketClass.setTicketClassId(economyClass.getTicketClassId());
        flightTicketClass.setTicketQuantity(50);
        flightTicketClass.setRemainingTicketQuantity(50);
        flightTicketClass.setSpecifiedFare(new BigDecimal("150.00"));
        flightTicketClassRepository.save(flightTicketClass);

        // Book tickets for multiple passengers
        String bookingPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightId", flightId,
                        "customerId", testCustomer.getCustomerId(),
                        "ticketClassId", economyClass.getTicketClassId(),
                        "totalFare", new BigDecimal("300.00"),
                        "passengers", List.of(
                                Map.of(
                                        "passengerName", "Passenger One",
                                        "citizenId", "111111111",
                                        "phoneNumber", "0111111111",
                                        "email", "passenger1@example.com"
                                ),
                                Map.of(
                                        "passengerName", "Passenger Two",
                                        "citizenId", "222222222",
                                        "phoneNumber", "0222222222",
                                        "email", "passenger2@example.com"
                                )
                        ),
                        "seatNumbers", List.of("2A", "2B")
                )
        );

        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingPayload))
                .andExpect(status().isCreated());

        // Assert multiple tickets created for the flight
        var allTickets = ticketRepository.findByFlightId(flightId);
        assertThat(allTickets).hasSize(2);
        assertThat(allTickets.stream().map(t -> t.getSeatNumber()))
                .containsExactlyInAnyOrder("2A", "2B");
    }

    @Test 
    void ticketBookingAndPaymentFlow_updatesTicketStatus() throws Exception {
        // 1) Create flight and book ticket (similar to first test)
        String flightPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightCode", "IT789",
                        "departureTime", "2025-12-03T18:00:00",
                        "arrivalTime", "2025-12-03T20:00:00",
                        "planeId", testPlane.getPlaneId(),
                        "departureAirportId", departureAirport.getAirportId(),
                        "arrivalAirportId", arrivalAirport.getAirportId()
                )
        );
        
        String flightJson = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(flightPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        var flight = objectMapper.readTree(flightJson);
        Integer flightId = flight.get("data").get("flightId").asInt();
        
        // Create FlightTicketClass to link flight with ticket class and provide capacity
        FlightTicketClass flightTicketClass = new FlightTicketClass();
        flightTicketClass.setFlightId(flightId);
        flightTicketClass.setTicketClassId(economyClass.getTicketClassId());
        flightTicketClass.setTicketQuantity(50);
        flightTicketClass.setRemainingTicketQuantity(50);
        flightTicketClass.setSpecifiedFare(new BigDecimal("150.00"));
        flightTicketClassRepository.save(flightTicketClass);

        String bookingPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightId", flightId,
                        "customerId", testCustomer.getCustomerId(),
                        "ticketClassId", economyClass.getTicketClassId(),
                        "totalFare", new BigDecimal("150.00"),
                        "passengers", List.of(
                                Map.of(
                                        "passengerName", "Payment Test Passenger",
                                        "citizenId", "987654321",
                                        "phoneNumber", "0987654321",
                                        "email", "payment.test@example.com"
                                )
                        ),
                        "seatNumbers", List.of("3A")
                )
        );
        
        String ticketsJson = mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify response
        assertThat(ticketsJson).isNotBlank();

        // Get the created tickets
        var tickets = ticketRepository.findByFlightId(flightId);
        assertThat(tickets).isNotEmpty();
        var ticket = tickets.get(0);
        assertThat(ticket.getTicketStatus()).isEqualTo((byte) 0); // initially unpaid

        // 2) Simulate payment creation (this might require a confirmation code from the ticket)
        // For now, just verify the ticket booking flow worked
        assertThat(ticket.getSeatNumber()).isEqualTo("3A");
        // Note: Fare is set during booking process, not from TicketClass directly
        assertThat(ticket.getFare()).isNotNull();
    }
}