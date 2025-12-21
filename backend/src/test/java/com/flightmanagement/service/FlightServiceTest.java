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
    @Nested
    @DisplayName("DeleteFlights Test")
    @Tag("deleteFlights")
    class DeleteFlightsTests {
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

        @Tag("deleteFlight")
        @DisplayName("Throw exception if flight not found")
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
        @DisplayName("Set delete timestamp")
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
            verify(flightRepository).findActiveById(1);
            verify(flightRepository).save(validFlight);
        }
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

        @Test
        @Tag("searchFlights")
        @DisplayName("TC1: Search with ticketClassId > 0 - Returns filtered flights")
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

        // ===== NHÓM 3: Empty Results =====

        @Test
        @Tag("searchFlights")
        @DisplayName("TC4: Search with ticketClassId but no flights found - Returns empty list")
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
        @DisplayName("TC5: Search without ticketClassId but no flights found - Returns empty list")
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

        // ===== NHÓM 5: Edge Cases =====

        @Test
        @Tag("searchFlights")
        @DisplayName("TC6: Search with same departure and arrival airports - Returns flights")
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
        @DisplayName("TC7: Search with very large passenger count - Returns flights")
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
        @DisplayName("TC8: Search returns single flight - Returns list with one element")
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
        @DisplayName("TC9: Search with past date - Returns flights")
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
        @DisplayName("TC10: Search with future date - Returns flights")
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

    // ===== NHÓM: UpdateFlight Balanced Full Path Coverage =====

    @Nested
    @DisplayName("UpdateFlight Tests - Full Path Coverage")
    @Tag("updateFlight")
    @SuppressWarnings("null")
    class UpdateFlightTests {

        private FlightRequest updateRequest;
        private Flight existingFlight;
        private Flight updatedFlight;
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

            updatedFlight = new Flight();
            updatedFlight.setFlightId(5);
            updatedFlight.setFlightCode("FL999");
            updatedFlight.setDepartureTime(newDepartureTime);
            updatedFlight.setArrivalTime(newArrivalTime);

            updatedFlightDto = new FlightDto();
            updatedFlightDto.setFlightId(5);
            updatedFlightDto.setFlightCode("FL999");

            parameterDto.setMinFlightDuration(30);
        }

        @Test
        @DisplayName("TC1: Update non-existent flight - Throws RuntimeException")
        void updateFlight_FlightNotFound_ThrowsRuntimeException() {
            // Arrange
            when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> flightService.updateFlight(999, updateRequest));
            assertTrue(ex.getMessage().contains("Flight not found"));
            verify(flightRepository).findActiveById(999);
            verify(flightRepository, never()).findByFlightCode(any());
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC2: Duplicate flight code from another flight - Throws IllegalArgumentException")
        void updateFlight_DuplicateCodeFromAnotherFlight_ThrowsValidationException() {
            // Arrange
            Flight anotherFlight = new Flight();
            anotherFlight.setFlightId(99);
            anotherFlight.setFlightCode("FL999");
            
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.findByFlightCode("FL999")).thenReturn(Optional.of(anotherFlight));

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.updateFlight(5, updateRequest));
            assertTrue(ex.getMessage().contains("already exists"));
            verify(flightRepository).findByFlightCode("FL999");
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC3: Arrival before departure - Throws IllegalArgumentException")
        void updateFlight_ArrivalBeforeDeparture_ThrowsValidationException() {
            // Arrange
            updateRequest.setArrivalTime(newDepartureTime.minusHours(1));
            when(flightRepository.findByFlightCode("FL999")).thenReturn(Optional.empty());
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.updateFlight(5, updateRequest));
            assertTrue(ex.getMessage().contains("Arrival time"));
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC4: Same departure and arrival airports - Throws IllegalArgumentException")
        void updateFlight_SameAirports_ThrowsValidationException() {
            // Arrange
            updateRequest.setArrivalAirportId(updateRequest.getDepartureAirportId());
            when(flightRepository.findByFlightCode("FL999")).thenReturn(Optional.empty());
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.updateFlight(5, updateRequest));
            assertTrue(ex.getMessage().contains("airports"));
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC5: Duration below minimum - Throws IllegalArgumentException")
        void updateFlight_DurationBelowMin_ThrowsValidationException() {
            // Arrange
            updateRequest.setArrivalTime(newDepartureTime.plusMinutes(10)); // < 30 min
            when(flightRepository.findByFlightCode("FL999")).thenReturn(Optional.empty());
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.updateFlight(5, updateRequest));
            assertTrue(ex.getMessage().contains("at least"));
            verify(parameterService).getLatestParameter();
            verify(flightRepository, never()).save(any(Flight.class));
        }

        @Test
        @DisplayName("TC6: Update flight with valid request - Success")
        void updateFlight_ValidRequest_Success() {
            // Arrange
            when(flightRepository.findActiveById(5)).thenReturn(Optional.of(existingFlight));
            when(flightRepository.findByFlightCode("FL999")).thenReturn(Optional.empty());
            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
            when(flightRepository.save(any(Flight.class))).thenReturn(updatedFlight);
            when(flightMapper.toDto(updatedFlight)).thenReturn(updatedFlightDto);

            // Act
            FlightDto result = flightService.updateFlight(5, updateRequest);

            // Assert
            assertNotNull(result);
            assertEquals(5, result.getFlightId());
            assertEquals("FL999", result.getFlightCode());
            verify(flightRepository).findActiveById(5);
            verify(flightRepository).findByFlightCode("FL999");
            verify(parameterService).getLatestParameter();
            verify(flightRepository).save(any(Flight.class));
            verify(flightMapper).toDto(updatedFlight);
        }
    }

    // ==================== createFlight Tests - Complete Path Coverage ====================

    @Nested
    @DisplayName("CreateFlight Tests - Full Path Coverage")
    @Tag("createFlight")
    class CreateFlightTests {

        private FlightRequest createRequest;
        private Flight mappedFlight;
        private Flight savedFlight;
        private FlightDto savedFlightDto;
        private ParameterDto parameterDto;

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

            mappedFlight = new Flight();
            mappedFlight.setFlightId(null);
            mappedFlight.setFlightCode("VN2024");

            savedFlight = new Flight();
            savedFlight.setFlightId(10);
            savedFlight.setFlightCode("VN2024");
            savedFlight.setDeletedAt(null);

            savedFlightDto = new FlightDto();
            savedFlightDto.setFlightId(10);
            savedFlightDto.setFlightCode("VN2024");

            parameterDto = new ParameterDto();
            parameterDto.setMinFlightDuration(30);

            when(parameterService.getLatestParameter()).thenReturn(parameterDto);
        }

        @Test
        @DisplayName("TC1: Duplicate flight code - Throws IllegalArgumentException")
        void createFlight_DuplicateCode_ThrowsValidationException() {
            // Arrange
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.createFlight(createRequest));
            assertTrue(ex.getMessage().contains("already exists"));
            verify(flightRepository).existsByFlightCode("VN2024");
            verify(flightMapper, never()).toEntityFromCreateRequest(any());
            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC2: Arrival before departure - Throws IllegalArgumentException")
        void createFlight_ArrivalBeforeDeparture_ThrowsValidationException() {
            // Arrange
            createRequest.setArrivalTime(createRequest.getDepartureTime().minusMinutes(10));
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.createFlight(createRequest));
            assertTrue(ex.getMessage().contains("Arrival time"));
            verify(flightRepository).existsByFlightCode("VN2024");
            verify(flightMapper, never()).toEntityFromCreateRequest(any());
            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC3: Same departure and arrival airports - Throws IllegalArgumentException")
        void createFlight_SameAirports_ThrowsValidationException() {
            // Arrange
            createRequest.setArrivalAirportId(createRequest.getDepartureAirportId());
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.createFlight(createRequest));
            assertTrue(ex.getMessage().contains("airports"));
            verify(flightRepository).existsByFlightCode("VN2024");
            verify(flightMapper, never()).toEntityFromCreateRequest(any());
            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC4: Duration below minimum - Throws IllegalArgumentException")
        void createFlight_DurationBelowMin_ThrowsValidationException() {
            // Arrange - Duration = 20 minutes < 30 minutes minimum
            createRequest.setArrivalTime(createRequest.getDepartureTime().plusMinutes(20));
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> flightService.createFlight(createRequest));
            assertTrue(ex.getMessage().contains("at least"));
            verify(flightRepository).existsByFlightCode("VN2024");
            verify(parameterService).getLatestParameter();
            verify(flightMapper, never()).toEntityFromCreateRequest(any());
            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC5: Create flight with valid request - Success")
        void createFlight_ValidRequest_Success() {
            // Arrange
            when(flightRepository.existsByFlightCode("VN2024")).thenReturn(false);
            when(flightMapper.toEntityFromCreateRequest(createRequest)).thenReturn(mappedFlight);
            when(flightRepository.save(mappedFlight)).thenReturn(savedFlight);
            when(flightMapper.toDto(savedFlight)).thenReturn(savedFlightDto);

            // Act
            FlightDto result = flightService.createFlight(createRequest);

            // Assert
            assertNotNull(result);
            assertEquals(10, result.getFlightId());
            assertEquals("VN2024", result.getFlightCode());
            verify(flightRepository).existsByFlightCode("VN2024");
            verify(parameterService).getLatestParameter();
            verify(flightMapper).toEntityFromCreateRequest(createRequest);
            verify(flightRepository).save(mappedFlight);
            verify(flightMapper).toDto(savedFlight);
        }
    }

//     // ==================== getFlightsByDateRange Tests - Complete Path Coverage ====================

//     @Nested
//     @DisplayName("GetFlightsByDateRange Tests - Full Path Coverage")
//     @Tag("getFlightsByDateRange")
//     class GetFlightsByDateRangeTests {

//         private LocalDateTime startDate;
//         private LocalDateTime endDate;
//         private List<Flight> flights;
//         private List<FlightDto> flightDtos;

//         @BeforeEach
//         void setUp() {
//             startDate = LocalDateTime.of(2024, 12, 20, 0, 0);
//             endDate = LocalDateTime.of(2024, 12, 30, 23, 59);

//             Flight flight1 = new Flight();
//             flight1.setFlightId(1);
//             flight1.setFlightCode("VN101");

//             Flight flight2 = new Flight();
//             flight2.setFlightId(2);
//             flight2.setFlightCode("VN102");

//             flights = Arrays.asList(flight1, flight2);

//             FlightDto dto1 = new FlightDto();
//             dto1.setFlightId(1);
//             dto1.setFlightCode("VN101");

//             FlightDto dto2 = new FlightDto();
//             dto2.setFlightId(2);
//             dto2.setFlightCode("VN102");

//             flightDtos = Arrays.asList(dto1, dto2);
//         }

//         // ===== NHÓM 1: Happy Paths - Flights Found =====

//         @Test
//         @DisplayName("TC1: Get flights within date range with multiple flights - Returns all flights")
//         void getFlightsByDateRange_MultipleFlights_ReturnsAll() {
//             // Arrange
//             when(flightRepository.findByDepartureDateRange(startDate, endDate))
//                 .thenReturn(flights);
//             when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

//             // Act
//             List<FlightDto> result = flightService.getFlightsByDateRange(startDate, endDate);

//             // Assert
//             assertNotNull(result);
//             assertEquals(2, result.size());
//             assertEquals("VN101", result.get(0).getFlightCode());
//             assertEquals("VN102", result.get(1).getFlightCode());
//             verify(flightRepository).findByDepartureDateRange(startDate, endDate);
//         }

//         @Test
//         @DisplayName("TC2: Get flights within date range with single flight - Returns list with one element")
//         void getFlightsByDateRange_SingleFlight_ReturnsList() {
//             // Arrange
//             when(flightRepository.findByDepartureDateRange(startDate, endDate))
//                 .thenReturn(Arrays.asList(flights.get(0)));
//             when(flightMapper.toDtoList(anyList())).thenReturn(Arrays.asList(flightDtos.get(0)));

//             // Act
//             List<FlightDto> result = flightService.getFlightsByDateRange(startDate, endDate);

//             // Assert
//             assertNotNull(result);
//             assertEquals(1, result.size());
//             verify(flightRepository).findByDepartureDateRange(startDate, endDate);
//         }

//         // ===== NHÓM 2: Empty Results =====

//         @Test
//         @DisplayName("TC3: Get flights within date range with no flights - Returns empty list")
//         void getFlightsByDateRange_NoFlights_ReturnsEmptyList() {
//             // Arrange
//             when(flightRepository.findByDepartureDateRange(startDate, endDate))
//                 .thenReturn(Collections.emptyList());
//             when(flightMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

//             // Act
//             List<FlightDto> result = flightService.getFlightsByDateRange(startDate, endDate);

//             // Assert
//             assertNotNull(result);
//             assertTrue(result.isEmpty());
//             verify(flightRepository).findByDepartureDateRange(startDate, endDate);
//         }

//         // ===== NHÓM 3: Date Range Edge Cases =====

//         @Test
//         @DisplayName("TC4: Get flights with same start and end date - Returns flights")
//         void getFlightsByDateRange_SameDateRange_ReturnsFlights() {
//             // Arrange
//             LocalDateTime sameDate = LocalDateTime.of(2024, 12, 25, 0, 0);
//             when(flightRepository.findByDepartureDateRange(sameDate, sameDate))
//                 .thenReturn(Arrays.asList(flights.get(0)));
//             when(flightMapper.toDtoList(anyList())).thenReturn(Arrays.asList(flightDtos.get(0)));

//             // Act
//             List<FlightDto> result = flightService.getFlightsByDateRange(sameDate, sameDate);

//             // Assert
//             assertNotNull(result);
//             assertEquals(1, result.size());
//         }

//         @Test
//         @DisplayName("TC5: Get flights with past date range - Returns flights")
//         void getFlightsByDateRange_PastDateRange_ReturnsFlights() {
//             // Arrange
//             LocalDateTime pastStart = LocalDateTime.of(2023, 12, 20, 0, 0);
//             LocalDateTime pastEnd = LocalDateTime.of(2023, 12, 30, 23, 59);

//             when(flightRepository.findByDepartureDateRange(pastStart, pastEnd))
//                 .thenReturn(flights);
//             when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

//             // Act
//             List<FlightDto> result = flightService.getFlightsByDateRange(pastStart, pastEnd);

//             // Assert
//             assertNotNull(result);
//             assertEquals(2, result.size());
//         }

//         @Test
//         @DisplayName("TC6: Get flights with future date range - Returns flights")
//         void getFlightsByDateRange_FutureDateRange_ReturnsFlights() {
//             // Arrange
//             LocalDateTime futureStart = LocalDateTime.of(2025, 12, 20, 0, 0);
//             LocalDateTime futureEnd = LocalDateTime.of(2025, 12, 30, 23, 59);

//             when(flightRepository.findByDepartureDateRange(futureStart, futureEnd))
//                 .thenReturn(flights);
//             when(flightMapper.toDtoList(flights)).thenReturn(flightDtos);

//             // Act
//             List<FlightDto> result = flightService.getFlightsByDateRange(futureStart, futureEnd);

//             // Assert
//             assertNotNull(result);
//             assertEquals(2, result.size());
//         }

//         // ===== NHÓM 4: Repository and Mapper Errors =====

//         @Test
//         @DisplayName("TC7: Get flights when repository throws exception - Propagates exception")
//         void getFlightsByDateRange_RepositoryThrowsException_PropagatesException() {
//             // Arrange
//             when(flightRepository.findByDepartureDateRange(startDate, endDate))
//                 .thenThrow(new RuntimeException("Database error"));

//             // Act & Assert
//             assertThrows(
//                 RuntimeException.class,
//                 () -> flightService.getFlightsByDateRange(startDate, endDate)
//             );
//         }

//         @Test
//         @DisplayName("TC8: Get flights when mapper throws exception - Propagates exception")
//         void getFlightsByDateRange_MapperThrowsException_PropagatesException() {
//             // Arrange
//             when(flightRepository.findByDepartureDateRange(startDate, endDate))
//                 .thenReturn(flights);
//             when(flightMapper.toDtoList(flights))
//                 .thenThrow(new RuntimeException("Mapping error"));

//             // Act & Assert
//             assertThrows(
//                 RuntimeException.class,
//                 () -> flightService.getFlightsByDateRange(startDate, endDate)
//             );
//         }

//         // ===== NHÓM 5: Large Result Sets =====

//         @Test
//         @DisplayName("TC9: Get flights with many flights returned - Returns all flights")
//         void getFlightsByDateRange_ManyFlights_ReturnsAll() {
//             // Arrange
//             List<Flight> manyFlights = new ArrayList<>();
//             List<FlightDto> manyDtos = new ArrayList<>();
//             for (int i = 0; i < 50; i++) {
//                 Flight flight = new Flight();
//                 flight.setFlightId(i);
//                 flight.setFlightCode("VN" + String.format("%03d", i));
//                 manyFlights.add(flight);

//                 FlightDto dto = new FlightDto();
//                 dto.setFlightId(i);
//                 dto.setFlightCode("VN" + String.format("%03d", i));
//                 manyDtos.add(dto);
//             }

//             when(flightRepository.findByDepartureDateRange(startDate, endDate))
//                 .thenReturn(manyFlights);
//             when(flightMapper.toDtoList(manyFlights)).thenReturn(manyDtos);

//             // Act
//             List<FlightDto> result = flightService.getFlightsByDateRange(startDate, endDate);

//             // Assert
//             assertNotNull(result);
//             assertEquals(50, result.size());
//         }
//     }

//     // ==================== checkFlightAvailability Tests - Complete Path Coverage ====================

//     @Nested
//     @DisplayName("CheckFlightAvailability Tests - Full Path Coverage")
//     @Tag("checkFlightAvailability")
//     class CheckFlightAvailabilityTests {

//         private List<FlightTicketClassDto> ticketClasses;

//         @BeforeEach
//         void setUp() {
//             FlightTicketClassDto class1 = new FlightTicketClassDto();
//             class1.setFlightId(1);
//             class1.setTicketClassId(1);

//             FlightTicketClassDto class2 = new FlightTicketClassDto();
//             class2.setFlightId(1);
//             class2.setTicketClassId(2);

//             FlightTicketClassDto class3 = new FlightTicketClassDto();
//             class3.setFlightId(1);
//             class3.setTicketClassId(3);

//             ticketClasses = Arrays.asList(class1, class2, class3);
//         }

//         // ===== NHÓM 1: Happy Paths - Flight Found with Classes =====

//         @Test
//         @DisplayName("TC1: Check availability for valid flight with multiple ticket classes - Returns all classes")
//         void checkFlightAvailability_ValidFlightMultipleClasses_ReturnsAll() {
//             // Arrange
//             when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
//             when(flightTicketClassService.getFlightTicketClassesByFlightId(1))
//                 .thenReturn(ticketClasses);

//             // Act
//             List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

//             // Assert
//             assertNotNull(result);
//             assertEquals(3, result.size());
//             verify(flightRepository).findActiveById(1);
//             verify(flightTicketClassService).getFlightTicketClassesByFlightId(1);
//         }

//         @Test
//         @DisplayName("TC2: Check availability for valid flight with single ticket class - Returns class")
//         void checkFlightAvailability_ValidFlightSingleClass_ReturnsClass() {
//             // Arrange
//             when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
//             when(flightTicketClassService.getFlightTicketClassesByFlightId(1))
//                 .thenReturn(Arrays.asList(ticketClasses.get(0)));

//             // Act
//             List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

//             // Assert
//             assertNotNull(result);
//             assertEquals(1, result.size());
//         }

//         // ===== NHÓM 2: No Ticket Classes Found =====

//         @Test
//         @DisplayName("TC3: Check availability for valid flight with no ticket classes - Returns empty list")
//         void checkFlightAvailability_NoTicketClasses_ReturnsEmptyList() {
//             // Arrange
//             when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
//             when(flightTicketClassService.getFlightTicketClassesByFlightId(1))
//                 .thenReturn(Collections.emptyList());

//             // Act
//             List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

//             // Assert
//             assertNotNull(result);
//             assertTrue(result.isEmpty());
//             verify(flightTicketClassService).getFlightTicketClassesByFlightId(1);
//         }

//         // ===== NHÓM 3: Flight Not Found =====

//         @Test
//         @DisplayName("TC4: Check availability for non-existent flight ID - Throws exception")
//         void checkFlightAvailability_FlightNotFound_ThrowsException() {
//             // Arrange
//             when(flightRepository.findActiveById(999)).thenReturn(Optional.empty());

//             // Act & Assert
//             RuntimeException exception = assertThrows(
//                 RuntimeException.class,
//                 () -> flightService.checkFlightAvailability(999)
//             );

//             assertTrue(exception.getMessage().contains("Flight not found"));
//             verify(flightTicketClassService, never()).getFlightTicketClassesByFlightId(any());
//         }

//         @Test
//         @DisplayName("TC5: Check availability for deleted flight (ID not found) - Throws exception")
//         void checkFlightAvailability_DeletedFlight_ThrowsException() {
//             // Arrange
//             when(flightRepository.findActiveById(5)).thenReturn(Optional.empty());

//             // Act & Assert
//             RuntimeException exception = assertThrows(
//                 RuntimeException.class,
//                 () -> flightService.checkFlightAvailability(5)
//             );

//             assertTrue(exception.getMessage().contains("Flight not found"));
//         }

//         // ===== NHÓM 4: Service Layer Errors =====

//         @Test
//         @DisplayName("TC6: Check availability when ticket class service throws exception - Wraps exception")
//         void checkFlightAvailability_ServiceThrowsException_WrapsException() {
//             // Arrange
//             when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
//             when(flightTicketClassService.getFlightTicketClassesByFlightId(1))
//                 .thenThrow(new RuntimeException("Service connection error"));

//             // Act & Assert
//             RuntimeException exception = assertThrows(
//                 RuntimeException.class,
//                 () -> flightService.checkFlightAvailability(1)
//             );

//             assertTrue(exception.getMessage().contains("Error checking flight availability"));
//         }

//         @Test
//         @DisplayName("TC7: Check availability when repository throws exception - Wraps exception")
//         void checkFlightAvailability_RepositoryThrowsException_WrapsException() {
//             // Arrange
//             when(flightRepository.findActiveById(1))
//                 .thenThrow(new RuntimeException("Database error"));

//             // Act & Assert
//             RuntimeException exception = assertThrows(
//                 RuntimeException.class,
//                 () -> flightService.checkFlightAvailability(1)
//             );

//             assertTrue(exception.getMessage().contains("Error checking flight availability"));
//         }

//         // ===== NHÓM 5: Large Result Sets =====

//         @Test
//         @DisplayName("TC8: Check availability with many ticket classes - Returns all classes")
//         void checkFlightAvailability_ManyTicketClasses_ReturnsAll() {
//             // Arrange
//             List<FlightTicketClassDto> manyClasses = new ArrayList<>();
//             for (int i = 1; i <= 20; i++) {
//                 FlightTicketClassDto dto = new FlightTicketClassDto();
//                 dto.setFlightId(1);
//                 dto.setTicketClassId(i);
//                 manyClasses.add(dto);
//             }

//             when(flightRepository.findActiveById(1)).thenReturn(Optional.of(validFlight));
//             when(flightTicketClassService.getFlightTicketClassesByFlightId(1))
//                 .thenReturn(manyClasses);

//             // Act
//             List<FlightTicketClassDto> result = flightService.checkFlightAvailability(1);

//             // Assert
//             assertNotNull(result);
//             assertEquals(20, result.size());
//         }

//         @Test
//         @DisplayName("TC9: Check availability with null flight ID - Throws exception")
//         void checkFlightAvailability_NullFlightId_ThrowsException() {
//             // Act & Assert
//             assertThrows(
//                 Exception.class,
//                 () -> flightService.checkFlightAvailability(null)
//             );
//         }
//     }
}