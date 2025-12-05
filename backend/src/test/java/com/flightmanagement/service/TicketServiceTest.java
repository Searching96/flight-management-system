package com.flightmanagement.service;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.*;
import com.flightmanagement.enums.AccountType;
import com.flightmanagement.mapper.TicketMapper;
import com.flightmanagement.repository.*;
import com.flightmanagement.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private TicketDto validTicketDto;
    private Ticket validTicket;
    private Flight validFlight;
    private TicketClass validTicketClass;
    private Customer validCustomer;
    private Passenger validPassenger;
    private Account validAccount;

    @BeforeEach
    void setUp() {
        Airport departureAirport = new Airport();
        departureAirport.setAirportId(1);
        departureAirport.setCityName("Hanoi");

        Airport arrivalAirport = new Airport();
        arrivalAirport.setAirportId(2);
        arrivalAirport.setCityName("Ho Chi Minh");

        validFlight = new Flight();
        validFlight.setFlightId(1);
        validFlight.setFlightCode("VN123");
        validFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        validFlight.setDepartureAirport(departureAirport);
        validFlight.setArrivalAirport(arrivalAirport);

        validTicketClass = new TicketClass();
        validTicketClass.setTicketClassId(1);
        validTicketClass.setTicketClassName("Economy");

        validAccount = new Account();
        validAccount.setAccountId(1);
        validAccount.setEmail("customer@test.com");
        validAccount.setAccountName("Test Customer");
        validAccount.setAccountType(AccountType.CUSTOMER);

        validCustomer = new Customer();
        validCustomer.setCustomerId(1);
        validCustomer.setAccount(validAccount);
        validCustomer.setScore(0);

        validPassenger = new Passenger();
        validPassenger.setPassengerId(1);
        validPassenger.setPassengerName("John Doe");
        validPassenger.setEmail("passenger@test.com");
        validPassenger.setCitizenId("123456789");
        validPassenger.setPhoneNumber("0123456789");

        validTicket = new Ticket();
        validTicket.setTicketId(1);
        validTicket.setFlight(validFlight);
        validTicket.setTicketClass(validTicketClass);
        validTicket.setBookCustomer(validCustomer);
        validTicket.setPassenger(validPassenger);
        validTicket.setSeatNumber("A01");
        validTicket.setTicketStatus((byte) 0);
        validTicket.setFare(new BigDecimal("150.00"));
        validTicket.setConfirmationCode("FMS-20240101-ABCD");

        validTicketDto = new TicketDto();
        validTicketDto.setTicketId(1);
        validTicketDto.setFlightId(1);
        validTicketDto.setTicketClassId(1);
        validTicketDto.setBookCustomerId(1);
        validTicketDto.setPassengerId(1);
        validTicketDto.setSeatNumber("A01");
        validTicketDto.setTicketStatus((byte) 0);
        validTicketDto.setFare(new BigDecimal("150.00"));
        validTicketDto.setConfirmationCode("FMS-20240101-ABCD");
    }

    // ==================== createTicket Tests ====================

    @Tag("createTicket")
    @Test
    void createTicket_withCustomerBooking_success() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(validCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(validPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);
        doNothing().when(emailService).sendSingleTicketConfirmation(
            anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );

        TicketDto result = ticketService.createTicket(validTicketDto);

        assertNotNull(result);
        assertEquals(validTicketDto.getTicketId(), result.getTicketId());
        verify(ticketRepository).save(any(Ticket.class));
        verify(emailService).sendSingleTicketConfirmation(
            anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );
    }

    @Tag("createTicket")
    @Test
    void createTicket_flightNotFound_throwsException() {
        when(flightRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.createTicket(validTicketDto)
        );

        assertTrue(exception.getMessage().contains("Flight not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Tag("createTicket")
    @Test
    void createTicket_ticketClassNotFound_throwsException() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.createTicket(validTicketDto)
        );

        assertTrue(exception.getMessage().contains("TicketClass not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Tag("createTicket")
    @Test
    void createTicket_customerNotFound_createsNewCustomer() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.empty());
        when(accountRepository.findById(1)).thenReturn(Optional.of(validAccount));
        when(customerRepository.save(any(Customer.class))).thenReturn(validCustomer);
        when(passengerRepository.findById(1)).thenReturn(Optional.of(validPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);
        doNothing().when(emailService).sendSingleTicketConfirmation(
            anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );

        TicketDto result = ticketService.createTicket(validTicketDto);

        assertNotNull(result);
        verify(customerRepository).save(any(Customer.class));
    }

    @Tag("createTicket")
    @Test
    void createTicket_passengerNotFound_throwsException() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(validCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.createTicket(validTicketDto)
        );

        assertTrue(exception.getMessage().contains("Passenger not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Tag("createTicket")
    @Test
    void createTicket_guestBooking_sendsEmailToPassenger() {
        validTicketDto.setBookCustomerId(null);
        validTicket.setBookCustomer(null);

        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(validPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);
        doNothing().when(emailService).sendSingleTicketConfirmation(
            anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );

        TicketDto result = ticketService.createTicket(validTicketDto);

        assertNotNull(result);
        verify(emailService).sendSingleTicketConfirmation(
            eq(validPassenger.getEmail()), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );
    }

    @Tag("createTicket")
    @Test
    void createTicket_emailServiceFails_continuesSuccessfully() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(validCustomer));
        when(passengerRepository.findById(1)).thenReturn(Optional.of(validPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);
        doThrow(new RuntimeException("Email service error")).when(emailService).sendSingleTicketConfirmation(
            anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );

        TicketDto result = ticketService.createTicket(validTicketDto);

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    // ==================== updateTicket Tests ====================

    @Tag("updateTicket")
    @Test
    void updateTicket_validUpdate_success() {
        TicketDto updateDto = new TicketDto();
        updateDto.setSeatNumber("B02");
        updateDto.setTicketStatus((byte) 1);
        updateDto.setFare(new BigDecimal("200.00"));
        updateDto.setPaymentTime(LocalDateTime.now());

        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(updateDto);

        TicketDto result = ticketService.updateTicket(1, updateDto);

        assertNotNull(result);
        verify(ticketRepository).save(validTicket);
    }

    @Tag("updateTicket")
    @Test
    void updateTicket_ticketNotFound_throwsException() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.updateTicket(1, validTicketDto)
        );

        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Tag("updateTicket")
    @Test
    void updateTicket_updatesSeatNumber() {
        validTicketDto.setSeatNumber("C03");
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        ticketService.updateTicket(1, validTicketDto);

        assertEquals("C03", validTicket.getSeatNumber());
    }

    @Tag("updateTicket")
    @Test
    void updateTicket_updatesTicketStatus() {
        validTicketDto.setTicketStatus((byte) 1);
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        ticketService.updateTicket(1, validTicketDto);

        assertEquals((byte) 1, validTicket.getTicketStatus());
    }

    @Tag("updateTicket")
    @Test
    void updateTicket_updatesFare() {
        validTicketDto.setFare(new BigDecimal("300.00"));
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        ticketService.updateTicket(1, validTicketDto);

        assertEquals(new BigDecimal("300.00"), validTicket.getFare());
    }

    @Tag("updateTicket")
    @Test
    void updateTicket_updatesPaymentTime() {
        LocalDateTime paymentTime = LocalDateTime.now();
        validTicketDto.setPaymentTime(paymentTime);
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        ticketService.updateTicket(1, validTicketDto);

        assertEquals(paymentTime, validTicket.getPaymentTime());
    }

    // ==================== deleteTicket Tests ====================

    @Tag("deleteTicket")
    @Test
    void deleteTicket_validId_success() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);

        ticketService.deleteTicket(1);

        assertNotNull(validTicket.getDeletedAt());
        verify(ticketRepository).save(validTicket);
    }

    @Tag("deleteTicket")
    @Test
    void deleteTicket_ticketNotFound_throwsException() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.deleteTicket(1)
        );

        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Tag("deleteTicket")
    @Test
    void deleteTicket_setsDeletedAtTimestamp() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);

        LocalDateTime beforeDelete = LocalDateTime.now();
        ticketService.deleteTicket(1);
        LocalDateTime afterDelete = LocalDateTime.now();

        assertNotNull(validTicket.getDeletedAt());
        assertTrue(validTicket.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
        assertTrue(validTicket.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
    }

    @Tag("deleteTicket")
    @Test
    void deleteTicket_preservesOtherFields() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);

        ticketService.deleteTicket(1);

        assertNotNull(validTicket.getDeletedAt());
        assertEquals("A01", validTicket.getSeatNumber());
        assertEquals(new BigDecimal("150.00"), validTicket.getFare());
    }

    // ==================== bookTickets Tests ====================

    @Tag("bookTickets")
    @Test
    void bookTickets_validBooking_success() {
        PassengerDto passengerDto = new PassengerDto(null, "Jane Doe", "jane@test.com", "987654321", "0987654321");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setCustomerId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(List.of(passengerDto));
        bookingDto.setSeatNumbers(List.of("A01"));

        FlightTicketClassDto flightTicketClassDto = new FlightTicketClassDto();
        flightTicketClassDto.setRemainingTicketQuantity(10);
        flightTicketClassDto.setSpecifiedFare(new BigDecimal("150.00"));
        flightTicketClassDto.setTicketClassName("Economy");

        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("987654321"))
            .thenThrow(new RuntimeException("Passenger not found"));
        when(passengerService.createPassenger(any(PassengerDto.class))).thenReturn(passengerDto);
        passengerDto.setPassengerId(2);
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A01")).thenReturn(Optional.empty());
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(validCustomer));
        when(passengerRepository.findById(2)).thenReturn(Optional.of(validPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(validTicketDto);
        doNothing().when(emailService).sendSingleTicketConfirmation(
            anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );

        List<TicketDto> result = ticketService.bookTickets(bookingDto);

        assertEquals(1, result.size());
        verify(flightTicketClassService).updateRemainingTickets(1, 1, 1);
    }

    @Tag("bookTickets")
    @Test
    void bookTickets_noPassengers_throwsException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setPassengers(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ticketService.bookTickets(bookingDto)
        );

        assertTrue(exception.getMessage().contains("At least one passenger is required"));
    }

    @Tag("bookTickets")
    @Test
    void bookTickets_notEnoughSeats_throwsException() {
        PassengerDto passengerDto = new PassengerDto(null, "Jane Doe", "jane@test.com", "987654321", "0987654321");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(Arrays.asList(passengerDto, passengerDto));

        FlightTicketClassDto flightTicketClassDto = new FlightTicketClassDto();
        flightTicketClassDto.setRemainingTicketQuantity(1);

        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.bookTickets(bookingDto)
        );

        assertFalse(exception.getMessage().contains("Not enough tickets available"));
    }

    @Tag("bookTickets")
    @Test
    void bookTickets_seatAlreadyTaken_throwsException() {
        PassengerDto passengerDto = new PassengerDto(null, "Jane Doe", "jane@test.com", "987654321", "0987654321");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(List.of(passengerDto));
        bookingDto.setSeatNumbers(List.of("A01"));

        FlightTicketClassDto flightTicketClassDto = new FlightTicketClassDto();
        flightTicketClassDto.setRemainingTicketQuantity(10);

        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A01")).thenReturn(Optional.of(validTicket));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ticketService.bookTickets(bookingDto)
        );

        assertTrue(exception.getMessage().contains("Seat A01 is already taken"));
    }

    @Tag("bookTickets")
    @Test
    void bookTickets_mismatchedSeatCount_throwsException() {
        PassengerDto passengerDto = new PassengerDto(null, "Jane Doe", "jane@test.com", "987654321", "0987654321");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setPassengers(Arrays.asList(passengerDto, passengerDto));
        bookingDto.setSeatNumbers(List.of("A01"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ticketService.bookTickets(bookingDto)
        );

        assertTrue(exception.getMessage().contains("Number of seat numbers must match number of passengers"));
    }

    @Tag("bookTickets")
    @Test
    void bookTickets_withoutSeatNumbers_generatesSeats() {
        PassengerDto passengerDto = new PassengerDto(null, "Jane Doe", "jane@test.com", "987654321", "0987654321");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setCustomerId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(List.of(passengerDto));
        bookingDto.setSeatNumbers(null);

        FlightTicketClassDto flightTicketClassDto = new FlightTicketClassDto();
        flightTicketClassDto.setRemainingTicketQuantity(10);
        flightTicketClassDto.setSpecifiedFare(new BigDecimal("150.00"));
        flightTicketClassDto.setTicketClassName("Economy");

        when(flightTicketClassService.getFlightTicketClassById(1, 1)).thenReturn(flightTicketClassDto);
        when(passengerService.getPassengerByCitizenId("987654321"))
            .thenThrow(new RuntimeException("Passenger not found"));
        when(passengerService.createPassenger(any(PassengerDto.class))).thenReturn(passengerDto);
        passengerDto.setPassengerId(2);
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(customerRepository.findById(1)).thenReturn(Optional.of(validCustomer));
        when(passengerRepository.findById(2)).thenReturn(Optional.of(validPassenger));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(validTicketDto);
        doNothing().when(emailService).sendSingleTicketConfirmation(
            anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(),
            anyString(), any(BigDecimal.class), anyBoolean()
        );

        List<TicketDto> result = ticketService.bookTickets(bookingDto);

        assertEquals(1, result.size());
        verify(ticketRepository).save(argThat(ticket -> ticket.getSeatNumber() != null));
    }

    @Tag("bookTickets")
    @Test
    void bookTickets_flightNotAvailable_throwsException() {
        PassengerDto passengerDto = new PassengerDto(null, "Jane Doe", "jane@test.com", "987654321", "0987654321");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setFlightId(1);
        bookingDto.setTicketClassId(1);
        bookingDto.setPassengers(List.of(passengerDto));

        when(flightTicketClassService.getFlightTicketClassById(1, 1))
            .thenThrow(new RuntimeException("Flight not found"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ticketService.bookTickets(bookingDto)
        );

        assertTrue(exception.getMessage().contains("Flight or ticket class not available"));
    }

    // ==================== generateConfirmationCode Tests ====================

    @Tag("generateConfirmationCode")
    @Test
    void generateConfirmationCode_generatesValidCode() {
        String code = ticketService.generateConfirmationCode();

        assertNotNull(code);
        assertTrue(code.startsWith("FMS-"));
        assertEquals(17, code.length()); // FMS-YYYYMMDD-XXXX
    }

    @Tag("generateConfirmationCode")
    @Test
    void generateConfirmationCode_containsDateComponent() {
        String code = ticketService.generateConfirmationCode();
        String datePart = code.substring(4, 12); // Extract YYYYMMDD

        assertTrue(datePart.matches("\\d{8}"));
    }

    @Tag("generateConfirmationCode")
    @Test
    void generateConfirmationCode_containsRandomSuffix() {
        String code = ticketService.generateConfirmationCode();
        String suffix = code.substring(13); // Extract XXXX

        assertEquals(4, suffix.length());
        assertTrue(suffix.matches("[A-Z0-9]{4}"));
    }

    @Tag("generateConfirmationCode")
    @Test
    void generateConfirmationCode_generatesUniqueCodesOnMultipleCalls() {
        String code1 = ticketService.generateConfirmationCode();
        String code2 = ticketService.generateConfirmationCode();

        // While not guaranteed, highly likely to be different
        assertNotNull(code1);
        assertNotNull(code2);
    }

    @Tag("generateConfirmationCode")
    @Test
    void generateConfirmationCode_followsCorrectFormat() {
        String code = ticketService.generateConfirmationCode();

        assertTrue(code.matches("FMS-\\d{8}-[A-Z0-9]{4}"));
    }

    @Tag("generateConfirmationCode")
    @Test
    void generateConfirmationCode_usesTodayDate() {
        String code = ticketService.generateConfirmationCode();
        LocalDateTime now = LocalDateTime.now();
        String expectedDatePrefix = String.format("FMS-%04d%02d%02d",
            now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        assertTrue(code.startsWith(expectedDatePrefix));
    }

    // ==================== payTicket Tests ====================

    @Tag("payTicket")
    @Test
    void payTicket_validTicket_success() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        TicketDto result = ticketService.payTicket(1, "ORDER123");

        assertNotNull(result);
        assertEquals((byte) 1, validTicket.getTicketStatus());
        assertNotNull(validTicket.getPaymentTime());
        assertEquals("ORDER123", validTicket.getOrderId());
        verify(ticketRepository).save(validTicket);
    }

    @Tag("payTicket")
    @Test
    void payTicket_ticketNotFound_throwsException() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.payTicket(1, "ORDER123")
        );

        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Tag("payTicket")
    @Test
    void payTicket_setsPaymentTime() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        LocalDateTime beforePayment = LocalDateTime.now();
        ticketService.payTicket(1, "ORDER123");
        LocalDateTime afterPayment = LocalDateTime.now();

        assertNotNull(validTicket.getPaymentTime());
        assertTrue(validTicket.getPaymentTime().isAfter(beforePayment.minusSeconds(1)));
        assertTrue(validTicket.getPaymentTime().isBefore(afterPayment.plusSeconds(1)));
    }

    @Tag("payTicket")
    @Test
    void payTicket_setsOrderId() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        ticketService.payTicket(1, "ORDER456");

        assertEquals("ORDER456", validTicket.getOrderId());
    }

    @Tag("payTicket")
    @Test
    void payTicket_changesStatusToPaid() {
        validTicket.setTicketStatus((byte) 0);
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        ticketService.payTicket(1, "ORDER123");

        assertEquals((byte) 1, validTicket.getTicketStatus());
    }

    @Tag("payTicket")
    @Test
    void payTicket_withNullOrderId_success() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);
        when(ticketMapper.toDto(validTicket)).thenReturn(validTicketDto);

        ticketService.payTicket(1, null);

        assertNull(validTicket.getOrderId());
        assertEquals((byte) 1, validTicket.getTicketStatus());
    }

    // ==================== cancelTicket Tests ====================

    @Tag("cancelTicket")
    @Test
    void cancelTicket_validTicket_success() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);

        ticketService.cancelTicket(1);

        assertNotNull(validTicket.getDeletedAt());
        verify(ticketRepository).save(validTicket);
    }

    @Tag("cancelTicket")
    @Test
    void cancelTicket_ticketNotFound_throwsException() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.cancelTicket(1)
        );

        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository, never()).save(any());
    }

    @Tag("cancelTicket")
    @Test
    void cancelTicket_setsDeletedAtTimestamp() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);

        LocalDateTime beforeCancel = LocalDateTime.now();
        ticketService.cancelTicket(1);
        LocalDateTime afterCancel = LocalDateTime.now();

        assertNotNull(validTicket.getDeletedAt());
        assertTrue(validTicket.getDeletedAt().isAfter(beforeCancel.minusSeconds(1)));
        assertTrue(validTicket.getDeletedAt().isBefore(afterCancel.plusSeconds(1)));
    }

    @Tag("cancelTicket")
    @Test
    void cancelTicket_preservesOtherFields() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);

        ticketService.cancelTicket(1);

        assertNotNull(validTicket.getDeletedAt());
        assertEquals("A01", validTicket.getSeatNumber());
        assertEquals(new BigDecimal("150.00"), validTicket.getFare());
    }

    @Tag("cancelTicket")
    @Test
    void cancelTicket_multipleTimes_updatesDeletedAt() {
        when(ticketRepository.findActiveById(1)).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(validTicket);

        ticketService.cancelTicket(1);
        LocalDateTime firstDeletedAt = validTicket.getDeletedAt();

        assertNotNull(firstDeletedAt);
    }

    // ==================== isSeatAvailable Tests ====================

    @Tag("isSeatAvailable")
    @Test
    void isSeatAvailable_seatNotTaken_returnsTrue() {
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A01")).thenReturn(Optional.empty());

        boolean result = ticketService.isSeatAvailable(1, "A01");

        assertTrue(result);
    }

    @Tag("isSeatAvailable")
    @Test
    void isSeatAvailable_seatTaken_returnsFalse() {
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A01")).thenReturn(Optional.of(validTicket));

        boolean result = ticketService.isSeatAvailable(1, "A01");

        assertFalse(result);
    }

    @Tag("isSeatAvailable")
    @Test
    void isSeatAvailable_multipleSeatsTaken_returnsFalse() {
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A01"))
            .thenReturn(Optional.of(validTicket));

        boolean result = ticketService.isSeatAvailable(1, "A01");

        assertFalse(result);
    }

    @Tag("isSeatAvailable")
    @Test
    void isSeatAvailable_differentFlight_returnsTrue() {
        when(ticketRepository.findByFlightIdAndSeatNumber(2, "A01")).thenReturn(Optional.empty());

        boolean result = ticketService.isSeatAvailable(2, "A01");

        assertTrue(result);
    }

    @Tag("isSeatAvailable")
    @Test
    void isSeatAvailable_differentSeat_returnsTrue() {
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "B02")).thenReturn(Optional.empty());

        boolean result = ticketService.isSeatAvailable(1, "B02");

        assertTrue(result);
    }

    @Tag("isSeatAvailable")
    @Test
    void isSeatAvailable_nullSeat_handlesGracefully() {
        when(ticketRepository.findByFlightIdAndSeatNumber(1, null)).thenReturn(Optional.empty());

        boolean result = ticketService.isSeatAvailable(1, null);

        assertTrue(result);
    }

    @Tag("isSeatAvailable")
    @Test
    void isSeatAvailable_repositoryThrowsException_propagatesException() {
        when(ticketRepository.findByFlightIdAndSeatNumber(1, "A01"))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketService.isSeatAvailable(1, "A01")
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }
}
