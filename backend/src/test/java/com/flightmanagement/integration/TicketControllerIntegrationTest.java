package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.dto.BookingDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.repository.TicketRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    private Integer createdTicketId;

    @Test
    @Order(1)
    @DisplayName("IT-01: Should get all tickets successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetAllTickets() throws Exception {
        mockMvc.perform(get("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Should get ticket by ID successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetTicketById() throws Exception {
        // Assuming there's at least one ticket in the database
        Integer ticketId = 1;

        mockMvc.perform(get("/api/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Ticket retrieved successfully"))
                .andExpect(jsonPath("$.data.ticketId").value(ticketId));
    }

    @Test
    @Order(3)
    @DisplayName("IT-03: Should book tickets successfully")
    @WithMockUser(roles = "CUSTOMER")
    @Transactional
    void testBookTickets() throws Exception {
        // Create passenger data
        PassengerDto passenger1 = new PassengerDto();
        passenger1.setPassengerName("John Doe");
        passenger1.setEmail("john.doe@test.com");
        passenger1.setPhoneNumber("1234567890");
        passenger1.setCitizenId("123456789012");

        PassengerDto passenger2 = new PassengerDto();
        passenger2.setPassengerName("Jane Smith");
        passenger2.setEmail("jane.smith@test.com");
        passenger2.setPhoneNumber("0987654321");
        passenger2.setCitizenId("210987654321");

        List<PassengerDto> passengers = Arrays.asList(passenger1, passenger2);
        List<String> seatNumbers = Arrays.asList("12A", "12B");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setCustomerId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(passengers);
        bookingDto.setTotalFare(new BigDecimal("500000"));
        bookingDto.setSeatNumbers(seatNumbers);

        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Tickets booked successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Should cancel a ticket successfully")
    @WithMockUser(roles = "CUSTOMER")
    @Transactional
    void testCancelTicket() throws Exception {
        // Create a ticket first
        TicketDto ticketDto = new TicketDto();
        ticketDto.setFlightId(1);
        ticketDto.setTicketClassId(1);
        ticketDto.setBookCustomerId(1);
        ticketDto.setPassengerId(1);
        ticketDto.setSeatNumber("15A");
        ticketDto.setTicketStatus((byte) 0); // Pending
        ticketDto.setFare(new BigDecimal("250000"));

        MvcResult createResult = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseContent = createResult.getResponse().getContentAsString();
        Integer ticketId = objectMapper.readTree(createResponseContent)
                .get("data")
                .get("ticketId")
                .asInt();

        // Now cancel the ticket
        mockMvc.perform(put("/api/tickets/{id}/cancel", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Ticket canceled successfully"));
    }

    @Test
    @Order(5)
    @DisplayName("IT-05: Should get tickets by flight ID successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetTicketsByFlightId() throws Exception {
        Integer flightId = 1;

        mockMvc.perform(get("/api/tickets/flight/{flightId}", flightId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(6)
    @DisplayName("IT-06: Should get tickets by customer ID successfully")
    @WithMockUser(roles = "CUSTOMER")
    void testGetTicketsByCustomerId() throws Exception {
        Integer customerId = 1;

        mockMvc.perform(get("/api/tickets/customer/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("IT-07: Should check seat availability successfully")
    @WithMockUser(roles = "CUSTOMER")
    void testCheckSeatAvailability() throws Exception {
        Integer flightId = 1;
        String seatNumber = "20A";

        mockMvc.perform(get("/api/tickets/seat-available")
                        .param("flightId", flightId.toString())
                        .param("seatNumber", seatNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Seat availability checked successfully"))
                .andExpect(jsonPath("$.data").isBoolean());
    }

    @Test
    @Order(8)
    @DisplayName("IT-08: Should get tickets by confirmation code successfully")
    @WithMockUser(roles = "CUSTOMER")
    void testGetTicketsByConfirmationCode() throws Exception {
        // First, generate a confirmation code
        MvcResult codeResult = mockMvc.perform(get("/api/tickets/confirmation-code")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String codeResponseContent = codeResult.getResponse().getContentAsString();
        String confirmationCode = objectMapper.readTree(codeResponseContent)
                .get("data")
                .asText();

        // Try to get tickets by confirmation code
        mockMvc.perform(get("/api/tickets/booking-lookup/{code}", confirmationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(9)
    @DisplayName("IT-09: Should return error when ticket ID not found")
    @WithMockUser(roles = "ADMIN")
    void testGetTicketByIdNotFound() throws Exception {
        Integer nonExistentId = 999999;

        mockMvc.perform(get("/api/tickets/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(10)
    @DisplayName("IT-10: Should get tickets by status successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetTicketsByStatus() throws Exception {
        Byte status = 1; // Paid status

        mockMvc.perform(get("/api/tickets/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
