package com.flightmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.dto.BookingDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.service.TicketService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private TicketDto testTicketDto;
    private BookingDto testBookingDto;
    private List<TicketDto> testTicketList;

    @BeforeEach
    void setUp() {
        // Setup test ticket
        testTicketDto = new TicketDto();
        testTicketDto.setTicketId(1);
        testTicketDto.setFlightId(1);
        testTicketDto.setPassengerId(1);
        testTicketDto.setBookCustomerId(1);
        testTicketDto.setTicketClassId(1);
        testTicketDto.setSeatNumber("1A");
        testTicketDto.setFare(new java.math.BigDecimal("299.99"));
        testTicketDto.setPaymentTime(LocalDateTime.now());
        testTicketDto.setTicketStatus((byte) 1); // Active status

        // Setup test passenger for guest booking
        PassengerDto testPassenger = new PassengerDto();
        testPassenger.setPassengerName("John Doe");
        testPassenger.setEmail("john.doe@example.com");
        testPassenger.setCitizenId("123456789");
        testPassenger.setPhoneNumber("555-0123");

        // Setup test booking for guest booking
        testBookingDto = new BookingDto();
        testBookingDto.setFlightId(1);
        testBookingDto.setTicketClassId(1);
        testBookingDto.setCustomerId(null); // Guest booking
        testBookingDto.setPassengers(Arrays.asList(testPassenger));

        testTicketList = Arrays.asList(testTicketDto);
    }

    @Test
    void getAllTickets_ShouldReturnAllTickets() throws Exception {
        when(ticketService.getAllTickets()).thenReturn(testTicketList);

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ticketId").value(1))
                .andExpect(jsonPath("$[0].flightId").value(1))
                .andExpect(jsonPath("$[0].seatNumber").value("1A"));
    }

    @Test
    void getTicketById_WithValidId_ShouldReturnTicket() throws Exception {
        when(ticketService.getTicketById(1)).thenReturn(testTicketDto);

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.flightId").value(1))
                .andExpect(jsonPath("$.seatNumber").value("1A"))
                .andExpect(jsonPath("$.price").value(299.99));
    }

    @Test
    void getTicketById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(ticketService.getTicketById(999)).thenThrow(new RuntimeException("Ticket not found"));

        mockMvc.perform(get("/api/tickets/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createTicket_WithValidData_ShouldCreateTicket() throws Exception {
        when(ticketService.createTicket(any(TicketDto.class))).thenReturn(testTicketDto);

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTicketDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.seatNumber").value("1A"));
    }

    @Test
    void createTicket_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(ticketService.createTicket(any(TicketDto.class)))
                .thenThrow(new RuntimeException("Invalid date format"));

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTicketDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid date format. Please use yyyy-MM-ddTHH:mm:ss format for datetime fields."));
    }

    @Test
    void bookTickets_WithValidGuestBooking_ShouldCreateTickets() throws Exception {
        when(ticketService.bookTickets(any(BookingDto.class))).thenReturn(testTicketList);

        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ticketId").value(1))
                .andExpect(jsonPath("$[0].seatNumber").value("1A"));
    }

    @Test
    void bookTickets_WithAuthenticatedUserBooking_ShouldCreateTickets() throws Exception {
        testBookingDto.setCustomerId(1); // Authenticated user booking
        when(ticketService.bookTickets(any(BookingDto.class))).thenReturn(testTicketList);

        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ticketId").value(1));
    }

    @Test
    void bookTickets_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(ticketService.bookTickets(any(BookingDto.class)))
                .thenThrow(new RuntimeException("Insufficient seats available"));

        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Insufficient seats available"));
    }

    @Test
    void updateTicket_WithValidData_ShouldUpdateTicket() throws Exception {
        TicketDto updatedTicket = new TicketDto();
        updatedTicket.setTicketId(1);
        updatedTicket.setSeatNumber("1B");
        
        when(ticketService.updateTicket(anyInt(), any(TicketDto.class))).thenReturn(updatedTicket);

        mockMvc.perform(put("/api/tickets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTicketDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.seatNumber").value("1B"));
    }

    @Test
    void payTicket_WithValidId_ShouldUpdateTicketStatus() throws Exception {
        TicketDto paidTicket = new TicketDto();
        paidTicket.setTicketId(1);
        paidTicket.setTicketStatus((byte) 2); // Paid status
        
        when(ticketService.payTicket(1)).thenReturn(paidTicket);

        mockMvc.perform(put("/api/tickets/1/pay"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.status").value(2));
    }

    @Test
    void cancelTicket_WithValidId_ShouldCancelTicket() throws Exception {
        doNothing().when(ticketService).cancelTicket(1);

        mockMvc.perform(put("/api/tickets/1/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelTicket_WithInvalidId_ShouldReturnError() throws Exception {
        doThrow(new RuntimeException("Ticket not found")).when(ticketService).cancelTicket(999);

        mockMvc.perform(put("/api/tickets/999/cancel"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteTicket_WithValidId_ShouldDeleteTicket() throws Exception {
        doNothing().when(ticketService).deleteTicket(1);

        mockMvc.perform(delete("/api/tickets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTicketsByFlightId_ShouldReturnTickets() throws Exception {
        when(ticketService.getTicketsByFlightId(1)).thenReturn(testTicketList);

        mockMvc.perform(get("/api/tickets/flight/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flightId").value(1));
    }

    @Test
    void getTicketsByCustomerId_ShouldReturnTickets() throws Exception {
        when(ticketService.getTicketsByCustomerId(1)).thenReturn(testTicketList);

        mockMvc.perform(get("/api/tickets/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value(1));
    }

    @Test
    void getTicketsByPassengerId_ShouldReturnTickets() throws Exception {
        when(ticketService.getTicketsByPassengerId(1)).thenReturn(testTicketList);

        mockMvc.perform(get("/api/tickets/passenger/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].passengerId").value(1));
    }

    @Test
    void getTicketsByStatus_ShouldReturnTickets() throws Exception {
        when(ticketService.getTicketsByStatus((byte) 1)).thenReturn(testTicketList);

        mockMvc.perform(get("/api/tickets/status/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value(1));
    }

    @Test
    void isSeatAvailable_WithAvailableSeat_ShouldReturnTrue() throws Exception {
        when(ticketService.isSeatAvailable(1, "2A")).thenReturn(true);

        mockMvc.perform(get("/api/tickets/seat-available")
                .param("flightId", "1")
                .param("seatNumber", "2A"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void isSeatAvailable_WithOccupiedSeat_ShouldReturnFalse() throws Exception {
        when(ticketService.isSeatAvailable(1, "1A")).thenReturn(false);

        mockMvc.perform(get("/api/tickets/seat-available")
                .param("flightId", "1")
                .param("seatNumber", "1A"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void bookTickets_WithEmptyPassengerList_ShouldReturnBadRequest() throws Exception {
        testBookingDto.setPassengers(Arrays.asList()); // Empty list
        when(ticketService.bookTickets(any(BookingDto.class)))
                .thenThrow(new RuntimeException("At least one passenger is required"));

        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: At least one passenger is required"));
    }

    @Test
    void bookTickets_WithInvalidFlightId_ShouldReturnBadRequest() throws Exception {
        testBookingDto.setFlightId(999);
        when(ticketService.bookTickets(any(BookingDto.class)))
                .thenThrow(new RuntimeException("Flight not found"));

        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Flight not found"));
    }

    @Test
    void bookTickets_WithSeatConflict_ShouldReturnBadRequest() throws Exception {
        when(ticketService.bookTickets(any(BookingDto.class)))
                .thenThrow(new RuntimeException("Seat number conflict detected"));

        mockMvc.perform(post("/api/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Seat number conflict detected"));
    }

    @Test
    void getTicketsByCustomerId_WithNoTickets_ShouldReturnEmptyList() throws Exception {
        when(ticketService.getTicketsByCustomerId(999)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tickets/customer/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getTicketsByFlightId_WithNoTickets_ShouldReturnEmptyList() throws Exception {
        when(ticketService.getTicketsByFlightId(999)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tickets/flight/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
