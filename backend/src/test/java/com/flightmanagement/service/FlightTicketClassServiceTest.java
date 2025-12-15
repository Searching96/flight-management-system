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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("FlightTicketClassService Tests - Full Path Coverage")
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

    // ====================================================================
    // NESTED TEST CLASS: createFlightTicketClass - Full Path Coverage
    // ====================================================================

    @Nested
    @DisplayName("CreateFlightTicketClass Tests - Full Path Coverage")
    @Tag("createFlightTicketClass")
    class CreateFlightTicketClassTests {

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
        
        @Test
        @DisplayName("TC1: Create with missing input (null FlightId or TicketClassId) - Throws IllegalArgumentException")
        void createFlightTicketClass_MissingInput_ThrowsIllegalArgumentException() {
            // Arrange - Test with null FlightId
            validDto.setFlightId(null);
            validDto.setTicketClassId(1);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> flightTicketClassService.createFlightTicketClass(validDto)
            );

            assertTrue(exception.getMessage().contains("FlightId and TicketClassId are required"));
            verify(flightRepository, never()).findById(anyInt());
            verify(ticketClassRepository, never()).findById(anyInt());
            verify(flightTicketClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC2: Create with duplicate data (already added) - Throws IllegalArgumentException")
        void createFlightTicketClass_DuplicateData_ThrowsIllegalArgumentException() {
            // Arrange - Setup valid IDs
            validDto.setFlightId(1);
            validDto.setTicketClassId(1);
            
            FlightTicketClass existingEntity = new FlightTicketClass();
            existingEntity.setFlightId(1);
            existingEntity.setTicketClassId(1);

            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(existingEntity));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> flightTicketClassService.createFlightTicketClass(validDto)
            );

            assertTrue(exception.getMessage().contains("Already added"));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightRepository, never()).findById(anyInt());
            verify(flightTicketClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC3: Create with Flight not found - Throws RuntimeException")
        void createFlightTicketClass_FlightNotFound_ThrowsRuntimeException() {
            // Arrange - Valid IDs, but Flight doesn't exist
            validDto.setFlightId(1);
            validDto.setTicketClassId(1);

            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.empty());
            when(flightRepository.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightTicketClassService.createFlightTicketClass(validDto)
            );

            assertTrue(exception.getMessage().contains("Flight not found"));
            assertTrue(exception.getMessage().contains("id: 1"));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightRepository).findById(1);
            verify(ticketClassRepository, never()).findById(anyInt());
            verify(flightTicketClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC4: Create with TicketClass not found - Throws RuntimeException")
        void createFlightTicketClass_TicketClassNotFound_ThrowsRuntimeException() {
            // Arrange - Valid Flight, but TicketClass doesn't exist
            validDto.setFlightId(1);
            validDto.setTicketClassId(1);

            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.empty());
            when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
            when(ticketClassRepository.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightTicketClassService.createFlightTicketClass(validDto)
            );

            assertTrue(exception.getMessage().contains("TicketClass not found"));
            assertTrue(exception.getMessage().contains("id: 1"));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightRepository).findById(1);
            verify(ticketClassRepository).findById(1);
            verify(flightTicketClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC5: Create with valid data (happy path) - Success")
        void createFlightTicketClass_ValidData_Success() {
            // Arrange - Setup all valid conditions
            validDto.setFlightId(1);
            validDto.setTicketClassId(1);

            FlightTicketClass savedEntity = new FlightTicketClass();
            savedEntity.setFlightId(1);
            savedEntity.setTicketClassId(1);
            savedEntity.setFlight(validFlight);
            savedEntity.setTicketClass(validTicketClass);
            savedEntity.setSpecifiedFare(new BigDecimal("150.00"));
            savedEntity.setTicketQuantity(100);
            savedEntity.setRemainingTicketQuantity(100);

            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.empty());
            when(flightRepository.findById(1)).thenReturn(Optional.of(validFlight));
            when(ticketClassRepository.findById(1)).thenReturn(Optional.of(validTicketClass));
            when(flightTicketClassRepository.save(any(FlightTicketClass.class))).thenReturn(savedEntity);
            when(flightTicketClassMapper.toDto(savedEntity)).thenReturn(validDto);

            // Act
            FlightTicketClassDto result = flightTicketClassService.createFlightTicketClass(validDto);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getFlightId());
            assertEquals(1, result.getTicketClassId());
            assertEquals(new BigDecimal("150.00"), result.getSpecifiedFare());
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightRepository).findById(1);
            verify(ticketClassRepository).findById(1);
            verify(flightTicketClassRepository).save(any(FlightTicketClass.class));
            verify(flightTicketClassMapper).toDto(savedEntity);
        }
    }

    // ====================================================================
    // NESTED TEST CLASS: updateFlightTicketClass - Full Path Coverage
    // ====================================================================

    @Nested
    @DisplayName("UpdateFlightTicketClass Tests")
    @Tag("updateFlightTicketClass")
    class UpdateFlightTicketClassTests {

        private FlightTicketClassDto updateDto;
        private FlightTicketClass validEntity;

        @BeforeEach
        void setUp() {
            validEntity = new FlightTicketClass();
            validEntity.setFlightId(1);
            validEntity.setTicketClassId(1);
            validEntity.setSpecifiedFare(new BigDecimal("150.00"));
            validEntity.setTicketQuantity(100);
            validEntity.setRemainingTicketQuantity(100);

            updateDto = new FlightTicketClassDto();
            updateDto.setFlightId(1);
            updateDto.setTicketClassId(1);
            updateDto.setSpecifiedFare(new BigDecimal("200.00"));
            updateDto.setTicketQuantity(150);
            updateDto.setRemainingTicketQuantity(120);
        }

        @Test
        @DisplayName("TC2: Happy Path - Update existing FlightTicketClass then save and return DTO")
        void updateFlightTicketClass_HappyPath_SaveAndReturnDto() {
            // Arrange
            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(validEntity));
            when(flightTicketClassRepository.save(validEntity)).thenReturn(validEntity);
            when(flightTicketClassMapper.toDto(validEntity)).thenReturn(updateDto);

            // Act
            FlightTicketClassDto result = flightTicketClassService.updateFlightTicketClass(1, 1, updateDto);

            // Assert
            assertNotNull(result);
            assertEquals(new BigDecimal("200.00"), result.getSpecifiedFare());
            assertEquals(150, result.getTicketQuantity());
            assertEquals(120, result.getRemainingTicketQuantity());
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightTicketClassRepository).save(validEntity);
            verify(flightTicketClassMapper).toDto(validEntity);
        }

        @Test
        @DisplayName("TC1: Entity Not Found - findBy... returns Empty then orElseThrow triggers RuntimeException")
        void updateFlightTicketClass_EntityNotFound_ThrowsRuntimeException() {
            // Arrange
            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightTicketClassService.updateFlightTicketClass(1, 1, updateDto)
            );

            assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
            assertTrue(exception.getMessage().contains("flight: 1"));
            assertTrue(exception.getMessage().contains("class: 1"));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightTicketClassRepository, never()).save(any());
            verify(flightTicketClassMapper, never()).toDto(any());
        }
    }

    // ====================================================================
    // NESTED TEST CLASS: deleteFlightTicketClass - Full Path Coverage
    // ====================================================================

    @Nested
    @DisplayName("DeleteFlightTicketClass Tests")
    @Tag("deleteFlightTicketClass")
    class DeleteFlightTicketClassTests {

        private FlightTicketClass validEntity;

        @BeforeEach
        void setUp() {
            validEntity = new FlightTicketClass();
            validEntity.setFlightId(1);
            validEntity.setTicketClassId(1);
            validEntity.setSpecifiedFare(new BigDecimal("150.00"));
            validEntity.setTicketQuantity(100);
            validEntity.setRemainingTicketQuantity(100);
        }

        @Test
        @DisplayName("TC2: Happy Path - Set deletedAt then save (Void success)")
        void deleteFlightTicketClass_HappyPath_SetDeletedAtAndSave_Void() {
            // Arrange
            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(validEntity));
            when(flightTicketClassRepository.save(validEntity)).thenReturn(validEntity);

            // Act
            LocalDateTime beforeDelete = LocalDateTime.now();
            flightTicketClassService.deleteFlightTicketClass(1, 1);
            LocalDateTime afterDelete = LocalDateTime.now();

            // Assert
            assertNotNull(validEntity.getDeletedAt());
            assertTrue(validEntity.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
            assertTrue(validEntity.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightTicketClassRepository).save(validEntity);
        }

        @Test
        @DisplayName("TC1: Entity Not Found - findBy... returns Empty then orElseThrow triggers RuntimeException")
        void deleteFlightTicketClass_EntityNotFound_ThrowsRuntimeException() {
            // Arrange
            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightTicketClassService.deleteFlightTicketClass(1, 1)
            );

            assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
            assertTrue(exception.getMessage().contains("flight: 1"));
            assertTrue(exception.getMessage().contains("class: 1"));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightTicketClassRepository, never()).save(any());
        }
    }

    // ====================================================================
    // NESTED TEST CLASS: updateRemainingTickets - Full Path Coverage
    // ====================================================================

    @Nested
    @DisplayName("UpdateRemainingTickets Tests")
    @Tag("updateRemainingTickets")
    class UpdateRemainingTicketsTests {

        private FlightTicketClass validEntity;

        @BeforeEach
        void setUp() {
            validEntity = new FlightTicketClass();
            validEntity.setFlightId(1);
            validEntity.setTicketClassId(1);
            validEntity.setSpecifiedFare(new BigDecimal("150.00"));
            validEntity.setTicketQuantity(100);
            validEntity.setRemainingTicketQuantity(10);
        }

        @Test
        @DisplayName("TC1: Update non-existent FlightTicketClass - RuntimeException")
        void updateRemainingTickets_NotFound_ThrowsException() {
            // Arrange
            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightTicketClassService.updateRemainingTickets(1, 1, 5)
            );

            assertTrue(exception.getMessage().contains("FlightTicketClass not found"));
            assertTrue(exception.getMessage().contains("flight: 1"));
            assertTrue(exception.getMessage().contains("class: 1"));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightTicketClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC2: Update with insufficient tickets - RuntimeException")
        void updateRemainingTickets_NotEnoughTickets_ThrowsException() {
            // Arrange
            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(validEntity));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightTicketClassService.updateRemainingTickets(1, 1, 11)
            );

            assertTrue(exception.getMessage().contains("Not enough tickets available"));
            assertTrue(exception.getMessage().contains("Requested: 11"));
            assertTrue(exception.getMessage().contains("Available: 10"));
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightTicketClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC3: Update with sufficient tickets - Success")
        void updateRemainingTickets_SufficientTickets_Success() {
            // Arrange
            when(flightTicketClassRepository.findByFlightIdAndTicketClassId(1, 1))
                .thenReturn(Optional.of(validEntity));
            when(flightTicketClassRepository.save(validEntity)).thenReturn(validEntity);

            // Act
            flightTicketClassService.updateRemainingTickets(1, 1, 4);

            // Assert
            assertEquals(6, validEntity.getRemainingTicketQuantity());
            verify(flightTicketClassRepository).findByFlightIdAndTicketClassId(1, 1);
            verify(flightTicketClassRepository).save(validEntity);
        }
    }

    // ====================================================================
    // NESTED TEST CLASS: calculateOccupiedSeatsByFlightIdAndTicketClassId
    // ====================================================================

    @Nested
    @DisplayName("CalculateOccupiedSeatsByFlightIdAndTicketClassId Tests")
    @Tag("calculateOccupiedSeatsByFlightIdAndTicketClassId")
    class CalculateOccupiedSeatsTests {

        @Test
        @DisplayName("TC1: Calculate with valid result from repository - Returns count")
        void calculateOccupiedSeats_ValidResult_ReturnsCount() {
            // Arrange
            when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
                .thenReturn(50);

            // Act
            Integer result = flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);

            // Assert
            assertEquals(50, result);
            verify(flightTicketClassRepository).calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);
        }

        @Test
        @DisplayName("TC2: Calculate with null result from repository - RuntimeException")
        void calculateOccupiedSeats_NullResult_ThrowsException() {
            // Arrange
            when(flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1))
                .thenReturn(null);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightTicketClassService.calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1)
            );

            assertTrue(exception.getMessage().contains("No occupied seats found"));
            assertTrue(exception.getMessage().contains("flightId: 1"));
            assertTrue(exception.getMessage().contains("ticketClassId: 1"));
            verify(flightTicketClassRepository).calculateOccupiedSeatsByFlightIdAndTicketClassId(1, 1);
        }
    }
}