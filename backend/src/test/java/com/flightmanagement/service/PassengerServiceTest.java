package com.flightmanagement.service;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.Passenger;
import com.flightmanagement.mapper.PassengerMapper;
import com.flightmanagement.repository.PassengerRepository;
import com.flightmanagement.service.impl.PassengerServiceImpl;
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
 * Test class for PassengerService - Core function for Passenger Management
 *
 * Available Tags:
 * - getAllPassengers: Tests for retrieving all active passengers
 * - getPassengerById: Tests for retrieving passenger by ID
 * - getPassengerByCitizenId: Tests for retrieving passenger by citizen ID
 * - createPassenger: Tests for creating new passengers
 * - updatePassenger: Tests for updating passenger information
 * - deletePassenger: Tests for passenger deletion (soft delete)
 * - getPassengersByEmail: Tests for retrieving passengers by email
 * - searchPassengersByName: Tests for searching passengers by name
 */
@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger testPassenger;
    private PassengerDto testPassengerDto;
    private Passenger testPassenger2;
    private PassengerDto testPassengerDto2;

    @BeforeEach
    void setUp() {
        // Setup test passenger entity
        testPassenger = new Passenger();
        testPassenger.setPassengerId(1);
        testPassenger.setPassengerName("John Doe");
        testPassenger.setEmail("john.doe@email.com");
        testPassenger.setCitizenId("123456789");
        testPassenger.setPhoneNumber("0123456789");
        testPassenger.setDeletedAt(null);

        // Setup test passenger DTO
        testPassengerDto = new PassengerDto();
        testPassengerDto.setPassengerId(1);
        testPassengerDto.setPassengerName("John Doe");
        testPassengerDto.setEmail("john.doe@email.com");
        testPassengerDto.setCitizenId("123456789");
        testPassengerDto.setPhoneNumber("0123456789");

        // Setup second test passenger
        testPassenger2 = new Passenger();
        testPassenger2.setPassengerId(2);
        testPassenger2.setPassengerName("Jane Smith");
        testPassenger2.setEmail("jane.smith@email.com");
        testPassenger2.setCitizenId("987654321");
        testPassenger2.setPhoneNumber("0987654321");
        testPassenger2.setDeletedAt(null);

        testPassengerDto2 = new PassengerDto();
        testPassengerDto2.setPassengerId(2);
        testPassengerDto2.setPassengerName("Jane Smith");
        testPassengerDto2.setEmail("jane.smith@email.com");
        testPassengerDto2.setCitizenId("987654321");
        testPassengerDto2.setPhoneNumber("0987654321");
    }

    // ================ GET ALL PASSENGERS TESTS ================

    @Test
    @Tag("getAllPassengers")
    void testGetAllPassengers_Success_ReturnsPassengerList() {
        // Given
        List<Passenger> mockPassengers = Arrays.asList(testPassenger, testPassenger2);
        List<PassengerDto> expectedDtos = Arrays.asList(testPassengerDto, testPassengerDto2);

        when(passengerRepository.findAllActive()).thenReturn(mockPassengers);
        when(passengerMapper.toDtoList(mockPassengers)).thenReturn(expectedDtos);

        // When
        List<PassengerDto> result = passengerService.getAllPassengers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getPassengerName());
        assertEquals("Jane Smith", result.get(1).getPassengerName());
        verify(passengerRepository).findAllActive();
        verify(passengerMapper).toDtoList(mockPassengers);
    }

    @Test
    @Tag("getAllPassengers")
    void testGetAllPassengers_EmptyDatabase_ReturnsEmptyList() {
        // Given
        when(passengerRepository.findAllActive()).thenReturn(Collections.emptyList());
        when(passengerMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<PassengerDto> result = passengerService.getAllPassengers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(passengerRepository).findAllActive();
        verify(passengerMapper).toDtoList(Collections.emptyList());
    }

    @Test
    @Tag("getAllPassengers")
    void testGetAllPassengers_RepositoryException_PropagatesException() {
        // Given
        when(passengerRepository.findAllActive()).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getAllPassengers());
        assertEquals("Database connection lost", exception.getMessage());
        verify(passengerRepository).findAllActive();
        verify(passengerMapper, never()).toDtoList(any());
    }

    @Test
    @Tag("getAllPassengers")
    void testGetAllPassengers_MapperException_PropagatesException() {
        // Given
        List<Passenger> mockPassengers = Arrays.asList(testPassenger);
        when(passengerRepository.findAllActive()).thenReturn(mockPassengers);
        when(passengerMapper.toDtoList(mockPassengers)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getAllPassengers());
        assertEquals("Mapping error", exception.getMessage());
        verify(passengerRepository).findAllActive();
        verify(passengerMapper).toDtoList(mockPassengers);
    }

    @Test
    @Tag("getAllPassengers")
    void testGetAllPassengers_LargeDataset_HandlesEfficiently() {
        // Given - Simulate large dataset
        List<Passenger> largePassengerList = Arrays.asList(testPassenger, testPassenger2, testPassenger, testPassenger2, testPassenger);
        List<PassengerDto> largeDtoList = Arrays.asList(testPassengerDto, testPassengerDto2, testPassengerDto, testPassengerDto2, testPassengerDto);

        when(passengerRepository.findAllActive()).thenReturn(largePassengerList);
        when(passengerMapper.toDtoList(largePassengerList)).thenReturn(largeDtoList);

        // When
        List<PassengerDto> result = passengerService.getAllPassengers();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(passengerRepository).findAllActive();
        verify(passengerMapper).toDtoList(largePassengerList);
    }

    @Test
    @Tag("getAllPassengers")
    void testGetAllPassengers_SinglePassenger_ReturnsListWithOneElement() {
        // Given
        List<Passenger> singlePassenger = Arrays.asList(testPassenger);
        List<PassengerDto> singleDto = Arrays.asList(testPassengerDto);

        when(passengerRepository.findAllActive()).thenReturn(singlePassenger);
        when(passengerMapper.toDtoList(singlePassenger)).thenReturn(singleDto);

        // When
        List<PassengerDto> result = passengerService.getAllPassengers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getPassengerName());
        verify(passengerRepository).findAllActive();
        verify(passengerMapper).toDtoList(singlePassenger);
    }

    // ================ GET PASSENGER BY ID TESTS ================

    @Test
    @Tag("getPassengerById")
    void testGetPassengerById_Success_ReturnsPassengerDto() {
        // Given
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // When
        PassengerDto result = passengerService.getPassengerById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPassengerId());
        assertEquals("John Doe", result.getPassengerName());
        assertEquals("john.doe@email.com", result.getEmail());
        assertEquals("123456789", result.getCitizenId());
        verify(passengerRepository).findActiveById(1);
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    @Tag("getPassengerById")
    void testGetPassengerById_NotFound_ThrowsRuntimeException() {
        // Given
        when(passengerRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengerById(999));
        assertEquals("Passenger not found with id: 999", exception.getMessage());
        verify(passengerRepository).findActiveById(999);
        verify(passengerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getPassengerById")
    void testGetPassengerById_NullId_HandledByRepository() {
        // Given
        when(passengerRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengerById(null));
        assertTrue(exception.getMessage().contains("Passenger not found with id: null"));
        verify(passengerRepository).findActiveById(null);
    }

    @Test
    @Tag("getPassengerById")
    void testGetPassengerById_RepositoryException_PropagatesException() {
        // Given
        when(passengerRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengerById(1));
        assertEquals("Database connection failed", exception.getMessage());
        verify(passengerRepository).findActiveById(1);
        verify(passengerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getPassengerById")
    void testGetPassengerById_MapperException_PropagatesException() {
        // Given
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerMapper.toDto(testPassenger)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengerById(1));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(passengerRepository).findActiveById(1);
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    @Tag("getPassengerById")
    void testGetPassengerById_SpecialCharactersInData_ReturnsCorrectDto() {
        // Given
        testPassenger.setPassengerName("José María O'Connor");
        testPassengerDto.setPassengerName("José María O'Connor");

        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // When
        PassengerDto result = passengerService.getPassengerById(1);

        // Then
        assertNotNull(result);
        assertEquals("José María O'Connor", result.getPassengerName());
        verify(passengerRepository).findActiveById(1);
        verify(passengerMapper).toDto(testPassenger);
    }

    // ================ GET PASSENGER BY CITIZEN ID TESTS ================

    @Test
    @Tag("getPassengerByCitizenId")
    void testGetPassengerByCitizenId_Success_ReturnsPassengerDto() {
        // Given
        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.of(testPassenger));
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // When
        PassengerDto result = passengerService.getPassengerByCitizenId("123456789");

        // Then
        assertNotNull(result);
        assertEquals("123456789", result.getCitizenId());
        assertEquals("John Doe", result.getPassengerName());
        verify(passengerRepository).findByCitizenId("123456789");
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    @Tag("getPassengerByCitizenId")
    void testGetPassengerByCitizenId_NotFound_ReturnsNull() {
        // Given
        when(passengerRepository.findByCitizenId("nonexistent")).thenReturn(Optional.empty());

        // When
        PassengerDto result = passengerService.getPassengerByCitizenId("nonexistent");

        // Then
        assertNull(result);
        verify(passengerRepository).findByCitizenId("nonexistent");
        verify(passengerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getPassengerByCitizenId")
    void testGetPassengerByCitizenId_NullCitizenId_HandledByRepository() {
        // Given
        when(passengerRepository.findByCitizenId(null)).thenReturn(Optional.empty());

        // When
        PassengerDto result = passengerService.getPassengerByCitizenId(null);

        // Then
        assertNull(result);
        verify(passengerRepository).findByCitizenId(null);
    }

    @Test
    @Tag("getPassengerByCitizenId")
    void testGetPassengerByCitizenId_EmptyCitizenId_ReturnsNull() {
        // Given
        when(passengerRepository.findByCitizenId("")).thenReturn(Optional.empty());

        // When
        PassengerDto result = passengerService.getPassengerByCitizenId("");

        // Then
        assertNull(result);
        verify(passengerRepository).findByCitizenId("");
    }

    @Test
    @Tag("getPassengerByCitizenId")
    void testGetPassengerByCitizenId_RepositoryException_PropagatesException() {
        // Given
        when(passengerRepository.findByCitizenId("123456789")).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengerByCitizenId("123456789"));
        assertEquals("Database error", exception.getMessage());
        verify(passengerRepository).findByCitizenId("123456789");
    }

    @Test
    @Tag("getPassengerByCitizenId")
    void testGetPassengerByCitizenId_MapperException_PropagatesException() {
        // Given
        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.of(testPassenger));
        when(passengerMapper.toDto(testPassenger)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengerByCitizenId("123456789"));
        assertEquals("Mapping failed", exception.getMessage());
        verify(passengerRepository).findByCitizenId("123456789");
        verify(passengerMapper).toDto(testPassenger);
    }

    // ================ CREATE PASSENGER TESTS ================

    @Test
    @Tag("createPassenger")
    void testCreatePassenger_Success_ReturnsCreatedPassengerDto() {
        // Given
        PassengerDto newPassengerDto = new PassengerDto();
        newPassengerDto.setPassengerName("New Passenger");
        newPassengerDto.setEmail("new@email.com");
        newPassengerDto.setCitizenId("555666777");
        newPassengerDto.setPhoneNumber("0555666777");

        Passenger savedPassenger = new Passenger();
        savedPassenger.setPassengerId(3);
        savedPassenger.setPassengerName("New Passenger");
        savedPassenger.setEmail("new@email.com");
        savedPassenger.setCitizenId("555666777");
        savedPassenger.setPhoneNumber("0555666777");

        PassengerDto resultDto = new PassengerDto();
        resultDto.setPassengerId(3);
        resultDto.setPassengerName("New Passenger");
        resultDto.setEmail("new@email.com");
        resultDto.setCitizenId("555666777");
        resultDto.setPhoneNumber("0555666777");

        when(passengerRepository.findByCitizenId("555666777")).thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenReturn(savedPassenger);
        when(passengerMapper.toDto(savedPassenger)).thenReturn(resultDto);

        // When
        PassengerDto result = passengerService.createPassenger(newPassengerDto);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getPassengerId());
        assertEquals("New Passenger", result.getPassengerName());
        assertEquals("new@email.com", result.getEmail());
        assertEquals("555666777", result.getCitizenId());

        verify(passengerRepository).findByCitizenId("555666777");
        verify(passengerRepository).save(argThat(passenger ->
            passenger.getPassengerName().equals("New Passenger") &&
            passenger.getEmail().equals("new@email.com") &&
            passenger.getCitizenId().equals("555666777") &&
            passenger.getDeletedAt() == null
        ));
        verify(passengerMapper).toDto(savedPassenger);
    }

    @Test
    @Tag("createPassenger")
    void testCreatePassenger_DuplicateCitizenId_ThrowsRuntimeException() {
        // Given
        PassengerDto newPassengerDto = new PassengerDto();
        newPassengerDto.setCitizenId("123456789"); // Same as existing passenger

        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.of(testPassenger));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.createPassenger(newPassengerDto));
        assertEquals("Passenger already exists with citizen ID: 123456789", exception.getMessage());
        verify(passengerRepository).findByCitizenId("123456789");
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @Tag("createPassenger")
    void testCreatePassenger_NullCitizenId_HandledByRepository() {
        // Given
        PassengerDto newPassengerDto = new PassengerDto();
        newPassengerDto.setPassengerName("Test");
        newPassengerDto.setCitizenId(null);

        when(passengerRepository.findByCitizenId(null)).thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // When
        PassengerDto result = passengerService.createPassenger(newPassengerDto);

        // Then
        assertNotNull(result);
        verify(passengerRepository).findByCitizenId(null);
        verify(passengerRepository).save(any(Passenger.class));
    }

    @Test
    @Tag("createPassenger")
    void testCreatePassenger_RepositoryException_PropagatesException() {
        // Given
        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenThrow(new RuntimeException("Save operation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.createPassenger(testPassengerDto));
        assertEquals("Save operation failed", exception.getMessage());
        verify(passengerRepository).findByCitizenId("123456789");
        verify(passengerRepository).save(any(Passenger.class));
    }

    @Test
    @Tag("createPassenger")
    void testCreatePassenger_MapperException_PropagatesException() {
        // Given
        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.createPassenger(testPassengerDto));
        assertEquals("Mapping failed", exception.getMessage());
        verify(passengerRepository).save(any(Passenger.class));
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    @Tag("createPassenger")
    void testCreatePassenger_SpecialCharacters_HandlesCorrectly() {
        // Given
        PassengerDto specialPassenger = new PassengerDto();
        specialPassenger.setPassengerName("José María O'Connor-Smith");
        specialPassenger.setEmail("jose.maria@email.com");
        specialPassenger.setCitizenId("XYZ-123-456");

        when(passengerRepository.findByCitizenId("XYZ-123-456")).thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // When
        PassengerDto result = passengerService.createPassenger(specialPassenger);

        // Then
        assertNotNull(result);
        verify(passengerRepository).findByCitizenId("XYZ-123-456");
        verify(passengerRepository).save(argThat(passenger ->
            passenger.getPassengerName().equals("José María O'Connor-Smith")
        ));
    }

    // ================ UPDATE PASSENGER TESTS ================

    @Test
    @Tag("updatePassenger")
    void testUpdatePassenger_Success_ReturnsUpdatedPassengerDto() {
        // Given
        PassengerDto updateRequest = new PassengerDto();
        updateRequest.setPassengerName("Updated Name");
        updateRequest.setEmail("updated@email.com");
        updateRequest.setPhoneNumber("0999888777");

        Passenger updatedPassenger = new Passenger();
        updatedPassenger.setPassengerId(1);
        updatedPassenger.setPassengerName("Updated Name");
        updatedPassenger.setEmail("updated@email.com");
        updatedPassenger.setPhoneNumber("0999888777");
        updatedPassenger.setCitizenId("123456789"); // CitizenId should not change

        PassengerDto resultDto = new PassengerDto();
        resultDto.setPassengerId(1);
        resultDto.setPassengerName("Updated Name");
        resultDto.setEmail("updated@email.com");
        resultDto.setPhoneNumber("0999888777");
        resultDto.setCitizenId("123456789");

        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenReturn(updatedPassenger);
        when(passengerMapper.toDto(updatedPassenger)).thenReturn(resultDto);

        // When
        PassengerDto result = passengerService.updatePassenger(1, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getPassengerName());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals("0999888777", result.getPhoneNumber());
        assertEquals("123456789", result.getCitizenId()); // Should remain unchanged

        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);
        verify(passengerMapper).toDto(updatedPassenger);

        // Verify the original entity was modified
        assertEquals("Updated Name", testPassenger.getPassengerName());
        assertEquals("updated@email.com", testPassenger.getEmail());
        assertEquals("0999888777", testPassenger.getPhoneNumber());
    }

    @Test
    @Tag("updatePassenger")
    void testUpdatePassenger_NotFound_ThrowsRuntimeException() {
        // Given
        when(passengerRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.updatePassenger(999, testPassengerDto));
        assertEquals("Passenger not found with id: 999", exception.getMessage());
        verify(passengerRepository).findActiveById(999);
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @Tag("updatePassenger")
    void testUpdatePassenger_NullFields_HandlesGracefully() {
        // Given
        PassengerDto updateRequest = new PassengerDto();
        updateRequest.setPassengerName(null);
        updateRequest.setEmail(null);
        updateRequest.setPhoneNumber(null);

        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // When
        PassengerDto result = passengerService.updatePassenger(1, updateRequest);

        // Then
        assertNotNull(result);
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);

        // Verify null values were set
        assertNull(testPassenger.getPassengerName());
        assertNull(testPassenger.getEmail());
        assertNull(testPassenger.getPhoneNumber());
    }

    @Test
    @Tag("updatePassenger")
    void testUpdatePassenger_CitizenIdNotUpdated_RemainsUnchanged() {
        // Given - CitizenId should not be updated in the service implementation
        String originalCitizenId = testPassenger.getCitizenId();

        PassengerDto updateRequest = new PassengerDto();
        updateRequest.setPassengerName("Updated Name");
        updateRequest.setCitizenId("999888777"); // This should be ignored

        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // When
        PassengerDto result = passengerService.updatePassenger(1, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(originalCitizenId, testPassenger.getCitizenId()); // Should remain unchanged
        assertEquals("Updated Name", testPassenger.getPassengerName());
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);
    }

    @Test
    @Tag("updatePassenger")
    void testUpdatePassenger_RepositoryException_PropagatesException() {
        // Given
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenThrow(new RuntimeException("Update failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.updatePassenger(1, testPassengerDto));
        assertEquals("Update failed", exception.getMessage());
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);
    }

    @Test
    @Tag("updatePassenger")
    void testUpdatePassenger_MapperException_PropagatesException() {
        // Given
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.updatePassenger(1, testPassengerDto));
        assertEquals("Mapping failed", exception.getMessage());
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);
        verify(passengerMapper).toDto(testPassenger);
    }

    // ================ DELETE PASSENGER TESTS ================

    @Test
    @Tag("deletePassenger")
    void testDeletePassenger_Success_SetsDeletedAt() {
        // Given
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenReturn(testPassenger);

        // When
        passengerService.deletePassenger(1);

        // Then
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(argThat(passenger -> passenger.getDeletedAt() != null));
        assertNotNull(testPassenger.getDeletedAt());
        assertTrue(testPassenger.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @Tag("deletePassenger")
    void testDeletePassenger_NotFound_ThrowsRuntimeException() {
        // Given
        when(passengerRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.deletePassenger(999));
        assertEquals("Passenger not found with id: 999", exception.getMessage());
        verify(passengerRepository).findActiveById(999);
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @Tag("deletePassenger")
    void testDeletePassenger_AlreadyDeleted_UpdatesDeletedAt() {
        // Given - passenger already has deletedAt set
        testPassenger.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenReturn(testPassenger);

        // When
        passengerService.deletePassenger(1);

        // Then - should still update deletedAt to current time
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);
        assertTrue(testPassenger.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @Tag("deletePassenger")
    void testDeletePassenger_NullId_HandledByRepository() {
        // Given
        when(passengerRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.deletePassenger(null));
        assertTrue(exception.getMessage().contains("Passenger not found with id: null"));
        verify(passengerRepository).findActiveById(null);
    }

    @Test
    @Tag("deletePassenger")
    void testDeletePassenger_RepositoryFindException_PropagatesException() {
        // Given
        when(passengerRepository.findActiveById(1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.deletePassenger(1));
        assertEquals("Database error", exception.getMessage());
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository, never()).save(any());
    }

    @Test
    @Tag("deletePassenger")
    void testDeletePassenger_RepositorySaveException_PropagatesException() {
        // Given
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(testPassenger)).thenThrow(new RuntimeException("Save operation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.deletePassenger(1));
        assertEquals("Save operation failed", exception.getMessage());
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);
    }

    // ================ GET PASSENGERS BY EMAIL TESTS ================

//    @Test
//    @Tag("getPassengersByEmail")
//    void testGetPassengersByEmail_Success_ReturnsPassengerList() {
//        // Given
//        String email = "john.doe@email.com";
//        List<Passenger> mockPassengers = Arrays.asList(testPassenger);
//        List<PassengerDto> expectedDtos = Arrays.asList(testPassengerDto);
//
//        when(passengerRepository.findByEmail(email)).thenReturn(mockPassengers);
//        when(passengerMapper.toDtoList(mockPassengers)).thenReturn(expectedDtos);
//
//        // When
//        List<PassengerDto> result = passengerService.getPassengersByEmail(email);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(email, result.get(0).getEmail());
//        verify(passengerRepository).findByEmail(email);
//        verify(passengerMapper).toDtoList(mockPassengers);
//    }

//    @Test
//    @Tag("getPassengersByEmail")
//    void testGetPassengersByEmail_MultiplePassengers_ReturnsAllMatching() {
//        // Given - Multiple passengers with same email
//        String email = "shared@email.com";
//        testPassenger2.setEmail(email);
//        List<Passenger> mockPassengers = Arrays.asList(testPassenger, testPassenger2);
//        List<PassengerDto> expectedDtos = Arrays.asList(testPassengerDto, testPassengerDto2);
//
//        when(passengerRepository.findByEmail(email)).thenReturn(mockPassengers);
//        when(passengerMapper.toDtoList(mockPassengers)).thenReturn(expectedDtos);
//
//        // When
//        List<PassengerDto> result = passengerService.getPassengersByEmail(email);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(passengerRepository).findByEmail(email);
//        verify(passengerMapper).toDtoList(mockPassengers);
//    }

//    @Test
//    @Tag("getPassengersByEmail")
//    void testGetPassengersByEmail_NotFound_ReturnsEmptyList() {
//        // Given
//        String email = "nonexistent@email.com";
//        when(passengerRepository.findByEmail(email)).thenReturn(Collections.emptyList());
//        when(passengerMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
//
//        // When
//        List<PassengerDto> result = passengerService.getPassengersByEmail(email);
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(passengerRepository).findByEmail(email);
//        verify(passengerMapper).toDtoList(Collections.emptyList());
//    }
//
//    @Test
//    @Tag("getPassengersByEmail")
//    void testGetPassengersByEmail_NullEmail_HandledByRepository() {
//        // Given
//        when(passengerRepository.findByEmail(null)).thenReturn(Collections.emptyList());
//        when(passengerMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
//
//        // When
//        List<PassengerDto> result = passengerService.getPassengersByEmail(null);
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(passengerRepository).findByEmail(null);
//    }

    @Test
    @Tag("getPassengersByEmail")
    void testGetPassengersByEmail_RepositoryException_PropagatesException() {
        // Given
        when(passengerRepository.findByEmail("test@email.com")).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengersByEmail("test@email.com"));
        assertEquals("Database error", exception.getMessage());
        verify(passengerRepository).findByEmail("test@email.com");
    }

//    @Test
//    @Tag("getPassengersByEmail")
//    void testGetPassengersByEmail_MapperException_PropagatesException() {
//        // Given
//        List<Passenger> mockPassengers = Arrays.asList(testPassenger);
//        when(passengerRepository.findByEmail("test@email.com")).thenReturn(mockPassengers);
//        when(passengerMapper.toDtoList(mockPassengers)).thenThrow(new RuntimeException("Mapping failed"));
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> passengerService.getPassengersByEmail("test@email.com"));
//        assertEquals("Mapping failed", exception.getMessage());
//        verify(passengerRepository).findByEmail("test@email.com");
//        verify(passengerMapper).toDtoList(mockPassengers);
//    }

    // ================ SEARCH PASSENGERS BY NAME TESTS ================

    @Test
    @Tag("searchPassengersByName")
    void testSearchPassengersByName_Success_ReturnsMatchingPassengers() {
        // Given
        String searchName = "John";
        List<Passenger> mockPassengers = Arrays.asList(testPassenger);
        List<PassengerDto> expectedDtos = Arrays.asList(testPassengerDto);

        when(passengerRepository.findByPassengerNameContainingIgnoreCase(searchName)).thenReturn(mockPassengers);
        when(passengerMapper.toDtoList(mockPassengers)).thenReturn(expectedDtos);

        // When
        List<PassengerDto> result = passengerService.searchPassengersByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPassengerName().contains("John"));
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase(searchName);
        verify(passengerMapper).toDtoList(mockPassengers);
    }

    @Test
    @Tag("searchPassengersByName")
    void testSearchPassengersByName_PartialMatch_ReturnsAllMatching() {
        // Given
        String searchName = "o"; // Should match "John Doe"
        List<Passenger> mockPassengers = Arrays.asList(testPassenger);
        List<PassengerDto> expectedDtos = Arrays.asList(testPassengerDto);

        when(passengerRepository.findByPassengerNameContainingIgnoreCase(searchName)).thenReturn(mockPassengers);
        when(passengerMapper.toDtoList(mockPassengers)).thenReturn(expectedDtos);

        // When
        List<PassengerDto> result = passengerService.searchPassengersByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase(searchName);
        verify(passengerMapper).toDtoList(mockPassengers);
    }

    @Test
    @Tag("searchPassengersByName")
    void testSearchPassengersByName_NoMatch_ReturnsEmptyList() {
        // Given
        String searchName = "NonExistentName";
        when(passengerRepository.findByPassengerNameContainingIgnoreCase(searchName)).thenReturn(Collections.emptyList());
        when(passengerMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<PassengerDto> result = passengerService.searchPassengersByName(searchName);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase(searchName);
        verify(passengerMapper).toDtoList(Collections.emptyList());
    }

    @Test
    @Tag("searchPassengersByName")
    void testSearchPassengersByName_EmptyString_HandledByRepository() {
        // Given
        when(passengerRepository.findByPassengerNameContainingIgnoreCase("")).thenReturn(Arrays.asList(testPassenger, testPassenger2));
        when(passengerMapper.toDtoList(any())).thenReturn(Arrays.asList(testPassengerDto, testPassengerDto2));

        // When
        List<PassengerDto> result = passengerService.searchPassengersByName("");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Empty string might match all passengers
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase("");
    }

    @Test
    @Tag("searchPassengersByName")
    void testSearchPassengersByName_NullName_HandledByRepository() {
        // Given
        when(passengerRepository.findByPassengerNameContainingIgnoreCase(null)).thenReturn(Collections.emptyList());
        when(passengerMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<PassengerDto> result = passengerService.searchPassengersByName(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase(null);
    }

    @Test
    @Tag("searchPassengersByName")
    void testSearchPassengersByName_RepositoryException_PropagatesException() {
        // Given
        when(passengerRepository.findByPassengerNameContainingIgnoreCase("John")).thenThrow(new RuntimeException("Search failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.searchPassengersByName("John"));
        assertEquals("Search failed", exception.getMessage());
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase("John");
    }

    @Test
    @Tag("searchPassengersByName")
    void testSearchPassengersByName_MapperException_PropagatesException() {
        // Given
        List<Passenger> mockPassengers = Arrays.asList(testPassenger);
        when(passengerRepository.findByPassengerNameContainingIgnoreCase("John")).thenReturn(mockPassengers);
        when(passengerMapper.toDtoList(mockPassengers)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.searchPassengersByName("John"));
        assertEquals("Mapping failed", exception.getMessage());
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase("John");
        verify(passengerMapper).toDtoList(mockPassengers);
    }
}