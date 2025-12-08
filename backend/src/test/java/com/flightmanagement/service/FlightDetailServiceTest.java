package com.flightmanagement.service;

import com.flightmanagement.dto.FlightDetailDto;
import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.mapper.FlightDetailMapper;
import com.flightmanagement.repository.FlightDetailRepository;
import com.flightmanagement.service.impl.FlightDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
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

        // ===== NHÓM 1: Happy Paths =====

        @Test
        @DisplayName("TC1: Create with valid request - Success")
        void createFlightDetail_ValidRequest_Success() {
            // Arrange
            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.createFlightDetail(validFlightDetailDto);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getFlightId());
            assertEquals(2, result.getMediumAirportId());
            verify(flightDetailRepository).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC2: Create with non-existent flight ID - Throws exception")
        void createFlightDetail_NonExistentFlight_ThrowsException() {
            // Arrange
            validFlightDetailDto.setFlightId(999); // Non-existent flight

            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class)))
                .thenThrow(new RuntimeException("Foreign key constraint violation"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightDetailService.createFlightDetail(validFlightDetailDto)
            );
        }

        @Test
        @DisplayName("TC3: Create with non-existent airport ID - Throws exception")
        void createFlightDetail_NonExistentAirport_ThrowsException() {
            // Arrange
            validFlightDetailDto.setMediumAirportId(888); // Non-existent airport

            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class)))
                .thenThrow(new RuntimeException("Foreign key constraint violation"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightDetailService.createFlightDetail(validFlightDetailDto)
            );
        }

        @Test
        @DisplayName("TC4: Create with zero layover duration - Success")
        void createFlightDetail_ZeroLayoverDuration_Success() {
            // Arrange
            validFlightDetailDto.setLayoverDuration(0);
            validFlightDetail.setLayoverDuration(0);

            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.createFlightDetail(validFlightDetailDto);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getLayoverDuration());
            verify(flightDetailRepository).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC5: Create with large layover duration (1440 min) - Success")
        void createFlightDetail_LargeLayoverDuration_Success() {
            // Arrange
            validFlightDetailDto.setLayoverDuration(1440);
            validFlightDetail.setLayoverDuration(1440);

            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.createFlightDetail(validFlightDetailDto);

            // Assert
            assertEquals(1440, result.getLayoverDuration());
        }

        @Test
        @DisplayName("TC6: Create with negative layover duration - Throws exception")
        void createFlightDetail_NegativeLayover_ThrowsException() {
            // Arrange
            validFlightDetailDto.setLayoverDuration(-30);

            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class)))
                .thenThrow(new IllegalArgumentException("Layover duration cannot be negative"));

            // Act & Assert
            assertThrows(
                Exception.class,
                () -> flightDetailService.createFlightDetail(validFlightDetailDto)
            );
        }

        @Test
        @DisplayName("TC7: Create with null arrival time - Throws exception")
        void createFlightDetail_NullArrivalTime_ThrowsException() {
            // Arrange
            validFlightDetailDto.setArrivalTime(null);

            // Act & Assert
            assertThrows(
                Exception.class,
                () -> flightDetailService.createFlightDetail(validFlightDetailDto)
            );

            verify(flightDetailRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC8: Create sets DeletedAt to null - Success")
        void createFlightDetail_SetsDeletedAtToNull() {
            // Arrange
            FlightDetail detailWithDeletedAt = new FlightDetail();
            detailWithDeletedAt.setFlightId(1);
            detailWithDeletedAt.setMediumAirportId(2);
            detailWithDeletedAt.setArrivalTime(arrivalTime);
            detailWithDeletedAt.setDeletedAt(LocalDateTime.now());

            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(detailWithDeletedAt);
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            flightDetailService.createFlightDetail(validFlightDetailDto);

            // Assert
            assertNull(detailWithDeletedAt.getDeletedAt());
            verify(flightDetailRepository).save(detailWithDeletedAt);
        }

        @Test
        @DisplayName("TC5: Create with minimal data - Success")
        void createFlightDetail_WithMinimalData_Success() {
            // Arrange
            FlightDetailDto minimalDto = new FlightDetailDto();
            minimalDto.setFlightId(1);
            minimalDto.setMediumAirportId(2);

            FlightDetail minimalDetail = new FlightDetail();
            minimalDetail.setFlightId(1);
            minimalDetail.setMediumAirportId(2);

            when(flightDetailMapper.toEntity(minimalDto)).thenReturn(minimalDetail);
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(minimalDetail);
            when(flightDetailMapper.toDto(minimalDetail)).thenReturn(minimalDto);

            // Act
            FlightDetailDto result = flightDetailService.createFlightDetail(minimalDto);

            // Assert
            assertNotNull(result);
            verify(flightDetailRepository).save(minimalDetail);
        }

        // ===== NHÓM 3: Exception Handling - Mapper =====

        @Test
        @DisplayName("TC6: Mapper throws exception - Propagates exception")
        void createFlightDetail_MapperThrowsException_PropagatesException() {
            // Arrange
            when(flightDetailMapper.toEntity(validFlightDetailDto))
                .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.createFlightDetail(validFlightDetailDto)
            );

            assertTrue(exception.getMessage().contains("Mapping error"));
            verify(flightDetailRepository, never()).save(any());
        }

        // ===== NHÓM 4: Exception Handling - Repository =====

        @Test
        @DisplayName("TC7: Repository save throws exception - Propagates exception")
        void createFlightDetail_RepositoryThrowsException_PropagatesException() {
            // Arrange
            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class)))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.createFlightDetail(validFlightDetailDto)
            );

            assertTrue(exception.getMessage().contains("Database error"));
        }

        // ===== NHÓM 5: Exception Handling - Output Mapper =====

        @Test
        @DisplayName("TC8: Output mapper throws exception - Propagates exception")
        void createFlightDetail_OutputMapperThrowsException_PropagatesException() {
            // Arrange
            when(flightDetailMapper.toEntity(validFlightDetailDto)).thenReturn(validFlightDetail);
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail))
                .thenThrow(new RuntimeException("Output mapping error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightDetailService.createFlightDetail(validFlightDetailDto)
            );
        }
    }

    // ==================== updateFlightDetail Tests ====================

    @Nested
    @DisplayName("UpdateFlightDetail Tests - Full Path Coverage")
    @Tag("updateFlightDetail")
    class UpdateFlightDetailTests {

        // ===== NHÓM 1: Happy Paths =====

        @Test
        @DisplayName("TC1: Update both arrival time and layover duration - Success")
        void updateFlightDetail_BothFieldsChanged_Success() {
            // Arrange
            LocalDateTime newArrivalTime = arrivalTime.plusHours(2);
            validFlightDetailDto.setArrivalTime(newArrivalTime);
            validFlightDetailDto.setLayoverDuration(120);

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

            // Assert
            assertNotNull(result);
            verify(flightDetailRepository).save(any(FlightDetail.class));
        }

        @Test
        @DisplayName("TC3: Update only arrival time - Success")
        void updateFlightDetail_OnlyArrivalTime_Success() {
            // Arrange
            LocalDateTime newArrivalTime = arrivalTime.plusHours(1);
            validFlightDetailDto.setArrivalTime(newArrivalTime);

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

            // Assert
            assertNotNull(result);
            assertEquals(newArrivalTime, validFlightDetail.getArrivalTime());
        }

        @Test
        @DisplayName("TC4: Update only layover duration - Success")
        void updateFlightDetail_OnlyLayoverDuration_Success() {
            // Arrange
            validFlightDetailDto.setLayoverDuration(90);

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

            // Assert
            assertNotNull(result);
            assertEquals(90, validFlightDetail.getLayoverDuration());
        }

        @Test
        @DisplayName("TC5: Update with zero layover duration - Success")
        void updateFlightDetail_ZeroLayoverDuration_Success() {
            // Arrange
            validFlightDetailDto.setLayoverDuration(0);

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

            // Assert
            assertEquals(0, result.getLayoverDuration());
        }

        @Test
        @DisplayName("TC6: Update with maximum layover duration (2880 min) - Success")
        void updateFlightDetail_MaximumLayoverDuration_Success() {
            // Arrange
            validFlightDetailDto.setLayoverDuration(2880); // 48 hours

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

            // Assert
            assertEquals(2880, result.getLayoverDuration());
        }

        @Test
        @DisplayName("TC7: Update with negative layover - Throws exception")
        void updateFlightDetail_NegativeLayover_ThrowsException() {
            // Arrange
            validFlightDetailDto.setLayoverDuration(-60);

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class)))
                .thenThrow(new IllegalArgumentException("Layover duration cannot be negative"));

            // Act & Assert
            assertThrows(
                Exception.class,
                () -> flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto)
            );
        }

        @Test
        @DisplayName("TC8: Update with same values (idempotent) - Success")
        void updateFlightDetail_SameValues_Success() {
            // Arrange - Same arrival time and layover as existing
            validFlightDetailDto.setArrivalTime(validFlightDetail.getArrivalTime());
            validFlightDetailDto.setLayoverDuration(validFlightDetail.getLayoverDuration());

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail)).thenReturn(validFlightDetailDto);

            // Act
            FlightDetailDto result = flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto);

            // Assert
            assertNotNull(result);
            verify(flightDetailRepository).save(any(FlightDetail.class));
        }
    }

    // ==================== Old UpdateFlightDetail tests marked deprecated ====================

    @Nested
    @DisplayName("[DEPRECATED] Old UpdateFlightDetail Tests")
    @Tag("deprecated")
    class DeprecatedUpdateFlightDetailTests {

        @Test
        @DisplayName("[OLD] TC6: Update non-existent detail - Throws exception")
        void updateFlightDetail_NotFound_ThrowsException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(999, 888))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.updateFlightDetail(999, 888, validFlightDetailDto)
            );

            assertTrue(exception.getMessage().contains("FlightDetail not found"));
            assertTrue(exception.getMessage().contains("flight: 999"));
            assertTrue(exception.getMessage().contains("airport: 888"));
            verify(flightDetailRepository, never()).save(any());
        }

        @Test
        @DisplayName("[OLD] TC7: Update with different IDs - Throws exception")
        void updateFlightDetail_DifferentIds_ThrowsException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto)
            );

            assertTrue(exception.getMessage().contains("FlightDetail not found"));
        }

        @Test
        @DisplayName("[OLD] TC8: Repository save throws exception - Propagates exception")
        void updateFlightDetail_RepositoryThrowsException_PropagatesException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class)))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto)
            );

            assertTrue(exception.getMessage().contains("Database error"));
        }

        @Test
        @DisplayName("[OLD] TC9: Output mapper throws exception - Propagates exception")
        void updateFlightDetail_MapperThrowsException_PropagatesException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);
            when(flightDetailMapper.toDto(validFlightDetail))
                .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> flightDetailService.updateFlightDetail(1, 2, validFlightDetailDto)
            );
        }
    }

    // ==================== deleteFlightDetail Tests ====================

    @Nested
    @DisplayName("DeleteFlightDetail Tests - Full Path Coverage")
    @Tag("deleteFlightDetail")
    class DeleteFlightDetailTests {

        // ===== NHÓM 1: Happy Paths =====

        @Test
        @DisplayName("TC1: Delete existing detail - Success")
        void deleteFlightDetail_ExistingDetail_Success() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);

            // Act
            flightDetailService.deleteFlightDetail(1, 2);

            // Assert
            assertNotNull(validFlightDetail.getDeletedAt());
            verify(flightDetailRepository).save(validFlightDetail);
        }

        @Test
        @DisplayName("TC2: Delete sets DeletedAt timestamp - Timestamp set correctly")
        void deleteFlightDetail_SetsDeletedAtTimestamp() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);

            LocalDateTime beforeDelete = LocalDateTime.now();

            // Act
            flightDetailService.deleteFlightDetail(1, 2);

            LocalDateTime afterDelete = LocalDateTime.now();

            // Assert
            assertNotNull(validFlightDetail.getDeletedAt());
            assertTrue(validFlightDetail.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
            assertTrue(validFlightDetail.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
        }

        @Test
        @DisplayName("TC3: Delete preserves other fields - Other data unchanged")
        void deleteFlightDetail_PreservesOtherFields() {
            // Arrange
            validFlightDetail.setLayoverDuration(90);
            validFlightDetail.setArrivalTime(arrivalTime);

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);

            // Act
            flightDetailService.deleteFlightDetail(1, 2);

            // Assert
            assertNotNull(validFlightDetail.getDeletedAt());
            assertEquals(90, validFlightDetail.getLayoverDuration());
            assertEquals(arrivalTime, validFlightDetail.getArrivalTime());
            assertEquals(1, validFlightDetail.getFlightId());
            assertEquals(2, validFlightDetail.getMediumAirportId());
        }

        // ===== NHÓM 2: Not Found Cases =====

        @Test
        @DisplayName("TC4: Delete non-existent detail - Throws exception")
        void deleteFlightDetail_NotFound_ThrowsException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(999, 888))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.deleteFlightDetail(999, 888)
            );

            assertTrue(exception.getMessage().contains("FlightDetail not found"));
            assertTrue(exception.getMessage().contains("flight: 999"));
            assertTrue(exception.getMessage().contains("airport: 888"));
            verify(flightDetailRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC5: Delete already deleted record - Throws exception")
        void deleteFlightDetail_AlreadyDeleted_ThrowsException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.deleteFlightDetail(1, 2)
            );

            assertTrue(exception.getMessage().contains("FlightDetail not found"));
        }

        // ===== NHÓM 3: Different ID Combinations =====

        @Test
        @DisplayName("TC6: Delete with different flight ID - Throws exception")
        void deleteFlightDetail_DifferentFlightId_ThrowsException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(5, 2))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.deleteFlightDetail(5, 2)
            );

            assertTrue(exception.getMessage().contains("flight: 5"));
            assertTrue(exception.getMessage().contains("airport: 2"));
        }

        @Test
        @DisplayName("TC7: Delete with different airport ID - Throws exception")
        void deleteFlightDetail_DifferentAirportId_ThrowsException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 5))
                .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.deleteFlightDetail(1, 5)
            );

            assertTrue(exception.getMessage().contains("flight: 1"));
            assertTrue(exception.getMessage().contains("airport: 5"));
        }

        // ===== NHÓM 4: Repository Exceptions =====

        @Test
        @DisplayName("TC8: Repository save throws exception - Propagates exception")
        void deleteFlightDetail_RepositoryThrowsException_PropagatesException() {
            // Arrange
            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class)))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> flightDetailService.deleteFlightDetail(1, 2)
            );

            assertTrue(exception.getMessage().contains("Database error"));
        }

        // ===== NHÓM 5: Edge Cases =====

        @Test
        @DisplayName("TC9: Delete with extreme timestamp values - Success")
        void deleteFlightDetail_ExtremeTimestampValues_Success() {
            // Arrange
            LocalDateTime extremeArrival = LocalDateTime.of(2025, 1, 1, 0, 0);
            validFlightDetail.setArrivalTime(extremeArrival);

            when(flightDetailRepository.findByFlightIdAndMediumAirportId(1, 2))
                .thenReturn(Optional.of(validFlightDetail));
            when(flightDetailRepository.save(any(FlightDetail.class))).thenReturn(validFlightDetail);

            // Act
            flightDetailService.deleteFlightDetail(1, 2);

            // Assert
            assertNotNull(validFlightDetail.getDeletedAt());
            assertEquals(extremeArrival, validFlightDetail.getArrivalTime());
        }
    }
}
