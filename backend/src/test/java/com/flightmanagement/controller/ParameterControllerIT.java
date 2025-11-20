package com.flightmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.repository.ParameterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ParameterControllerIT {

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
    @Autowired ParameterRepository parameterRepository;

    @BeforeEach
    void cleanup() {
        // Keep tests independent - clean up the database
        parameterRepository.deleteAll();
    }

    @Test
    void initializeDefaultParameters_createsNewParameters() throws Exception {
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxMediumAirport").exists())
                .andExpect(jsonPath("$.minFlightDuration").exists())
                .andExpect(jsonPath("$.minLayoverDuration").exists())
                .andExpect(jsonPath("$.maxLayoverDuration").exists())
                .andExpect(jsonPath("$.minBookingInAdvanceDuration").exists())
                .andExpect(jsonPath("$.maxBookingHoldDuration").exists());

        // Verify parameters exist in database
        assert parameterRepository.count() == 1;
    }

    @Test
    void getParameters_afterInitialization_returnsParameters() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        // Then retrieve them
        mockMvc.perform(get("/api/parameters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.maxMediumAirport").isNumber())
                .andExpect(jsonPath("$.minFlightDuration").isNumber())
                .andExpect(jsonPath("$.minLayoverDuration").isNumber())
                .andExpect(jsonPath("$.maxLayoverDuration").isNumber())
                .andExpect(jsonPath("$.minBookingInAdvanceDuration").isNumber())
                .andExpect(jsonPath("$.maxBookingHoldDuration").isNumber());
    }

    @Test
    void updateParameters_updatesAllValues() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        // Update with new values
        Map<String, Object> updatePayload = Map.of(
                "maxMediumAirport", 15,
                "minFlightDuration", 45,
                "minLayoverDuration", 30,
                "maxLayoverDuration", 600,
                "minBookingInAdvanceDuration", 24,
                "maxBookingHoldDuration", 48
        );

        mockMvc.perform(put("/api/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxMediumAirport").value(15))
                .andExpect(jsonPath("$.minFlightDuration").value(45))
                .andExpect(jsonPath("$.minLayoverDuration").value(30))
                .andExpect(jsonPath("$.maxLayoverDuration").value(600))
                .andExpect(jsonPath("$.minBookingInAdvanceDuration").value(24))
                .andExpect(jsonPath("$.maxBookingHoldDuration").value(48));
    }

    @Test
    void updateMaxMediumAirports_updatesSpecificValue() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        int newValue = 25;
        
        mockMvc.perform(put("/api/parameters/max-medium-airports/{value}", newValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxMediumAirport").value(newValue));
    }

    @Test
    void updateMinFlightDuration_updatesSpecificValue() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        int newValue = 90;
        
        mockMvc.perform(put("/api/parameters/min-flight-duration/{value}", newValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minFlightDuration").value(newValue));
    }

    @Test
    void updateMinLayoverDuration_updatesSpecificValue() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        int newValue = 45;
        
        mockMvc.perform(put("/api/parameters/min-layover-duration/{value}", newValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minLayoverDuration").value(newValue));
    }

    @Test
    void updateMaxLayoverDuration_updatesSpecificValue() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        int newValue = 720;
        
        mockMvc.perform(put("/api/parameters/max-layover-duration/{value}", newValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxLayoverDuration").value(newValue));
    }

    @Test
    void updateMinBookingAdvance_updatesSpecificValue() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        int newValue = 48;
        
        mockMvc.perform(put("/api/parameters/min-booking-advance/{value}", newValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minBookingInAdvanceDuration").value(newValue));
    }

    @Test
    void updateMaxBookingHold_updatesSpecificValue() throws Exception {
        // First initialize parameters
        mockMvc.perform(post("/api/parameters/initialize"))
                .andExpect(status().isOk());

        int newValue = 72;
        
        mockMvc.perform(put("/api/parameters/max-booking-hold/{value}", newValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxBookingHoldDuration").value(newValue));
    }
}