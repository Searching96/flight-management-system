package com.flightmanagement.service;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.mapper.FlightMapper;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
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

    @Tag("searchFlights")
    @Test
    void searchFlights_withTicketClass_returnsFilteredFlights() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(1, 2, LocalDateTime.now(), 2, 1);
        List<Flight> flights = Arrays.asList(validFlight);
        List<FlightDto> expectedDtos = Arrays.asList(validFlightDto);

        when(flightRepository.findFlightsWithTicketClass(1, 2, criteria.getDepartureDate(), 1, 2))
            .thenReturn(flights);
        when(flightMapper.toDtoList(flights)).thenReturn(expectedDtos);

        List<FlightDto> result = flightService.searchFlights(criteria);

        assertEquals(1, result.size());
        verify(flightRepository).findFlightsWithTicketClass(1, 2, criteria.getDepartureDate(), 1, 2);
    }

    @Tag("searchFlights")
    @Test
    void searchFlights_withoutTicketClass_returnsAllFlights() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(1, 2, LocalDateTime.now(), 2, null);
        List<Flight> flights = Arrays.asList(validFlight);
        List<FlightDto> expectedDtos = Arrays.asList(validFlightDto);

        when(flightRepository.findFlightsByRoute(1, 2, criteria.getDepartureDate()))
            .thenReturn(flights);
        when(flightMapper.toDtoList(flights)).thenReturn(expectedDtos);

        List<FlightDto> result = flightService.searchFlights(criteria);

        assertEquals(1, result.size());
        verify(flightRepository).findFlightsByRoute(1, 2, criteria.getDepartureDate());
    }

    @Tag("searchFlights")
    @Test
    void searchFlights_withTicketClassZero_returnsAllFlights() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(1, 2, LocalDateTime.now(), 2, 0);
        List<Flight> flights = Arrays.asList(validFlight);
        List<FlightDto> expectedDtos = Arrays.asList(validFlightDto);

        when(flightRepository.findFlightsByRoute(1, 2, criteria.getDepartureDate()))
            .thenReturn(flights);
        when(flightMapper.toDtoList(flights)).thenReturn(expectedDtos);

        List<FlightDto> result = flightService.searchFlights(criteria);

        assertEquals(1, result.size());
        verify(flightRepository).findFlightsByRoute(1, 2, criteria.getDepartureDate());
    }

    @Tag("searchFlights")
    @Test
    void searchFlights_noFlightsFound_returnsEmptyList() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(1, 2, LocalDateTime.now(), 2, null);

        when(flightRepository.findFlightsByRoute(1, 2, criteria.getDepartureDate()))
            .thenReturn(Collections.emptyList());
        when(flightMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<FlightDto> result = flightService.searchFlights(criteria);

        assertTrue(result.isEmpty());
    }

    @Tag("searchFlights")
    @Test
    void searchFlights_repositoryThrowsException_wrapsException() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(1, 2, LocalDateTime.now(), 2, 1);

        when(flightRepository.findFlightsWithTicketClass(anyInt(), anyInt(), any(), anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightService.searchFlights(criteria)
        );

        assertTrue(exception.getMessage().contains("Failed to search flights"));
    }

    @Tag("searchFlights")
    @Test
    void searchFlights_multipleFlightsWithTicketClass_returnsAll() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(1, 2, LocalDateTime.now(), 2, 1);
        Flight flight2 = new Flight();
        flight2.setFlightId(2);
        List<Flight> flights = Arrays.asList(validFlight, flight2);
        List<FlightDto> expectedDtos = Arrays.asList(validFlightDto, new FlightDto());

        when(flightRepository.findFlightsWithTicketClass(1, 2, criteria.getDepartureDate(), 1, 2))
            .thenReturn(flights);
        when(flightMapper.toDtoList(flights)).thenReturn(expectedDtos);

        List<FlightDto> result = flightService.searchFlights(criteria);

        assertEquals(2, result.size());
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
