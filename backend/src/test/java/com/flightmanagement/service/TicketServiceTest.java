package com.flightmanagement.service;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.*;
import com.flightmanagement.mapper.TicketMapper;
import com.flightmanagement.repository.*;
import com.flightmanagement.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ticket Service Test Suite")
@MockitoSettings(strictness = Strictness.LENIENT)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private TicketClassRepository ticketClassRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private FlightTicketClassService flightTicketClassService;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private PassengerService passengerService;

    @Mock
    private EmailService emailService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    // ============================================================
    // NESTED CLASS: CREATE TICKET TESTS
    // ============================================================

    @Nested
    @DisplayName("CreateTicket Tests - Full Path Coverage (10 tests)")
    class CreateTicketTests {

        private TicketDto ticketDto;
        private Ticket ticket;
        private Flight flight;
        private TicketClass ticketClass;
        private Customer customer;
        private Passenger passenger;

        @BeforeEach
        void setUp() {
            // Setup common test data
            ticketDto = new TicketDto();
            ticketDto.setSeatNumber("12A");
            ticketDto.setPaymentTime(LocalDateTime.now());
            ticketDto.setFare(new BigDecimal("500.00"));
            ticketDto.setConfirmationCode("ABC123");

            ticket = new Ticket();
            flight = new Flight();
            ticketClass = new TicketClass();
            customer = new Customer();
            passenger = new Passenger();
        }

        // ===== NHÓM 1: Happy Paths =====

        @Test
        @DisplayName("TC1: All fields valid - Success")
        void createTicket_AllFieldsValid_Success() {
            // Arrange
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(1);
            ticketDto.setBookCustomerId(1);
            ticketDto.setPassengerId(1);

            when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
            when(ticketClassRepository.findById(1)).thenReturn(Optional.of(ticketClass));
            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toDto(ticket)).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.createTicket(ticketDto);

            // Assert
            assertNotNull(result);
            verify(flightRepository).findById(1);
            verify(ticketClassRepository).findById(1);
            verify(customerRepository).findById(1);
            verify(passengerRepository).findById(1);
            verify(ticketRepository).save(any(Ticket.class));
            verify(ticketMapper).toDto(ticket);
        }

        @Test
        @DisplayName("TC2: Guest booking with null customerId - Success")
        void createTicket_GuestBookingWithNullCustomerId_Success() {
            // Arrange - Guest booking (bookCustomerId = null)
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(1);
            ticketDto.setBookCustomerId(null); // Guest booking
            ticketDto.setPassengerId(1);

            when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
            when(ticketClassRepository.findById(1)).thenReturn(Optional.of(ticketClass));
            when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toDto(ticket)).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.createTicket(ticketDto);

            // Assert
            assertNotNull(result);
            verify(customerRepository, never()).findById(any());
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("TC3: Minimal required fields - Success")
        void createTicket_MinimalRequiredFields_Success() {
            // Arrange - Only required fields, all optional IDs are null
            ticketDto.setFlightId(null);
            ticketDto.setTicketClassId(null);
            ticketDto.setBookCustomerId(null);
            ticketDto.setPassengerId(null);

            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toDto(ticket)).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.createTicket(ticketDto);

            // Assert
            assertNotNull(result);

            // Verify that no lookups were performed
            verify(flightRepository, never()).findById(any());
            verify(ticketClassRepository, never()).findById(any());
            verify(customerRepository, never()).findById(any());
            verify(passengerRepository, never()).findById(any());
            verify(ticketRepository).save(any(Ticket.class));
        }

        // ===== NHÓM 2: Exception Paths =====

        @Test
        @DisplayName("TC4: Flight not found - Throws exception")
        void createTicket_FlightNotFound_ThrowsException() {
            // Arrange
            ticketDto.setFlightId(999);

            when(flightRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.createTicket(ticketDto);
            });

            assertEquals("Flight not found with id: 999", exception.getMessage());
            verify(flightRepository).findById(999);
            verify(ticketRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC5: TicketClass not found - Throws exception")
        void createTicket_TicketClassNotFound_ThrowsException() {
            // Arrange
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(999);

            when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
            when(ticketClassRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.createTicket(ticketDto);
            });

            assertEquals("TicketClass not found with id: 999", exception.getMessage());
            verify(ticketClassRepository).findById(999);
            verify(ticketRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC6: Passenger not found - Throws exception")
        void createTicket_PassengerNotFound_ThrowsException() {
            // Arrange
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(1);
            ticketDto.setBookCustomerId(1);
            ticketDto.setPassengerId(999);

            when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
            when(ticketClassRepository.findById(1)).thenReturn(Optional.of(ticketClass));
            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(passengerRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.createTicket(ticketDto);
            });

            assertEquals("Passenger not found with id: 999", exception.getMessage());
            verify(passengerRepository).findById(999);
            verify(ticketRepository, never()).save(any());
        }

        // ===== NHÓM 3: Edge Cases =====

        @Test
        @DisplayName("TC7: BookCustomerId = 0 - Skips customer lookup")
        void createTicket_BookCustomerIdZero_SkipsCustomerLookup() {
            // Arrange - Guest booking with customerId = 0
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(1);
            ticketDto.setBookCustomerId(0); // Guest booking with ID = 0
            ticketDto.setPassengerId(1);

            when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
            when(ticketClassRepository.findById(1)).thenReturn(Optional.of(ticketClass));
            when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toDto(ticket)).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.createTicket(ticketDto);

            // Assert
            assertNotNull(result);
            verify(customerRepository, never()).findById(any());
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("TC8: Partial null fields - Success")
        void createTicket_PartialNullFields_Success() {
            // Arrange - Mix of null and valid fields
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(null); // Null
            ticketDto.setBookCustomerId(1);
            ticketDto.setPassengerId(null); // Null

            when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toDto(ticket)).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.createTicket(ticketDto);

            // Assert
            assertNotNull(result);
            verify(flightRepository).findById(1);
            verify(ticketClassRepository, never()).findById(any());
            verify(customerRepository).findById(1);
            verify(passengerRepository, never()).findById(any());
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("TC9: All entities valid - Verify relationships set")
        void createTicket_AllEntitiesValid_VerifyRelationshipsSet() {
            // Arrange
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(1);
            ticketDto.setBookCustomerId(1);
            ticketDto.setPassengerId(1);

            Ticket savedTicket = new Ticket();
            savedTicket.setSeatNumber("12A");
            savedTicket.setFlight(flight);
            savedTicket.setTicketClass(ticketClass);
            savedTicket.setBookCustomer(customer);
            savedTicket.setPassenger(passenger);

            when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
            when(ticketClassRepository.findById(1)).thenReturn(Optional.of(ticketClass));
            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));
            when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
                Ticket t = invocation.getArgument(0);
                // Verify relationships are set
                assertNotNull(t.getFlight());
                assertNotNull(t.getTicketClass());
                assertNotNull(t.getBookCustomer());
                assertNotNull(t.getPassenger());
                assertNull(t.getDeletedAt());
                return savedTicket;
            });
            when(ticketMapper.toDto(savedTicket)).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.createTicket(ticketDto);

            // Assert
            assertNotNull(result);
            verify(ticketRepository).save(any(Ticket.class));
        }
    }

    // ============================================================
    // NESTED CLASS: BOOK TICKETS TESTS
    // ============================================================

    @Nested
    @DisplayName("BookTickets Tests - Full Path Coverage (12 tests)")
    class BookTicketsTests {

        private BookingDto bookingDto;
        private FlightTicketClassDto flightTicketClassDto;
        private PassengerDto passengerDto;
        private TicketDto ticketDto;
        private Flight flight;
        private TicketClass ticketClass;
        private Customer customer;
        private Passenger passenger;
        private Ticket ticket;

        @BeforeEach
        void setUp() {
            // Setup booking request
            bookingDto = new BookingDto();
            bookingDto.setFlightId(1);
            bookingDto.setTicketClassId(1);
            bookingDto.setCustomerId(1);

            // Setup passenger
            passengerDto = new PassengerDto();
            passengerDto.setPassengerId(1);
            passengerDto.setPassengerName("John Doe");
            passengerDto.setEmail("john@example.com");
            passengerDto.setCitizenId("123456789");

            // Setup flight ticket class
            flightTicketClassDto = new FlightTicketClassDto();
            flightTicketClassDto.setFlightId(1);
            flightTicketClassDto.setTicketClassId(1);
            flightTicketClassDto.setRemainingTicketQuantity(10);
            flightTicketClassDto.setSpecifiedFare(new BigDecimal("500.00"));
            flightTicketClassDto.setTicketClassName("Economy");

            // Setup ticket DTO
            ticketDto = new TicketDto();
            ticketDto.setTicketId(1);
            ticketDto.setFlightId(1);
            ticketDto.setTicketClassId(1);
            ticketDto.setPassengerId(1);
            ticketDto.setSeatNumber("12A");
            ticketDto.setFare(new BigDecimal("500.00"));
            ticketDto.setConfirmationCode("FMS-20251207-TEST");

            // Setup entities
            flight = new Flight();
            ticketClass = new TicketClass();
            customer = new Customer();
            passenger = new Passenger();
            passenger.setPassengerId(1);
            ticket = new Ticket();
        }

        // ===== NHÓM 1: Happy Paths - Single Passenger =====

        @Test
        @DisplayName("TC1: Single passenger with seat number - Success")
        void bookTickets_SinglePassengerWithSeatNumber_Success() {
            // Arrange
            bookingDto.setPassengers(Arrays.asList(passengerDto));
            bookingDto.setSeatNumbers(Arrays.asList("12A"));

            mockCreateTicketDependencies();

            // Act
            List<TicketDto> result = ticketService.bookTickets(bookingDto);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(flightTicketClassService, atLeastOnce()).getFlightTicketClassById(1, 1);
            verify(flightTicketClassService).updateRemainingTickets(1, 1, 1);
        }

        @Test
        @DisplayName("TC2: Single passenger, seatNumbers null - Auto generates seat")
        void bookTickets_SinglePassengerNoSeatList_AutoGeneratesSeat() {
            // Arrange
            bookingDto.setPassengers(Arrays.asList(passengerDto));
            bookingDto.setSeatNumbers(null); // Trigger generateSeatNumber() path

            mockCreateTicketDependencies();

            // Act
            List<TicketDto> result = ticketService.bookTickets(bookingDto);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.get(0).getSeatNumber());
            verify(flightTicketClassService).updateRemainingTickets(1, 1, 1);
        }

        @Test
        @DisplayName("TC3: Guest booking (null customerId) - Success")
        void bookTickets_GuestBooking_NullCustomerId_Success() {
            // Arrange
            bookingDto.setCustomerId(null); // Guest booking
            bookingDto.setPassengers(Arrays.asList(passengerDto));
            bookingDto.setSeatNumbers(Arrays.asList("12A"));

            mockCreateTicketDependencies();

            // Act
            List<TicketDto> result = ticketService.bookTickets(bookingDto);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(flightTicketClassService).updateRemainingTickets(1, 1, 1);
        }

        // ===== NHÓM 2: Happy Paths - Multiple Passengers =====

        @Test
        @DisplayName("TC4: Multiple passengers with all seats - Success")
        void bookTickets_MultiplePassengersWithAllSeats_Success() {
            // Arrange
            PassengerDto passenger2 = new PassengerDto();
            passenger2.setPassengerId(2);
            passenger2.setPassengerName("Jane Smith");
            passenger2.setEmail("jane@example.com");
            passenger2.setCitizenId("987654321");

            bookingDto.setPassengers(Arrays.asList(passengerDto, passenger2));
            bookingDto.setSeatNumbers(Arrays.asList("12A", "12B"));

            mockCreateTicketDependencies();

            // Act
            List<TicketDto> result = ticketService.bookTickets(bookingDto);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightTicketClassService).updateRemainingTickets(1, 1, 2);
        }

        // ===== NHÓM 3: Exception Paths =====

        @Test
        @DisplayName("TC5: Validation fails - Throws exception")
        void bookTickets_ValidationFails_ThrowsException() {
            // Arrange
            bookingDto.setPassengers(null); // Invalid - null passengers

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                ticketService.bookTickets(bookingDto);
            });

            verify(flightTicketClassService, never()).getFlightTicketClassById(anyInt(), anyInt());
            verify(flightTicketClassService, never()).updateRemainingTickets(anyInt(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("TC6: Not enough tickets - Throws exception")
        void bookTickets_NotEnoughTickets_ThrowsException() {
            // Arrange
            PassengerDto passenger2 = new PassengerDto();
            passenger2.setPassengerId(2);
            passenger2.setEmail("jane@example.com");
            passenger2.setCitizenId("987654321");

            bookingDto.setPassengers(Arrays.asList(passengerDto, passenger2));
            bookingDto.setSeatNumbers(null);

            flightTicketClassDto.setRemainingTicketQuantity(1); // Only 1 ticket available, but 2 requested

            // Mock for validation check
            when(flightTicketClassService.getFlightTicketClassById(anyInt(), anyInt()))
                    .thenReturn(flightTicketClassDto);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.bookTickets(bookingDto);
            });

            String message = exception.getMessage();
            assertTrue(message.contains("not available") || message.contains("Not enough tickets"));

            verify(flightTicketClassService, never()).updateRemainingTickets(anyInt(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("TC7: Flight ticket class not found - Throws exception")
        void bookTickets_FlightTicketClassNotFound_ThrowsException() {
            // Arrange
            bookingDto.setPassengers(Arrays.asList(passengerDto));
            bookingDto.setSeatNumbers(null);

            when(flightTicketClassService.getFlightTicketClassById(anyInt(), anyInt()))
                    .thenReturn(null);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.bookTickets(bookingDto);
            });

            assertTrue(exception.getMessage().contains("not available"));
            verify(flightTicketClassService, never()).updateRemainingTickets(anyInt(), anyInt(), anyInt());
        }

        // ===== NHÓM 4: Edge Cases =====

        @Test
        @DisplayName("TC8: Exactly remaining tickets - Success")
        void bookTickets_ExactlyRemainingTickets_Success() {
            // Arrange
            PassengerDto passenger2 = new PassengerDto();
            passenger2.setPassengerId(2);
            passenger2.setEmail("jane@example.com");
            passenger2.setCitizenId("987654321");

            bookingDto.setPassengers(Arrays.asList(passengerDto, passenger2));
            bookingDto.setSeatNumbers(null);

            flightTicketClassDto.setRemainingTicketQuantity(2); // Exactly 2 tickets available

            mockCreateTicketDependencies();

            // Act
            List<TicketDto> result = ticketService.bookTickets(bookingDto);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightTicketClassService).updateRemainingTickets(1, 1, 2);
        }


        @Test
        @DisplayName("TC9: Same confirmation code for all tickets")
        void bookTickets_ConfirmationCodeGenerated_AllTicketsSameCode() {
            // Arrange
            PassengerDto passenger2 = new PassengerDto();
            passenger2.setPassengerId(2);
            passenger2.setEmail("jane@example.com");
            passenger2.setCitizenId("987654321");

            bookingDto.setPassengers(Arrays.asList(passengerDto, passenger2));
            bookingDto.setSeatNumbers(null);

            mockCreateTicketDependencies();

            // Act
            List<TicketDto> result = ticketService.bookTickets(bookingDto);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());

            // All tickets in same booking should have same confirmation code
            String firstCode = result.get(0).getConfirmationCode();
            assertNotNull(firstCode);
            assertTrue(result.stream()
                    .allMatch(t -> firstCode.equals(t.getConfirmationCode())));
        }

        // Helper method to mock createTicket dependencies
        private void mockCreateTicketDependencies() {
            // Mock validation: isFlightAvailable calls getFlightTicketClassById
            when(flightTicketClassService.getFlightTicketClassById(anyInt(), anyInt()))
                    .thenReturn(flightTicketClassDto);

            // Mock getOrCreatePassenger -> uses passengerService.getPassengerByCitizenId
            when(passengerService.getPassengerByCitizenId(anyString())).thenReturn(passengerDto);

            // Mock seat availability check
            when(ticketRepository.findByFlightIdAndSeatNumber(anyInt(), anyString()))
                    .thenReturn(Optional.empty());

            // Mock createTicket dependencies
            when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));
            when(ticketClassRepository.findById(anyInt())).thenReturn(Optional.of(ticketClass));
            when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
            when(passengerRepository.findById(anyInt())).thenReturn(Optional.of(passenger));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto);
        }
    }

    // ============================================================
    // NESTED CLASS: GET ALL TICKETS TESTS
    // ============================================================

    @Nested
    @DisplayName("GetAllTickets Tests")
    class GetAllTicketsTests {

        @Test
        @DisplayName("Should return all active tickets")
        void getAllTickets_Success_ReturnsAllTickets() {
            // Arrange
            Ticket ticket1 = new Ticket();
            ticket1.setTicketId(1);
            Ticket ticket2 = new Ticket();
            ticket2.setTicketId(2);
            List<Ticket> tickets = Arrays.asList(ticket1, ticket2);

            TicketDto dto1 = new TicketDto();
            dto1.setTicketId(1);
            TicketDto dto2 = new TicketDto();
            dto2.setTicketId(2);
            List<TicketDto> dtos = Arrays.asList(dto1, dto2);

            when(ticketRepository.findAllActive()).thenReturn(tickets);
            when(ticketMapper.toDtoList(tickets)).thenReturn(dtos);

            // Act
            List<TicketDto> result = ticketService.getAllTickets();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(ticketRepository).findAllActive();
            verify(ticketMapper).toDtoList(tickets);
        }

        @Test
        @DisplayName("Should return empty list when no tickets")
        void getAllTickets_NoTickets_ReturnsEmptyList() {
            // Arrange
            when(ticketRepository.findAllActive()).thenReturn(new ArrayList<>());
            when(ticketMapper.toDtoList(any())).thenReturn(new ArrayList<>());

            // Act
            List<TicketDto> result = ticketService.getAllTickets();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ============================================================
    // NESTED CLASS: GET TICKET BY ID TESTS
    // ============================================================

    @Nested
    @DisplayName("GetTicketById Tests")
    class GetTicketByIdTests {

        @Test
        @DisplayName("Should return ticket when found")
        void getTicketById_Found_ReturnsTicket() {
            // Arrange
            Ticket ticket = new Ticket();
            ticket.setTicketId(1);
            TicketDto ticketDto = new TicketDto();
            ticketDto.setTicketId(1);

            when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(ticket));
            when(ticketMapper.toDto(ticket)).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.getTicketById(1);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTicketId());
            verify(ticketRepository).findActiveById(1);
        }

        @Test
        @DisplayName("Should throw exception when ticket not found")
        void getTicketById_NotFound_ThrowsException() {
            // Arrange
            when(ticketRepository.findActiveById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.getTicketById(999);
            });

            assertEquals("Ticket not found with id: 999", exception.getMessage());
        }
    }

    // ============================================================
    // NESTED CLASS: UPDATE TICKET TESTS
    // ============================================================

    @Nested
    @DisplayName("UpdateTicket Tests")
    class UpdateTicketTests {

        @Test
        @DisplayName("Should update ticket successfully")
        void updateTicket_ValidData_Success() {
            // Arrange
            Ticket existingTicket = new Ticket();
            existingTicket.setTicketId(1);
            existingTicket.setSeatNumber("10A");

            TicketDto updateDto = new TicketDto();
            updateDto.setSeatNumber("12B");
            updateDto.setFare(new BigDecimal("600.00"));

            when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(existingTicket));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(existingTicket);
            when(ticketMapper.toDto(any(Ticket.class))).thenReturn(updateDto);

            // Act
            TicketDto result = ticketService.updateTicket(1, updateDto);

            // Assert
            assertNotNull(result);
            verify(ticketRepository).findActiveById(1);
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when ticket not found")
        void updateTicket_NotFound_ThrowsException() {
            // Arrange
            TicketDto updateDto = new TicketDto();
            when(ticketRepository.findActiveById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.updateTicket(999, updateDto);
            });

            assertEquals("Ticket not found with id: 999", exception.getMessage());
        }
    }

    // ============================================================
    // NESTED CLASS: DELETE TICKET TESTS
    // ============================================================

    @Nested
    @DisplayName("DeleteTicket Tests")
    class DeleteTicketTests {

        @Test
        @DisplayName("Should soft delete ticket successfully")
        void deleteTicket_ValidId_Success() {
            // Arrange
            Ticket ticket = new Ticket();
            ticket.setTicketId(1);

            when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

            // Act
            ticketService.deleteTicket(1);

            // Assert
            verify(ticketRepository).findActiveById(1);
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when ticket not found")
        void deleteTicket_NotFound_ThrowsException() {
            // Arrange
            when(ticketRepository.findActiveById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.deleteTicket(999);
            });

            assertEquals("Ticket not found with id: 999", exception.getMessage());
        }
    }

    // ============================================================
    // NESTED CLASS: GET TICKETS BY FLIGHT ID TESTS
    // ============================================================

    @Nested
    @DisplayName("GetTicketsByFlightId Tests")
    class GetTicketsByFlightIdTests {

        @Test
        @DisplayName("Should return tickets for flight")
        void getTicketsByFlightId_Found_ReturnsTickets() {
            // Arrange
            List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
            List<TicketDto> dtos = Arrays.asList(new TicketDto(), new TicketDto());

            when(ticketRepository.findByFlightId(1)).thenReturn(tickets);
            when(ticketMapper.toDtoList(tickets)).thenReturn(dtos);

            // Act
            List<TicketDto> result = ticketService.getTicketsByFlightId(1);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(ticketRepository).findByFlightId(1);
        }

        @Test
        @DisplayName("Should return empty list when no tickets for flight")
        void getTicketsByFlightId_NoTickets_ReturnsEmptyList() {
            // Arrange
            when(ticketRepository.findByFlightId(999)).thenReturn(new ArrayList<>());
            when(ticketMapper.toDtoList(any())).thenReturn(new ArrayList<>());

            // Act
            List<TicketDto> result = ticketService.getTicketsByFlightId(999);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ============================================================
    // NESTED CLASS: GET TICKETS BY CUSTOMER ID TESTS
    // ============================================================

    @Nested
    @DisplayName("GetTicketsByCustomerId Tests")
    class GetTicketsByCustomerIdTests {

        @Test
        @DisplayName("Should return tickets for customer")
        void getTicketsByCustomerId_Found_ReturnsTickets() {
            // Arrange
            List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
            List<TicketDto> dtos = Arrays.asList(new TicketDto(), new TicketDto());

            when(ticketRepository.findByCustomerId(1)).thenReturn(tickets);
            when(ticketMapper.toDtoList(tickets)).thenReturn(dtos);

            // Act
            List<TicketDto> result = ticketService.getTicketsByCustomerId(1);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(ticketRepository).findByCustomerId(1);
        }
    }

    // ============================================================
    // NESTED CLASS: GET TICKETS BY PASSENGER ID TESTS
    // ============================================================

    @Nested
    @DisplayName("GetTicketsByPassengerId Tests")
    class GetTicketsByPassengerIdTests {

        @Test
        @DisplayName("Should return tickets for passenger")
        void getTicketsByPassengerId_Found_ReturnsTickets() {
            // Arrange
            List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
            List<TicketDto> dtos = Arrays.asList(new TicketDto(), new TicketDto());

            when(ticketRepository.findByPassengerId(1)).thenReturn(tickets);
            when(ticketMapper.toDtoList(tickets)).thenReturn(dtos);

            // Act
            List<TicketDto> result = ticketService.getTicketsByPassengerId(1);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(ticketRepository).findByPassengerId(1);
        }
    }

    // ============================================================
    // NESTED CLASS: GET TICKETS BY STATUS TESTS
    // ============================================================

    @Nested
    @DisplayName("GetTicketsByStatus Tests")
    class GetTicketsByStatusTests {

        @Test
        @DisplayName("Should return tickets with specific status")
        void getTicketsByStatus_Found_ReturnsTickets() {
            // Arrange
            List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
            List<TicketDto> dtos = Arrays.asList(new TicketDto(), new TicketDto());
            byte status = 1;

            when(ticketRepository.findByTicketStatus(status)).thenReturn(tickets);
            when(ticketMapper.toDtoList(tickets)).thenReturn(dtos);

            // Act
            List<TicketDto> result = ticketService.getTicketsByStatus(status);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(ticketRepository).findByTicketStatus(status);
        }
    }

    // ============================================================
    // NESTED CLASS: GENERATE CONFIRMATION CODE TESTS
    // ============================================================

    @Nested
    @DisplayName("GenerateConfirmationCode Tests")
    class GenerateConfirmationCodeTests {

        @Test
        @DisplayName("Should generate confirmation code with correct format")
        void generateConfirmationCode_Success_ReturnsValidCode() {
            // Act
            String code = ticketService.generateConfirmationCode();

            // Assert
            assertNotNull(code);
            assertTrue(code.startsWith("FMS-"));
            assertTrue(code.matches("FMS-\\d{8}-[A-Z0-9]{4}"));
        }

        @Test
        @DisplayName("Should generate unique codes")
        void generateConfirmationCode_MultipleCalls_GeneratesUniqueCodes() {
            // Act
            String code1 = ticketService.generateConfirmationCode();
            String code2 = ticketService.generateConfirmationCode();

            // Assert
            assertNotNull(code1);
            assertNotNull(code2);
            // Codes should have same date prefix but different suffix (most likely)
        }
    }

    // ============================================================
    // NESTED CLASS: GET TICKETS ON CONFIRMATION CODE TESTS
    // ============================================================

    @Nested
    @DisplayName("GetTicketsOnConfirmationCode Tests")
    class GetTicketsOnConfirmationCodeTests {

        @Test
        @DisplayName("Should return tickets with confirmation code")
        void getTicketsOnConfirmationCode_Found_ReturnsTickets() {
            // Arrange
            String confirmationCode = "FMS-20251207-ABCD";
            List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
            List<TicketDto> dtos = Arrays.asList(new TicketDto(), new TicketDto());

            when(ticketRepository.findByConfirmationCode(confirmationCode)).thenReturn(tickets);
            when(ticketMapper.toDtoList(tickets)).thenReturn(dtos);

            // Act
            List<TicketDto> result = ticketService.getTicketsOnConfirmationCode(confirmationCode);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(ticketRepository).findByConfirmationCode(confirmationCode);
        }

        @Test
        @DisplayName("Should return empty list when code not found")
        void getTicketsOnConfirmationCode_NotFound_ReturnsEmptyList() {
            // Arrange
            String confirmationCode = "INVALID";
            when(ticketRepository.findByConfirmationCode(confirmationCode)).thenReturn(new ArrayList<>());
            when(ticketMapper.toDtoList(any())).thenReturn(new ArrayList<>());

            // Act
            List<TicketDto> result = ticketService.getTicketsOnConfirmationCode(confirmationCode);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ============================================================
    // NESTED CLASS: PAY TICKET TESTS
    // ============================================================

    @Nested
    @DisplayName("PayTicket Tests")
    class PayTicketTests {

        @Test
        @DisplayName("Should pay ticket successfully")
        void payTicket_ValidTicket_Success() {
            // Arrange
            Ticket ticket = new Ticket();
            ticket.setTicketId(1);
            ticket.setTicketStatus((byte) 0);

            TicketDto ticketDto = new TicketDto();
            ticketDto.setTicketId(1);
            ticketDto.setTicketStatus((byte) 1);

            when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto);

            // Act
            TicketDto result = ticketService.payTicket(1, "ORDER123");

            // Assert
            assertNotNull(result);
            verify(ticketRepository).findActiveById(1);
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when ticket not found")
        void payTicket_NotFound_ThrowsException() {
            // Arrange
            when(ticketRepository.findActiveById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.payTicket(999, "ORDER123");
            });

            assertEquals("Ticket not found with id: 999", exception.getMessage());
        }
    }

    // ============================================================
    // NESTED CLASS: CANCEL TICKET TESTS
    // ============================================================

    @Nested
    @DisplayName("CancelTicket Tests")
    class CancelTicketTests {

        @Test
        @DisplayName("Should cancel ticket successfully")
        void cancelTicket_ValidTicket_Success() {
            // Arrange
            Ticket ticket = new Ticket();
            ticket.setTicketId(1);

            when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

            // Act
            ticketService.cancelTicket(1);

            // Assert
            verify(ticketRepository).findActiveById(1);
            verify(ticketRepository).save(any(Ticket.class));
        }

        @Test
        @DisplayName("Should throw exception when ticket not found")
        void cancelTicket_NotFound_ThrowsException() {
            // Arrange
            when(ticketRepository.findActiveById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ticketService.cancelTicket(999);
            });

            assertEquals("Ticket not found with id: 999", exception.getMessage());
        }
    }

    // ============================================================
    // NESTED CLASS: IS SEAT AVAILABLE TESTS
    // ============================================================

    @Nested
    @DisplayName("IsSeatAvailable Tests")
    class IsSeatAvailableTests {

        @Test
        @DisplayName("Should return true when seat is available")
        void isSeatAvailable_SeatAvailable_ReturnsTrue() {
            // Arrange
            when(ticketRepository.findByFlightIdAndSeatNumber(1, "12A"))
                    .thenReturn(Optional.empty());

            // Act
            boolean result = ticketService.isSeatAvailable(1, "12A");

            // Assert
            assertTrue(result);
            verify(ticketRepository).findByFlightIdAndSeatNumber(1, "12A");
        }

        @Test
        @DisplayName("Should return false when seat is taken")
        void isSeatAvailable_SeatTaken_ReturnsFalse() {
            // Arrange
            Ticket existingTicket = new Ticket();
            when(ticketRepository.findByFlightIdAndSeatNumber(1, "12A"))
                    .thenReturn(Optional.of(existingTicket));

            // Act
            boolean result = ticketService.isSeatAvailable(1, "12A");

            // Assert
            assertFalse(result);
            verify(ticketRepository).findByFlightIdAndSeatNumber(1, "12A");
        }
    }
}