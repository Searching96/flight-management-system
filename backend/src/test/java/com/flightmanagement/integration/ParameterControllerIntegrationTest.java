package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.repository.ParameterRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParameterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParameterRepository parameterRepository;

    @BeforeEach
    void cleanup() {
        // Parameters are system-wide, don't delete them
        // Let the existing parameters remain for integration tests
    }

    @Test
    @Order(1)
    @DisplayName("IT-01: Should initialize default parameters")
    @WithMockUser(roles = "ADMIN")
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
    @Order(2)
    @DisplayName("IT-02: Should get all parameters successfully")
    @WithMockUser(roles = "ADMIN")
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
    @Order(3)
    @DisplayName("IT-03: Should update all parameters in bulk")
    @WithMockUser(roles = "ADMIN")
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
    @Order(4)
    @DisplayName("IT-04: Should update max medium airports parameter")
    @WithMockUser(roles = "ADMIN")
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
    @Order(5)
    @DisplayName("IT-05: Should update min flight duration parameter")
    @WithMockUser(roles = "ADMIN")
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
    @Order(6)
    @DisplayName("IT-06: Should update min layover duration parameter")
    @WithMockUser(roles = "ADMIN")
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
    @Order(7)
    @DisplayName("IT-07: Should update max layover duration parameter")
    @WithMockUser(roles = "ADMIN")
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
    @Order(8)
    @DisplayName("IT-08: Should update min booking advance parameter")
    @WithMockUser(roles = "ADMIN")
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
    @Order(9)
    @DisplayName("IT-09: Should update max booking hold parameter")
    @WithMockUser(roles = "ADMIN")
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