package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Airport;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.Plane;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class AuthProtectedActionsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    // Repositories for test setup and cleanup
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketClassRepository ticketClassRepository;
    @Autowired
    private FlightTicketClassRepository flightTicketClassRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private PlaneRepository planeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private FlightDetailRepository flightDetailRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatboxRepository chatboxRepository;

    private Airport departureAirport;
    private Airport arrivalAirport;
    private Plane testPlane;
    private TicketClass economyClass;
    private Customer testCustomer;
    private String validAuthToken;

    @BeforeEach
    void setupTestData() throws Exception {
        // Use @DirtiesContext instead of manual cleanup to avoid foreign key issues
        // Tests will work with fresh application context each time
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
        testPlane.setPlaneCode("VN-AUTH123");
        testPlane.setPlaneType("Boeing 737");
        testPlane.setSeatQuantity(180);
        testPlane = planeRepository.save(testPlane);

        // Create ticket class
        economyClass = new TicketClass();
        economyClass.setTicketClassName("Economy");
        economyClass.setColor("Blue");
        economyClass = ticketClassRepository.save(economyClass);

        // Register and authenticate user to get valid token
        String uniqueId = String.valueOf(System.currentTimeMillis());
        Map<String, Object> registerPayload = Map.of(
                "email", "auth.test." + uniqueId + "@example.com",
                "password", "P@ssw0rd123",
                "accountName", "Auth Test User",
                "citizenId", "AUTH" + uniqueId,
                "phoneNumber", "09" + uniqueId.substring(uniqueId.length() - 8),
                "accountType", 1 // customer
        );

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract token from registration response
        var registerJson = objectMapper.readTree(registerResponse);
        validAuthToken = registerJson.get("data").get("accessToken").asText();
        
        // Get the customer ID from the created account
        String email = registerPayload.get("email").toString();
        Account account = accountRepository.findByEmail(email).orElseThrow();
        testCustomer = customerRepository.findById(account.getAccountId()).orElseThrow();
        
        assertThat(validAuthToken).isNotBlank();
        assertThat(testCustomer).isNotNull();
    }

    @Test
    @Order(1)
    @DisplayName("IT-01: Should allow authenticated user to access protected flight creation")
    @WithMockUser(roles = "ADMIN")
    void authenticatedUser_canAccessProtectedFlightCreation() throws Exception {
        // 1) Try to create flight WITHOUT authentication - should fail
        String flightPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightCode", "AUTH001",
                        "departureTime", "2025-12-01T08:00:00",
                        "arrivalTime", "2025-12-01T10:00:00",
                        "planeId", testPlane.getPlaneId(),
                        "departureAirportId", departureAirport.getAirportId(),
                        "arrivalAirportId", arrivalAirport.getAirportId()
                )
        );

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(flightPayload))
                .andExpect(status().isForbidden()); // Expecting 403 - authentication required

        // 2) Try WITH authentication - should succeed
        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validAuthToken)
                        .content(flightPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.flightCode").value("AUTH001"));
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Should allow authenticated user to book tickets after login")
    @WithMockUser(roles = "CUSTOMER")
    void authenticatedUser_canBookTicketsAfterLogin() throws Exception {
        // 1) Create flight first
        String flightPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightCode", "AUTH002",
                        "departureTime", "2025-12-02T14:00:00",
                        "arrivalTime", "2025-12-02T16:00:00",
                        "planeId", testPlane.getPlaneId(),
                        "departureAirportId", departureAirport.getAirportId(),
                        "arrivalAirportId", arrivalAirport.getAirportId()
                )
        );
        
        String flightResponse = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validAuthToken)
                        .content(flightPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        var flightJson = objectMapper.readTree(flightResponse);
        Integer flightId = flightJson.get("data").get("flightId").asInt();
        
        // Create FlightTicketClass for availability
        FlightTicketClass flightTicketClass = new FlightTicketClass();
        flightTicketClass.setFlightId(flightId);
        flightTicketClass.setTicketClassId(economyClass.getTicketClassId());
        flightTicketClass.setTicketQuantity(50);
        flightTicketClass.setRemainingTicketQuantity(50);
        flightTicketClass.setSpecifiedFare(new BigDecimal("200.00"));
        flightTicketClassRepository.save(flightTicketClass);

        // 2) Try to book tickets WITHOUT authentication - should fail
        String bookingPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightId", flightId,
                        "customerId", testCustomer.getCustomerId(),
                        "ticketClassId", economyClass.getTicketClassId(),
                        "totalFare", new BigDecimal("200.00"),
                        "passengers", List.of(
                                Map.of(
                                        "passengerName", "Auth Test Passenger",
                                        "citizenId", "AUTH123456",
                                        "phoneNumber", "0987654321",
                                        "email", "auth.passenger@example.com"
                                )
                        ),
                        "seatNumbers", List.of("1A")
                )
        );

        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingPayload))
                .andExpect(status().isForbidden()); // Expecting 403 - authentication required

        // 3) Try WITH authentication - should succeed
        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validAuthToken)
                        .content(bookingPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @Order(3)
    @DisplayName("IT-03: Should allow authenticated user to access personal tickets")
    @WithMockUser(roles = "CUSTOMER")
    void authenticatedUser_canAccessPersonalTickets() throws Exception {
        // 1) Create flight and book ticket
        String flightPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightCode", "AUTH003", 
                        "departureTime", "2025-12-03T18:00:00",
                        "arrivalTime", "2025-12-03T20:00:00",
                        "planeId", testPlane.getPlaneId(),
                        "departureAirportId", departureAirport.getAirportId(),
                        "arrivalAirportId", arrivalAirport.getAirportId()
                )
        );
        
        String flightResponse = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validAuthToken)
                        .content(flightPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
                
        var flightJson = objectMapper.readTree(flightResponse);
        Integer flightId = flightJson.get("data").get("flightId").asInt();
        
        // Create FlightTicketClass for availability
        FlightTicketClass flightTicketClass = new FlightTicketClass();
        flightTicketClass.setFlightId(flightId);
        flightTicketClass.setTicketClassId(economyClass.getTicketClassId());
        flightTicketClass.setTicketQuantity(50);
        flightTicketClass.setRemainingTicketQuantity(50);
        flightTicketClass.setSpecifiedFare(new BigDecimal("300.00"));
        flightTicketClassRepository.save(flightTicketClass);

        String bookingPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightId", flightId,
                        "customerId", testCustomer.getCustomerId(),
                        "ticketClassId", economyClass.getTicketClassId(),
                        "totalFare", new BigDecimal("300.00"),
                        "passengers", List.of(
                                Map.of(
                                        "passengerName", "Personal Ticket Test",
                                        "citizenId", "PERS123456",
                                        "phoneNumber", "0123456789",
                                        "email", "personal.test@example.com"
                                )
                        ),
                        "seatNumbers", List.of("2A")
                )
        );

        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validAuthToken)
                        .content(bookingPayload))
                .andExpect(status().isCreated());

        // 2) Try to access tickets WITHOUT authentication - should fail
        mockMvc.perform(get("/api/tickets/customer/" + testCustomer.getCustomerId()))
                .andExpect(status().isForbidden()); // Expecting 403 - authentication required

        // 3) Try WITH authentication - should succeed and return tickets
        mockMvc.perform(get("/api/tickets/customer/" + testCustomer.getCustomerId())
                        .header("Authorization", "Bearer " + validAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].seatNumber").value("2A"));
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Should reject protected actions with invalid token")
    @WithMockUser(roles = "CUSTOMER")
    void invalidToken_rejectsProtectedActions() throws Exception {
        // Try to access protected endpoints with invalid token
        String invalidToken = "invalid.jwt.token";

        String flightPayload = objectMapper.writeValueAsString(
                Map.of(
                        "flightCode", "INVALID001",
                        "departureTime", "2025-12-01T08:00:00",
                        "arrivalTime", "2025-12-01T10:00:00",
                        "planeId", testPlane.getPlaneId(),
                        "departureAirportId", departureAirport.getAirportId(),
                        "arrivalAirportId", arrivalAirport.getAirportId()
                )
        );

        // Flight creation with invalid token
        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(flightPayload))
                .andExpect(status().isForbidden()); // Invalid token = 403

        // Ticket access with invalid token
        mockMvc.perform(get("/api/tickets/customer/" + testCustomer.getCustomerId())
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isForbidden()); // Invalid token = 403
    }
}