package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.mapper.FlightTicketClassMapper;
import com.flightmanagement.repository.FlightTicketClassRepository;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.repository.TicketClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightTicketClassServiceImplTest {

    @Mock
    private FlightTicketClassRepository flightTicketClassRepository;

    @Mock
    private FlightTicketClassMapper flightTicketClassMapper;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private TicketClassRepository ticketClassRepository;

    @InjectMocks
    private FlightTicketClassServiceImpl flightTicketClassService;

    private FlightTicketClass testFlightTicketClass;
    private FlightTicketClassDto testFlightTicketClassDto;
    private Flight testFlight;
    private TicketClass testTicketClass;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setFlightId(1);
        testFlight.setFlightCode("AA123");

        testTicketClass = new TicketClass();
        testTicketClass.setTicketClassId(1);
        testTicketClass.setTicketClassName("Economy");

        testFlightTicketClass = new FlightTicketClass();
        testFlightTicketClass.setFlightId(1);
        testFlightTicketClass.setTicketClassId(1);
        testFlightTicketClass.setTicketQuantity(100);
        testFlightTicketClass.setRemainingTicketQuantity(80);
        testFlightTicketClass.setSpecifiedFare(new BigDecimal("299.99"));
        testFlightTicketClass.setFlight(testFlight);
        testFlightTicketClass.setTicketClass(testTicketClass);

        testFlightTicketClassDto = new FlightTicketClassDto();
        testFlightTicketClassDto.setFlightId(1);
        testFlightTicketClassDto.setTicketClassId(1);
        testFlightTicketClassDto.setTicketQuantity(100);
        testFlightTicketClassDto.setRemainingTicketQuantity(80);
        testFlightTicketClassDto.setSpecifiedFare(new BigDecimal("299.99"));
        testFlightTicketClassDto.setTicketClassName("Economy");
    }

    @Test
    void getAllFlightTicketClasses_ShouldReturnDtoList() {
        // Arrange
        List<FlightTicketClass> entities = Arrays.asList(testFlightTicketClass);
        List<FlightTicketClassDto> expectedDtos = Arrays.asList(testFlightTicketClassDto);

        when(flightTicketClassRepository.findAllActive()).thenReturn(entities);
        when(flightTicketClassMapper.toDtoList(entities)).thenReturn(expectedDtos);

        // Act
        List<FlightTicketClassDto> result = flightTicketClassService.getAllFlightTicketClasses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFlightTicketClassDto.getFlightId(), result.get(0).getFlightId());
        verify(flightTicketClassRepository).findAllActive();
        verify(flightTicketClassMapper).toDtoList(entities);
    }

    @Test
    void getFlightTicketClassById_WhenExists_ShouldReturnDto() {
        // Arrange
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(testFlightTicketClass));
        when(flightTicketClassMapper.toDto(testFlightTicketClass)).thenReturn(testFlightTicketClassDto);

        // Act
        FlightTicketClassDto result = flightTicketClassService.getFlightTicketClassById(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(testFlightTicketClassDto.getFlightId(), result.getFlightId());
        assertEquals(testFlightTicketClassDto.getTicketClassId(), result.getTicketClassId());
        verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
        verify(flightTicketClassMapper).toDto(testFlightTicketClass);
    }

    @Test
    void getFlightTicketClassById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> flightTicketClassService.getFlightTicketClassById(1, 1));
        
        assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
        verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
        verify(flightTicketClassMapper, never()).toDto(any());
    }

    @Test
    void updateRemainingTickets_WithSufficientQuantity_ShouldUpdateSuccessfully() {
        // Arrange
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(testFlightTicketClass));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class)))
                .thenReturn(testFlightTicketClass);

        // Act
        flightTicketClassService.updateRemainingTickets(1, 1, 5);

        // Assert
        assertEquals(75, testFlightTicketClass.getRemainingTicketQuantity());
        verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
        verify(flightTicketClassRepository).save(testFlightTicketClass);
    }

    @Test
    void updateRemainingTickets_WithInsufficientQuantity_ShouldThrowException() {
        // Arrange
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(testFlightTicketClass));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> flightTicketClassService.updateRemainingTickets(1, 1, 100));
        
        assertTrue(exception.getMessage().contains("Not enough tickets available"));
        verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Test
    void createFlightTicketClass_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(testTicketClass));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class)))
                .thenReturn(testFlightTicketClass);
        when(flightTicketClassMapper.toDto(testFlightTicketClass))
                .thenReturn(testFlightTicketClassDto);

        // Act
        FlightTicketClassDto result = flightTicketClassService.createFlightTicketClass(testFlightTicketClassDto);

        // Assert
        assertNotNull(result);
        assertEquals(testFlightTicketClassDto.getFlightId(), result.getFlightId());
        verify(flightRepository).findById(1);
        verify(ticketClassRepository).findById(1);
        verify(flightTicketClassRepository).save(any(FlightTicketClass.class));
        verify(flightTicketClassMapper).toDto(testFlightTicketClass);
    }

    @Test
    void createFlightTicketClass_WithInvalidFlightId_ShouldThrowException() {
        // Arrange
        when(flightRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> flightTicketClassService.createFlightTicketClass(testFlightTicketClassDto));
        
        assertTrue(exception.getMessage().contains("Flight not found"));
        verify(flightRepository).findById(1);
        verify(ticketClassRepository, never()).findById(anyInt());
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Test
    void getFlightTicketClassesByFlightId_ShouldReturnFlightClasses() {
        // Arrange
        List<FlightTicketClass> entities = Arrays.asList(testFlightTicketClass);
        List<FlightTicketClassDto> expectedDtos = Arrays.asList(testFlightTicketClassDto);

        when(flightTicketClassRepository.findByFlightId(1)).thenReturn(entities);
        when(flightTicketClassMapper.toDtoList(entities)).thenReturn(expectedDtos);

        // Act
        List<FlightTicketClassDto> result = flightTicketClassService.getFlightTicketClassesByFlightId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFlightTicketClassDto.getFlightId(), result.get(0).getFlightId());
        verify(flightTicketClassRepository).findByFlightId(1);
        verify(flightTicketClassMapper).toDtoList(entities);
    }
}
