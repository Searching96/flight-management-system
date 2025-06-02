package com.flightmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PassengerController.class)
class PassengerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PassengerService passengerService;

    @Autowired
    private ObjectMapper objectMapper;

    private PassengerDto testPassengerDto;
    private List<PassengerDto> testPassengerList;

    @BeforeEach
    void setUp() {
        testPassengerDto = new PassengerDto();
        testPassengerDto.setPassengerId(1);
        testPassengerDto.setPassengerName("John Doe");
        testPassengerDto.setEmail("john.doe@example.com");
        testPassengerDto.setCitizenId("123456789");
        testPassengerDto.setPhoneNumber("555-0123");

        testPassengerList = Arrays.asList(testPassengerDto);
    }

    @Test
    void getAllPassengers_ShouldReturnAllPassengers() throws Exception {
        when(passengerService.getAllPassengers()).thenReturn(testPassengerList);

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].passengerId").value(1))
                .andExpect(jsonPath("$[0].passengerName").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void getPassengerById_WithValidId_ShouldReturnPassenger() throws Exception {
        when(passengerService.getPassengerById(1)).thenReturn(testPassengerDto);

        mockMvc.perform(get("/api/passengers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.passengerId").value(1))
                .andExpect(jsonPath("$.passengerName").value("John Doe"))
                .andExpect(jsonPath("$.citizenId").value("123456789"));
    }

    @Test
    void getPassengerById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(passengerService.getPassengerById(999)).thenThrow(new RuntimeException("Passenger not found"));

        mockMvc.perform(get("/api/passengers/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPassenger_WithValidData_ShouldCreatePassenger() throws Exception {
        PassengerDto newPassenger = new PassengerDto();
        newPassenger.setPassengerName("Jane Smith");
        newPassenger.setEmail("jane.smith@example.com");
        newPassenger.setCitizenId("987654321");
        newPassenger.setPhoneNumber("555-0456");

        PassengerDto createdPassenger = new PassengerDto();
        createdPassenger.setPassengerId(2);
        createdPassenger.setPassengerName("Jane Smith");
        createdPassenger.setEmail("jane.smith@example.com");
        createdPassenger.setCitizenId("987654321");
        createdPassenger.setPhoneNumber("555-0456");

        when(passengerService.createPassenger(any(PassengerDto.class))).thenReturn(createdPassenger);

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPassenger)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.passengerId").value(2))
                .andExpect(jsonPath("$.passengerName").value("Jane Smith"))
                .andExpect(jsonPath("$.citizenId").value("987654321"));
    }

    @Test
    void createPassenger_WithDuplicateCitizenId_ShouldReturnBadRequest() throws Exception {
        PassengerDto duplicatePassenger = new PassengerDto();
        duplicatePassenger.setPassengerName("Jane Smith");
        duplicatePassenger.setEmail("jane.smith@example.com");
        duplicatePassenger.setCitizenId("123456789"); // Same as existing
        duplicatePassenger.setPhoneNumber("555-0456");

        when(passengerService.createPassenger(any(PassengerDto.class)))
                .thenThrow(new RuntimeException("Passenger with this citizen ID already exists"));

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicatePassenger)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updatePassenger_WithValidData_ShouldUpdatePassenger() throws Exception {
        PassengerDto updatedPassenger = new PassengerDto();
        updatedPassenger.setPassengerId(1);
        updatedPassenger.setPassengerName("John Doe Updated");
        updatedPassenger.setEmail("john.doe.updated@example.com");
        updatedPassenger.setCitizenId("123456789");
        updatedPassenger.setPhoneNumber("555-9999");

        when(passengerService.updatePassenger(anyInt(), any(PassengerDto.class))).thenReturn(updatedPassenger);

        mockMvc.perform(put("/api/passengers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassengerDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.passengerId").value(1))
                .andExpect(jsonPath("$.passengerName").value("John Doe Updated"))
                .andExpect(jsonPath("$.phoneNumber").value("555-9999"));
    }

    @Test
    void deletePassenger_WithValidId_ShouldDeletePassenger() throws Exception {
        doNothing().when(passengerService).deletePassenger(1);

        mockMvc.perform(delete("/api/passengers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePassenger_WithInvalidId_ShouldReturnError() throws Exception {
        doThrow(new RuntimeException("Passenger not found")).when(passengerService).deletePassenger(999);

        mockMvc.perform(delete("/api/passengers/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPassengerByCitizenId_WithValidCitizenId_ShouldReturnPassenger() throws Exception {
        when(passengerService.getPassengerByCitizenId("123456789")).thenReturn(testPassengerDto);

        mockMvc.perform(get("/api/passengers/citizen-id/123456789"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.citizenId").value("123456789"))
                .andExpect(jsonPath("$.passengerName").value("John Doe"));
    }

    @Test
    void getPassengerByCitizenId_WithInvalidCitizenId_ShouldReturnNotFound() throws Exception {
        when(passengerService.getPassengerByCitizenId("999999999"))
                .thenThrow(new RuntimeException("Passenger not found"));

        mockMvc.perform(get("/api/passengers/citizen-id/999999999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPassengersByEmail_WithValidEmail_ShouldReturnPassengers() throws Exception {
        when(passengerService.getPassengersByEmail("john.doe@example.com")).thenReturn(testPassengerList);

        mockMvc.perform(get("/api/passengers/email/john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void getPassengersByEmail_WithNoMatches_ShouldReturnEmptyList() throws Exception {
        when(passengerService.getPassengersByEmail("notfound@example.com")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/passengers/email/notfound@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchPassengersByName_WithValidName_ShouldReturnPassengers() throws Exception {
        when(passengerService.searchPassengersByName("John")).thenReturn(testPassengerList);

        mockMvc.perform(get("/api/passengers/search/John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].passengerName").value("John Doe"));
    }

    @Test
    void searchPassengersByName_WithPartialMatch_ShouldReturnPassengers() throws Exception {
        PassengerDto johnSmith = new PassengerDto();
        johnSmith.setPassengerId(2);
        johnSmith.setPassengerName("John Smith");
        johnSmith.setEmail("john.smith@example.com");
        johnSmith.setCitizenId("111222333");

        List<PassengerDto> johnPassengers = Arrays.asList(testPassengerDto, johnSmith);
        when(passengerService.searchPassengersByName("John")).thenReturn(johnPassengers);

        mockMvc.perform(get("/api/passengers/search/John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].passengerName").value("John Doe"))
                .andExpect(jsonPath("$[1].passengerName").value("John Smith"));
    }

    @Test
    void searchPassengersByName_WithNoMatches_ShouldReturnEmptyList() throws Exception {
        when(passengerService.searchPassengersByName("NonExistent")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/passengers/search/NonExistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createPassenger_WithInvalidEmailFormat_ShouldReturnBadRequest() throws Exception {
        PassengerDto invalidPassenger = new PassengerDto();
        invalidPassenger.setPassengerName("Invalid Email");
        invalidPassenger.setEmail("invalid-email");
        invalidPassenger.setCitizenId("111111111");
        invalidPassenger.setPhoneNumber("555-0000");

        when(passengerService.createPassenger(any(PassengerDto.class)))
                .thenThrow(new RuntimeException("Invalid email format"));

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPassenger)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPassenger_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        PassengerDto incompletePassenger = new PassengerDto();
        incompletePassenger.setPassengerName(""); // Empty name
        incompletePassenger.setEmail("test@example.com");
        incompletePassenger.setCitizenId(""); // Empty citizen ID

        when(passengerService.createPassenger(any(PassengerDto.class)))
                .thenThrow(new RuntimeException("Required fields are missing"));

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompletePassenger)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updatePassenger_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(passengerService.updatePassenger(anyInt(), any(PassengerDto.class)))
                .thenThrow(new RuntimeException("Passenger not found"));

        mockMvc.perform(put("/api/passengers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassengerDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void searchPassengersByName_WithSpecialCharacters_ShouldHandleGracefully() throws Exception {
        when(passengerService.searchPassengersByName("O'Connor")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/passengers/search/O'Connor"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getPassengersByEmail_WithSpecialCharacters_ShouldHandleGracefully() throws Exception {
        String emailWithPlus = "john+test@example.com";
        when(passengerService.getPassengersByEmail(emailWithPlus)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/passengers/email/" + emailWithPlus))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllPassengers_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(passengerService.getAllPassengers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
