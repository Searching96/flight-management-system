package com.flightmanagement.service;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.mapper.FlightDetailMapper;
import com.flightmanagement.repository.FlightDetailRepository;
import com.flightmanagement.service.impl.FlightDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightDetailServiceTest {

    @Mock
    private FlightDetailRepository flightDetailRepository;

    @Mock
    private FlightDetailMapper flightDetailMapper;

    @InjectMocks
    private FlightDetailServiceImpl flightDetailService;

    private FlightDetail existingFlightDetail;
    private FlightDetailDto updateFlightDetailDto;
    private FlightDetailDto expectedResponseDto;
    private LocalDateTime testArrivalTime;
    private Integer testLayoverDuration;

    @BeforeEach
    void setUp() {
        testArrivalTime = LocalDateTime.of(2024, 1, 15, 14, 30);
        testLayoverDuration = 120; // 2 hours

        // Setup existing flight detail entity
        existingFlightDetail = new FlightDetail();
        existingFlightDetail.setFlightId(1);
        existingFlightDetail.setMediumAirportId(2);
        existingFlightDetail.setArrivalTime(LocalDateTime.of(2024, 1, 15, 12, 0)); // Original time
        existingFlightDetail.setLayoverDuration(90); // Original duration
        existingFlightDetail.setDeletedAt(null);

        // Setup update DTO
        updateFlightDetailDto = new FlightDetailDto();
        updateFlightDetailDto.setFlightId(1);
        updateFlightDetailDto.setMediumAirportId(2);
        updateFlightDetailDto.setArrivalTime(testArrivalTime);
        updateFlightDetailDto.setLayoverDuration(testLayoverDuration);
        updateFlightDetailDto.setMediumAirportName("Test Airport");
        updateFlightDetailDto.setMediumCityName("Test City");

        // Setup expected response DTO
        expectedResponseDto = new FlightDetailDto();
        expectedResponseDto.setFlightId(1);
        expectedResponseDto.setMediumAirportId(2);
        expectedResponseDto.setArrivalTime(testArrivalTime);
        expectedResponseDto.setLayoverDuration(testLayoverDuration);
        expectedResponseDto.setMediumAirportName("Test Airport");
        expectedResponseDto.setMediumCityName("Test City");
    }

    @Test
    void testUpdateFlightDetail_Success() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;

        FlightDetail updatedFlightDetail = new FlightDetail();
        updatedFlightDetail.setFlightId(flightId);
        updatedFlightDetail.setMediumAirportId(mediumAirportId);
        updatedFlightDetail.setArrivalTime(testArrivalTime);
        updatedFlightDetail.setLayoverDuration(testLayoverDuration);
        updatedFlightDetail.setDeletedAt(null);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
        when(flightDetailRepository.save(existingFlightDetail)).thenReturn(updatedFlightDetail);
        when(flightDetailMapper.toDto(updatedFlightDetail)).thenReturn(expectedResponseDto);

        // Act
        FlightDetailDto result = flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateFlightDetailDto);

        // Assert
        assertNotNull(result);
        assertEquals(flightId, result.getFlightId());
        assertEquals(mediumAirportId, result.getMediumAirportId());
        assertEquals(testArrivalTime, result.getArrivalTime());
        assertEquals(testLayoverDuration, result.getLayoverDuration());
        assertEquals("Test Airport", result.getMediumAirportName());
        assertEquals("Test City", result.getMediumCityName());

        // Verify that the existing entity was updated with new values
        assertEquals(testArrivalTime, existingFlightDetail.getArrivalTime());
        assertEquals(testLayoverDuration, existingFlightDetail.getLayoverDuration());

        // Verify method calls
        verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
        verify(flightDetailRepository).save(existingFlightDetail);
        verify(flightDetailMapper).toDto(updatedFlightDetail);
    }

    @Test
    void testUpdateFlightDetail_FlightDetailNotFound_ThrowsRuntimeException() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateFlightDetailDto);
        });

        assertEquals("FlightDetail not found for flight: 1 and airport: 2", exception.getMessage());

        // Verify that save and mapper methods are never called
        verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
        verify(flightDetailRepository, never()).save(any(FlightDetail.class));
        verify(flightDetailMapper, never()).toDto(any(FlightDetail.class));
    }

    @Test
    void testUpdateFlightDetail_WithNullArrivalTime_Success() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;
        updateFlightDetailDto.setArrivalTime(null);

        FlightDetail updatedFlightDetail = new FlightDetail();
        updatedFlightDetail.setFlightId(flightId);
        updatedFlightDetail.setMediumAirportId(mediumAirportId);
        updatedFlightDetail.setArrivalTime(null);
        updatedFlightDetail.setLayoverDuration(testLayoverDuration);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
        when(flightDetailRepository.save(existingFlightDetail)).thenReturn(updatedFlightDetail);
        when(flightDetailMapper.toDto(updatedFlightDetail)).thenReturn(expectedResponseDto);

        // Act
        FlightDetailDto result = flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateFlightDetailDto);

        // Assert
        assertNotNull(result);
        assertNull(existingFlightDetail.getArrivalTime());
        assertEquals(testLayoverDuration, existingFlightDetail.getLayoverDuration());

        verify(flightDetailRepository).save(existingFlightDetail);
        verify(flightDetailMapper).toDto(updatedFlightDetail);
    }

    @Test
    void testUpdateFlightDetail_WithNullLayoverDuration_Success() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;
        updateFlightDetailDto.setLayoverDuration(null);

        FlightDetail updatedFlightDetail = new FlightDetail();
        updatedFlightDetail.setFlightId(flightId);
        updatedFlightDetail.setMediumAirportId(mediumAirportId);
        updatedFlightDetail.setArrivalTime(testArrivalTime);
        updatedFlightDetail.setLayoverDuration(null);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
        when(flightDetailRepository.save(existingFlightDetail)).thenReturn(updatedFlightDetail);
        when(flightDetailMapper.toDto(updatedFlightDetail)).thenReturn(expectedResponseDto);

        // Act
        FlightDetailDto result = flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateFlightDetailDto);

        // Assert
        assertNotNull(result);
        assertEquals(testArrivalTime, existingFlightDetail.getArrivalTime());
        assertNull(existingFlightDetail.getLayoverDuration());

        verify(flightDetailRepository).save(existingFlightDetail);
        verify(flightDetailMapper).toDto(updatedFlightDetail);
    }

    @Test
    void testUpdateFlightDetail_RepositoryThrowsException_PropagatesException() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
        when(flightDetailRepository.save(existingFlightDetail))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateFlightDetailDto);
        });

        assertEquals("Database error", exception.getMessage());

        verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
        verify(flightDetailRepository).save(existingFlightDetail);
        verify(flightDetailMapper, never()).toDto(any(FlightDetail.class));
    }

    @Test
    void testUpdateFlightDetail_MapperThrowsException_PropagatesException() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;

        FlightDetail updatedFlightDetail = new FlightDetail();
        updatedFlightDetail.setFlightId(flightId);
        updatedFlightDetail.setMediumAirportId(mediumAirportId);
        updatedFlightDetail.setArrivalTime(testArrivalTime);
        updatedFlightDetail.setLayoverDuration(testLayoverDuration);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
        when(flightDetailRepository.save(existingFlightDetail)).thenReturn(updatedFlightDetail);
        when(flightDetailMapper.toDto(updatedFlightDetail))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateFlightDetailDto);
        });

        assertEquals("Mapping error", exception.getMessage());

        verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
        verify(flightDetailRepository).save(existingFlightDetail);
        verify(flightDetailMapper).toDto(updatedFlightDetail);
    }

    @Test
    void testUpdateFlightDetail_ValidatesEntityUpdate() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;
        LocalDateTime newArrivalTime = LocalDateTime.of(2024, 2, 1, 16, 45);
        Integer newLayoverDuration = 180;

        updateFlightDetailDto.setArrivalTime(newArrivalTime);
        updateFlightDetailDto.setLayoverDuration(newLayoverDuration);

        FlightDetail updatedFlightDetail = new FlightDetail();
        updatedFlightDetail.setFlightId(flightId);
        updatedFlightDetail.setMediumAirportId(mediumAirportId);
        updatedFlightDetail.setArrivalTime(newArrivalTime);
        updatedFlightDetail.setLayoverDuration(newLayoverDuration);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
        when(flightDetailRepository.save(existingFlightDetail)).thenReturn(updatedFlightDetail);
        when(flightDetailMapper.toDto(updatedFlightDetail)).thenReturn(expectedResponseDto);

        // Act
        flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateFlightDetailDto);

        // Assert - Verify the entity was properly updated before saving
        assertEquals(newArrivalTime, existingFlightDetail.getArrivalTime());
        assertEquals(newLayoverDuration, existingFlightDetail.getLayoverDuration());
        assertEquals(flightId, existingFlightDetail.getFlightId()); // Should remain unchanged
        assertEquals(mediumAirportId, existingFlightDetail.getMediumAirportId()); // Should remain unchanged

        verify(flightDetailRepository).save(existingFlightDetail);
    }
}
