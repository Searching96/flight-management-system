package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.repository.FlightTicketClassRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FlightTicketClassControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlightTicketClassRepository flightTicketClassRepository;

    @Test
    @Order(1)
    @DisplayName("IT-01: Should get all flight ticket classes successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetAllFlightTicketClasses() throws Exception {
        mockMvc.perform(get("/api/flight-ticket-classes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight ticket classes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Should get flight ticket class by ID successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetFlightTicketClassById() throws Exception {
        // Assuming there's at least one flight ticket class in the database
        Integer flightId = 1;
        Integer ticketClassId = 1;

        mockMvc.perform(get("/api/flight-ticket-classes/{flightId}/{ticketClassId}", 
                        flightId, ticketClassId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight ticket class retrieved successfully"))
                .andExpect(jsonPath("$.data.flightId").value(flightId))
                .andExpect(jsonPath("$.data.ticketClassId").value(ticketClassId));
    }

    @Test
    @Order(3)
    @DisplayName("IT-03: Should create a new flight ticket class successfully")
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testCreateFlightTicketClass() throws Exception {
        FlightTicketClassDto dto = new FlightTicketClassDto();
        dto.setFlightId(1);
        dto.setTicketClassId(2);
        dto.setTicketQuantity(100);
        dto.setRemainingTicketQuantity(100);
        dto.setSpecifiedFare(new BigDecimal("500000"));
        dto.setIsAvailable(true);

        mockMvc.perform(post("/api/flight-ticket-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight ticket class created successfully"))
                .andExpect(jsonPath("$.data.flightId").value(1))
                .andExpect(jsonPath("$.data.ticketClassId").value(2))
                .andExpect(jsonPath("$.data.ticketQuantity").value(100));
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Should update an existing flight ticket class successfully")
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testUpdateFlightTicketClass() throws Exception {
        // First create a flight ticket class to update
        FlightTicketClassDto createDto = new FlightTicketClassDto();
        createDto.setFlightId(1);
        createDto.setTicketClassId(3);
        createDto.setTicketQuantity(80);
        createDto.setRemainingTicketQuantity(80);
        createDto.setSpecifiedFare(new BigDecimal("600000"));
        createDto.setIsAvailable(true);

        mockMvc.perform(post("/api/flight-ticket-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        // Now update the flight ticket class
        FlightTicketClassDto updateDto = new FlightTicketClassDto();
        updateDto.setFlightId(1);
        updateDto.setTicketClassId(3);
        updateDto.setTicketQuantity(120);
        updateDto.setRemainingTicketQuantity(100);
        updateDto.setSpecifiedFare(new BigDecimal("650000"));
        updateDto.setIsAvailable(true);

        mockMvc.perform(put("/api/flight-ticket-classes/{flightId}/{ticketClassId}", 1, 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight ticket class updated successfully"))
                .andExpect(jsonPath("$.data.ticketQuantity").value(120))
                .andExpect(jsonPath("$.data.specifiedFare").value(650000));
    }

    @Test
    @Order(5)
    @DisplayName("IT-05: Should delete a flight ticket class successfully")
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testDeleteFlightTicketClass() throws Exception {
        // First create a flight ticket class to delete
        FlightTicketClassDto createDto = new FlightTicketClassDto();
        createDto.setFlightId(2);
        createDto.setTicketClassId(1);
        createDto.setTicketQuantity(50);
        createDto.setRemainingTicketQuantity(50);
        createDto.setSpecifiedFare(new BigDecimal("400000"));
        createDto.setIsAvailable(true);

        mockMvc.perform(post("/api/flight-ticket-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        // Now delete the flight ticket class
        mockMvc.perform(delete("/api/flight-ticket-classes/{flightId}/{ticketClassId}", 2, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight ticket class deleted successfully"));
    }

    @Test
    @Order(6)
    @DisplayName("IT-06: Should get flight ticket classes by flight ID successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetFlightTicketClassesByFlightId() throws Exception {
        Integer flightId = 1;

        mockMvc.perform(get("/api/flight-ticket-classes/flight/{flightId}", flightId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight ticket classes for flight retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("IT-07: Should get available flight ticket classes successfully")
    @WithMockUser(roles = "CUSTOMER")
    void testGetAvailableFlightTicketClasses() throws Exception {
        mockMvc.perform(get("/api/flight-ticket-classes/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Available flight ticket classes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(8)
    @DisplayName("IT-08: Should calculate occupied seats successfully")
    @WithMockUser(roles = "ADMIN")
    void testCalculateOccupiedSeats() throws Exception {
        Integer flightId = 1;
        Integer ticketClassId = 1;

        mockMvc.perform(get("/api/flight-ticket-classes/occupied-seats/{flightId}/{ticketClassId}", 
                        flightId, ticketClassId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Occupied seats calculated successfully"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @Order(9)
    @DisplayName("IT-09: Should update remaining tickets successfully")
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testUpdateRemainingTickets() throws Exception {
        Integer flightId = 1;
        Integer ticketClassId = 1;
        Integer quantity = -2; // Reduce by 2

        mockMvc.perform(put("/api/flight-ticket-classes/{flightId}/{ticketClassId}/update-remaining", 
                        flightId, ticketClassId)
                        .param("quantity", quantity.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Remaining tickets updated successfully"));
    }

    @Test
    @Order(10)
    @DisplayName("IT-10: Should return error when flight ticket class not found")
    @WithMockUser(roles = "ADMIN")
    void testGetFlightTicketClassByIdNotFound() throws Exception {
        Integer nonExistentFlightId = 999999;
        Integer nonExistentTicketClassId = 999999;

        mockMvc.perform(get("/api/flight-ticket-classes/{flightId}/{ticketClassId}", 
                        nonExistentFlightId, nonExistentTicketClassId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
