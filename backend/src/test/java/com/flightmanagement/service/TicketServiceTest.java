package com.flightmanagement.service;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.*;
import com.flightmanagement.mapper.TicketMapper;
import com.flightmanagement.repository.*;
import com.flightmanagement.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private FlightTicketClassService flightTicketClassService;

    @Mock
    private PassengerService passengerService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private TicketClassRepository ticketClassRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private BookingDto bookingDto;
    private PassengerDto passenger1;
    private PassengerDto passenger2;
    private FlightTicketClassDto flightTicketClassDto;
    private TicketDto ticketDto1;
    private TicketDto ticketDto2;
    private Flight mockFlight;
    private TicketClass mockTicketClass;
    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        // Setup passengers
        passenger1 = new PassengerDto();
        passenger1.setPassengerId(1);
        passenger1.setCitizenId("123456789");
        passenger1.setPassengerName("John Doe");
        passenger1.setEmail("john@example.com");

        passenger2 = new PassengerDto();
        passenger2.setPassengerId(2);
        passenger2.setCitizenId("987654321");
        passenger2.setPassengerName("Jane Smith");
        passenger2.setEmail("jane@example.com");

        // Setup booking request
        bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setCustomerId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(Arrays.asList(passenger1, passenger2));
        bookingDto.setSeatNumbers(Arrays.asList("E01", "E02"));
        bookingDto.setTotalFare(new BigDecimal("200.00"));

        // Setup flight ticket class
        flightTicketClassDto = new FlightTicketClassDto();
        flightTicketClassDto.setFlightId(1);
        flightTicketClassDto.setTicketClassId(1);
        flightTicketClassDto.setRemainingTicketQuantity(10);
        flightTicketClassDto.setSpecifiedFare(new BigDecimal("100.00"));
        flightTicketClassDto.setTicketClassName("Economy");

        // Setup created tickets
        ticketDto1 = new TicketDto();
        ticketDto1.setTicketId(1);
        ticketDto1.setFlightId(1);
        ticketDto1.setPassengerId(1);
        ticketDto1.setSeatNumber("E01");
        ticketDto1.setFare(new BigDecimal("100.00"));
        ticketDto1.setTicketStatus((byte) 0);

        ticketDto2 = new TicketDto();
        ticketDto2.setTicketId(2);
        ticketDto2.setFlightId(1);
        ticketDto2.setPassengerId(2);
        ticketDto2.setSeatNumber("E02");
        ticketDto2.setFare(new BigDecimal("100.00"));
        ticketDto2.setTicketStatus((byte) 0);

        // Setup mock entities
        mockFlight = new Flight();
        mockFlight.setFlightId(1);
        mockFlight.setFlightCode("FL001");

        mockTicketClass = new TicketClass();
        mockTicketClass.setTicketClassId(1);
        mockTicketClass.setTicketClassName("Economy");

        Account mockAccount = new Account();
        mockAccount.setAccountId(1);
        mockAccount.setEmail("customer@example.com");
        mockAccount.setAccountName("Customer Name");
        mockAccount.setAccountType(1);

        mockCustomer = new Customer();
        mockCustomer.setCustomerId(1);
        mockCustomer.setAccount(mockAccount);
        mockCustomer.setScore(0);
    }

    @Test
    void testBookTickets_Success_WithProvidedSeatNumbers() {
//        List<String> s = Collections.emptyList();
        // Arrange
        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789")).thenReturn(passenger1);
        when(passengerService.getPassengerByCitizenId("987654321")).thenReturn(passenger2);
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01")).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E02")).thenReturn(Optional.empty());
        when(flightRepository.findById(1)).thenReturn(Optional.of(mockFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(mockTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(mockCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(new Passenger()));
        when(passengerRepository.findById(2)).thenReturn(Optional.of(new Passenger()));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto1, ticketDto2);

        // Act
        List<TicketDto> result = ticketService.bookTickets(bookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("E01", result.get(0).getSeatNumber());
        assertEquals("E02", result.get(1).getSeatNumber());

        verify(flightTicketClassService).getFlightTicketClassById(1, 1);
        verify(flightTicketClassService).updateRemainingTickets(1, 1, 2);
        verify(ticketRepository, times(2)).save(any(Ticket.class));
        verify(passengerService).getPassengerByCitizenId("123456789");
        verify(passengerService).getPassengerByCitizenId("987654321");
    }

    @Test
    void testBookTickets_Success_WithGeneratedSeatNumbers() {
        // Arrange
        bookingDto.setSeatNumbers(null); // No seat numbers provided

        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789")).thenReturn(passenger1);
        when(passengerService.getPassengerByCitizenId("987654321")).thenReturn(passenger2);
        when(flightRepository.findById(1)).thenReturn(Optional.of(mockFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(mockTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(mockCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(new Passenger()));
        when(passengerRepository.findById(2)).thenReturn(Optional.of(new Passenger()));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto1, ticketDto2);

        // Act
        List<TicketDto> result = ticketService.bookTickets(bookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(flightTicketClassService).updateRemainingTickets(1, 1, 2);
        verify(ticketRepository, times(2)).save(any(Ticket.class));
    }

    @Test
    void testBookTickets_Success_CreateNewPassenger() {
        // Arrange
        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789"))
                .thenThrow(new RuntimeException("Passenger not found"));
        when(passengerService.createPassenger(passenger1)).thenReturn(passenger1);
        when(passengerService.getPassengerByCitizenId("987654321")).thenReturn(passenger2);
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01")).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E02")).thenReturn(Optional.empty());
        when(flightRepository.findById(1)).thenReturn(Optional.of(mockFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(mockTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(mockCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(new Passenger()));
        when(passengerRepository.findById(2)).thenReturn(Optional.of(new Passenger()));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto1, ticketDto2);

        // Act
        List<TicketDto> result = ticketService.bookTickets(bookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(passengerService).createPassenger(passenger1);
        verify(passengerService).getPassengerByCitizenId("987654321");
    }

    @Test
    void testBookTickets_ThrowsException_NoPassengers() {
        // Arrange
        bookingDto.setPassengers(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("At least one passenger is required", exception.getMessage());
        verify(flightTicketClassService, never()).getFlightTicketClassById(anyInt(), anyInt());
    }

    @Test
    void testBookTickets_ThrowsException_EmptyPassengerList() {
        // Arrange
        bookingDto.setPassengers(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("At least one passenger is required", exception.getMessage());
    }

    @Test
    void testBookTickets_ThrowsException_SeatNumberCountMismatch() {
        // Arrange
        bookingDto.setSeatNumbers(Arrays.asList("E01")); // Only 1 seat for 2 passengers

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("Number of seat numbers must match number of passengers", exception.getMessage());
    }

    @Test
    void testBookTickets_ThrowsException_SeatAlreadyTaken() {
        // Arrange
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01"))
                .thenReturn(Optional.empty()); // Seat is taken

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("Flight or ticket class not available for the requested number of passengers", exception.getMessage());
    }

    @Test
    void testBookTickets_ThrowsException_NotEnoughTicketsAvailable() {
        // Arrange
        flightTicketClassDto.setRemainingTicketQuantity(1); // Only 1 ticket available for 2 passengers

        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01")).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E02")).thenReturn(Optional.empty());
        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("Flight or ticket class not available for the requested number of passengers", exception.getMessage());
        verify(flightTicketClassService, never()).updateRemainingTickets(anyInt(), anyInt(), anyInt());
    }

    @Test
    void testBookTickets_ThrowsException_FlightTicketClassNotFound() {
        // Arrange
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01")).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E02")).thenReturn(Optional.empty());
        when(flightTicketClassService.getFlightTicketClassById(1, 1))
                .thenThrow(new RuntimeException("Flight ticket class not found"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("Flight or ticket class not available for the requested number of passengers", exception.getMessage());
    }

    @Test
    void testBookTickets_ThrowsException_FlightNotFound() {
        // Arrange
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01")).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E02")).thenReturn(Optional.empty());
        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789")).thenReturn(passenger1);
        when(flightRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("Flight not found with id: 1", exception.getMessage());
    }

    @Test
    void testBookTickets_ThrowsException_TicketClassNotFound() {
        // Arrange
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01")).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E02")).thenReturn(Optional.empty());
        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789")).thenReturn(passenger1);
        when(flightRepository.findById(1)).thenReturn(Optional.of(mockFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.bookTickets(bookingDto);
        });

        assertEquals("TicketClass not found with id: 1", exception.getMessage());
    }

    @Test
    void testBookTickets_Success_WithoutCustomerId() {
        // Arrange - Guest booking
        bookingDto.setCustomerId(null);

        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789")).thenReturn(passenger1);
        when(passengerService.getPassengerByCitizenId("987654321")).thenReturn(passenger2);
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E01")).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "E02")).thenReturn(Optional.empty());
        when(flightRepository.findById(1)).thenReturn(Optional.of(mockFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(mockTicketClass));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(new Passenger()));
        when(passengerRepository.findById(2)).thenReturn(Optional.of(new Passenger()));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto1, ticketDto2);

        // Act
        List<TicketDto> result = ticketService.bookTickets(bookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository, never()).findById(anyInt());
        verify(flightTicketClassService).updateRemainingTickets(1, 1, 2);
    }
}
