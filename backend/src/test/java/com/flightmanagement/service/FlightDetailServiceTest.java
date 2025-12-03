package com.flightmanagement.service;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.mapper.FlightDetailMapper;
import com.flightmanagement.repository.FlightDetailRepository;
import com.flightmanagement.service.impl.FlightDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
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

    private FlightDetailDto validFlightDetailDto;
    private FlightDetail validFlightDetail;
    private LocalDateTime arrivalTime;

    @BeforeEach
    void setUp() {
        arrivalTime = LocalDateTime.now().plusHours(3);

        validFlightDetailDto = new FlightDetailDto();
        validFlightDetailDto.setFlightId(1);
        validFlightDetailDto.setMediumAirportId(2);
        validFlightDetailDto.setMediumAirportName("Medium Airport");
        validFlightDetailDto.setMediumCityName("Medium City");
        validFlightDetailDto.setArrivalTime(arrivalTime);
        validFlightDetailDto.setLayoverDuration(60);

        validFlightDetail = new FlightDetail();
        validFlightDetail.setFlightId(1);
        validFlightDetail.setMediumAirportId(2);
        validFlightDetail.setArrivalTime(arrivalTime);
        validFlightDetail.setLayoverDuration(60);
    }

    // ==================== createFlightDetail Tests ====================

    @Tag("createFlightDetail")
    @Test
    void createFlightDetail_validDto_success() {
        when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        FlightDetailDto result = flightDetailService.createFlightDetail(validFlightDetailDto);

        assertNotNull(result);
        assertEquals(validFlightDetailDto.getFlightId(), result.getFlightId());
        assertEquals(validFlightDetailDto.getMediumAirportId(), result.getMediumAirportId());
        verify(flightDetailRepository).save(any(FlightDetail.class));
    }

    @Tag("createFlightDetail")
    @Test
    void createFlightDetail_setsDeletedAtToNull() {
        FlightDetail detailWithDeletedAt = new FlightDetail();
        detailWithDeletedAt.setDeletedAt(LocalDateTime.now());

        when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(detailWithDeletedAt);
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        flightDetailService.createFlightDetail(validFlightDetailDto);

        assertNull(detailWithDeletedAt.getDeletedAt());
        verify(flightDetailRepository).save(detailWithDeletedAt);
    }

    @Tag("createFlightDetail")
    @Test
    void createFlightDetail_withMinimalData_success() {
        FlightDetailDto minimalDto = new FlightDetailDto();
        minimalDto.setFlightId(1);
        minimalDto.setMediumAirportId(2);
        minimalDto.setArrivalTime(arrivalTime);

        FlightDetail minimalDetail = new FlightDetail();
        minimalDetail.setFlightId(1);
        minimalDetail.setMediumAirportId(2);

        when(flightDetailMapper.toEntity(minimalDto)).thenReturn(minimalDetail);
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(minimalDetail);
        when(flightDetailMapper.toDto(minimalDetail)).thenReturn(minimalDto);

        FlightDetailDto result = flightDetailService.createFlightDetail(minimalDto);

        assertNotNull(result);
        verify(flightDetailRepository).save(minimalDetail);
    }

    @Tag("createFlightDetail")
    @Test
    void createFlightDetail_withZeroLayoverDuration_success() {
        validFlightDetailDto.setLayoverDuration(0);
        validFlightDetail.setLayoverDuration(0);

        when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        FlightDetailDto result = flightDetailService.createFlightDetail(validFlightDetailDto);

        assertNotNull(result);
        assertEquals(0, result.getLayoverDuration());
    }

    @Tag("createFlightDetail")
    @Test
    void createFlightDetail_repositoryThrowsException_propagatesException() {
        when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
        when(flightDetailRepository.save(any(FlightDetail.class)))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.createFlightDetail(validFlightDetailDto)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Tag("createFlightDetail")
    @Test
    void createFlightDetail_mapperThrowsException_propagatesException() {
        when(flightDetailMapper.toEntity(validFlightDetailDto))
            .thenThrow(new RuntimeException("Mapping error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.createFlightDetail(validFlightDetailDto)
        );

        assertTrue(exception.getMessage().contains("Mapping error"));
        verify(flightDetailRepository, never()).save(any());
    }

    @Tag("createFlightDetail")
    @Test
    void createFlightDetail_withLargeLayoverDuration_success() {
        validFlightDetailDto.setLayoverDuration(500);
        validFlightDetail.setLayoverDuration(500);

        when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        FlightDetailDto result = flightDetailService.createFlightDetail(validFlightDetailDto);

        assertNotNull(result);
        assertEquals(500, result.getLayoverDuration());
    }

    // ==================== updateFlightDetail Tests ====================

    @Tag("updateFlightDetail")
    @Test
    void updateFlightDetail_validUpdate_success() {
        LocalDateTime newArrivalTime = arrivalTime.plusHours(1);
        validFlightDetailDto.setArrivalTime(newArrivalTime);
        validFlightDetailDto.setLayoverDuration(90);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

        assertNotNull(result);
        assertEquals(newArrivalTime, validFlightDetail.getArrivalTime());
        assertEquals(90, validFlightDetail.getLayoverDuration());
        verify(flightDetailRepository).save(validFlightDetail);
    }

    @Tag("updateFlightDetail")
    @Test
    void updateFlightDetail_flightDetailNotFound_throwsException() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto)
        );

        assertTrue(exception.getMessage().contains("FlightDetail not found"));
        assertTrue(exception.getMessage().contains("flight: 1"));
        assertTrue(exception.getMessage().contains("airport: 2"));
        verify(flightDetailRepository, never()).save(any());
    }

    @Tag("updateFlightDetail")
    @Test
    void updateFlightDetail_onlyArrivalTime_success() {
        LocalDateTime newArrivalTime = arrivalTime.plusHours(2);
        validFlightDetailDto.setArrivalTime(newArrivalTime);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

        assertNotNull(result);
        assertEquals(newArrivalTime, validFlightDetail.getArrivalTime());
    }

    @Tag("updateFlightDetail")
    @Test
    void updateFlightDetail_onlyLayoverDuration_success() {
        validFlightDetailDto.setLayoverDuration(120);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

        assertNotNull(result);
        assertEquals(120, validFlightDetail.getLayoverDuration());
    }

    @Tag("updateFlightDetail")
    @Test
    void updateFlightDetail_zeroLayoverDuration_success() {
        validFlightDetailDto.setLayoverDuration(0);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
        when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

        FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

        assertNotNull(result);
        assertEquals(0, validFlightDetail.getLayoverDuration());
    }

    @Tag("updateFlightDetail")
    @Test
    void updateFlightDetail_repositoryThrowsException_propagatesException() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class)))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Tag("updateFlightDetail")
    @Test
    void updateFlightDetail_differentFlightAndAirportIds_throwsException() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(999, 888))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.updateFlightDetail(999, 888, validFlightDetailDto)
        );

        assertTrue(exception.getMessage().contains("FlightDetail not found"));
        assertTrue(exception.getMessage().contains("flight: 999"));
        assertTrue(exception.getMessage().contains("airport: 888"));
    }

    // ==================== deleteFlightDetail Tests ====================

    @Tag("deleteFlightDetail")
    @Test
    void deleteFlightDetail_validIds_success() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);

        flightDetailService.deleteFlightDetail(1, 2);

        assertNotNull(validFlightDetail.getDeletedAt());
        verify(flightDetailRepository).save(validFlightDetail);
    }

    @Tag("deleteFlightDetail")
    @Test
    void deleteFlightDetail_flightDetailNotFound_throwsException() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.deleteFlightDetail(1, 2)
        );

        assertTrue(exception.getMessage().contains("FlightDetail not found"));
        assertTrue(exception.getMessage().contains("flight: 1"));
        assertTrue(exception.getMessage().contains("airport: 2"));
        verify(flightDetailRepository, never()).save(any());
    }

    @Tag("deleteFlightDetail")
    @Test
    void deleteFlightDetail_setsDeletedAtTimestamp() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);

        LocalDateTime beforeDelete = LocalDateTime.now();
        flightDetailService.deleteFlightDetail(1, 2);
        LocalDateTime afterDelete = LocalDateTime.now();

        assertNotNull(validFlightDetail.getDeletedAt());
        assertTrue(validFlightDetail.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
        assertTrue(validFlightDetail.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
    }

    @Tag("deleteFlightDetail")
    @Test
    void deleteFlightDetail_alreadyDeletedRecord_throwsException() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.deleteFlightDetail(1, 2)
        );

        assertTrue(exception.getMessage().contains("FlightDetail not found"));
    }

    @Tag("deleteFlightDetail")
    @Test
    void deleteFlightDetail_repositoryThrowsException_propagatesException() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class)))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.deleteFlightDetail(1, 2)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Tag("deleteFlightDetail")
    @Test
    void deleteFlightDetail_differentFlightAndAirportIds_throwsException() {
        when(flightDetailRepository.findByFlightIdAndMediumAirportId(999, 888))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> flightDetailService.deleteFlightDetail(999, 888)
        );

        assertTrue(exception.getMessage().contains("FlightDetail not found"));
        assertTrue(exception.getMessage().contains("flight: 999"));
        assertTrue(exception.getMessage().contains("airport: 888"));
    }

    @Tag("deleteFlightDetail")
    @Test
    void deleteFlightDetail_preservesOtherFields() {
        validFlightDetail.setLayoverDuration(90);
        validFlightDetail.setArrivalTime(arrivalTime);

        when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
            .thenReturn(Optional.of(validFlightDetail));
        when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);

        flightDetailService.deleteFlightDetail(1, 2);

        assertNotNull(validFlightDetail.getDeletedAt());
        assertEquals(90, validFlightDetail.getLayoverDuration());
        assertEquals(arrivalTime, validFlightDetail.getArrivalTime());
    }
}
