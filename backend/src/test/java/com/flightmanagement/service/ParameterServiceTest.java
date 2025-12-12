package com.flightmanagement.service;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.entity.Parameter;
import com.flightmanagement.mapper.ParameterMapper;
import com.flightmanagement.repository.ParameterRepository;
import com.flightmanagement.service.impl.ParameterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParameterServiceTest {

    @Mock
    private ParameterRepository parameterRepository;

    @Mock
    private ParameterMapper parameterMapper;

    @InjectMocks
    private ParameterServiceImpl parameterService;

    private Parameter validParameter;
    private ParameterDto validParameterDto;

    @BeforeEach
    void setUp() {
        // Setup valid parameter entity
        validParameter = new Parameter();
        validParameter.setId(1);
        validParameter.setMaxMediumAirport(2);
        validParameter.setMinFlightDuration(30);
        validParameter.setMinLayoverDuration(30);
        validParameter.setMaxLayoverDuration(720);
        validParameter.setMinBookingInAdvanceDuration(1);
        validParameter.setMaxBookingHoldDuration(24);
        validParameter.setDeletedAt(null);

        // Setup valid parameter DTO
        validParameterDto = new ParameterDto();
        validParameterDto.setId(1);
        validParameterDto.setMaxMediumAirport(2);
        validParameterDto.setMinFlightDuration(30);
        validParameterDto.setMinLayoverDuration(30);
        validParameterDto.setMaxLayoverDuration(720);
        validParameterDto.setMinBookingInAdvanceDuration(1);
        validParameterDto.setMaxBookingHoldDuration(24);
    }

    // ==================== getLatestParameter Tests ====================

    @Nested
    @DisplayName("GetLatestParameter Tests")
    @Tag("getLatestParameter")
    class GetLatestParameterTests {

        @Test
        @Tag("getLatestParameter")
        @DisplayName("Get latest parameter - Returns parameter DTO")
        void getLatestParameter_WithExistingParameter_ReturnsParameterDto() {
            // Arrange
            when(parameterRepository.findLatestParameter()).thenReturn(Optional.of(validParameter));
            when(parameterMapper.toDto(validParameter)).thenReturn(validParameterDto);

            // Act
            ParameterDto result = parameterService.getLatestParameter();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals(2, result.getMaxMediumAirport());
            assertEquals(30, result.getMinFlightDuration());
            assertEquals(30, result.getMinLayoverDuration());
            assertEquals(720, result.getMaxLayoverDuration());
            assertEquals(1, result.getMinBookingInAdvanceDuration());
            assertEquals(24, result.getMaxBookingHoldDuration());
            
            verify(parameterRepository).findLatestParameter();
            verify(parameterMapper).toDto(validParameter);
        }

        @Test
        @Tag("getLatestParameter")
        @DisplayName("Get latest parameter when none exists - Throws exception")
        void getLatestParameter_NoParameterExists_ThrowsException() {
            // Arrange
            when(parameterRepository.findLatestParameter()).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                parameterService.getLatestParameter();
            });

            assertEquals("No parameters found", exception.getMessage());
            verify(parameterRepository).findLatestParameter();
            verify(parameterMapper, never()).toDto(any());
        }

        @Test
        @Tag("getLatestParameter")
        @DisplayName("Repository throws exception - Propagates exception")
        void getLatestParameter_RepositoryThrowsException_PropagatesException() {
            // Arrange
            when(parameterRepository.findLatestParameter())
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                parameterService.getLatestParameter();
            });

            assertEquals("Database error", exception.getMessage());
            verify(parameterRepository).findLatestParameter();
        }
    }

    // ==================== updateParameters Tests ====================

    @Nested
    @DisplayName("UpdateParameters Tests")
    @Tag("updateParameters")
    class UpdateParametersTests {

        @Test
        @Tag("updateParameters")
        @DisplayName("Update parameters - Deletes old and creates new")
        void updateParameters_WithValidDto_DeletesOldAndCreatesNew() {
            // Arrange
            Parameter newParameter = new Parameter();
            newParameter.setId(null);
            newParameter.setMaxMediumAirport(3);
            newParameter.setMinFlightDuration(45);
            newParameter.setMinLayoverDuration(40);
            newParameter.setMaxLayoverDuration(800);
            newParameter.setMinBookingInAdvanceDuration(2);
            newParameter.setMaxBookingHoldDuration(48);
            newParameter.setDeletedAt(null);

            Parameter savedParameter = new Parameter();
            savedParameter.setId(2);
            savedParameter.setMaxMediumAirport(3);
            savedParameter.setMinFlightDuration(45);
            savedParameter.setMinLayoverDuration(40);
            savedParameter.setMaxLayoverDuration(800);
            savedParameter.setMinBookingInAdvanceDuration(2);
            savedParameter.setMaxBookingHoldDuration(48);

            ParameterDto inputDto = new ParameterDto(1, 3, 45, 40, 800, 2, 48);
            ParameterDto resultDto = new ParameterDto(2, 3, 45, 40, 800, 2, 48);

            when(parameterMapper.toEntity(inputDto)).thenReturn(newParameter);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(resultDto);

            // Act
            ParameterDto result = parameterService.updateParameters(inputDto);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getId());
            assertEquals(3, result.getMaxMediumAirport());
            assertEquals(45, result.getMinFlightDuration());
            
            verify(parameterRepository).deleteAll();
            verify(parameterMapper).toEntity(inputDto);
            verify(parameterRepository).save(any(Parameter.class));
            verify(parameterMapper).toDto(savedParameter);
        }

        @Test
        @Tag("updateParameters")
        @DisplayName("Update parameters ensures new record - Sets ID to null")
        void updateParameters_EnsuresNewRecord_SetsIdToNull() {
            // Arrange
            Parameter entityBeforeSave = new Parameter();
            Parameter savedParameter = new Parameter();
            savedParameter.setId(5);

            when(parameterMapper.toEntity(validParameterDto)).thenReturn(entityBeforeSave);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(validParameterDto);

            // Act
            parameterService.updateParameters(validParameterDto);

            // Assert
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(argThat(param -> param.getId() == null && param.getDeletedAt() == null));
        }

        @Test
        @Tag("updateParameters")
        @DisplayName("Repository throws exception during save - Propagates exception")
        void updateParameters_RepositorySaveThrowsException_PropagatesException() {
            // Arrange
            Parameter newParameter = new Parameter();
            when(parameterMapper.toEntity(validParameterDto)).thenReturn(newParameter);
            when(parameterRepository.save(any(Parameter.class)))
                .thenThrow(new RuntimeException("Save failed"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                parameterService.updateParameters(validParameterDto);
            });

            assertEquals("Save failed", exception.getMessage());
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }
    }

    // ==================== Individual Update Methods Tests ====================

    @Nested
    @DisplayName("Individual Update Methods Tests")
    @Tag("individualUpdates")
    class IndividualUpdateMethodsTests {

        @BeforeEach
        void setUp() {
            // Setup for individual update methods
            when(parameterRepository.findLatestParameter()).thenReturn(Optional.of(validParameter));
            when(parameterMapper.toDto(validParameter)).thenReturn(validParameterDto);
        }

        @Test
        @Tag("individualUpdates")
        @DisplayName("Update max medium airports - Updates only that field")
        void updateMaxMediumAirports_UpdatesField_CallsUpdateParameters() {
            // Arrange
            Parameter savedParameter = new Parameter();
            savedParameter.setMaxMediumAirport(5);
            
            ParameterDto updatedDto = new ParameterDto();
            updatedDto.setMaxMediumAirport(5);

            when(parameterMapper.toEntity(any(ParameterDto.class))).thenReturn(savedParameter);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(updatedDto);

            // Act
            parameterService.updateMaxMediumAirports(5);

            // Assert
            verify(parameterRepository).findLatestParameter();
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }

        @Test
        @Tag("individualUpdates")
        @DisplayName("Update min flight duration - Updates only that field")
        void updateMinFlightDuration_UpdatesField_CallsUpdateParameters() {
            // Arrange
            Parameter savedParameter = new Parameter();
            ParameterDto updatedDto = new ParameterDto();
            updatedDto.setMinFlightDuration(60);

            when(parameterMapper.toEntity(any(ParameterDto.class))).thenReturn(savedParameter);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(updatedDto);

            // Act
            parameterService.updateMinFlightDuration(60);

            // Assert
            verify(parameterRepository).findLatestParameter();
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }

        @Test
        @Tag("individualUpdates")
        @DisplayName("Update max layover duration - Updates only that field")
        void updateMaxLayoverDuration_UpdatesField_CallsUpdateParameters() {
            // Arrange
            Parameter savedParameter = new Parameter();
            ParameterDto updatedDto = new ParameterDto();
            updatedDto.setMaxLayoverDuration(1000);

            when(parameterMapper.toEntity(any(ParameterDto.class))).thenReturn(savedParameter);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(updatedDto);

            // Act
            parameterService.updateMaxLayoverDuration(1000);

            // Assert
            verify(parameterRepository).findLatestParameter();
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }

        @Test
        @Tag("individualUpdates")
        @DisplayName("Update min layover duration - Updates only that field")
        void updateMinLayoverDuration_UpdatesField_CallsUpdateParameters() {
            // Arrange
            Parameter savedParameter = new Parameter();
            ParameterDto updatedDto = new ParameterDto();
            updatedDto.setMinLayoverDuration(45);

            when(parameterMapper.toEntity(any(ParameterDto.class))).thenReturn(savedParameter);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(updatedDto);

            // Act
            parameterService.updateMinLayoverDuration(45);

            // Assert
            verify(parameterRepository).findLatestParameter();
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }

        @Test
        @Tag("individualUpdates")
        @DisplayName("Update min booking in advance duration - Updates only that field")
        void updateMinBookingInAdvanceDuration_UpdatesField_CallsUpdateParameters() {
            // Arrange
            Parameter savedParameter = new Parameter();
            ParameterDto updatedDto = new ParameterDto();
            updatedDto.setMinBookingInAdvanceDuration(3);

            when(parameterMapper.toEntity(any(ParameterDto.class))).thenReturn(savedParameter);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(updatedDto);

            // Act
            parameterService.updateMinBookingInAdvanceDuration(3);

            // Assert
            verify(parameterRepository).findLatestParameter();
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }

        @Test
        @Tag("individualUpdates")
        @DisplayName("Update max booking hold duration - Updates only that field")
        void updateMaxBookingHoldDuration_UpdatesField_CallsUpdateParameters() {
            // Arrange
            Parameter savedParameter = new Parameter();
            ParameterDto updatedDto = new ParameterDto();
            updatedDto.setMaxBookingHoldDuration(72);

            when(parameterMapper.toEntity(any(ParameterDto.class))).thenReturn(savedParameter);
            when(parameterRepository.save(any(Parameter.class))).thenReturn(savedParameter);
            when(parameterMapper.toDto(savedParameter)).thenReturn(updatedDto);

            // Act
            parameterService.updateMaxBookingHoldDuration(72);

            // Assert
            verify(parameterRepository).findLatestParameter();
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }

        @Test
        @Tag("individualUpdates")
        @DisplayName("Individual update when no parameter exists - Throws exception")
        void individualUpdate_NoParameterExists_ThrowsException() {
            // Arrange
            when(parameterRepository.findLatestParameter()).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                parameterService.updateMaxMediumAirports(5);
            });

            assertEquals("No parameters found", exception.getMessage());
            verify(parameterRepository).findLatestParameter();
            verify(parameterRepository, never()).deleteAll();
        }
    }

    // ==================== initializeDefaultParameters Tests ====================

    @Nested
    @DisplayName("InitializeDefaultParameters Tests")
    @Tag("initializeDefaultParameters")
    class InitializeDefaultParametersTests {

        @Test
        @Tag("initializeDefaultParameters")
        @DisplayName("Initialize default parameters - Creates default values")
        void initializeDefaultParameters_CreatesDefaultValues() {
            // Arrange
            when(parameterRepository.save(any(Parameter.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            parameterService.initializeDefaultParameters();

            // Assert
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(argThat(param ->
                param.getMaxMediumAirport() == 2 &&
                param.getMinFlightDuration() == 30 &&
                param.getMinLayoverDuration() == 30 &&
                param.getMaxLayoverDuration() == 720 &&
                param.getMinBookingInAdvanceDuration() == 1 &&
                param.getMaxBookingHoldDuration() == 24 &&
                param.getDeletedAt() == null
            ));
        }

        @Test
        @Tag("initializeDefaultParameters")
        @DisplayName("Initialize default parameters - Deletes all existing first")
        void initializeDefaultParameters_DeletesExistingFirst() {
            // Arrange
            when(parameterRepository.save(any(Parameter.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            parameterService.initializeDefaultParameters();

            // Assert
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }

        @Test
        @Tag("initializeDefaultParameters")
        @DisplayName("Initialize default parameters with save failure - Propagates exception")
        void initializeDefaultParameters_SaveFails_PropagatesException() {
            // Arrange
            when(parameterRepository.save(any(Parameter.class)))
                .thenThrow(new RuntimeException("Save failed"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                parameterService.initializeDefaultParameters();
            });

            assertEquals("Save failed", exception.getMessage());
            verify(parameterRepository).deleteAll();
            verify(parameterRepository).save(any(Parameter.class));
        }
    }
}
