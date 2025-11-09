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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    // NEW TEST 1: Get all flight details - Success
    @Test
    void testGetAllFlightDetails_Success() {
        // Arrange
        FlightDetail flightDetail1 = new FlightDetail();
        flightDetail1.setFlightId(1);
        flightDetail1.setMediumAirportId(2);
        flightDetail1.setArrivalTime(testArrivalTime);
        flightDetail1.setLayoverDuration(90);
        flightDetail1.setDeletedAt(null);

        FlightDetail flightDetail2 = new FlightDetail();
        flightDetail2.setFlightId(2);
        flightDetail2.setMediumAirportId(3);
        flightDetail2.setArrivalTime(testArrivalTime.plusHours(2));
        flightDetail2.setLayoverDuration(120);
        flightDetail2.setDeletedAt(null);

        List<FlightDetail> flightDetails = Arrays.asList(flightDetail1, flightDetail2);

        FlightDetailDto dto1 = new FlightDetailDto();
        dto1.setFlightId(1);
        dto1.setMediumAirportId(2);

        FlightDetailDto dto2 = new FlightDetailDto();
        dto2.setFlightId(2);
        dto2.setMediumAirportId(3);

        List<FlightDetailDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(flightDetailRepository.findAllActive()).thenReturn(flightDetails);
        when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

        // Act
        List<FlightDetailDto> result = flightDetailService.getAllFlightDetails();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getFlightId());
        assertEquals(2, result.get(1).getFlightId());

        verify(flightDetailRepository).findAllActive();
        verify(flightDetailMapper).toDtoList(flightDetails);
    }

    // NEW TEST 2: Get flight details by flight ID - Success
    @Test
    void testGetFlightDetailsByFlightId_Success() {
        // Arrange
        Integer flightId = 1;

        FlightDetail flightDetail1 = new FlightDetail();
        flightDetail1.setFlightId(flightId);
        flightDetail1.setMediumAirportId(2);
        flightDetail1.setArrivalTime(testArrivalTime);
        flightDetail1.setLayoverDuration(90);

        FlightDetail flightDetail2 = new FlightDetail();
        flightDetail2.setFlightId(flightId);
        flightDetail2.setMediumAirportId(3);
        flightDetail2.setArrivalTime(testArrivalTime.plusHours(1));
        flightDetail2.setLayoverDuration(60);

        List<FlightDetail> flightDetails = Arrays.asList(flightDetail1, flightDetail2);

        FlightDetailDto dto1 = new FlightDetailDto();
        dto1.setFlightId(flightId);
        dto1.setMediumAirportId(2);

        FlightDetailDto dto2 = new FlightDetailDto();
        dto2.setFlightId(flightId);
        dto2.setMediumAirportId(3);

        List<FlightDetailDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(flightDetailRepository.findByFlightId(flightId)).thenReturn(flightDetails);
        when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

        // Act
        List<FlightDetailDto> result = flightDetailService.getFlightDetailsByFlightId(flightId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getMediumAirportId());
        assertEquals(3, result.get(1).getMediumAirportId());

        verify(flightDetailRepository).findByFlightId(flightId);
        verify(flightDetailMapper).toDtoList(flightDetails);
    }

    // NEW TEST 3: Create flight detail - Success
    @Test
    void testCreateFlightDetail_Success() {
        // Arrange
        FlightDetailDto newFlightDetailDto = new FlightDetailDto();
        newFlightDetailDto.setFlightId(1);
        newFlightDetailDto.setMediumAirportId(2);
        newFlightDetailDto.setArrivalTime(testArrivalTime);
        newFlightDetailDto.setLayoverDuration(testLayoverDuration);

        FlightDetail flightDetailEntity = new FlightDetail();
        flightDetailEntity.setFlightId(1);
        flightDetailEntity.setMediumAirportId(2);
        flightDetailEntity.setArrivalTime(testArrivalTime);
        flightDetailEntity.setLayoverDuration(testLayoverDuration);
        flightDetailEntity.setDeletedAt(null);

        FlightDetail savedFlightDetail = new FlightDetail();
        savedFlightDetail.setFlightId(1);
        savedFlightDetail.setMediumAirportId(2);
        savedFlightDetail.setArrivalTime(testArrivalTime);
        savedFlightDetail.setLayoverDuration(testLayoverDuration);
        savedFlightDetail.setDeletedAt(null);

        FlightDetailDto savedDto = new FlightDetailDto();
        savedDto.setFlightId(1);
        savedDto.setMediumAirportId(2);
        savedDto.setArrivalTime(testArrivalTime);
        savedDto.setLayoverDuration(testLayoverDuration);

        when(flightDetailMapper.toEntity(newFlightDetailDto)).thenReturn(flightDetailEntity);
        when(flightDetailRepository.save(flightDetailEntity)).thenReturn(savedFlightDetail);
        when(flightDetailMapper.toDto(savedFlightDetail)).thenReturn(savedDto);

        // Act
        FlightDetailDto result = flightDetailService.createFlightDetail(newFlightDetailDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getFlightId());
        assertEquals(2, result.getMediumAirportId());
        assertEquals(testArrivalTime, result.getArrivalTime());
        assertEquals(testLayoverDuration, result.getLayoverDuration());
        assertNull(flightDetailEntity.getDeletedAt());

        verify(flightDetailMapper).toEntity(newFlightDetailDto);
        verify(flightDetailRepository).save(flightDetailEntity);
        verify(flightDetailMapper).toDto(savedFlightDetail);
    }

    // NEW TEST 4: Delete flight detail - Success
    @Test
    void testDeleteFlightDetail_Success() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;

        FlightDetail flightDetail = new FlightDetail();
        flightDetail.setFlightId(flightId);
        flightDetail.setMediumAirportId(mediumAirportId);
        flightDetail.setArrivalTime(testArrivalTime);
        flightDetail.setLayoverDuration(testLayoverDuration);
        flightDetail.setDeletedAt(null);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(flightDetail));
        when(flightDetailRepository.save(flightDetail)).thenReturn(flightDetail);

        // Act
        flightDetailService.deleteFlightDetail(flightId, mediumAirportId);

        // Assert
        assertNotNull(flightDetail.getDeletedAt());
        assertTrue(flightDetail.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(flightDetail.getDeletedAt().isAfter(LocalDateTime.now().minusSeconds(5)));

        verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
        verify(flightDetailRepository).save(flightDetail);
    }

    // BONUS TEST: Delete flight detail not found
    @Test
    void testDeleteFlightDetail_NotFound_ThrowsException() {
        // Arrange
        Integer flightId = 1;
        Integer mediumAirportId = 2;

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightDetailService.deleteFlightDetail(flightId, mediumAirportId);
        });

        assertEquals("FlightDetail not found for flight: 1 and airport: 2", exception.getMessage());

        verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
        verify(flightDetailRepository, never()).save(any(FlightDetail.class));
    }

    // BONUS TEST: Get flight details by airport ID - Success
    @Test
    void testGetFlightDetailsByAirportId_Success() {
        // Arrange
        Integer airportId = 2;

        FlightDetail flightDetail1 = new FlightDetail();
        flightDetail1.setFlightId(1);
        flightDetail1.setMediumAirportId(airportId);
        flightDetail1.setArrivalTime(testArrivalTime);

        FlightDetail flightDetail2 = new FlightDetail();
        flightDetail2.setFlightId(2);
        flightDetail2.setMediumAirportId(airportId);
        flightDetail2.setArrivalTime(testArrivalTime.plusHours(2));

        List<FlightDetail> flightDetails = Arrays.asList(flightDetail1, flightDetail2);

        FlightDetailDto dto1 = new FlightDetailDto();
        dto1.setFlightId(1);
        dto1.setMediumAirportId(airportId);

        FlightDetailDto dto2 = new FlightDetailDto();
        dto2.setFlightId(2);
        dto2.setMediumAirportId(airportId);

        List<FlightDetailDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(flightDetailRepository.findByMediumAirportId(airportId)).thenReturn(flightDetails);
        when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

        // Act
        List<FlightDetailDto> result = flightDetailService.getFlightDetailsByAirportId(airportId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getFlightId());
        assertEquals(2, result.get(1).getFlightId());

        verify(flightDetailRepository).findByMediumAirportId(airportId);
        verify(flightDetailMapper).toDtoList(flightDetails);
    }
}
