package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

//    @Autowired
//    private AccountRepository accountRepository;

    @BeforeEach
    void cleanup() {
        // Use @Sql or avoid cleanup since we're using dev database
        // The dev database should be in a clean state for integration tests
        // Only clean up data created by this specific test if needed
    }

    @Test
    @Order(1)
    @DisplayName("IT-01: Should register and login successfully")
    @WithMockUser(roles = "CUSTOMER")
    void registerAndLogin_returnsToken() throws Exception {
        // Use timestamp to ensure unique email
        String uniqueEmail = "ituser" + System.currentTimeMillis() + "@example.com";
        
        // Register a new customer
        Map<String, Object> registerPayload = Map.of(
                "email", uniqueEmail,
                "password", "P@ssw0rd123",
                "accountName", "IT Test User",
                "citizenId", "123456789",
                "phoneNumber", "0123456789",
                "accountType", 1 // customer
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.userDetails.email").value(uniqueEmail));

        // Now login with the registered user
        Map<String, Object> loginPayload = Map.of(
                "email", uniqueEmail,
                "password", "P@ssw0rd123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.userDetails.email").value(uniqueEmail));
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Should return error for invalid email during registration")
    @WithMockUser(roles = "CUSTOMER")
    void register_invalidEmail_returnsInternalServerError() throws Exception {
        Map<String, Object> registerPayload = Map.of(
                "email", "invalid-email",
                "password", "P@ssw0rd123",
                "accountName", "Test User",
                "citizenId", "123456789",
                "phoneNumber", "0123456789",
                "accountType", 1
        );

        // Validation happens at entity level, so we expect 500 error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Email should be valid")));
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Should return error for invalid credentials during login")
    @WithMockUser(roles = "CUSTOMER")
    void login_invalidCredentials_returnsUnauthorized() throws Exception {
        Map<String, Object> loginPayload = Map.of(
                "email", "nonexistent@example.com",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    @DisplayName("IT-05: Should process forgot password request successfully")
    @WithMockUser(roles = "CUSTOMER")
    void forgotPassword_validEmail_returnsOk() throws Exception {
        // First register a user
        Map<String, Object> registerPayload = Map.of(
                "email", "resetuser@example.com",
                "password", "P@ssw0rd123",
                "accountName", "Reset User",
                "citizenId", "987654321",
                "phoneNumber", "0987654321",
                "accountType", 1
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated());

        // Request password reset - need to include phoneNumber as it's required by the DTO
        Map<String, Object> resetPayload = Map.of(
                "email", "resetuser@example.com",
                "phoneNumber", "0987654321"
        );

        mockMvc.perform(post("/api/auth/forget-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset email sent"));
    }
}