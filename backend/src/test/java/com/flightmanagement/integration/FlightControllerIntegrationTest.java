package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.FlightRequest;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.repository.FlightRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FlightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlightRepository flightRepository;

    private Integer createdFlightId;

    @Test
    @Order(1)
    @DisplayName("IT-01: Should get all flights successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetAllFlights() throws Exception {
        mockMvc.perform(get("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flights retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Should get flight by ID successfully")
    @WithMockUser(roles = "ADMIN")
    void testGetFlightById() throws Exception {
        // Assuming there's at least one flight in the database
        Integer flightId = 1;

        mockMvc.perform(get("/api/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight retrieved successfully"))
                .andExpect(jsonPath("$.data.flightId").value(flightId));
    }

    @Test
    @Order(3)
    @DisplayName("IT-03: Should create a new flight successfully")
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testCreateFlight() throws Exception {
        LocalDateTime departureTime = LocalDateTime.now().plusDays(7);
        LocalDateTime arrivalTime = departureTime.plusHours(3);

        FlightRequest request = new FlightRequest();
        request.setFlightCode("IT-TEST-001");
        request.setDepartureTime(departureTime);
        request.setArrivalTime(arrivalTime);
        request.setPlaneId(1);
        request.setDepartureAirportId(1);
        request.setArrivalAirportId(2);

        MvcResult result = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight created successfully"))
                .andExpect(jsonPath("$.data.flightCode").value("IT-TEST-001"))
                .andExpect(jsonPath("$.data.flightId").isNumber())
                .andReturn();

        // Extract the created flight ID for use in subsequent tests
        String responseContent = result.getResponse().getContentAsString();
        createdFlightId = objectMapper.readTree(responseContent)
                .get("data")
                .get("flightId")
                .asInt();
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Should update an existing flight successfully")
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testUpdateFlight() throws Exception {
        // First create a flight to update
        LocalDateTime departureTime = LocalDateTime.now().plusDays(8);
        LocalDateTime arrivalTime = departureTime.plusHours(3);

        FlightRequest createRequest = new FlightRequest();
        createRequest.setFlightCode("IT-TEST-002");
        createRequest.setDepartureTime(departureTime);
        createRequest.setArrivalTime(arrivalTime);
        createRequest.setPlaneId(1);
        createRequest.setDepartureAirportId(1);
        createRequest.setArrivalAirportId(2);

        MvcResult createResult = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseContent = createResult.getResponse().getContentAsString();
        Integer flightId = objectMapper.readTree(createResponseContent)
                .get("data")
                .get("flightId")
                .asInt();

        // Now update the flight
        FlightRequest updateRequest = new FlightRequest();
        updateRequest.setFlightCode("IT-TEST-002-UPDATED");
        updateRequest.setDepartureTime(departureTime.plusHours(1));
        updateRequest.setArrivalTime(arrivalTime.plusHours(1));
        updateRequest.setPlaneId(1);
        updateRequest.setDepartureAirportId(1);
        updateRequest.setArrivalAirportId(2);

        mockMvc.perform(put("/api/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight updated successfully"))
                .andExpect(jsonPath("$.data.flightCode").value("IT-TEST-002-UPDATED"));
    }

    @Test
    @Order(5)
    @DisplayName("IT-05: Should delete a flight successfully")
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testDeleteFlight() throws Exception {
        // First create a flight to delete
        LocalDateTime departureTime = LocalDateTime.now().plusDays(9);
        LocalDateTime arrivalTime = departureTime.plusHours(3);

        FlightRequest createRequest = new FlightRequest();
        createRequest.setFlightCode("IT-TEST-DELETE");
        createRequest.setDepartureTime(departureTime);
        createRequest.setArrivalTime(arrivalTime);
        createRequest.setPlaneId(1);
        createRequest.setDepartureAirportId(1);
        createRequest.setArrivalAirportId(2);

        MvcResult createResult = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseContent = createResult.getResponse().getContentAsString();
        Integer flightId = objectMapper.readTree(createResponseContent)
                .get("data")
                .get("flightId")
                .asInt();

        // Now delete the flight
        mockMvc.perform(delete("/api/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight deleted successfully"));
    }

    @Test
    @Order(6)
    @DisplayName("IT-06: Should search flights with criteria successfully")
    @WithMockUser(roles = "ADMIN")
    void testSearchFlights() throws Exception {
        LocalDateTime departureDate = LocalDateTime.now().plusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        mockMvc.perform(get("/api/flights/search")
                        .param("departureAirportId", "1")
                        .param("arrivalAirportId", "2")
                        .param("departureDate", departureDate.format(formatter))
                        .param("passengerCount", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Flight search completed"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("IT-07: Should return error when flight ID not found")
    @WithMockUser(roles = "ADMIN")
    void testGetFlightByIdNotFound() throws Exception {
        Integer nonExistentId = 999999;

        mockMvc.perform(get("/api/flights/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(8)
    @DisplayName("IT-08: Should validate flight creation with invalid data")
    @WithMockUser(roles = "ADMIN")
    void testCreateFlightWithInvalidData() throws Exception {
        FlightRequest invalidRequest = new FlightRequest();
        // Missing required fields

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is4xxClientError());
    }
}
