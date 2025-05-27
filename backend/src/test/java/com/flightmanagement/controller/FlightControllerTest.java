package com.flightmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.FlightSearchCriteria;
import com.flightmanagement.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    private FlightDto testFlightDto;
    private FlightSearchCriteria testSearchCriteria;

    @BeforeEach
    void setUp() {
        testFlightDto = new FlightDto();
        testFlightDto.setFlightId(1);
        testFlightDto.setFlightCode("AA123");
        testFlightDto.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlightDto.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(3));
        testFlightDto.setDepartureAirportId(1);
        testFlightDto.setDepartureAirportName("JFK Airport");
        testFlightDto.setDepartureCityName("New York");
        testFlightDto.setArrivalAirportId(2);
        testFlightDto.setArrivalAirportName("LAX Airport");
        testFlightDto.setArrivalCityName("Los Angeles");

        testSearchCriteria = new FlightSearchCriteria();
        testSearchCriteria.setDepartureAirportId(1);
        testSearchCriteria.setArrivalAirportId(2);
        testSearchCriteria.setDepartureDate(LocalDateTime.now().plusDays(1));
        testSearchCriteria.setPassengerCount(1);
        testSearchCriteria.setTicketClassId(1);
    }

    @Test
    void getAllFlights_ShouldReturnFlightList() throws Exception {
        // Arrange
        List<FlightDto> flights = Arrays.asList(testFlightDto);
        when(flightService.getAllFlights()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flightId").value(1))
                .andExpect(jsonPath("$[0].flightCode").value("AA123"));
    }

    @Test
    void getFlightById_WhenExists_ShouldReturnFlight() throws Exception {
        // Arrange
        when(flightService.getFlightById(1)).thenReturn(testFlightDto);

        // Act & Assert
        mockMvc.perform(get("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flightId").value(1))
                .andExpect(jsonPath("$.flightCode").value("AA123"));
    }

    @Test
    void searchFlights_WithValidCriteria_ShouldReturnFlights() throws Exception {
        // Arrange
        List<FlightDto> flights = Arrays.asList(testFlightDto);
        when(flightService.searchFlights(any(FlightSearchCriteria.class))).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(get("/api/flights/search")
                .param("departureAirportId", "1")
                .param("arrivalAirportId", "2")
                .param("departureDate", "2024-12-01T10:00:00")
                .param("passengerCount", "1")
                .param("ticketClassId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flightId").value(1));
    }

    @Test
    void createFlight_WithValidData_ShouldCreateFlight() throws Exception {
        // Arrange
        when(flightService.createFlight(any())).thenReturn(testFlightDto);

        String flightJson = objectMapper.writeValueAsString(testFlightDto);

        // Act & Assert
        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(flightJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flightId").value(1))
                .andExpect(jsonPath("$.flightCode").value("AA123"));
    }

    @Test
    void updateFlight_WithValidData_ShouldUpdateFlight() throws Exception {
        // Arrange
        testFlightDto.setFlightCode("AA124");
        when(flightService.updateFlight(anyInt(), any())).thenReturn(testFlightDto);

        String flightJson = objectMapper.writeValueAsString(testFlightDto);

        // Act & Assert
        mockMvc.perform(put("/api/flights/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(flightJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flightCode").value("AA124"));
    }

    @Test
    void deleteFlight_WithValidId_ShouldDeleteFlight() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/flights/1"))
                .andExpect(status().isOk());
    }
}
