package com.flightmanagement.service;

import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.FlightSearchCriteria;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.mapper.FlightMapper;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightServiceImpl flightService;

    private FlightSearchCriteria searchCriteria;
    private List<Flight> mockFlights;
    private List<FlightDto> mockFlightDtos;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime departureDate = LocalDateTime.of(2024, 1, 15, 10, 0);

        searchCriteria = new FlightSearchCriteria();
        searchCriteria.setDepartureAirportId(1);
        searchCriteria.setArrivalAirportId(2);
        searchCriteria.setDepartureDate(departureDate);
        searchCriteria.setPassengerCount(2);

        Flight flight1 = new Flight();
        flight1.setFlightId(1);
        flight1.setFlightCode("FL001");
        flight1.setDepartureTime(departureDate);

        Flight flight2 = new Flight();
        flight2.setFlightId(2);
        flight2.setFlightCode("FL002");
        flight2.setDepartureTime(departureDate.plusHours(2));

        FlightDto flightDto1 = new FlightDto();
        flightDto1.setFlightId(1);
        flightDto1.setFlightCode("FL001");

        FlightDto flightDto2 = new FlightDto();
        flightDto2.setFlightId(2);
        flightDto2.setFlightCode("FL002");

        mockFlights = Arrays.asList(flight1, flight2);
        mockFlightDtos = Arrays.asList(flightDto1, flightDto2);
    }

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
    void testSearchFlights_RepositoryThrowsException_ThrowsRuntimeException() {
        // Arrange
        searchCriteria.setTicketClassId(1);

        when(flightRepository.findFlightsWithTicketClass(
            eq(1), eq(2), eq(searchCriteria.getDepartureDate()), eq(1), eq(2)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.searchFlights(searchCriteria);
        });

        assertEquals("Failed to search flights", exception.getMessage());
        verify(flightRepository).findFlightsWithTicketClass(1, 2, searchCriteria.getDepartureDate(), 1, 2);
        verify(flightMapper, never()).toDtoList(any());
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
}
