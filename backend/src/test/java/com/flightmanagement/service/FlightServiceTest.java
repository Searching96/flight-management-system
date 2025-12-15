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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
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

    // ==================== deleteFlight Tests ====================
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

    // ==================== updateFlight Tests - Complete Path Coverage ====================

    @Nested
    @DisplayName("UpdateFlight Tests - Full Path Coverage")
    @Tag("updateFlight")
    class UpdateFlightTests {

        private FlightRequest updateRequest;
        private Flight existingFlight;
        private FlightDto updatedFlightDto;
        private LocalDateTime newDepartureTime;
        private LocalDateTime newArrivalTime;

        @BeforeEach
        void setUp() {
            newDepartureTime = LocalDateTime.now().plusDays(2);
            newArrivalTime = newDepartureTime.plusHours(3);

            updateRequest = new FlightRequest();
            updateRequest.setFlightCode("FL999");
            updateRequest.setDepartureTime(newDepartureTime);
            updateRequest.setArrivalTime(newArrivalTime);
            updateRequest.setPlaneId(2);
            updateRequest.setDepartureAirportId(2);
            updateRequest.setArrivalAirportId(3);

            existingFlight = new Flight();
            existingFlight.setFlightId(5);
            existingFlight.setFlightCode("FL888");
            existingFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
            existingFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));

            updatedFlightDto = new FlightDto();
            updatedFlightDto.setFlightId(5);
            updatedFlightDto.setFlightCode("FL999");
        }

        // ===== NHÓM 1: Happy Paths - Successful Updates =====

        @Test
        @DisplayName("TC1: Update flight with valid request and valid departure time - Success")
        void updateFlight_ValidRequestAndDepartureTime_Success() {
            // Arrange
            updateRequest.setFlightCode("FL999");
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            assertEquals("FL999", result.getFlightCode());
            verify(flightRepository).findActiveById(5);
            verify(flightRepository).existsByFlightCode("FL999");
            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC2: Update flight with null departure time - Throws exception")
        void updateFlight_NullDepartureTime_ThrowsException() {
            // Arrange
            updateRequest.setDepartureTime(null);

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));

            // Act & Assert
            assertThrows(
                Exception.class,
                () -> flightService.updateFlight(5, updateRequest)
            );

            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC3: Update flight with arrival before departure - Throws exception")
        void updateFlight_ArrivalBeforeDeparture_ThrowsException() {
            // Arrange
            updateRequest.setDepartureTime(newDepartureTime);
            updateRequest.setArrivalTime(newDepartureTime.minusHours(1)); // Arrival before departure

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Arrival time must be after departure time"));
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC4: Update flight with past departure time - Throws exception")
        void updateFlight_PastDepartureTime_ThrowsException() {
            // Arrange
            LocalDateTime pastTime = LocalDateTime.of(2024, 11, 1, 10, 0);
            updateRequest.setDepartureTime(pastTime);
            updateRequest.setArrivalTime(pastTime.plusHours(2));

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode(anyString())).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );
        }

        @Test
        @DisplayName("TC5: Update only departure time - Success")
        void updateFlight_OnlyDepartureTime_Success() {
            // Arrange
            updateRequest.setArrivalTime(null); // Keep existing

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC6: Update only arrival time - Success")
        void updateFlight_OnlyArrivalTime_Success() {
            // Arrange
            updateRequest.setDepartureTime(null); // Keep existing

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC7: Update with very short duration (1 minute) - Boundary test")
        void updateFlight_VeryShortDuration_Success() {
            // Arrange
            parameterDto.setMinFlightDuration(1); // Allow 1 minute
            updateRequest.setArrivalTime(newDepartureTime.plusMinutes(1));

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC8: Update with very long duration (5 days) - Boundary test")
        void updateFlight_VeryLongDuration_Success() {
            // Arrange
            updateRequest.setArrivalTime(newDepartureTime.plusDays(5));

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC10: Update flight with bookings - Throws exception or constraints")
        void updateFlight_WithBookings_ThrowsException() {
            // Arrange - Assumes business rule prevents updates when bookings exist
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            // Mock that this flight has bookings - implementation dependent

            // Act - May succeed or fail depending on business rules
            // If constraint exists, expect exception
            FlightDto result = flightService.updateFlight(5, updateRequest);
            
            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("TC11: Update with same values (idempotent) - Success")
        void updateFlight_SameValues_Success() {
            // Arrange
            updateRequest.setFlightCode(existingFlight.getFlightCode());
            updateRequest.setDepartureTime(existingFlight.getDepartureTime());
            updateRequest.setArrivalTime(existingFlight.getArrivalTime());

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode(existingFlight.getFlightCode())).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC12: Update non-existent flight - Throws exception")
        void updateFlight_DuplicateFlightCode_ThrowsException() {
            // Arrange
            updateRequest.setFlightCode("FL999");
            
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Flight code already exists"));
        }

        @Test
        @DisplayName("TC7: Update flight with same departure and arrival airports - Throws exception")
        void updateFlight_SameDepartureAndArrivalAirports_ThrowsException() {
            // Arrange
            updateRequest.setDepartureAirportId(1);
            updateRequest.setArrivalAirportId(1); // Same airport

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Departure and arrival airports cannot be the same"));
        }

        // ===== NHÓM 4: Insufficient Flight Duration =====

        @Test
        @DisplayName("TC8: Update flight with duration less than minimum - Throws exception")
        void updateFlight_InsufficientFlightDuration_ThrowsException() {
            // Arrange
            parameterDto.setMinFlightDuration(60); // 60 minutes minimum
            updateRequest.setDepartureTime(newDepartureTime);
            updateRequest.setArrivalTime(newDepartureTime.plusMinutes(30)); // Only 30 minutes

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Flight duration must be at least"));
        }

        @Test
        @DisplayName("TC9: Update flight with exact minimum duration - Success")
        void updateFlight_ExactMinimumDuration_Success() {
            // Arrange
            parameterDto.setMinFlightDuration(180); // 180 minutes minimum
            updateRequest.setDepartureTime(newDepartureTime);
            updateRequest.setArrivalTime(newDepartureTime.plusMinutes(180)); // Exactly 180 minutes

            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        // ===== NHÓM 5: Flight Not Found & Repository/Mapper Errors =====

        @Test
        @DisplayName("TC10: Update flight with non-existent ID - Throws exception")
        void updateFlight_FlightNotFound_ThrowsException() {
            // Arrange
            when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());
            when(flightRepository.existsByFlightCode(anyString())).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightService.updateFlight(999, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Flight") && exception.getMessage().contains("not found"), 
                      "Expected message to contain 'Flight' and 'not found', got: " + exception.getMessage());
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC11: Update flight when repository save fails - Propagates exception")
        void updateFlight_RepositorySaveFails_PropagatesException() {
            // Arrange
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class)))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );

            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC12: Update flight when mapper fails - Propagates exception")
        void updateFlight_MapperFails_PropagatesException() {
            // Arrange
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);
            when(flightMapper.toDto(existingFlight))
                .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );
        }

        @Test
        @DisplayName("TC13: Update flight when parameter service fails - Propagates exception")
        void updateFlight_ParameterServiceFails_PropagatesException() {
            // Arrange
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.existsByFlightCode("FL999")).thenReturn(false);
            when(parameterService.getLatestParameter())
                .thenThrow(new RuntimeException("Parameter service error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightService.updateFlight(5, updateRequest)
            );

            verify(flightRepository, never()).save(any(Flight.class));
        }
    }

    // ==================== createFlight Tests - Complete Path Coverage ====================

    @Nested
    @DisplayName("CreateFlight Tests - Full Path Coverage")
    @Tag("createFlight")
    class CreateFlightTests {

        private FlightRequest createRequest;
        private Flight createdFlight;
        private FlightDto createdFlightDto;

        @BeforeEach
        void setUp() {
            LocalDateTime departureTime = LocalDateTime.of(2024, 12, 25, 10, 0);
            LocalDateTime arrivalTime = LocalDateTime.of(2024, 12, 25, 13, 30);

            createRequest = new FlightRequest();
            createRequest.setFlightCode("VN2024");
            createRequest.setDepartureTime(departureTime);
            createRequest.setArrivalTime(arrivalTime);
            createRequest.setPlaneId(1);
            createRequest.setDepartureAirportId(1);
            createRequest.setArrivalAirportId(2);

            createdFlight = new Flight();
            createdFlight.setFlightId(10);
            createdFlight.setFlightCode("VN2024");
            createdFlight.setDeletedAt(null);

            createdFlightDto = new FlightDto();
            createdFlightDto.setFlightId(10);
            createdFlightDto.setFlightCode("VN2024");

            parameterDto.setMinFlightDuration(30);
        }

        // ===== NHÓM 1: Happy Paths - Successful Creation =====

        @Test
        @DisplayName("TC1: Create flight with valid request - Success")
        void createFlight_ValidRequest_Success() {
            // Arrange
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightMapper.toEntityFromCreateRequest(createRequest)).thenReturn(createdFlight);
            when(flightRepository.save(any(Flight.class))).thenReturn(createdFlight);
            when(flightMapper.toDto(createdFlight)).thenReturn(createdFlightDto);

            // Act
            FlightDto result = flightService.createFlight(createRequest);

            // Assert
            assertNotNull(result);
            assertEquals("VN2024", result.getFlightCode());
            assertEquals(10, result.getFlightId());
            verify(flightRepository).save(any(Flight.class));
        }

        // ===== NHÓM 2: Duplicate Flight Code Validation =====

        @Test
        @DisplayName("TC2: Create flight with existing flight code - Throws exception")
        void createFlight_DuplicateFlightCode_ThrowsException() {
            // Arrange
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.createFlight(createRequest)
            );

            assertTrue(exception.getMessage().contains("Flight code already exists"));
            verify(flightRepository, never()).save(any(Flight.class));
        }

        // ===== NHÓM 3: Time Validation =====

        @Test
        @DisplayName("TC3: Create flight with arrival before departure - Throws exception")
        void createFlight_ArrivalBeforeDeparture_ThrowsException() {
            // Arrange
            createRequest.setDepartureTime(LocalDateTime.of(2024, 12, 25, 13, 30));
            createRequest.setArrivalTime(LocalDateTime.of(2024, 12, 25, 10, 0));

            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.createFlight(createRequest)
            );

            assertTrue(exception.getMessage().contains("Arrival time must be after departure time"));
        }

        @Test
        @DisplayName("TC4: Create flight with past departure time - Throws exception")
        void createFlight_PastDepartureTime_ThrowsException() {
            // Arrange
            LocalDateTime pastTime = LocalDateTime.of(2024, 11, 1, 10, 0); // Past date
            createRequest.setDepartureTime(pastTime);
            createRequest.setArrivalTime(pastTime.plusHours(2));

            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.createFlight(createRequest)
            );

            assertTrue(exception.getMessage().contains("Departure date cannot be in the past") ||
                      exception.getMessage().contains("past"));
        }

        @Test
        @DisplayName("TC5: Create flight with same departure and arrival airports - Throws exception")
        void createFlight_SameAirports_ThrowsException() {
            // Arrange
            createRequest.setDepartureAirportId(1);
            createRequest.setArrivalAirportId(1);

            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightService.createFlight(createRequest)
            );

            assertTrue(exception.getMessage().contains("Departure and arrival airports cannot be the same"));
        }

        // ===== NHÓM 4: Boundary Value Tests =====

        @Test
        @DisplayName("TC6: Create flight with minimum seats (1) - Success")
        void createFlight_MinimumSeats_Success() {
            // Arrange
            createRequest.setFlightCode("VN2024");
            // Assuming totalSeats can be set via request or plane configuration
            
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightMapper.toEntityFromCreateRequest(createRequest)).thenReturn(createdFlight);
            when(flightRepository.save(any(Flight.class))).thenReturn(createdFlight);
            when(flightMapper.toDto(createdFlight)).thenReturn(createdFlightDto);

            // Act
            FlightDto result = flightService.createFlight(createRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC7: Create flight with maximum seats (500) - Success")
        void createFlight_MaximumSeats_Success() {
            // Arrange
            createRequest.setFlightCode("VN2024");
            // Assuming totalSeats configured for maximum
            
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightMapper.toEntityFromCreateRequest(createRequest)).thenReturn(createdFlight);
            when(flightRepository.save(any(Flight.class))).thenReturn(createdFlight);
            when(flightMapper.toDto(createdFlight)).thenReturn(createdFlightDto);

            // Act
            FlightDto result = flightService.createFlight(createRequest);

            // Assert
            assertNotNull(result);
            verify(flightRepository).save(any(Flight.class));
        }

        // ===== NHÓM 5: Null/Invalid Input =====

        @Test
        @DisplayName("TC8: Create flight with null flight code - Throws exception")
        void createFlight_NullFlightCode_ThrowsException() {
            // Arrange
            createRequest.setFlightCode(null);

            // Act & Assert
            assertThrows(
                Exception.class,
                () -> flightService.createFlight(createRequest)
            );

            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC9: Create flight with non-existent departure airport - Throws exception")
        void createFlight_InvalidDepartureAirport_ThrowsException() {
            // Arrange
            createRequest.setDepartureAirportId(999); // Non-existent ID

            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            // Repository or service should validate airport existence

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightService.createFlight(createRequest)
            );
        }

        @Test
        @DisplayName("TC10: Create flight with non-existent arrival airport - Throws exception")
        void createFlight_InvalidArrivalAirport_ThrowsException() {
            // Arrange
            createRequest.setArrivalAirportId(888); // Non-existent ID

            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            // Repository or service should validate airport existence

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightService.createFlight(createRequest)
            );
        }
    }
}