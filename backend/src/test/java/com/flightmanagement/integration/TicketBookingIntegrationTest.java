package com.flightmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;                
import com.flightmanagement.dto.BookingDto;
import com.flightmanagement.dto.PassengerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class TicketBookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bookTickets_WithValidData_ShouldCreateBooking() throws Exception {
        // Arrange
        PassengerDto passenger = new PassengerDto();
        passenger.setPassengerName("John Doe");
        passenger.setEmail("john.doe@email.com");
        passenger.setCitizenId("123456789");
        passenger.setPhoneNumber("+1234567890");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1); // Assuming demo data exists
        bookingDto.setCustomerId(1); // Assuming demo customer exists
        bookingDto.setTicketClassId(1); // Assuming demo ticket class exists
        bookingDto.setPassengers(Arrays.asList(passenger));
        bookingDto.setSeatNumbers(Arrays.asList("A1"));

        String requestBody = objectMapper.writeValueAsString(bookingDto);

        // Act & Assert
        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flightId").value(1))
                .andExpect(jsonPath("$[0].seatNumber").value("A1"))
                .andExpect(jsonPath("$[0].ticketStatus").value(2)); // Unpaid
    }

    @Test
    void bookTickets_WithMultiplePassengers_ShouldCreateMultipleTickets() throws Exception {
        // Arrange
        PassengerDto passenger1 = new PassengerDto();
        passenger1.setPassengerName("John Doe");
        passenger1.setEmail("john.doe@email.com");
        passenger1.setCitizenId("123456789");
        passenger1.setPhoneNumber("+1234567890");

        PassengerDto passenger2 = new PassengerDto();
        passenger2.setPassengerName("Jane Smith");
        passenger2.setEmail("jane.smith@email.com");
        passenger2.setCitizenId("987654321");
        passenger2.setPhoneNumber("+0987654321");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setCustomerId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(Arrays.asList(passenger1, passenger2));
        bookingDto.setSeatNumbers(Arrays.asList("A1", "A2"));

        String requestBody = objectMapper.writeValueAsString(bookingDto);

        // Act & Assert
        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].seatNumber").value("A1"))
                .andExpect(jsonPath("$[1].seatNumber").value("A2"));
    }

    @Test
    void bookTickets_WithInvalidFlightId_ShouldReturnError() throws Exception {
        // Arrange
        PassengerDto passenger = new PassengerDto();
        passenger.setPassengerName("John Doe");
        passenger.setEmail("john.doe@email.com");
        passenger.setCitizenId("123456789");
        passenger.setPhoneNumber("+1234567890");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(999); // Non-existent flight
        bookingDto.setCustomerId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(Arrays.asList(passenger));
        bookingDto.setSeatNumbers(Arrays.asList("A1"));

        String requestBody = objectMapper.writeValueAsString(bookingDto);

        // Act & Assert
        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookTickets_WithInsufficientSeats_ShouldReturnError() throws Exception {
        // Arrange - Attempt to book more passengers than available seats
        // This test assumes the demo flight has limited availability
        PassengerDto passenger = new PassengerDto();
        passenger.setPassengerName("John Doe");
        passenger.setEmail("john.doe@email.com");
        passenger.setCitizenId("123456789");
        passenger.setPhoneNumber("+1234567890");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setCustomerId(1);
        bookingDto.setTicketClassId(1);
        // Create many passengers to exceed capacity
        List<PassengerDto> manyPassengers = Arrays.asList(
            passenger, passenger, passenger, passenger, passenger,
            passenger, passenger, passenger, passenger, passenger
        );
        bookingDto.setPassengers(manyPassengers);

        String requestBody = objectMapper.writeValueAsString(bookingDto);

        // Act & Assert
        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Not enough tickets available")));
    }
}
