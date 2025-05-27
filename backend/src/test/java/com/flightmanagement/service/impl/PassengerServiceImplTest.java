package com.flightmanagement.service.impl;

import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.entity.Passenger;
import com.flightmanagement.mapper.PassengerMapper;
import com.flightmanagement.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger testPassenger;
    private PassengerDto testPassengerDto;

    @BeforeEach
    void setUp() {
        testPassenger = new Passenger();
        testPassenger.setPassengerId(1);
        testPassenger.setPassengerName("John Doe");
        testPassenger.setEmail("john.doe@email.com");
        testPassenger.setCitizenId("123456789");
        testPassenger.setPhoneNumber("+1234567890");

        testPassengerDto = new PassengerDto();
        testPassengerDto.setPassengerId(1);
        testPassengerDto.setPassengerName("John Doe");
        testPassengerDto.setEmail("john.doe@email.com");
        testPassengerDto.setCitizenId("123456789");
        testPassengerDto.setPhoneNumber("+1234567890");
    }

    @Test
    void getAllPassengers_ShouldReturnDtoList() {
        // Arrange
        List<Passenger> entities = Arrays.asList(testPassenger);
        List<PassengerDto> expectedDtos = Arrays.asList(testPassengerDto);

        when(passengerRepository.findAllActive()).thenReturn(entities);
        when(passengerMapper.toDtoList(entities)).thenReturn(expectedDtos);

        // Act
        List<PassengerDto> result = passengerService.getAllPassengers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPassengerDto.getPassengerName(), result.get(0).getPassengerName());
        verify(passengerRepository).findAllActive();
        verify(passengerMapper).toDtoList(entities);
    }

    @Test
    void getPassengerById_WhenExists_ShouldReturnDto() {
        // Arrange
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // Act
        PassengerDto result = passengerService.getPassengerById(1);

        // Assert
        assertNotNull(result);
        assertEquals(testPassengerDto.getPassengerId(), result.getPassengerId());
        assertEquals(testPassengerDto.getPassengerName(), result.getPassengerName());
        verify(passengerRepository).findActiveById(1);
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    void getPassengerById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(passengerRepository.findActiveById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> passengerService.getPassengerById(1));
        
        assertTrue(exception.getMessage().contains("Passenger not found"));
        verify(passengerRepository).findActiveById(1);
        verify(passengerMapper, never()).toDto(any());
    }

    @Test
    void getPassengerByCitizenId_WhenExists_ShouldReturnDto() {
        // Arrange
        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.of(testPassenger));
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // Act
        PassengerDto result = passengerService.getPassengerByCitizenId("123456789");

        // Assert
        assertNotNull(result);
        assertEquals(testPassengerDto.getCitizenId(), result.getCitizenId());
        verify(passengerRepository).findByCitizenId("123456789");
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    void createPassenger_WithUniqueData_ShouldCreateSuccessfully() {
        // Arrange
        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.empty());
        when(passengerRepository.save(any(Passenger.class))).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // Act
        PassengerDto result = passengerService.createPassenger(testPassengerDto);

        // Assert
        assertNotNull(result);
        assertEquals(testPassengerDto.getPassengerName(), result.getPassengerName());
        verify(passengerRepository).findByCitizenId("123456789");
        verify(passengerRepository).save(any(Passenger.class));
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    void createPassenger_WithDuplicateCitizenId_ShouldThrowException() {
        // Arrange
        when(passengerRepository.findByCitizenId("123456789")).thenReturn(Optional.of(testPassenger));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> passengerService.createPassenger(testPassengerDto));
        
        assertTrue(exception.getMessage().contains("Passenger already exists"));
        verify(passengerRepository).findByCitizenId("123456789");
        verify(passengerRepository, never()).save(any());
    }

    @Test
    void updatePassenger_WithValidId_ShouldUpdateSuccessfully() {
        // Arrange
        testPassengerDto.setPassengerName("John Smith");
        testPassengerDto.setEmail("john.smith@email.com");

        when(passengerRepository.findActiveById(1)).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(testPassenger);
        when(passengerMapper.toDto(testPassenger)).thenReturn(testPassengerDto);

        // Act
        PassengerDto result = passengerService.updatePassenger(1, testPassengerDto);

        // Assert
        assertNotNull(result);
        verify(passengerRepository).findActiveById(1);
        verify(passengerRepository).save(testPassenger);
        verify(passengerMapper).toDto(testPassenger);
    }

    @Test
    void searchPassengersByName_ShouldReturnMatchingPassengers() {
        // Arrange
        List<Passenger> entities = Arrays.asList(testPassenger);
        List<PassengerDto> expectedDtos = Arrays.asList(testPassengerDto);

        when(passengerRepository.findByPassengerNameContainingIgnoreCase("John"))
                .thenReturn(entities);
        when(passengerMapper.toDtoList(entities)).thenReturn(expectedDtos);

        // Act
        List<PassengerDto> result = passengerService.searchPassengersByName("John");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPassengerDto.getPassengerName(), result.get(0).getPassengerName());
        verify(passengerRepository).findByPassengerNameContainingIgnoreCase("John");
        verify(passengerMapper).toDtoList(entities);
    }
}
