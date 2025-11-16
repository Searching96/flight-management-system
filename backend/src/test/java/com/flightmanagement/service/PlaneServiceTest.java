package com.flightmanagement.service;

import com.flightmanagement.dto.PlaneDto;
import com.flightmanagement.entity.Plane;
import com.flightmanagement.mapper.PlaneMapper;
import com.flightmanagement.repository.PlaneRepository;
import com.flightmanagement.service.impl.PlaneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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

/**
 * Test class for PlaneService - Core function for Aircraft Management
 * 
 * Available Tags:
 * - getAllPlanes: Tests for retrieving all aircraft
 * - getPlaneById: Tests for retrieving aircraft by ID
 * - createPlane: Tests for aircraft creation
 * - updatePlane: Tests for aircraft updates
 * - deletePlane: Tests for aircraft deletion (soft delete)
 * - getPlaneByCode: Tests for retrieving aircraft by code
 * - getPlanesByType: Tests for type-based aircraft search
 */
@ExtendWith(MockitoExtension.class)
public class PlaneServiceTest {

    @Mock
    private PlaneRepository planeRepository;

    @Mock
    private PlaneMapper planeMapper;

    @InjectMocks
    private PlaneServiceImpl planeService;

    private Plane testPlane;
    private PlaneDto testPlaneDto;
    private Plane testPlane2;
    private PlaneDto testPlaneDto2;

    @BeforeEach
    void setUp() {
        // Setup test plane entity
        testPlane = new Plane();
        testPlane.setPlaneId(1);
        testPlane.setPlaneCode("VN-001");
        testPlane.setPlaneType("Boeing 787");
        testPlane.setSeatQuantity(300);
        testPlane.setDeletedAt(null);

        // Setup test plane DTO
        testPlaneDto = new PlaneDto();
        testPlaneDto.setPlaneId(1);
        testPlaneDto.setPlaneCode("VN-001");
        testPlaneDto.setPlaneType("Boeing 787");
        testPlaneDto.setSeatQuantity(300);

        // Setup second test plane
        testPlane2 = new Plane();
        testPlane2.setPlaneId(2);
        testPlane2.setPlaneCode("VN-002");
        testPlane2.setPlaneType("Airbus A320");
        testPlane2.setSeatQuantity(180);
        testPlane2.setDeletedAt(null);

        testPlaneDto2 = new PlaneDto();
        testPlaneDto2.setPlaneId(2);
        testPlaneDto2.setPlaneCode("VN-002");
        testPlaneDto2.setPlaneType("Airbus A320");
        testPlaneDto2.setSeatQuantity(180);
    }

    // ================ GET ALL PLANES TESTS ================

    @Test
    @Tag("getAllPlanes")
    void testGetAllPlanes_Success_ReturnsPlaneList() {
        // Given
        List<Plane> mockPlanes = Arrays.asList(testPlane, testPlane2);
        List<PlaneDto> expectedDtos = Arrays.asList(testPlaneDto, testPlaneDto2);

        when(planeRepository.findAllActive()).thenReturn(mockPlanes);
        when(planeMapper.toDtoList(mockPlanes)).thenReturn(expectedDtos);

        // When
        List<PlaneDto> result = planeService.getAllPlanes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("VN-001", result.get(0).getPlaneCode());
        assertEquals("VN-002", result.get(1).getPlaneCode());
        verify(planeRepository).findAllActive();
        verify(planeMapper).toDtoList(mockPlanes);
    }

    @Test
    @Tag("getAllPlanes")
    void testGetAllPlanes_EmptyDatabase_ReturnsEmptyList() {
        // Given
        when(planeRepository.findAllActive()).thenReturn(Collections.emptyList());
        when(planeMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<PlaneDto> result = planeService.getAllPlanes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planeRepository).findAllActive();
        verify(planeMapper).toDtoList(Collections.emptyList());
    }

    @Test
    @Tag("getAllPlanes")
    void testGetAllPlanes_RepositoryException_PropagatesException() {
        // Given
        when(planeRepository.findAllActive()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getAllPlanes());
        assertEquals("Database connection failed", exception.getMessage());
        verify(planeRepository).findAllActive();
        verify(planeMapper, never()).toDtoList(any());
    }

    @Test
    @Tag("getAllPlanes")
    void testGetAllPlanes_MapperException_PropagatesException() {
        // Given
        List<Plane> mockPlanes = Arrays.asList(testPlane);
        when(planeRepository.findAllActive()).thenReturn(mockPlanes);
        when(planeMapper.toDtoList(mockPlanes)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getAllPlanes());
        assertEquals("Mapping error", exception.getMessage());
        verify(planeRepository).findAllActive();
        verify(planeMapper).toDtoList(mockPlanes);
    }

    @Test
    @Tag("getAllPlanes")
    void testGetAllPlanes_LargeDataset_HandlesEfficiently() {
        // Given - Simulate large dataset
        List<Plane> largePlaneList = Arrays.asList(testPlane, testPlane2, testPlane, testPlane2, testPlane);
        List<PlaneDto> largeDtoList = Arrays.asList(testPlaneDto, testPlaneDto2, testPlaneDto, testPlaneDto2, testPlaneDto);

        when(planeRepository.findAllActive()).thenReturn(largePlaneList);
        when(planeMapper.toDtoList(largePlaneList)).thenReturn(largeDtoList);

        // When
        List<PlaneDto> result = planeService.getAllPlanes();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(planeRepository).findAllActive();
        verify(planeMapper).toDtoList(largePlaneList);
    }

    // ================ GET PLANE BY ID TESTS ================

    @Test
    @Tag("getPlaneById")
    void testGetPlaneById_Success_ReturnsPlaneDto() {
        // Given
        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeMapper.toDto(testPlane)).thenReturn(testPlaneDto);

        // When
        PlaneDto result = planeService.getPlaneById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPlaneId());
        assertEquals("VN-001", result.getPlaneCode());
        assertEquals("Boeing 787", result.getPlaneType());
        assertEquals(300, result.getSeatQuantity());
        verify(planeRepository).findActiveById(1);
        verify(planeMapper).toDto(testPlane);
    }

    @Test
    @Tag("getPlaneById")
    void testGetPlaneById_NotFound_ThrowsRuntimeException() {
        // Given
        when(planeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneById(999));
        assertEquals("Plane not found with id: 999", exception.getMessage());
        verify(planeRepository).findActiveById(999);
        verify(planeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getPlaneById")
    void testGetPlaneById_NullId_HandledByRepository() {
        // Given
        when(planeRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneById(null));
        assertTrue(exception.getMessage().contains("Plane not found with id: null"));
        verify(planeRepository).findActiveById(null);
    }

    @Test
    @Tag("getPlaneById")
    void testGetPlaneById_MapperException_PropagatesException() {
        // Given
        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeMapper.toDto(testPlane)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneById(1));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(planeRepository).findActiveById(1);
        verify(planeMapper).toDto(testPlane);
    }

    @Test
    @Tag("getPlaneById")
    void testGetPlaneById_RepositoryException_PropagatesException() {
        // Given
        when(planeRepository.findActiveById(1)).thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneById(1));
        assertEquals("Database query failed", exception.getMessage());
        verify(planeRepository).findActiveById(1);
        verify(planeMapper, never()).toDto(any());
    }

    // ================ CREATE PLANE TESTS ================

    @Test
    @Tag("createPlane")
    void testCreatePlane_Success_ReturnsCreatedPlaneDto() {
        // Given
        PlaneDto inputDto = new PlaneDto();
        inputDto.setPlaneCode("VN-003");
        inputDto.setPlaneType("Boeing 777");
        inputDto.setSeatQuantity(350);

        Plane mappedPlane = new Plane();
        mappedPlane.setPlaneCode("VN-003");
        mappedPlane.setPlaneType("Boeing 777");
        mappedPlane.setSeatQuantity(350);

        Plane savedPlane = new Plane();
        savedPlane.setPlaneId(3);
        savedPlane.setPlaneCode("VN-003");
        savedPlane.setPlaneType("Boeing 777");
        savedPlane.setSeatQuantity(350);
        savedPlane.setDeletedAt(null);

        PlaneDto resultDto = new PlaneDto();
        resultDto.setPlaneId(3);
        resultDto.setPlaneCode("VN-003");
        resultDto.setPlaneType("Boeing 777");
        resultDto.setSeatQuantity(350);

        when(planeMapper.toEntity(inputDto)).thenReturn(mappedPlane);
        when(planeRepository.save(any(Plane.class))).thenReturn(savedPlane);
        when(planeMapper.toDto(savedPlane)).thenReturn(resultDto);

        // When
        PlaneDto result = planeService.createPlane(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getPlaneId());
        assertEquals("VN-003", result.getPlaneCode());
        assertEquals("Boeing 777", result.getPlaneType());
        assertEquals(350, result.getSeatQuantity());
        verify(planeMapper).toEntity(inputDto);
        verify(planeRepository).save(argThat(plane -> plane.getDeletedAt() == null));
        verify(planeMapper).toDto(savedPlane);
    }

    @Test
    @Tag("createPlane")
    void testCreatePlane_NullInput_HandledByMapper() {
        // Given
        when(planeMapper.toEntity(null)).thenThrow(new RuntimeException("Input cannot be null"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.createPlane(null));
        assertEquals("Input cannot be null", exception.getMessage());
        verify(planeMapper).toEntity(null);
        verify(planeRepository, never()).save(any());
    }

    @Test
    @Tag("createPlane")
    void testCreatePlane_InvalidSeatQuantity_AllowedByService() {
        // Given - Service doesn't validate seat quantity, repository might
        PlaneDto invalidDto = new PlaneDto();
        invalidDto.setPlaneCode("VN-004");
        invalidDto.setPlaneType("Test Plane");
        invalidDto.setSeatQuantity(-10); // Invalid seat quantity

        when(planeMapper.toEntity(invalidDto)).thenReturn(testPlane);
        when(planeRepository.save(any(Plane.class))).thenReturn(testPlane);
        when(planeMapper.toDto(testPlane)).thenReturn(testPlaneDto);

        // When
        PlaneDto result = planeService.createPlane(invalidDto);

        // Then - Service passes through, validation might happen at repository/database level
        assertNotNull(result);
        verify(planeMapper).toEntity(invalidDto);
        verify(planeRepository).save(any(Plane.class));
    }

    @Test
    @Tag("createPlane")
    void testCreatePlane_RepositoryException_PropagatesException() {
        // Given
        when(planeMapper.toEntity(testPlaneDto)).thenReturn(testPlane);
        when(planeRepository.save(any(Plane.class))).thenThrow(new RuntimeException("Duplicate plane code"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.createPlane(testPlaneDto));
        assertEquals("Duplicate plane code", exception.getMessage());
        verify(planeMapper).toEntity(testPlaneDto);
        verify(planeRepository).save(any(Plane.class));
        verify(planeMapper, never()).toDto(any());
    }

    @Test
    @Tag("createPlane")
    void testCreatePlane_MapperToEntityException_PropagatesException() {
        // Given
        when(planeMapper.toEntity(testPlaneDto)).thenThrow(new RuntimeException("Entity mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.createPlane(testPlaneDto));
        assertEquals("Entity mapping failed", exception.getMessage());
        verify(planeMapper).toEntity(testPlaneDto);
        verify(planeRepository, never()).save(any());
    }

    @Test
    @Tag("createPlane")
    void testCreatePlane_MapperToDtoException_PropagatesException() {
        // Given
        when(planeMapper.toEntity(testPlaneDto)).thenReturn(testPlane);
        when(planeRepository.save(any(Plane.class))).thenReturn(testPlane);
        when(planeMapper.toDto(testPlane)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.createPlane(testPlaneDto));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(planeMapper).toEntity(testPlaneDto);
        verify(planeRepository).save(any(Plane.class));
        verify(planeMapper).toDto(testPlane);
    }

    // ================ UPDATE PLANE TESTS ================

    @Test
    @Tag("updatePlane")
    void testUpdatePlane_Success_ReturnsUpdatedPlaneDto() {
        // Given
        PlaneDto updateDto = new PlaneDto();
        updateDto.setPlaneCode("VN-001-UPDATED");
        updateDto.setPlaneType("Boeing 787-9");
        updateDto.setSeatQuantity(320);

        Plane updatedPlane = new Plane();
        updatedPlane.setPlaneId(1);
        updatedPlane.setPlaneCode("VN-001-UPDATED");
        updatedPlane.setPlaneType("Boeing 787-9");
        updatedPlane.setSeatQuantity(320);

        PlaneDto resultDto = new PlaneDto();
        resultDto.setPlaneId(1);
        resultDto.setPlaneCode("VN-001-UPDATED");
        resultDto.setPlaneType("Boeing 787-9");
        resultDto.setSeatQuantity(320);

        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenReturn(updatedPlane);
        when(planeMapper.toDto(updatedPlane)).thenReturn(resultDto);

        // When
        PlaneDto result = planeService.updatePlane(1, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("VN-001-UPDATED", result.getPlaneCode());
        assertEquals("Boeing 787-9", result.getPlaneType());
        assertEquals(320, result.getSeatQuantity());
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(testPlane);
        verify(planeMapper).toDto(updatedPlane);

        // Verify the original entity was modified
        assertEquals("VN-001-UPDATED", testPlane.getPlaneCode());
        assertEquals("Boeing 787-9", testPlane.getPlaneType());
        assertEquals(320, testPlane.getSeatQuantity());
    }

    @Test
    @Tag("updatePlane")
    void testUpdatePlane_NotFound_ThrowsRuntimeException() {
        // Given
        when(planeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.updatePlane(999, testPlaneDto));
        assertEquals("Plane not found with id: 999", exception.getMessage());
        verify(planeRepository).findActiveById(999);
        verify(planeRepository, never()).save(any());
        verify(planeMapper, never()).toDto(any());
    }

    @Test
    @Tag("updatePlane")
    void testUpdatePlane_NullValues_UpdatesWithNullValues() {
        // Given
        PlaneDto nullDto = new PlaneDto();
        nullDto.setPlaneCode(null);
        nullDto.setPlaneType(null);
        nullDto.setSeatQuantity(null);

        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenReturn(testPlane);
        when(planeMapper.toDto(testPlane)).thenReturn(testPlaneDto);

        // When
        PlaneDto result = planeService.updatePlane(1, nullDto);

        // Then
        assertNotNull(result);
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(testPlane);
        
        // Verify null values were set
        assertNull(testPlane.getPlaneCode());
        assertNull(testPlane.getPlaneType());
        assertNull(testPlane.getSeatQuantity());
    }

    @Test
    @Tag("updatePlane")
    void testUpdatePlane_ZeroSeatQuantity_AllowedByService() {
        // Given
        PlaneDto updateDto = new PlaneDto();
        updateDto.setPlaneCode("VN-001");
        updateDto.setPlaneType("Boeing 787");
        updateDto.setSeatQuantity(0);

        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenReturn(testPlane);
        when(planeMapper.toDto(testPlane)).thenReturn(testPlaneDto);

        // When
        PlaneDto result = planeService.updatePlane(1, updateDto);

        // Then
        assertNotNull(result);
        assertEquals(0, testPlane.getSeatQuantity());
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(testPlane);
    }

    @Test
    @Tag("updatePlane")
    void testUpdatePlane_RepositoryException_PropagatesException() {
        // Given
        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenThrow(new RuntimeException("Database save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.updatePlane(1, testPlaneDto));
        assertEquals("Database save failed", exception.getMessage());
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(testPlane);
    }

    @Test
    @Tag("updatePlane")
    void testUpdatePlane_MapperException_PropagatesException() {
        // Given
        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenReturn(testPlane);
        when(planeMapper.toDto(testPlane)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.updatePlane(1, testPlaneDto));
        assertEquals("Mapping failed", exception.getMessage());
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(testPlane);
        verify(planeMapper).toDto(testPlane);
    }

    // ================ DELETE PLANE TESTS ================

    @Test
    @Tag("deletePlane")
    void testDeletePlane_Success_SetsDeletedAt() {
        // Given
        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenReturn(testPlane);

        // When
        planeService.deletePlane(1);

        // Then
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(argThat(plane -> plane.getDeletedAt() != null));
        assertNotNull(testPlane.getDeletedAt());
        assertTrue(testPlane.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @Tag("deletePlane")
    void testDeletePlane_NotFound_ThrowsRuntimeException() {
        // Given
        when(planeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.deletePlane(999));
        assertEquals("Plane not found with id: 999", exception.getMessage());
        verify(planeRepository).findActiveById(999);
        verify(planeRepository, never()).save(any());
    }

    @Test
    @Tag("deletePlane")
    void testDeletePlane_AlreadyDeleted_UpdatesDeletedAt() {
        // Given - plane already has deletedAt set
        testPlane.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenReturn(testPlane);

        // When
        planeService.deletePlane(1);

        // Then - should still update deletedAt to current time
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(testPlane);
        assertTrue(testPlane.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @Tag("deletePlane")
    void testDeletePlane_RepositoryFindException_PropagatesException() {
        // Given
        when(planeRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.deletePlane(1));
        assertEquals("Database connection lost", exception.getMessage());
        verify(planeRepository).findActiveById(1);
        verify(planeRepository, never()).save(any());
    }

    @Test
    @Tag("deletePlane")
    void testDeletePlane_RepositorySaveException_PropagatesException() {
        // Given
        when(planeRepository.findActiveById(1)).thenReturn(Optional.of(testPlane));
        when(planeRepository.save(testPlane)).thenThrow(new RuntimeException("Save operation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.deletePlane(1));
        assertEquals("Save operation failed", exception.getMessage());
        verify(planeRepository).findActiveById(1);
        verify(planeRepository).save(testPlane);
    }

    @Test
    @Tag("deletePlane")
    void testDeletePlane_NullId_HandledByRepository() {
        // Given
        when(planeRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.deletePlane(null));
        assertTrue(exception.getMessage().contains("Plane not found with id: null"));
        verify(planeRepository).findActiveById(null);
    }

    // ================ GET PLANE BY CODE TESTS ================

    @Test
    @Tag("getPlaneByCode")
    void testGetPlaneByCode_Success_ReturnsPlaneDto() {
        // Given
        when(planeRepository.findByPlaneCode("VN-001")).thenReturn(Optional.of(testPlane));
        when(planeMapper.toDto(testPlane)).thenReturn(testPlaneDto);

        // When
        PlaneDto result = planeService.getPlaneByCode("VN-001");

        // Then
        assertNotNull(result);
        assertEquals("VN-001", result.getPlaneCode());
        assertEquals("Boeing 787", result.getPlaneType());
        verify(planeRepository).findByPlaneCode("VN-001");
        verify(planeMapper).toDto(testPlane);
    }

    @Test
    @Tag("getPlaneByCode")
    void testGetPlaneByCode_NotFound_ThrowsRuntimeException() {
        // Given
        when(planeRepository.findByPlaneCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneByCode("INVALID"));
        assertEquals("Plane not found with code: INVALID", exception.getMessage());
        verify(planeRepository).findByPlaneCode("INVALID");
        verify(planeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getPlaneByCode")
    void testGetPlaneByCode_NullCode_HandledByRepository() {
        // Given
        when(planeRepository.findByPlaneCode(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneByCode(null));
        assertEquals("Plane not found with code: null", exception.getMessage());
        verify(planeRepository).findByPlaneCode(null);
    }

    @Test
    @Tag("getPlaneByCode")
    void testGetPlaneByCode_EmptyString_HandledByRepository() {
        // Given
        when(planeRepository.findByPlaneCode("")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneByCode(""));
        assertEquals("Plane not found with code: ", exception.getMessage());
        verify(planeRepository).findByPlaneCode("");
    }

    @Test
    @Tag("getPlaneByCode")
    void testGetPlaneByCode_RepositoryException_PropagatesException() {
        // Given
        when(planeRepository.findByPlaneCode("VN-001")).thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneByCode("VN-001"));
        assertEquals("Database query failed", exception.getMessage());
        verify(planeRepository).findByPlaneCode("VN-001");
        verify(planeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getPlaneByCode")
    void testGetPlaneByCode_MapperException_PropagatesException() {
        // Given
        when(planeRepository.findByPlaneCode("VN-001")).thenReturn(Optional.of(testPlane));
        when(planeMapper.toDto(testPlane)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlaneByCode("VN-001"));
        assertEquals("Mapping failed", exception.getMessage());
        verify(planeRepository).findByPlaneCode("VN-001");
        verify(planeMapper).toDto(testPlane);
    }

    // ================ GET PLANES BY TYPE TESTS ================

    @Test
    @Tag("getPlanesByType")
    void testGetPlanesByType_Success_ReturnsMatchingPlanes() {
        // Given
        List<Plane> boeingPlanes = Arrays.asList(testPlane);
        List<PlaneDto> expectedDtos = Arrays.asList(testPlaneDto);

        when(planeRepository.findByPlaneType("Boeing 787")).thenReturn(boeingPlanes);
        when(planeMapper.toDtoList(boeingPlanes)).thenReturn(expectedDtos);

        // When
        List<PlaneDto> result = planeService.getPlanesByType("Boeing 787");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Boeing 787", result.get(0).getPlaneType());
        verify(planeRepository).findByPlaneType("Boeing 787");
        verify(planeMapper).toDtoList(boeingPlanes);
    }

    @Test
    @Tag("getPlanesByType")
    void testGetPlanesByType_MultipleResults_ReturnsAllMatching() {
        // Given
        Plane boeing1 = new Plane();
        boeing1.setPlaneId(1);
        boeing1.setPlaneCode("VN-001");
        boeing1.setPlaneType("Boeing 787");

        Plane boeing2 = new Plane();
        boeing2.setPlaneId(3);
        boeing2.setPlaneCode("VN-003");
        boeing2.setPlaneType("Boeing 787");

        List<Plane> boeingPlanes = Arrays.asList(boeing1, boeing2);
        List<PlaneDto> expectedDtos = Arrays.asList(testPlaneDto, testPlaneDto);

        when(planeRepository.findByPlaneType("Boeing 787")).thenReturn(boeingPlanes);
        when(planeMapper.toDtoList(boeingPlanes)).thenReturn(expectedDtos);

        // When
        List<PlaneDto> result = planeService.getPlanesByType("Boeing 787");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(planeRepository).findByPlaneType("Boeing 787");
    }

    @Test
    @Tag("getPlanesByType")
    void testGetPlanesByType_NoResults_ReturnsEmptyList() {
        // Given
        when(planeRepository.findByPlaneType("Nonexistent Type")).thenReturn(Collections.emptyList());
        when(planeMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<PlaneDto> result = planeService.getPlanesByType("Nonexistent Type");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planeRepository).findByPlaneType("Nonexistent Type");
        verify(planeMapper).toDtoList(Collections.emptyList());
    }

    @Test
    @Tag("getPlanesByType")
    void testGetPlanesByType_NullType_HandledByRepository() {
        // Given
        when(planeRepository.findByPlaneType(null)).thenReturn(Collections.emptyList());
        when(planeMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<PlaneDto> result = planeService.getPlanesByType(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planeRepository).findByPlaneType(null);
    }

    @Test
    @Tag("getPlanesByType")
    void testGetPlanesByType_EmptyString_HandledByRepository() {
        // Given
        when(planeRepository.findByPlaneType("")).thenReturn(Collections.emptyList());
        when(planeMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<PlaneDto> result = planeService.getPlanesByType("");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planeRepository).findByPlaneType("");
    }

    @Test
    @Tag("getPlanesByType")
    void testGetPlanesByType_RepositoryException_PropagatesException() {
        // Given
        when(planeRepository.findByPlaneType("Boeing 787")).thenThrow(new RuntimeException("Type search failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlanesByType("Boeing 787"));
        assertEquals("Type search failed", exception.getMessage());
        verify(planeRepository).findByPlaneType("Boeing 787");
        verify(planeMapper, never()).toDtoList(any());
    }

    @Test
    @Tag("getPlanesByType")
    void testGetPlanesByType_MapperException_PropagatesException() {
        // Given
        List<Plane> planes = Arrays.asList(testPlane);
        when(planeRepository.findByPlaneType("Boeing 787")).thenReturn(planes);
        when(planeMapper.toDtoList(planes)).thenThrow(new RuntimeException("List mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> planeService.getPlanesByType("Boeing 787"));
        assertEquals("List mapping failed", exception.getMessage());
        verify(planeRepository).findByPlaneType("Boeing 787");
        verify(planeMapper).toDtoList(planes);
    }
}