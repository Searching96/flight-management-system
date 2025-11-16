package com.flightmanagement.service;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.mapper.FlightTicketClassMapper;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.repository.FlightTicketClassRepository;
import com.flightmanagement.repository.TicketClassRepository;
import com.flightmanagement.service.impl.FlightTicketClassServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightTicketClassServiceTest {

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

    private FlightTicketClassDto validDto;
    private FlightTicketClass validEntity;
    private Flight validFlight;
    private TicketClass validTicketClass;

    @BeforeEach
    void setUp() {
        validDto = new FlightTicketClassDto();
        validDto.setFlightId(1);
        validDto.setTicketClassId(1);
        validDto.setSpecifiedFare(new BigDecimal("150.00"));
        validDto.setTicketQuantity(100);
        validDto.setRemainingTicketQuantity(100);

        validFlight = new Flight();
        validFlight.setFlightId(1);
        validFlight.setFlightCode("FL123");

        validTicketClass = new TicketClass();
        validTicketClass.setTicketClassId(1);
        validTicketClass.setTicketClassName("Economy");

        validEntity = new FlightTicketClass();
        validEntity.setFlightId(1);
        validEntity.setTicketClassId(1);
        validEntity.setSpecifiedFare(new BigDecimal("150.00"));
        validEntity.setTicketQuantity(100);
        validEntity.setRemainingTicketQuantity(100);
        validEntity.setFlight(validFlight);
        validEntity.setTicketClass(validTicketClass);
    }

    // ==================== createFlightTicketClass Tests ====================

    @Tag("createFlightTicketClass")
    @Test
    void createFlightTicketClass_validDto_success() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(validDto);

        FlightTicketClassDto result = flightTicketClassService.createFlightTicketClass(validDto);

        assertNotNull(result);
        assertEquals(validDto.getFlightId(), result.getFlightId());
        assertEquals(validDto.getTicketClassId(), result.getTicketClassId());
        verify(flightTicketClassRepository).save(any(FlightTicketClass.class));
    }

    @Tag("createFlightTicketClass")
    @Test
    void createFlightTicketClass_setsDeletedAtToNull() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(validDto);

        flightTicketClassService.createFlightTicketClass(validDto);

        verify(flightTicketClassRepository).save(argThat(ftc -> ftc.getDeletedAt() == null));
    }

    @Tag("createFlightTicketClass")
    @Test
    void createFlightTicketClass_flightNotFound_throwsException() {
        when(flightRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.createFlightTicketClass(validDto)
        );

        assertTrue(exception.getMessage().contains("Flight not found"));
        assertTrue(exception.getMessage().contains("id: 1"));
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Tag("createFlightTicketClass")
    @Test
    void createFlightTicketClass_ticketClassNotFound_throwsException() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.createFlightTicketClass(validDto)
        );

        assertTrue(exception.getMessage().contains("TicketClass not found"));
        assertTrue(exception.getMessage().contains("id: 1"));
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Tag("createFlightTicketClass")
    @Test
    void createFlightTicketClass_nullFlightId_skipsFlightSet() {
        validDto.setFlightId(null);
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(validDto);

        FlightTicketClassDto result = flightTicketClassService.createFlightTicketClass(validDto);

        assertNotNull(result);
        verify(flightRepository, never()).findById(any());
        verify(flightTicketClassRepository).save(argThat(ftc -> ftc.getFlight() == null));
    }

    @Tag("createFlightTicketClass")
    @Test
    void createFlightTicketClass_nullTicketClassId_skipsTicketClassSet() {
        validDto.setTicketClassId(null);
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(validDto);

        FlightTicketClassDto result = flightTicketClassService.createFlightTicketClass(validDto);

        assertNotNull(result);
        verify(ticketClassRepository, never()).findById(any());
        verify(flightTicketClassRepository).save(argThat(ftc -> ftc.getTicketClass() == null));
    }

    @Tag("createFlightTicketClass")
    @Test
    void createFlightTicketClass_setsAllFieldsCorrectly() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
        when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(validDto);

        flightTicketClassService.createFlightTicketClass(validDto);

        verify(flightTicketClassRepository).save(argThat(ftc ->
            ftc.getSpecifiedFare().equals(new BigDecimal("150.00")) &&
            ftc.getTicketQuantity() == 100 &&
            ftc.getRemainingTicketQuantity() == 100 &&
            ftc.getFlightId() == 1 &&
            ftc.getTicketClassId() == 1
        ));
    }

    // ==================== updateFlightTicketClass Tests ====================

    @Tag("updateFlightTicketClass")
    @Test
    void updateFlightTicketClass_validUpdate_success() {
        FlightTicketClassDto updateDto = new FlightTicketClassDto();
        updateDto.setSpecifiedFare(new BigDecimal("200.00"));
        updateDto.setTicketQuantity(150);
        updateDto.setRemainingTicketQuantity(120);

        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        FlightTicketClassDto result = flightTicketClassService.updateFlightTicketClass(1, 1, updateDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), validEntity.getSpecifiedFare());
        assertEquals(150, validEntity.getTicketQuantity());
        assertEquals(120, validEntity.getRemainingTicketQuantity());
        verify(flightTicketClassRepository).save(validEntity);
    }

    @Tag("updateFlightTicketClass")
    @Test
    void updateFlightTicketClass_notFound_throwsException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.updateFlightTicketClass(1, 1, validDto)
        );

        assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
        assertTrue(exception.getMessage().contains("flight: 1"));
        assertTrue(exception.getMessage().contains("class: 1"));
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Tag("updateFlightTicketClass")
    @Test
    void updateFlightTicketClass_updatesOnlySpecifiedFare() {
        FlightTicketClassDto updateDto = new FlightTicketClassDto();
        updateDto.setSpecifiedFare(new BigDecimal("250.00"));
        updateDto.setTicketQuantity(100);
        updateDto.setRemainingTicketQuantity(100);

        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        flightTicketClassService.updateFlightTicketClass(1, 1, updateDto);

        assertEquals(new BigDecimal("250.00"), validEntity.getSpecifiedFare());
    }

    @Tag("updateFlightTicketClass")
    @Test
    void updateFlightTicketClass_updatesOnlyTicketQuantity() {
        FlightTicketClassDto updateDto = new FlightTicketClassDto();
        updateDto.setSpecifiedFare(new BigDecimal("150.00"));
        updateDto.setTicketQuantity(200);
        updateDto.setRemainingTicketQuantity(100);

        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        flightTicketClassService.updateFlightTicketClass(1, 1, updateDto);

        assertEquals(200, validEntity.getTicketQuantity());
    }

    @Tag("updateFlightTicketClass")
    @Test
    void updateFlightTicketClass_updatesOnlyRemainingTickets() {
        FlightTicketClassDto updateDto = new FlightTicketClassDto();
        updateDto.setSpecifiedFare(new BigDecimal("150.00"));
        updateDto.setTicketQuantity(100);
        updateDto.setRemainingTicketQuantity(50);

        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        flightTicketClassService.updateFlightTicketClass(1, 1, updateDto);

        assertEquals(50, validEntity.getRemainingTicketQuantity());
    }

    @Tag("updateFlightTicketClass")
    @Test
    void updateFlightTicketClass_differentFlightAndClassIds_throwsException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(999, 888))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.updateFlightTicketClass(999, 888, validDto)
        );

        assertTrue(exception.getMessage().contains("flight: 999"));
        assertTrue(exception.getMessage().contains("class: 888"));
    }

    @Tag("updateFlightTicketClass")
    @Test
    void updateFlightTicketClass_updatesAllFieldsSimultaneously() {
        FlightTicketClassDto updateDto = new FlightTicketClassDto();
        updateDto.setSpecifiedFare(new BigDecimal("300.00"));
        updateDto.setTicketQuantity(250);
        updateDto.setRemainingTicketQuantity(180);

        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);
        when(flightTicketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        flightTicketClassService.updateFlightTicketClass(1, 1, updateDto);

        assertEquals(new BigDecimal("300.00"), validEntity.getSpecifiedFare());
        assertEquals(250, validEntity.getTicketQuantity());
        assertEquals(180, validEntity.getRemainingTicketQuantity());
    }

    // ==================== deleteFlightTicketClass Tests ====================

    @Tag("deleteFlightTicketClass")
    @Test
    void deleteFlightTicketClass_validIds_success() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);

        flightTicketClassService.deleteFlightTicketClass(1, 1);

        assertNotNull(validEntity.getDeletedAt());
        verify(flightTicketClassRepository).save(validEntity);
    }

    @Tag("deleteFlightTicketClass")
    @Test
    void deleteFlightTicketClass_notFound_throwsException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.deleteFlightTicketClass(1, 1)
        );

        assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
        assertTrue(exception.getMessage().contains("flight: 1"));
        assertTrue(exception.getMessage().contains("class: 1"));
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Tag("deleteFlightTicketClass")
    @Test
    void deleteFlightTicketClass_setsDeletedAtTimestamp() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);

        LocalDateTime beforeDelete = LocalDateTime.now();
        flightTicketClassService.deleteFlightTicketClass(1, 1);
        LocalDateTime afterDelete = LocalDateTime.now();

        assertNotNull(validEntity.getDeletedAt());
        assertTrue(validEntity.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
        assertTrue(validEntity.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
    }

    @Tag("deleteFlightTicketClass")
    @Test
    void deleteFlightTicketClass_preservesOtherFields() {
        validEntity.setSpecifiedFare(new BigDecimal("150.00"));
        validEntity.setTicketQuantity(100);

        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);

        flightTicketClassService.deleteFlightTicketClass(1, 1);

        assertNotNull(validEntity.getDeletedAt());
        assertEquals(new BigDecimal("150.00"), validEntity.getSpecifiedFare());
        assertEquals(100, validEntity.getTicketQuantity());
    }

    @Tag("deleteFlightTicketClass")
    @Test
    void deleteFlightTicketClass_differentIds_throwsException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(999, 888))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.deleteFlightTicketClass(999, 888)
        );

        assertTrue(exception.getMessage().contains("flight: 999"));
        assertTrue(exception.getMessage().contains("class: 888"));
    }

    @Tag("deleteFlightTicketClass")
    @Test
    void deleteFlightTicketClass_repositoryThrowsException_propagatesException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class)))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.deleteFlightTicketClass(1, 1)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    // ==================== updateRemainingTickets Tests ====================

    @Tag("updateRemainingTickets")
    @Test
    void updateRemainingTickets_validQuantity_success() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);

        flightTicketClassService.updateRemainingTickets(1, 1, 10);

        assertEquals(90, validEntity.getRemainingTicketQuantity());
        verify(flightTicketClassRepository).save(validEntity);
    }

    @Tag("updateRemainingTickets")
    @Test
    void updateRemainingTickets_notFound_throwsException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.updateRemainingTickets(1, 1, 10)
        );

        assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
        assertTrue(exception.getMessage().contains("flight: 1"));
        assertTrue(exception.getMessage().contains("class: 1"));
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Tag("updateRemainingTickets")
    @Test
    void updateRemainingTickets_notEnoughTickets_throwsException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.updateRemainingTickets(1, 1, 150)
        );

        assertTrue(exception.getMessage().contains("Not enough tickets available"));
        assertTrue(exception.getMessage().contains("Requested: 150"));
        assertTrue(exception.getMessage().contains("Available: 100"));
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Tag("updateRemainingTickets")
    @Test
    void updateRemainingTickets_exactRemainingQuantity_success() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);

        flightTicketClassService.updateRemainingTickets(1, 1, 100);

        assertEquals(0, validEntity.getRemainingTicketQuantity());
        verify(flightTicketClassRepository).save(validEntity);
    }

    @Tag("updateRemainingTickets")
    @Test
    void updateRemainingTickets_oneMoreThanAvailable_throwsException() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.updateRemainingTickets(1, 1, 101)
        );

        assertTrue(exception.getMessage().contains("Not enough tickets available"));
        assertTrue(exception.getMessage().contains("Requested: 101"));
        verify(flightTicketClassRepository, never()).save(any());
    }

    @Tag("updateRemainingTickets")
    @Test
    void updateRemainingTickets_negativeQuantity_calculatesCorrectly() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);

        flightTicketClassService.updateRemainingTickets(1, 1, -10);

        assertEquals(110, validEntity.getRemainingTicketQuantity());
        verify(flightTicketClassRepository).save(validEntity);
    }

    @Tag("updateRemainingTickets")
    @Test
    void updateRemainingTickets_zeroQuantity_noChange() {
        when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
            .thenReturn(Optional.of(validEntity));
        when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(validEntity);

        flightTicketClassService.updateRemainingTickets(1, 1, 0);

        assertEquals(100, validEntity.getRemainingTicketQuantity());
        verify(flightTicketClassRepository).save(validEntity);
    }

    // ==================== calculateOccupiedSeatsByFlightIdAndTicketClassId Tests ====================

    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    @Test
    void calculateOccupiedSeatsByFlightIdAndTicketClassId_validIds_returnsOccupiedSeats() {
        when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
            .thenReturn(50);

        Integer result = flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);

        assertEquals(50, result);
        verify(flightTicketClassRepository).calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);
    }

    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    @Test
    void calculateOccupiedSeatsByFlightIdAndTicketClassId_nullResult_throwsException() {
        when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
            .thenReturn(null);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1)
        );

        assertTrue(exception.getMessage().contains("No occupied seats found"));
        assertTrue(exception.getMessage().contains("flightId: 1"));
        assertTrue(exception.getMessage().contains("ticketClassId: 1"));
    }

    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    @Test
    void calculateOccupiedSeatsByFlightIdAndTicketClassId_zeroOccupiedSeats_returnsZero() {
        when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
            .thenReturn(0);

        Integer result = flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);

        assertEquals(0, result);
    }

    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    @Test
    void calculateOccupiedSeatsByFlightIdAndTicketClassId_allSeatsOccupied_returnsFullCount() {
        when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
            .thenReturn(100);

        Integer result = flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);

        assertEquals(100, result);
    }

    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    @Test
    void calculateOccupiedSeatsByFlightIdAndTicketClassId_differentIds_calculatesCorrectly() {
        when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(2, 3))
            .thenReturn(75);

        Integer result = flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(2, 3);

        assertEquals(75, result);
        verify(flightTicketClassRepository).calculateOccupiedSeatsByFlightIdAndTicketClassId(2, 3);
    }

    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    @Test
    void calculateOccupiedSeatsByFlightIdAndTicketClassId_repositoryThrowsException_propagatesException() {
        when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    @Test
    void calculateOccupiedSeatsByFlightIdAndTicketClassId_printsDebugInfo() {
        when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
            .thenReturn(42);

        flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);

        // This test verifies the method executes successfully with debug output
        verify(flightTicketClassRepository).calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);
    }
}
