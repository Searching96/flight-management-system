package com.flightmanagement.service.impl;

import com.flightmanagement.dto.BookingDto;
import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.entity.*;
import com.flightmanagement.mapper.TicketMapper;
import com.flightmanagement.repository.*;
import com.flightmanagement.service.FlightTicketClassService;
import com.flightmanagement.service.PassengerService;
import com.flightmanagement.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

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
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    // Test entities
    private Ticket testTicket;
    private TicketDto testTicketDto;
    private Flight testFlight;
    private TicketClass testTicketClass;
    private Passenger testPassenger;
    private Customer testCustomer;

    // Test DTOs
    private BookingDto testBookingDto;
    private PassengerDto testPassengerDto;
    private FlightTicketClassDto testFlightTicketClassDto;

    @BeforeEach
    void setUp() {
        // Set up test entities
        testFlight = new Flight();
        testFlight.setFlightId(1);
        testFlight.setFlightCode("AA123");
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(3));

        testTicketClass = new TicketClass();
        testTicketClass.setTicketClassId(1);
        testTicketClass.setTicketClassName("Economy");
        testTicketClass.setColor("blue");

        testPassenger = new Passenger();
        testPassenger.setPassengerId(1);
        testPassenger.setPassengerName("John Doe");
        testPassenger.setEmail("john.doe@email.com");
        testPassenger.setCitizenId("123456789");
        testPassenger.setPhoneNumber("+1234567890");        testCustomer = new Customer();
        testCustomer.setCustomerId(1);        testTicket = new Ticket();
        testTicket.setTicketId(1);
        testTicket.setSeatNumber("A1");
        testTicket.setFare(new BigDecimal("299.99"));
        testTicket.setTicketStatus((byte) 2); // Unpaid
        testTicket.setFlight(testFlight);
        testTicket.setTicketClass(testTicketClass);
        testTicket.setPassenger(testPassenger);
        testTicket.setBookCustomer(testCustomer);

        // Set up test DTOs
        testTicketDto = new TicketDto();
        testTicketDto.setTicketId(1);
        testTicketDto.setFlightId(1);
        testTicketDto.setTicketClassId(1);
        testTicketDto.setBookCustomerId(1);
        testTicketDto.setPassengerId(1);
        testTicketDto.setSeatNumber("A1");
        testTicketDto.setFare(new BigDecimal("299.99"));
        testTicketDto.setTicketStatus((byte) 2);

        testPassengerDto = new PassengerDto();
        testPassengerDto.setPassengerId(1);
        testPassengerDto.setPassengerName("John Doe");
        testPassengerDto.setEmail("john.doe@email.com");
        testPassengerDto.setCitizenId("123456789");
        testPassengerDto.setPhoneNumber("+1234567890");

        testFlightTicketClassDto = new FlightTicketClassDto();
        testFlightTicketClassDto.setFlightId(1);
        testFlightTicketClassDto.setTicketClassId(1);
        testFlightTicketClassDto.setTicketQuantity(100);
        testFlightTicketClassDto.setRemainingTicketQuantity(80);
        testFlightTicketClassDto.setSpecifiedFare(new BigDecimal("299.99"));
        testFlightTicketClassDto.setTicketClassName("Economy");

        // Set up booking DTO for guest booking (customerId is null)
        testBookingDto = new BookingDto();
        testBookingDto.setFlightId(1);
        testBookingDto.setTicketClassId(1);
        testBookingDto.setCustomerId(null); // Guest booking
        testBookingDto.setPassengers(Arrays.asList(testPassengerDto));
        testBookingDto.setSeatNumbers(Arrays.asList("A1"));
    }

    // Basic service method tests
    @Test
    void getAllTickets_ShouldReturnDtoList() {
        // Arrange
        List<Ticket> entities = Arrays.asList(testTicket);
        List<TicketDto> expectedDtos = Arrays.asList(testTicketDto);

        when(ticketRepository.findAll()).thenReturn(entities);
        when(ticketMapper.toDtoList(entities)).thenReturn(expectedDtos);

        // Act
        List<TicketDto> result = ticketService.getAllTickets();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTicketDto.getTicketId(), result.get(0).getTicketId());
        verify(ticketRepository).findAll();
        verify(ticketMapper).toDtoList(entities);
    }

    @Test
    void getTicketById_WhenExists_ShouldReturnDto() {
        // Arrange
        when(ticketRepository.findById(1)).thenReturn(Optional.of(testTicket));
        when(ticketMapper.toDto(testTicket)).thenReturn(testTicketDto);

        // Act
        TicketDto result = ticketService.getTicketById(1);

        // Assert
        assertNotNull(result);
        assertEquals(testTicketDto.getTicketId(), result.getTicketId());
        verify(ticketRepository).findById(1);
        verify(ticketMapper).toDto(testTicket);
    }

    @Test
    void getTicketById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ticketService.getTicketById(1));

        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository).findById(1);
        verify(ticketMapper, never()).toDto(any());
    }

    // Guest booking specific tests
    @Test
    void bookTickets_GuestBooking_ShouldCreateTicketsWithNullCustomerId() {
        // Arrange
        when(flightTicketClassService.getFlightTicketClassById(1, 1))
                .thenReturn(testFlightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789"))
                .thenThrow(new RuntimeException("Passenger not found"))
                .thenReturn(testPassengerDto); // First call throws, second returns created passenger
        when(passengerService.createPassenger(any(PassengerDto.class)))
                .thenReturn(testPassengerDto);
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(testPassenger));
        
        // Set up ticket creation
        testTicketDto.setBookCustomerId(null); // Guest booking
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(testTicketDto);

        // Act
        List<TicketDto> result = ticketService.bookTickets(testBookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getBookCustomerId()); // Verify guest booking
        assertEquals(testPassengerDto.getPassengerId(), result.get(0).getPassengerId());
        
        verify(flightTicketClassService).getFlightTicketClassById(1, 1);
        verify(passengerService).createPassenger(any(PassengerDto.class));
        verify(flightTicketClassService).updateRemainingTickets(1, 1, 1);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void bookTickets_GuestBookingWithExistingPassenger_ShouldUseExistingPassenger() {
        // Arrange
        when(flightTicketClassService.getFlightTicketClassById(1, 1))
                .thenReturn(testFlightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789"))
                .thenReturn(testPassengerDto); // Passenger already exists
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(testPassenger));
        
        testTicketDto.setBookCustomerId(null); // Guest booking
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(testTicketDto);

        // Act
        List<TicketDto> result = ticketService.bookTickets(testBookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getBookCustomerId()); // Verify guest booking
        
        verify(passengerService).getPassengerByCitizenId("123456789");
        verify(passengerService, never()).createPassenger(any()); // Should not create new passenger
    }

    @Test
    void bookTickets_AuthenticatedUser_ShouldCreateTicketsWithCustomerId() {
        // Arrange
        testBookingDto.setCustomerId(1); // Authenticated user
        testTicketDto.setBookCustomerId(1);
        
        when(flightTicketClassService.getFlightTicketClassById(1, 1))
                .thenReturn(testFlightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789"))
                .thenReturn(testPassengerDto);
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(testPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(testTicketDto);

        // Act
        List<TicketDto> result = ticketService.bookTickets(testBookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getBookCustomerId()); // Verify authenticated booking
        
        verify(customerRepository).findById(1);
    }

    @Test
    void bookTickets_MultiplePassengers_ShouldCreateMultipleTickets() {
        // Arrange
        PassengerDto passenger2 = new PassengerDto();
        passenger2.setPassengerId(2);
        passenger2.setPassengerName("Jane Smith");
        passenger2.setEmail("jane@email.com");
        passenger2.setCitizenId("987654321");
        passenger2.setPhoneNumber("+0987654321");

        testBookingDto.setPassengers(Arrays.asList(testPassengerDto, passenger2));
        testBookingDto.setSeatNumbers(Arrays.asList("A1", "A2"));

        when(flightTicketClassService.getFlightTicketClassById(1, 1))
                .thenReturn(testFlightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789"))
                .thenReturn(testPassengerDto);
        when(passengerService.getPassengerByCitizenId("987654321"))
                .thenReturn(passenger2);
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(passengerRepository.findById(anyInt())).thenReturn(Optional.of(testPassenger));
        
        TicketDto ticket2 = new TicketDto();
        ticket2.setTicketId(2);
        ticket2.setSeatNumber("A2");
        ticket2.setPassengerId(2);
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(any(Ticket.class)))
                .thenReturn(testTicketDto)
                .thenReturn(ticket2);

        // Act
        List<TicketDto> result = ticketService.bookTickets(testBookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(flightTicketClassService).updateRemainingTickets(1, 1, 2);
        verify(ticketRepository, times(2)).save(any(Ticket.class));
    }

    @Test
    void bookTickets_InsufficientSeats_ShouldThrowException() {
        // Arrange
        testFlightTicketClassDto.setRemainingTicketQuantity(0); // No seats available
        when(flightTicketClassService.getFlightTicketClassById(1, 1))
                .thenReturn(testFlightTicketClassDto);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ticketService.bookTickets(testBookingDto));

        assertTrue(exception.getMessage().contains("Not enough tickets available"));
        verify(flightTicketClassService).getFlightTicketClassById(1, 1);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void bookTickets_InvalidFlightId_ShouldThrowException() {
        // Arrange
        when(flightTicketClassService.getFlightTicketClassById(999, 1))
                .thenThrow(new RuntimeException("FlightTicketClass not found"));

        testBookingDto.setFlightId(999);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ticketService.bookTickets(testBookingDto));

        assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void bookTickets_EmptyPassengerList_ShouldThrowException() {
        // Arrange
        testBookingDto.setPassengers(Arrays.asList()); // Empty passenger list

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTickets(testBookingDto));

        assertTrue(exception.getMessage().contains("At least one passenger is required"));
        verify(flightTicketClassService, never()).getFlightTicketClassById(anyInt(), anyInt());
    }

    @Test
    void bookTickets_SeatNumberMismatch_ShouldThrowException() {
        // Arrange
        testBookingDto.setSeatNumbers(Arrays.asList("A1", "A2")); // 2 seats for 1 passenger

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTickets(testBookingDto));

        assertTrue(exception.getMessage().contains("Number of seat numbers must match number of passengers"));
    }

    @Test
    void bookTickets_SeatAlreadyTaken_ShouldThrowException() {
        // Arrange
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A1")).thenReturn(Optional.of(testTicket));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTickets(testBookingDto));

        assertTrue(exception.getMessage().contains("Seat A1 is already taken"));
    }

    @Test
    void bookTickets_NoSeatNumbers_ShouldGenerateSeats() {
        // Arrange
        testBookingDto.setSeatNumbers(null); // No seat numbers provided
        
        when(flightTicketClassService.getFlightTicketClassById(1, 1))
                .thenReturn(testFlightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("123456789"))
                .thenReturn(testPassengerDto);
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(testPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(testTicketDto);

        // Act
        List<TicketDto> result = ticketService.bookTickets(testBookingDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getSeatNumber()); // Seat should be generated
    }

    // Other service method tests
    @Test
    void createTicket_WithValidData_ShouldCreateTicket() {
        // Arrange
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(testPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(testTicket)).thenReturn(testTicketDto);

        // Act
        TicketDto result = ticketService.createTicket(testTicketDto);

        // Assert
        assertNotNull(result);
        assertEquals(testTicketDto.getTicketId(), result.getTicketId());
        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketMapper).toDto(testTicket);
    }

    @Test
    void createTicket_GuestBookingWithNullCustomerId_ShouldCreateTicket() {
        // Arrange
        testTicketDto.setBookCustomerId(null); // Guest booking
        
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(testPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(testTicket)).thenReturn(testTicketDto);

        // Act
        TicketDto result = ticketService.createTicket(testTicketDto);

        // Assert
        assertNotNull(result);
        verify(customerRepository, never()).findById(anyInt()); // Should not try to find customer
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void updateTicket_WithValidData_ShouldUpdateTicket() {
        // Arrange
        when(ticketRepository.findById(1)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(testTicket)).thenReturn(testTicketDto);

        // Act
        TicketDto result = ticketService.updateTicket(1, testTicketDto);

        // Assert
        assertNotNull(result);
        verify(ticketRepository).findById(1);
        verify(ticketRepository).save(testTicket);
        verify(ticketMapper).toDto(testTicket);
    }

    @Test
    void payTicket_WithValidId_ShouldUpdateStatus() {
        // Arrange
        when(ticketRepository.findById(1)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketMapper.toDto(testTicket)).thenReturn(testTicketDto);

        // Act
        TicketDto result = ticketService.payTicket(1);

        // Assert
        assertNotNull(result);
        assertEquals((byte) 1, testTicket.getTicketStatus()); // Should be paid
        verify(ticketRepository).findById(1);
        verify(ticketRepository).save(testTicket);
    }

    @Test
    void cancelTicket_WithValidId_ShouldUpdateStatus() {
        // Arrange
        when(ticketRepository.findById(1)).thenReturn(Optional.of(testTicket));

        // Act
        ticketService.cancelTicket(1);

        // Assert
        assertEquals((byte) 3, testTicket.getTicketStatus()); // Should be cancelled
        verify(ticketRepository).findById(1);
        verify(ticketRepository).save(testTicket);
    }

    @Test
    void getTicketsByCustomerId_ShouldReturnCustomerTickets() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(testTicket);
        List<TicketDto> expectedDtos = Arrays.asList(testTicketDto);

        when(ticketRepository.findByCustomerId(1)).thenReturn(tickets);
        when(ticketMapper.toDtoList(tickets)).thenReturn(expectedDtos);

        // Act
        List<TicketDto> result = ticketService.getTicketsByCustomerId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository).findByCustomerId(1);
    }

    @Test
    void getTicketsByPassengerId_ShouldReturnPassengerTickets() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(testTicket);
        List<TicketDto> expectedDtos = Arrays.asList(testTicketDto);

        when(ticketRepository.findByPassengerId(1)).thenReturn(tickets);
        when(ticketMapper.toDtoList(tickets)).thenReturn(expectedDtos);

        // Act
        List<TicketDto> result = ticketService.getTicketsByPassengerId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository).findByPassengerId(1);
    }

    @Test
    void isSeatAvailable_WhenAvailable_ShouldReturnTrue() {
        // Arrange
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A1")).thenReturn(Optional.of(testTicket));

        // Act
        boolean result = ticketService.isSeatAvailable(1, "A1");

        // Assert
        assertTrue(result);
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A1")).thenReturn(Optional.of(testTicket));
    }

    @Test
    void isSeatAvailable_WhenTaken_ShouldReturnFalse() {
        // Arrange
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A1")).thenReturn(Optional.of(testTicket));

        // Act
        boolean result = ticketService.isSeatAvailable(1, "A1");

        // Assert
        assertFalse(result);
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A1")).thenReturn(Optional.of(testTicket));
    }
}
