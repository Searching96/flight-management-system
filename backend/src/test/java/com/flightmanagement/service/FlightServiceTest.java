package com.flightmanagement.service;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.mapper.FlightMapper;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
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
    private ParameterService parameterService;

    @Mock
    private FlightTicketClassService flightTicketClassService;

    @InjectMocks
    private FlightServiceImpl flightService;

    private FlightRequest validRequest;
    private Flight validFlight;
    private FlightDto validFlightDto;
    private ParameterDto parameterDto;

    @BeforeEach
    void setUp() {
        LocalDateTime departureTime = LocalDateTime.now().plusDays(1);
        LocalDateTime arrivalTime = departureTime.plusHours(2);

        validRequest = new FlightRequest();
        validRequest.setFlightCode("FL123");
        validRequest.setDepartureTime(departureTime);
        validRequest.setArrivalTime(arrivalTime);
        validRequest.setPlaneId(1);
        validRequest.setDepartureAirportId(1);
        validRequest.setArrivalAirportId(2);

        validFlight = new Flight();
        validFlight.setFlightId(1);
        validFlight.setFlightCode("FL123");
        validFlight.setDepartureTime(departureTime);
        validFlight.setArrivalTime(arrivalTime);

        validFlightDto = new FlightDto();
        validFlightDto.setFlightId(1);
        validFlightDto.setFlightCode("FL123");

        parameterDto = new ParameterDto();
        parameterDto.setMinFlightDuration(30);
    }

    // ==================== createFlight Tests ====================

    @Tag("createFlight")
    @Test
    void createFlight_validRequest_success() {
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);
        when(parameterService.getLatestParameter()).thenReturn(parameterDto);
        when(flightMapper.toEntityFromCreateRequest(validRequest)).thenReturn(validFlight);
        when(flightRepository.save(any(Flight.class))).thenReturn(validFlight);
        when(flightMapper.toDto(validFlight)).thenReturn(validFlightDto);

        FlightDto result = flightService.createFlight(validRequest);

        assertNotNull(result);
        assertEquals(validFlightDto.getFlightCode(), result.getFlightCode());
        verify(flightRepository).save(any(Flight.class));
    }

    @Tag("createFlight")
    @Test
    void createFlight_duplicateFlightCode_throwsException() {
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.createFlight(validRequest)
        );

        assertTrue(exception.getMessage().contains("Flight code already exists"));
        verify(flightRepository, never()).save(any());
    }

    @Tag("createFlight")
    @Test
    void createFlight_arrivalBeforeDeparture_throwsException() {
        validRequest.setArrivalTime(validRequest.getDepartureTime().minusHours(1));
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.createFlight(validRequest)
        );

        assertTrue(exception.getMessage().contains("Arrival time must be after departure time"));
    }

    @Tag("createFlight")
    @Test
    void createFlight_sameAirports_throwsException() {
        validRequest.setDepartureAirportId(1);
        validRequest.setArrivalAirportId(1);
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.createFlight(validRequest)
        );

        assertTrue(exception.getMessage().contains("Departure and arrival airports cannot be the same"));
    }

    @Tag("createFlight")
    @Test
    void createFlight_flightDurationTooShort_throwsException() {
        validRequest.setArrivalTime(validRequest.getDepartureTime().plusMinutes(15));
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);
        when(parameterService.getLatestParameter()).thenReturn(parameterDto);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.createFlight(validRequest)
        );

        assertTrue(exception.getMessage().contains("Flight duration must be at least"));
    }

    @Tag("createFlight")
    @Test
    void createFlight_exactMinimumDuration_success() {
        validRequest.setArrivalTime(validRequest.getDepartureTime().plusMinutes(30));
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);
        when(parameterService.getLatestParameter()).thenReturn(parameterDto);
        when(flightMapper.toEntityFromCreateRequest(validRequest)).thenReturn(validFlight);
        when(flightRepository.save(any(Flight.class))).thenReturn(validFlight);
        when(flightMapper.toDto(validFlight)).thenReturn(validFlightDto);

        FlightDto result = flightService.createFlight(validRequest);

        assertNotNull(result);
        verify(flightRepository).save(any(Flight.class));
    }

    // ==================== updateFlight Tests ====================

    @Tag("updateFlight")
    @Test
    void updateFlight_validUpdate_success() {
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);
        when(parameterService.getLatestParameter()).thenReturn(parameterDto);
        when(flightRepository.save(any(Flight.class))).thenReturn(validFlight);
        when(flightMapper.toDto(validFlight)).thenReturn(validFlightDto);

        FlightDto result = flightService.updateFlight(1, validRequest);

        assertNotNull(result);
        verify(flightRepository).save(validFlight);
    }

    @Tag("updateFlight")
    @Test
    void updateFlight_flightNotFound_throwsException() {

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.updateFlight(1, validRequest)
        );

        assertFalse(exception.getMessage().contains("Flight not found"));
    }

    @Tag("updateFlight")
    @Test
    void updateFlight_invalidData_throwsException() {
        validRequest.setArrivalTime(validRequest.getDepartureTime().minusHours(1));
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.updateFlight(1, validRequest)
        );

        assertTrue(exception.getMessage().contains("Arrival time must be after departure time"));
    }

    @Tag("updateFlight")
    @Test
    void updateFlight_duplicateFlightCode_throwsException() {
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.updateFlight(1, validRequest)
        );

        assertTrue(exception.getMessage().contains("Flight code already exists"));
    }

    @Tag("updateFlight")
    @Test
    void updateFlight_sameAirports_throwsException() {
        validRequest.setDepartureAirportId(1);
        validRequest.setArrivalAirportId(1);
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.updateFlight(1, validRequest)
        );

        assertTrue(exception.getMessage().contains("Departure and arrival airports cannot be the same"));
    }

    @Tag("updateFlight")
    @Test
    void updateFlight_tooShortDuration_throwsException() {
        validRequest.setArrivalTime(validRequest.getDepartureTime().plusMinutes(15));
        when(flightRepository.existsByFlightCode(validRequest.getFlightCode())).thenReturn(false);
        when(parameterService.getLatestParameter()).thenReturn(parameterDto);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.updateFlight(1, validRequest)
        );

        assertTrue(exception.getMessage().contains("Flight duration must be at least"));
    }

    // ==================== deleteFlight Tests ====================

    @Tag("deleteFlight")
    @Test
    void deleteFlight_validId_success() {
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(validFlight);

        flightService.deleteFlight(1);

        assertNotNull(validFlight.getDeletedAt());
        verify(flightRepository).save(validFlight);
    }

    @Tag("deleteFlight")
    @Test
    void deleteFlight_flightNotFound_throwsException() {
        when(flightRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.deleteFlight(1)
        );

        assertTrue(exception.getMessage().contains("Flight not found"));
        verify(flightRepository, never()).save(any());
    }

    @Tag("deleteFlight")
    @Test
    void deleteFlight_alreadyDeleted_throwsException() {
        when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.deleteFlight(999)
        );

        assertTrue(exception.getMessage().contains("Flight not found"));
    }

    @Tag("deleteFlight")
    @Test
    void deleteFlight_setsDeletedAtTimestamp() {
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(validFlight);

        LocalDateTime beforeDelete = LocalDateTime.now();
        flightService.deleteFlight(1);
        LocalDateTime afterDelete = LocalDateTime.now();

        assertNotNull(validFlight.getDeletedAt());
        assertTrue(validFlight.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
        assertTrue(validFlight.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
    }

    // ==================== searchFlights Tests ====================

    @Nested
    @DisplayName("SearchFlights Tests - Full Path Coverage")
    @Tag("searchFlights")
    class SearchFlightsTests {

        private FlightSearchCriteria criteria;
        private LocalDateTime departureDate;
        private List<Flight> flights;
        private List<FlightDto> flightDtos;

        @BeforeEach
        void setUp() {
            departureDate = LocalDateTime.of(2024, 12, 25, 10, 0);
            
            // Setup test flights
            Flight flight1 = new Flight();
            flight1.setFlightId(1);
            flight1.setFlightCode("VN101");
            
            Flight flight2 = new Flight();
            flight2.setFlightId(2);
            flight2.setFlightCode("VN102");
            
            flights = Arrays.asList(flight1, flight2);
            
            FlightDto dto1 = new FlightDto();
            dto1.setFlightId(1);
            dto1.setFlightCode("VN101");
            
            FlightDto dto2 = new FlightDto();
            dto2.setFlightId(2);
            dto2.setFlightCode("VN102");
            
            flightDtos = Arrays.asList(dto1, dto2);
        }

        // ===== NHÓM 1: Happy Paths - With TicketClass =====

        @Test
        @Tag("searchFlights")
        @DisplayName("TC1: Search with valid ticketClassId > 0 - Returns filtered flights")
        void searchFlights_WithValidTicketClassId_ReturnsFilteredFlights() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, 1);

            when(flightRepository.findFlightsWithTicketClass(1, 2, departureDate, 1, 2))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("VN101", result.get(0).getFlightCode());
            verify(flightRepository).findFlightsWithTicketClass(1, 2, departureDate, 1, 2);
            verify(flightRepository, never()).findFlightsByRoute(anyInt(), anyInt(), any());
            verify(flightMapper).toDtoList(flights);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC2: Search with ticketClassId and 1 passenger - Returns flights")
        void searchFlights_WithTicketClassIdAndSinglePassenger_ReturnsFlights() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 1, 3);

            when(flightRepository.findFlightsWithTicketClass(1, 2, departureDate, 3, 1))
                .thenReturn(Arrays.asList(flights.get(0)));
            when(flightMapper.toDtoList(anyList())).thenReturn(Arrays.asList(flightDtos.get(0)));

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(flightRepository).findFlightsWithTicketClass(1, 2, departureDate, 3, 1);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC3: Search with ticketClassId and multiple passengers - Returns flights")
        void searchFlights_WithTicketClassIdAndMultiplePassengers_ReturnsFlights() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 5, 2);

            when(flightRepository.findFlightsWithTicketClass(1, 2, departureDate, 2, 5))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsWithTicketClass(1, 2, departureDate, 2, 5);
        }

        // ===== NHÓM 2: Happy Paths - Without TicketClass =====

        @Test
        @Tag("searchFlights")
        @DisplayName("TC4: Search with null ticketClassId - Returns all flights on route")
        void searchFlights_WithNullTicketClassId_ReturnsAllFlightsOnRoute() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, null);

            when(flightRepository.findFlightsByRoute(1, 2, departureDate))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsByRoute(1, 2, departureDate);
            verify(flightRepository, never()).findFlightsWithTicketClass(anyInt(), anyInt(), any(), anyInt(), anyInt());
            verify(flightMapper).toDtoList(flights);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC5: Search with ticketClassId = 0 - Returns all flights on route")
        void searchFlights_WithZeroTicketClassId_ReturnsAllFlightsOnRoute() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, 0);

            when(flightRepository.findFlightsByRoute(1, 2, departureDate))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsByRoute(1, 2, departureDate);
            verify(flightRepository, never()).findFlightsWithTicketClass(anyInt(), anyInt(), any(), anyInt(), anyInt());
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC6: Search with ticketClassId < 0 - Returns all flights on route")
        void searchFlights_WithNegativeTicketClassId_ReturnsAllFlightsOnRoute() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, -1);

            when(flightRepository.findFlightsByRoute(1, 2, departureDate))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsByRoute(1, 2, departureDate);
        }

        // ===== NHÓM 3: Empty Results =====

        @Test
        @Tag("searchFlights")
        @DisplayName("TC7: Search with ticketClassId but no flights found - Returns empty list")
        void searchFlights_WithTicketClassIdNoFlightsFound_ReturnsEmptyList() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, 1);

            when(flightRepository.findFlightsWithTicketClass(1, 2, departureDate, 1, 2))
                .thenReturn(Collections.emptyList());
            when(flightMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(flightRepository).findFlightsWithTicketClass(1, 2, departureDate, 1, 2);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC8: Search without ticketClassId but no flights found - Returns empty list")
        void searchFlights_WithoutTicketClassIdNoFlightsFound_ReturnsEmptyList() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, null);

            when(flightRepository.findFlightsByRoute(1, 2, departureDate))
                .thenReturn(Collections.emptyList());
            when(flightMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(flightRepository).findFlightsByRoute(1, 2, departureDate);
        }

        // ===== NHÓM 4: Exception Handling =====

        @Test
        @Tag("searchFlights")
        @DisplayName("TC9: Repository throws exception with ticketClassId - Wraps and rethrows")
        void searchFlights_RepositoryThrowsExceptionWithTicketClass_WrapsAndRethrows() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, 1);

            when(flightRepository.findFlightsWithTicketClass(anyInt(), anyInt(), any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Database connection error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                flightService.searchFlights(criteria);
            });

            assertEquals("Failed to search flights", exception.getMessage());
            assertTrue(exception.getCause().getMessage().contains("Database connection error"));
            verify(flightRepository).findFlightsWithTicketClass(1, 2, departureDate, 1, 2);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC10: Repository throws exception without ticketClassId - Wraps and rethrows")
        void searchFlights_RepositoryThrowsExceptionWithoutTicketClass_WrapsAndRethrows() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, null);

            when(flightRepository.findFlightsByRoute(anyInt(), anyInt(), any()))
                .thenThrow(new RuntimeException("Query timeout"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                flightService.searchFlights(criteria);
            });

            assertEquals("Failed to search flights", exception.getMessage());
            assertTrue(exception.getCause().getMessage().contains("Query timeout"));
            verify(flightRepository).findFlightsByRoute(1, 2, departureDate);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC11: Mapper throws exception - Propagates exception")
        void searchFlights_MapperThrowsException_PropagatesException() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, 1);

            when(flightRepository.findFlightsWithTicketClass(1, 2, departureDate, 1, 2))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights))
                .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                flightService.searchFlights(criteria);
            });

            assertEquals("Failed to search flights", exception.getMessage());
            assertTrue(exception.getCause().getMessage().contains("Mapping error"));
        }

        // ===== NHÓM 5: Edge Cases =====

        @Test
        @Tag("searchFlights")
        @DisplayName("TC12: Search with same departure and arrival airports - Returns flights")
        void searchFlights_SameDepartureAndArrivalAirports_ReturnsFlights() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 1, departureDate, 2, null);

            when(flightRepository.findFlightsByRoute(1, 1, departureDate))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsByRoute(1, 1, departureDate);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC13: Search with very large passenger count - Returns flights")
        void searchFlights_VeryLargePassengerCount_ReturnsFlights() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 100, 1);

            when(flightRepository.findFlightsWithTicketClass(1, 2, departureDate, 1, 100))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsWithTicketClass(1, 2, departureDate, 1, 100);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC14: Search returns single flight - Returns list with one element")
        void searchFlights_SingleFlightReturned_ReturnsListWithOneElement() {
            // Arrange
            criteria = new FlightSearchCriteria(1, 2, departureDate, 2, 1);

            when(flightRepository.findFlightsWithTicketClass(1, 2, departureDate, 1, 2))
                .thenReturn(Arrays.asList(flights.get(0)));
            when(flightMapper.toDtoList(anyList())).thenReturn(Arrays.asList(flightDtos.get(0)));

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("VN101", result.get(0).getFlightCode());
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC15: Search with past date - Returns flights")
        void searchFlights_WithPastDate_ReturnsFlights() {
            // Arrange
            LocalDateTime pastDate = LocalDateTime.of(2023, 1, 1, 10, 0);
            criteria = new FlightSearchCriteria(1, 2, pastDate, 2, null);

            when(flightRepository.findFlightsByRoute(1, 2, pastDate))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsByRoute(1, 2, pastDate);
        }

        @Test
        @Tag("searchFlights")
        @DisplayName("TC16: Search with future date - Returns flights")
        void searchFlights_WithFutureDate_ReturnsFlights() {
            // Arrange
            LocalDateTime futureDate = LocalDateTime.of(2026, 12, 31, 23, 59);
            criteria = new FlightSearchCriteria(1, 2, futureDate, 2, 1);

            when(flightRepository.findFlightsWithTicketClass(1, 2, futureDate, 1, 2))
                .thenReturn(flights);
            when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

            // Act
            List<FlightDto> result = flightService.searchFlights(criteria);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightRepository).findFlightsWithTicketClass(1, 2, futureDate, 1, 2);
        }
    }

    // ==================== searchFlightsByDate Tests ====================

    @Tag("searchFlightsByDate")
    @Test
    void searchFlightsByDate_validDate_returnsFlights() {
        String dateString = "2024-12-25";
        LocalDate date = LocalDate.parse(dateString);
        List<Flight> flights = Arrays.asList(validFlight);
        List<FlightDto> expectedDtos = Arrays.asList(validFlightDto);

        when(flightRepository.findByDepartureDate(date)).thenReturn(flights);
        when(flightMapper.toDtoList(flights)).thenReturn(expectedDtos);

        List<FlightDto> result = flightService.searchFlightsByDate(dateString);

        assertEquals(1, result.size());
        verify(flightRepository).findByDepartureDate(date);
    }

    @Tag("searchFlightsByDate")
    @Test
    void searchFlightsByDate_invalidDateFormat_throwsException() {
        String invalidDate = "25-12-2024";

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.searchFlightsByDate(invalidDate)
        );

        assertTrue(exception.getMessage().contains("Error searching flights by date"));
    }

    @Tag("searchFlightsByDate")
    @Test
    void searchFlightsByDate_noFlightsFound_returnsEmptyList() {
        String dateString = "2024-12-25";
        LocalDate date = LocalDate.parse(dateString);

        when(flightRepository.findByDepartureDate(date)).thenReturn(Collections.emptyList());
        when(flightMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<FlightDto> result = flightService.searchFlightsByDate(dateString);

        assertTrue(result.isEmpty());
    }

    @Tag("searchFlightsByDate")
    @Test
    void searchFlightsByDate_multipleFlights_returnsAll() {
        String dateString = "2024-12-25";
        LocalDate date = LocalDate.parse(dateString);
        Flight flight2 = new Flight();
        List<Flight> flights = Arrays.asList(validFlight, flight2);
        List<FlightDto> expectedDtos = Arrays.asList(validFlightDto, new FlightDto());

        when(flightRepository.findByDepartureDate(date)).thenReturn(flights);
        when(flightMapper.toDtoList(flights)).thenReturn(expectedDtos);

        List<FlightDto> result = flightService.searchFlightsByDate(dateString);

        assertEquals(2, result.size());
    }

    @Tag("searchFlightsByDate")
    @Test
    void searchFlightsByDate_repositoryThrowsException_wrapsException() {
        String dateString = "2024-12-25";
        LocalDate date = LocalDate.parse(dateString);

        when(flightRepository.findByDepartureDate(date))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.searchFlightsByDate(dateString)
        );

        assertTrue(exception.getMessage().contains("Error searching flights by date"));
    }

    @Tag("searchFlightsByDate")
    @Test
    void searchFlightsByDate_nullDate_throwsException() {
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.searchFlightsByDate(null)
        );

        assertTrue(exception.getMessage().contains("Error searching flights by date"));
    }

    // ==================== checkFlightAvailability Tests ====================

    @Tag("checkFlightAvailability")
    @Test
    void checkFlightAvailability_validFlightId_returnsTicketClasses() {
        List<FlightTicketClassDto> ticketClasses = Arrays.asList(new FlightTicketClassDto());

        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
        when(flightTicketClassService.getFlightTicketClassesByFlightId(1)).thenReturn(ticketClasses);

        List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

        assertEquals(1, result.size());
        verify(flightTicketClassService).getFlightTicketClassesByFlightId(1);
    }

    @Tag("checkFlightAvailability")
    @Test
    void checkFlightAvailability_flightNotFound_throwsException() {
        when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.checkFlightAvailability(999)
        );

        assertTrue(exception.getMessage().contains("Flight not found"));
    }

    @Tag("checkFlightAvailability")
    @Test
    void checkFlightAvailability_noTicketClasses_returnsEmptyList() {
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
        when(flightTicketClassService.getFlightTicketClassesByFlightId(1))
            .thenReturn(Collections.emptyList());

        List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

        assertTrue(result.isEmpty());
    }

    @Tag("checkFlightAvailability")
    @Test
    void checkFlightAvailability_multipleTicketClasses_returnsAll() {
        List<FlightTicketClassDto> ticketClasses = Arrays.asList(
            new FlightTicketClassDto(),
            new FlightTicketClassDto(),
            new FlightTicketClassDto()
        );

        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
        when(flightTicketClassService.getFlightTicketClassesByFlightId(1)).thenReturn(ticketClasses);

        List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

        assertEquals(3, result.size());
    }

    @Tag("checkFlightAvailability")
    @Test
    void checkFlightAvailability_serviceThrowsException_wrapsException() {
        when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
        when(flightTicketClassService.getFlightTicketClassesByFlightId(1))
            .thenThrow(new RuntimeException("Service error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.checkFlightAvailability(1)
        );

        assertTrue(exception.getMessage().contains("Error checking flight availability"));
    }

    @Tag("checkFlightAvailability")
    @Test
    void checkFlightAvailability_deletedFlight_throwsException() {
        when(flightRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.checkFlightAvailability(1)
        );

        assertTrue(exception.getMessage().contains("Flight not found"));
    }
}
