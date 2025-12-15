package com.flightmanagement.service;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.mapper.FlightDetailMapper;
import com.flightmanagement.repository.FlightDetailRepository;
import com.flightmanagement.service.impl.FlightDetailServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // ==================== getAll/getBy Tests ====================

    @Nested
    @DisplayName("GetAllFlightDetails Tests - Full Path Coverage")
    @Tag("getAllFlightDetails")
    class GetAllFlightDetailsTests {
        @Test
        @DisplayName("TC1: Get all flight details - Returns multiple records")
        void getAllFlightDetails_ReturnsMultipleRecords() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail, new FlightDetail());
            List<FlightDetailDto> expectedDtos = Arrays.asList(validFlightDetailDto, new FlightDetailDto());

            when(flightDetailRepository.findAllActive()).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

            // Act
            List<FlightDetailDto> result = flightDetailService.getAllFlightDetails();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(flightDetailRepository).findAllActive();
            verify(flightDetailMapper).toDtoList(flightDetails);
        }

        @Test
        @DisplayName("TC2: Get all flight details - Returns single record")
        void getAllFlightDetails_ReturnsSingleRecord() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail);
            List<FlightDetailDto> expectedDtos = Arrays.asList(validFlightDetailDto);

            when(flightDetailRepository.findAllActive()).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

            // Act
            List<FlightDetailDto> result = flightDetailService.getAllFlightDetails();

            // Assert
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("TC3: Get all flight details - Returns empty list")
        void getAllFlightDetails_ReturnsEmptyList() {
            // Arrange
            when(flightDetailRepository.findAllActive()).thenReturn(Collections.emptyList());
            when(flightDetailMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<FlightDetailDto> result = flightDetailService.getAllFlightDetails();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC4: Get all flight details - Repository throws exception")
        void getAllFlightDetails_RepositoryThrowsException() {
            // Arrange
            when(flightDetailRepository.findAllActive())
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> flightDetailService.getAllFlightDetails());
        }

        @Test
        @DisplayName("TC5: Get all flight details - Mapper throws exception")
        void getAllFlightDetails_MapperThrowsException() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail);

            when(flightDetailRepository.findAllActive()).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails))
                .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> flightDetailService.getAllFlightDetails());
        }
    }

    // ==================== getFlightDetailsByFlightId Tests ====================

    @Nested
    @DisplayName("GetFlightDetailsByFlightId Tests - Full Path Coverage")
    @Tag("getFlightDetailsByFlightId")
    class GetFlightDetailsByFlightIdTests {

        @Test
        @DisplayName("TC1: Get by flight ID - Returns multiple records")
        void getFlightDetailsByFlightId_ReturnsMultipleRecords() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail, new FlightDetail());
            List<FlightDetailDto> expectedDtos = Arrays.asList(validFlightDetailDto, new FlightDetailDto());

            when(flightDetailRepository.findByFlightId(1)).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

            // Act
            List<FlightDetailDto> result = flightDetailService.getFlightDetailsByFlightId(1);

            // Assert
            assertEquals(2, result.size());
            verify(flightDetailRepository).findByFlightId(1);
        }

        @Test
        @DisplayName("TC2: Get by flight ID - Returns single record")
        void getFlightDetailsByFlightId_ReturnsSingleRecord() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail);
            List<FlightDetailDto> expectedDtos = Arrays.asList(validFlightDetailDto);

            when(flightDetailRepository.findByFlightId(1)).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

            // Act
            List<FlightDetailDto> result = flightDetailService.getFlightDetailsByFlightId(1);

            // Assert
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("TC3: Get by flight ID - Returns empty list")
        void getFlightDetailsByFlightId_ReturnsEmptyList() {
            // Arrange
            when(flightDetailRepository.findByFlightId(999)).thenReturn(Collections.emptyList());
            when(flightDetailMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<FlightDetailDto> result = flightDetailService.getFlightDetailsByFlightId(999);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC4: Get by flight ID - Repository throws exception")
        void getFlightDetailsByFlightId_RepositoryThrowsException() {
            // Arrange
            when(flightDetailRepository.findByFlightId(1))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> flightDetailService.getFlightDetailsByFlightId(1));
        }

        @Test
        @DisplayName("TC5: Get by flight ID with large result set")
        void getFlightDetailsByFlightId_LargeResultSet() {
            // Arrange
            List<FlightDetail> flightDetails = new ArrayList<>();
            List<FlightDetailDto> expectedDtos = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                flightDetails.add(new FlightDetail());
                expectedDtos.add(new FlightDetailDto());
            }

            when(flightDetailRepository.findByFlightId(1)).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

            // Act
            List<FlightDetailDto> result = flightDetailService.getFlightDetailsByFlightId(1);

            // Assert
            assertEquals(10, result.size());
        }
    }

    // ==================== getFlightDetailsByAirportId Tests ====================

    @Nested
    @DisplayName("GetFlightDetailsByAirportId Tests - Full Path Coverage")
    @Tag("getFlightDetailsByAirportId")
    class GetFlightDetailsByAirportIdTests {

        @Test
        @DisplayName("TC1: Get by airport ID - Returns multiple records")
        void getFlightDetailsByAirportId_ReturnsMultipleRecords() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail, new FlightDetail());
            List<FlightDetailDto> expectedDtos = Arrays.asList(validFlightDetailDto, new FlightDetailDto());

            when(flightDetailRepository.findByMediumAirportId(2)).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

            // Act
            List<FlightDetailDto> result = flightDetailService.getFlightDetailsByAirportId(2);

            // Assert
            assertEquals(2, result.size());
            verify(flightDetailRepository).findByMediumAirportId(2);
        }

        @Test
        @DisplayName("TC2: Get by airport ID - Returns empty list")
        void getFlightDetailsByAirportId_ReturnsEmptyList() {
            // Arrange
            when(flightDetailRepository.findByMediumAirportId(999)).thenReturn(Collections.emptyList());
            when(flightDetailMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<FlightDetailDto> result = flightDetailService.getFlightDetailsByAirportId(999);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC3: Get by airport ID - Repository throws exception")
        void getFlightDetailsByAirportId_RepositoryThrowsException() {
            // Arrange
            when(flightDetailRepository.findByMediumAirportId(2))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> flightDetailService.getFlightDetailsByAirportId(2));
        }

        @Test
        @DisplayName("TC4: Get by airport ID with single result")
        void getFlightDetailsByAirportId_SingleResult() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail);
            List<FlightDetailDto> expectedDtos = Arrays.asList(validFlightDetailDto);

            when(flightDetailRepository.findByMediumAirportId(2)).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails)).thenReturn(expectedDtos);

            // Act
            List<FlightDetailDto> result = flightDetailService.getFlightDetailsByAirportId(2);

            // Assert
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("TC5: Get by airport ID - Mapper throws exception")
        void getFlightDetailsByAirportId_MapperThrowsException() {
            // Arrange
            List<FlightDetail> flightDetails = Arrays.asList(validFlightDetail);

            when(flightDetailRepository.findByMediumAirportId(2)).thenReturn(flightDetails);
            when(flightDetailMapper.toDtoList(flightDetails))
                .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> flightDetailService.getFlightDetailsByAirportId(2));
        }
    }

    // ==================== createFlightDetail Tests ====================

    @Nested
    @DisplayName("CreateFlightDetail Tests - Full Path Coverage")
    @Tag("createFlightDetail")
    class CreateFlightDetailTests {
        @Test
        @DisplayName("TC1: Null DTO - Throws IllegalArgumentException")
        void createFlightDetail_NullDto_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> flightDetailService.createFlightDetail(null));
            verify(flightDetailRepository, never()).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC2: Missing flightId - Throws IllegalArgumentException")
        void createFlightDetail_MissingFlightId_ThrowsIllegalArgumentException() {
            validFlightDetailDto.setFlightId(null);

            assertThrows(IllegalArgumentException.class, () -> flightDetailService.createFlightDetail(validFlightDetailDto));
            verify(flightDetailRepository, never()).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC3: Missing mediumAirportId - Throws IllegalArgumentException")
        void createFlightDetail_MissingAirportId_ThrowsIllegalArgumentException() {
            validFlightDetailDto.setMediumAirportId(null);

            assertThrows(IllegalArgumentException.class, () -> flightDetailService.createFlightDetail(validFlightDetailDto));
            verify(flightDetailRepository, never()).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC4: Missing arrivalTime - Throws IllegalArgumentException")
        void createFlightDetail_MissingArrivalTime_ThrowsIllegalArgumentException() {
            validFlightDetailDto.setArrivalTime(null);

            assertThrows(IllegalArgumentException.class, () -> flightDetailService.createFlightDetail(validFlightDetailDto));
            verify(flightDetailRepository, never()).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC5: Missing layoverDuration - Throws IllegalArgumentException")
        void createFlightDetail_MissingLayoverDuration_ThrowsIllegalArgumentException() {
            validFlightDetailDto.setLayoverDuration(null);

            assertThrows(IllegalArgumentException.class, () -> flightDetailService.createFlightDetail(validFlightDetailDto));
            verify(flightDetailRepository, never()).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC6: Create with valid request - Success")
        void createFlightDetail_ValidRequest_Success() {
            validFlightDetail.setDeletedAt(LocalDateTime.now());

            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            FlightDetailDto result = flightDetailService.createFlightDetail(validFlightDetailDto);

            assertNotNull(result);
            assertEquals(1, result.getFlightId());
            assertEquals(2, result.getMediumAirportId());
            assertNull(validFlightDetail.getDeletedAt());
            verify(flightDetailRepository).save(any(FlightDetail.class));
            verify(flightDetailMapper).toDto(validFlightDetail);
        }
    }

    // ========================================
    // UPDATE FLIGHT DETAIL TESTS
    // ========================================

    @Nested
    @DisplayName("UpdateFlightDetail Tests - Full Path Coverage")
    @Tag("updateFlightDetail")
    class UpdateFlightDetailTests {

        private FlightDetail existingFlightDetail;
        private FlightDetailDto updateDto;
        private FlightDetail updatedFlightDetail;
        private FlightDetailDto returnedDto;
        private LocalDateTime newArrivalTime;

        @BeforeEach
        void setUp() {
            newArrivalTime = LocalDateTime.of(2024, 12, 25, 14, 30);

            existingFlightDetail = new FlightDetail();
            existingFlightDetail.setFlightId(1);
            existingFlightDetail.setMediumAirportId(1);
            existingFlightDetail.setArrivalTime(LocalDateTime.of(2024, 12, 25, 13, 0));
            existingFlightDetail.setLayoverDuration(60);

            updateDto = new FlightDetailDto();
            updateDto.setArrivalTime(newArrivalTime);
            updateDto.setLayoverDuration(90);

            updatedFlightDetail = new FlightDetail();
            existingFlightDetail.setFlightId(1);
            existingFlightDetail.setMediumAirportId(1);
            updatedFlightDetail.setArrivalTime(newArrivalTime);
            updatedFlightDetail.setLayoverDuration(90);

            returnedDto = new FlightDetailDto();
            existingFlightDetail.setFlightId(1);
            existingFlightDetail.setMediumAirportId(1);
            returnedDto.setArrivalTime(newArrivalTime);
            returnedDto.setLayoverDuration(90);
        }

        // ===== NHÓM 1: Happy Path - Successful Update =====

        @Test
        @DisplayName("TC1: Update flight detail with valid data - Success")
        void updateFlightDetail_ValidData_Success() {
            // Arrange
            Integer flightId = 100;
            Integer mediumAirportId = 50;

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
            when(flightDetailRepository.save(existingFlightDetail))
                .thenReturn(updatedFlightDetail);
            when(flightDetailMapper.toDto(updatedFlightDetail))
                .thenReturn(returnedDto);

            // Act
            FlightDetailDto result = flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateDto);

            // Assert
            assertNotNull(result);
            assertEquals(newArrivalTime, result.getArrivalTime());
            assertEquals(90, result.getLayoverDuration());
            
            verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
            verify(flightDetailRepository).save(existingFlightDetail);
            verify(flightDetailMapper).toDto(updatedFlightDetail);
        }

        // ===== NHÓM 2: Error Paths - FlightDetail Not Found =====

        @Test
        @DisplayName("TC2: Update non-existent flight detail - Throws RuntimeException")
        void updateFlightDetail_NotFound_ThrowsRuntimeException() {
            // Arrange
            Integer flightId = 999;
            Integer mediumAirportId = 999;

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> flightDetailService.updateFlightDetail(flightId, mediumAirportId, updateDto));

            verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
            verify(flightDetailRepository, never()).save(any(FlightDetail.class));
            verify(flightDetailMapper, never()).toDto(any(FlightDetail.class));
        }
    }

    // ========================================
    // DELETE FLIGHT DETAIL TESTS
    // ========================================

    @Nested
    @DisplayName("DeleteFlightDetail Tests - Full Path Coverage")
    @Tag("deleteFlightDetail")
    class DeleteFlightDetailTests {

        private FlightDetail existingFlightDetail;

        @BeforeEach
        void setUp() {
            existingFlightDetail = new FlightDetail();
            existingFlightDetail.setFlightId(1);
            existingFlightDetail.setMediumAirportId(1);
            existingFlightDetail.setArrivalTime(LocalDateTime.of(2024, 12, 25, 13, 0));
            existingFlightDetail.setDeletedAt(null);
        }
        
        // ===== NHÓM 1: Error Paths - FlightDetail Not Found =====

        @Test
        @DisplayName("TC1: Delete non-existent flight detail - Throws RuntimeException")
        void deleteFlightDetail_NotFound_ThrowsRuntimeException() {
            // Arrange
            Integer flightId = 999;
            Integer mediumAirportId = 999;

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> flightDetailService.deleteFlightDetail(flightId, mediumAirportId));

            verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
            verify(flightDetailRepository, never()).save(any(FlightDetail.class));
        }

        // ===== NHÓM 2: Happy Path - Successful Soft Delete =====

        @Test
        @DisplayName("TC2: Delete existing flight detail - Success")
        void deleteFlightDetail_ValidIds_Success() {
            // Arrange
            Integer flightId = 100;
            Integer mediumAirportId = 50;

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(flightId, mediumAirportId))
                .thenReturn(Optional.of(existingFlightDetail));
            when(flightDetailRepository.save(existingFlightDetail))
                .thenReturn(existingFlightDetail);

            // Act
            flightDetailService.deleteFlightDetail(flightId, mediumAirportId);

            // Assert
            assertNotNull(existingFlightDetail.getDeletedAt());
            assertEquals(true, existingFlightDetail.getDeletedAt().isBefore(LocalDateTime.now()) || existingFlightDetail.getDeletedAt().isEqual(LocalDateTime.now()));

            verify(flightDetailRepository).findByFlightIdAndMediumAirportId(flightId, mediumAirportId);
            verify(flightDetailRepository).save(existingFlightDetail);
        }

    }
}
