package com.flightmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.repository.AccountRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIT {

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
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    void cleanup() {
        // Keep tests independent - clean up the database
        accountRepository.deleteAll();
    }

    @Test
    void registerAndLogin_returnsToken() throws Exception {
        // Register a new customer
        Map<String, Object> registerPayload = Map.of(
                "email", "ituser@example.com",
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
                .andExpect(jsonPath("$.data.userDetails.email").value("ituser@example.com"));

        // Login with the registered user
        Map<String, Object> loginPayload = Map.of(
                "email", "ituser@example.com",
                "password", "P@ssw0rd123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.userDetails.email").value("ituser@example.com"));
    }

    @Test
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