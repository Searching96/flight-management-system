package com.flightmanagement.service;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.Airport;
import com.flightmanagement.mapper.FlightMapper;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private FlightTicketClassService flightTicketClassService;

    @Mock
    private ParameterService parameterService;

    @InjectMocks
    private FlightServiceImpl flightService;

    private FlightSearchCriteria searchCriteria;
    private List<Flight> mockFlights;
    private List<FlightDto> mockFlightDtos;
    private Flight mockFlight;
    private FlightDto mockFlightDto;
    private FlightRequest flightRequest;
    private ParameterDto parameterDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime departureDate = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime arrivalDate = LocalDateTime.of(2024, 1, 15, 12, 30);

        searchCriteria = new FlightSearchCriteria();
        searchCriteria.setDepartureAirportId(1);
        searchCriteria.setArrivalAirportId(2);
        searchCriteria.setDepartureDate(departureDate);
        searchCriteria.setPassengerCount(2);

        Airport departureAirport = new Airport();
        departureAirport.setAirportId(1);
        departureAirport.setAirportName("Hanoi Airport");

        Airport arrivalAirport = new Airport();
        arrivalAirport.setAirportId(2);
        arrivalAirport.setAirportName("Ho Chi Minh Airport");

        Flight flight1 = new Flight();
        flight1.setFlightId(1);
        flight1.setFlightCode("FL001");
        flight1.setDepartureTime(departureDate);
        flight1.setArrivalTime(arrivalDate);
        flight1.setDepartureAirport(departureAirport);
        flight1.setArrivalAirport(arrivalAirport);

        Flight flight2 = new Flight();
        flight2.setFlightId(2);
        flight2.setFlightCode("FL002");
        flight2.setDepartureTime(departureDate.plusHours(2));
        flight2.setArrivalTime(arrivalDate.plusHours(2));
        flight2.setDepartureAirport(departureAirport);
        flight2.setArrivalAirport(arrivalAirport);

        mockFlight = flight1;

        FlightDto flightDto1 = new FlightDto();
        flightDto1.setFlightId(1);
        flightDto1.setFlightCode("FL001");

        FlightDto flightDto2 = new FlightDto();
        flightDto2.setFlightId(2);
        flightDto2.setFlightCode("FL002");

        mockFlightDto = flightDto1;
        mockFlights = Arrays.asList(flight1, flight2);
        mockFlightDtos = Arrays.asList(flightDto1, flightDto2);

        // Setup flight request
        flightRequest = new FlightRequest();
        flightRequest.setFlightCode("FL003");
        flightRequest.setDepartureAirportId(1);
        flightRequest.setArrivalAirportId(2);
        flightRequest.setDepartureTime(departureDate);
        flightRequest.setArrivalTime(arrivalDate);

        // Setup parameter
        parameterDto = new ParameterDto();
        parameterDto.setMinFlightDuration(30);
    }

    // Existing tests...
    @Test
    void testSearchFlights_WithTicketClass_Success() {
        // Arrange
        searchCriteria.setTicketClassId(1);

        when(flightRepository.findFlightsWithTicketClass(
            eq(1), eq(2), eq(searchCriteria.getDepartureDate()), eq(1), eq(2)))
            .thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenReturn(mockFlightDtos);

        // Act
        List<FlightDto> result = flightService.searchFlights(searchCriteria);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("FL001", result.get(0).getFlightCode());
        assertEquals("FL002", result.get(1).getFlightCode());

        verify(flightRepository).findFlightsWithTicketClass(1, 2, searchCriteria.getDepartureDate(), 1, 2);
        verify(flightRepository, never()).findFlightsByRoute(anyInt(), anyInt(), any(LocalDateTime.class));
        verify(flightMapper).toDtoList(mockFlights);
    }

    @Test
    void testSearchFlights_WithoutTicketClass_Success() {
        // Arrange
        searchCriteria.setTicketClassId(null);

        when(flightRepository.findFlightsByRoute(
            eq(1), eq(2), eq(searchCriteria.getDepartureDate())))
            .thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenReturn(mockFlightDtos);

        // Act
        List<FlightDto> result = flightService.searchFlights(searchCriteria);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("FL001", result.get(0).getFlightCode());
        assertEquals("FL002", result.get(1).getFlightCode());

        verify(flightRepository).findFlightsByRoute(1, 2, searchCriteria.getDepartureDate());
        verify(flightRepository, never()).findFlightsWithTicketClass(anyInt(), anyInt(), any(LocalDateTime.class), anyInt(), anyInt());
        verify(flightMapper).toDtoList(mockFlights);
    }

    @Test
    void testSearchFlights_WithTicketClassZero_UsesRouteSearch() {
        // Arrange
        searchCriteria.setTicketClassId(0);

        when(flightRepository.findFlightsByRoute(
            eq(1), eq(2), eq(searchCriteria.getDepartureDate())))
            .thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenReturn(mockFlightDtos);

        // Act
        List<FlightDto> result = flightService.searchFlights(searchCriteria);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(flightRepository).findFlightsByRoute(1, 2, searchCriteria.getDepartureDate());
        verify(flightRepository, never()).findFlightsWithTicketClass(anyInt(), anyInt(), any(LocalDateTime.class), anyInt(), anyInt());
    }

    @Test
    void testSearchFlights_NoFlightsFound_ReturnsEmptyList() {
        // Arrange
        searchCriteria.setTicketClassId(1);

        when(flightRepository.findFlightsWithTicketClass(
            eq(1), eq(2), eq(searchCriteria.getDepartureDate()), eq(1), eq(2)))
            .thenReturn(Collections.emptyList());
        when(flightMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<FlightDto> result = flightService.searchFlights(searchCriteria);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(flightRepository).findFlightsWithTicketClass(1, 2, searchCriteria.getDepartureDate(), 1, 2);
        verify(flightMapper).toDtoList(Collections.emptyList());
    }

    @Test
    void testSearchFlights_MapperThrowsException_ThrowsRuntimeException() {
        // Arrange
        searchCriteria.setTicketClassId(1);

        when(flightRepository.findFlightsWithTicketClass(
            eq(1), eq(2), eq(searchCriteria.getDepartureDate()), eq(1), eq(2)))
            .thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.searchFlights(searchCriteria);
        });

        assertEquals("Failed to search flights", exception.getMessage());
        verify(flightRepository).findFlightsWithTicketClass(1, 2, searchCriteria.getDepartureDate(), 1, 2);
        verify(flightMapper).toDtoList(mockFlights);
    }

    // NEW TEST 1: Get all flights
    @Test
    void testGetAllFlights_Success() {
        // Arrange
        when(flightRepository.findAllActive()).thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenReturn(mockFlightDtos);

        // Act
        List<FlightDto> result = flightService.getAllFlights();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(flightRepository).findAllActive();
        verify(flightMapper).toDtoList(mockFlights);
    }

    // NEW TEST 2: Get all flights returns empty list
    @Test
    void testGetAllFlights_EmptyList() {
        // Arrange
        when(flightRepository.findAllActive()).thenReturn(Collections.emptyList());
        when(flightMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<FlightDto> result = flightService.getAllFlights();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(flightRepository).findAllActive();
    }

    // NEW TEST 3: Get flight by ID success
    @Test
    void testGetFlightById_Success() {
        // Arrange
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(mockFlight));
        when(flightMapper.toDto(mockFlight)).thenReturn(mockFlightDto);

        // Act
        FlightDto result = flightService.getFlightById(1);

        // Assert
        assertNotNull(result);
        assertEquals("FL001", result.getFlightCode());
        verify(flightRepository).findActiveById(1);
        verify(flightMapper).toDto(mockFlight);
    }

    // NEW TEST 4: Get flight by ID not found
    @Test
    void testGetFlightById_NotFound_ThrowsException() {
        // Arrange
        when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.getFlightById(999);
        });

        assertEquals("Flight not found with id: 999", exception.getMessage());
        verify(flightRepository).findActiveById(999);
        verify(flightMapper, never()).toDto(any());
    }

    // NEW TEST 5: Create flight success
    @Test
    void testCreateFlight_Success() {
        // Arrange
        when(flightRepository.existsByFlightCode("FL003")).thenReturn(false);
        when(parameterService.getParameterSet()).thenReturn(parameterDto);
        when(flightMapper.toEntityFromCreateRequest(flightRequest)).thenReturn(mockFlight);
        when(flightRepository.save(any(Flight.class))).thenReturn(mockFlight);
        when(flightMapper.toDto(mockFlight)).thenReturn(mockFlightDto);

        // Act
        FlightDto result = flightService.createFlight(flightRequest);

        // Assert
        assertNotNull(result);
        verify(flightRepository).existsByFlightCode("FL003");
        verify(flightRepository).save(any(Flight.class));
        verify(flightMapper).toDto(mockFlight);
    }

    // NEW TEST 6: Create flight with duplicate code
    @Test
    void testCreateFlight_DuplicateCode_ThrowsException() {
        // Arrange
        when(flightRepository.existsByFlightCode("FL003")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight(flightRequest);
        });

        assertEquals("Flight code already exists: FL003", exception.getMessage());
        verify(flightRepository).existsByFlightCode("FL003");
        verify(flightRepository, never()).save(any());
    }

    // NEW TEST 7: Create flight with arrival before departure
    @Test
    void testCreateFlight_ArrivalBeforeDeparture_ThrowsException() {
        // Arrange
        flightRequest.setArrivalTime(flightRequest.getDepartureTime().minusHours(1));
        when(flightRepository.existsByFlightCode("FL003")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight(flightRequest);
        });

        assertEquals("Arrival time must be after departure time", exception.getMessage());
        verify(flightRepository, never()).save(any());
    }

    // NEW TEST 8: Create flight with same departure and arrival airports
    @Test
    void testCreateFlight_SameAirports_ThrowsException() {
        // Arrange
        flightRequest.setArrivalAirportId(1);
        when(flightRepository.existsByFlightCode("FL003")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight(flightRequest);
        });

        assertEquals("Departure and arrival airports cannot be the same", exception.getMessage());
        verify(flightRepository, never()).save(any());
    }

    // NEW TEST 9: Create flight with duration less than minimum
    @Test
    void testCreateFlight_DurationTooShort_ThrowsException() {
        // Arrange
        flightRequest.setArrivalTime(flightRequest.getDepartureTime().plusMinutes(15));
        when(flightRepository.existsByFlightCode("FL003")).thenReturn(false);
        when(parameterService.getParameterSet()).thenReturn(parameterDto);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight(flightRequest);
        });

        assertTrue(exception.getMessage().contains("Flight duration must be at least"));
        verify(flightRepository, never()).save(any());
    }

    // NEW TEST 10: Update flight success
    @Test
    void testUpdateFlight_Success() {
        // Arrange
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(mockFlight));
        when(flightRepository.existsByFlightCode("FL003")).thenReturn(false);
        when(parameterService.getParameterSet()).thenReturn(parameterDto);
        when(flightRepository.save(any(Flight.class))).thenReturn(mockFlight);
        when(flightMapper.toDto(mockFlight)).thenReturn(mockFlightDto);

        // Act
        FlightDto result = flightService.updateFlight(1, flightRequest);

        // Assert
        assertNotNull(result);
        verify(flightRepository).findActiveById(1);
        verify(flightRepository).save(mockFlight);
        verify(flightMapper).toDto(mockFlight);
    }

    // NEW TEST 11: Update flight not found
//    @Test
//    void testUpdateFlight_NotFound_ThrowsException() {
//        // Arrange
//        when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            flightService.updateFlight(999, flightRequest);
//        });
//
//        assertEquals("Flight not found with id: 999", exception.getMessage());
//        verify(flightRepository).findActiveById(999);
//        verify(flightRepository, never()).save(any());
//    }

    // NEW TEST 12: Delete flight success
    @Test
    void testDeleteFlight_Success() {
        // Arrange
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(mockFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(mockFlight);

        // Act
        flightService.deleteFlight(1);

        // Assert
        verify(flightRepository).findActiveById(1);
        verify(flightRepository).save(mockFlight);
        assertNotNull(mockFlight.getDeletedAt());
    }

    // NEW TEST 13: Delete flight not found
    @Test
    void testDeleteFlight_NotFound_ThrowsException() {
        // Arrange
        when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.deleteFlight(999);
        });

        assertEquals("Flight not found with id: 999", exception.getMessage());
        verify(flightRepository).findActiveById(999);
        verify(flightRepository, never()).save(any());
    }

    // NEW TEST 14: Get flight by code success
    @Test
    void testGetFlightByCode_Success() {
        // Arrange
        when(flightRepository.findByFlightCode("FL001")).thenReturn(Optional.of(mockFlight));
        when(flightMapper.toDto(mockFlight)).thenReturn(mockFlightDto);

        // Act
        FlightDto result = flightService.getFlightByCode("FL001");

        // Assert
        assertNotNull(result);
        assertEquals("FL001", result.getFlightCode());
        verify(flightRepository).findByFlightCode("FL001");
        verify(flightMapper).toDto(mockFlight);
    }

    // NEW TEST 15: Get flight by code not found
    @Test
    void testGetFlightByCode_NotFound_ThrowsException() {
        // Arrange
        when(flightRepository.findByFlightCode("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.getFlightByCode("INVALID");
        });

        assertEquals("Flight not found with code: INVALID", exception.getMessage());
        verify(flightRepository).findByFlightCode("INVALID");
        verify(flightMapper, never()).toDto(any());
    }

    // NEW TEST 16: Get flights by route success
    @Test
    void testGetFlightsByRoute_Success() {
        // Arrange
        LocalDateTime departureDate = LocalDateTime.of(2024, 1, 15, 10, 0);
        when(flightRepository.findFlights(1, 2, departureDate)).thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenReturn(mockFlightDtos);

        // Act
        List<FlightDto> result = flightService.getFlightsByRoute(1, 2, departureDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(flightRepository).findFlights(1, 2, departureDate);
        verify(flightMapper).toDtoList(mockFlights);
    }

    // NEW TEST 17: Get flights by date range success
    @Test
    void testGetFlightsByDateRange_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 20, 23, 59);
        when(flightRepository.findByDepartureDateRange(startDate, endDate)).thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenReturn(mockFlightDtos);

        // Act
        List<FlightDto> result = flightService.getFlightsByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(flightRepository).findByDepartureDateRange(startDate, endDate);
        verify(flightMapper).toDtoList(mockFlights);
    }

    // NEW TEST 18: Search flights by date success
    @Test
    void testSearchFlightsByDate_Success() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 15);
        when(flightRepository.findByDepartureDate(date)).thenReturn(mockFlights);
        when(flightMapper.toDtoList(mockFlights)).thenReturn(mockFlightDtos);

        // Act
        List<FlightDto> result = flightService.searchFlightsByDate("2024-01-15");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(flightRepository).findByDepartureDate(date);
        verify(flightMapper).toDtoList(mockFlights);
    }

    // NEW TEST 19: Search flights by date invalid format
    @Test
    void testSearchFlightsByDate_InvalidFormat_ThrowsException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.searchFlightsByDate("invalid-date");
        });

        assertTrue(exception.getMessage().contains("Error searching flights by date"));
        verify(flightRepository, never()).findByDepartureDate(any());
    }

    // NEW TEST 20: Check flight availability success
    @Test
    void testCheckFlightAvailability_Success() {
        // Arrange
        FlightTicketClassDto ftcDto1 = new FlightTicketClassDto();
        ftcDto1.setFlightId(1);
        ftcDto1.setTicketClassId(1);
        ftcDto1.setRemainingTicketQuantity(50);

        FlightTicketClassDto ftcDto2 = new FlightTicketClassDto();
        ftcDto2.setFlightId(1);
        ftcDto2.setTicketClassId(2);
        ftcDto2.setRemainingTicketQuantity(30);

        List<FlightTicketClassDto> availabilities = Arrays.asList(ftcDto1, ftcDto2);

        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(mockFlight));
        when(flightTicketClassService.getFlightTicketClassesByFlightId(1)).thenReturn(availabilities);

        // Act
        List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50, result.get(0).getRemainingTicketQuantity());
        verify(flightRepository).findActiveById(1);
        verify(flightTicketClassService).getFlightTicketClassesByFlightId(1);
    }
}
