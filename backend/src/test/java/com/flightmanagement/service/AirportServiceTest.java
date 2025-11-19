package com.flightmanagement.service;

import com.flightmanagement.dto.AirportDto;
import com.flightmanagement.entity.Airport;
import com.flightmanagement.mapper.AirportMapper;
import com.flightmanagement.repository.AirportRepository;
import com.flightmanagement.service.impl.AirportServiceImpl;
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
 * Test class for AirportService - Core function for Airport Management
 * 
 * Available Tags:
 * - getAllAirports: Tests for retrieving all airports
 * - getAirportById: Tests for retrieving airport by ID
 * - createAirport: Tests for airport creation
 * - updateAirport: Tests for airport updates
 * - deleteAirport: Tests for airport deletion (soft delete)
 * - getAirportsByCity: Tests for city-based airport search
 * - getAirportsByCountry: Tests for country-based airport search
 * - searchAirportsByName: Tests for name-based airport search
 */
@ExtendWith(MockitoExtension.class)
public class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirportMapper airportMapper;

    @InjectMocks
    private AirportServiceImpl airportService;

    private Airport testAirport;
    private AirportDto testAirportDto;
    private Airport testAirport2;
    private AirportDto testAirportDto2;

    @BeforeEach
    void setUp() {
        // Setup test airport entity
        testAirport = new Airport();
        testAirport.setAirportId(1);
        testAirport.setAirportName("Tan Son Nhat International Airport");
        testAirport.setCityName("Ho Chi Minh City");
        testAirport.setCountryName("Vietnam");
        testAirport.setDeletedAt(null);

        // Setup test airport DTO
        testAirportDto = new AirportDto();
        testAirportDto.setAirportId(1);
        testAirportDto.setAirportName("Tan Son Nhat International Airport");
        testAirportDto.setCityName("Ho Chi Minh City");
        testAirportDto.setCountryName("Vietnam");

        // Setup second test airport
        testAirport2 = new Airport();
        testAirport2.setAirportId(2);
        testAirport2.setAirportName("Noi Bai International Airport");
        testAirport2.setCityName("Hanoi");
        testAirport2.setCountryName("Vietnam");
        testAirport2.setDeletedAt(null);

        testAirportDto2 = new AirportDto();
        testAirportDto2.setAirportId(2);
        testAirportDto2.setAirportName("Noi Bai International Airport");
        testAirportDto2.setCityName("Hanoi");
        testAirportDto2.setCountryName("Vietnam");
    }

    // ================ GET ALL AIRPORTS TESTS ================

    @Test
    @Tag("getAllAirports")
    void testGetAllAirports_Success_ReturnsAirportList() {
        // Given
        List<Airport> mockAirports = Arrays.asList(testAirport, testAirport2);
        List<AirportDto> expectedDtos = Arrays.asList(testAirportDto, testAirportDto2);

        when(airportRepository.findAllActive()).thenReturn(mockAirports);
        when(airportMapper.toDtoList(mockAirports)).thenReturn(expectedDtos);

        // When
        List<AirportDto> result = airportService.getAllAirports();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tan Son Nhat International Airport", result.get(0).getAirportName());
        assertEquals("Noi Bai International Airport", result.get(1).getAirportName());
        verify(airportRepository).findAllActive();
        verify(airportMapper).toDtoList(mockAirports);
    }

    @Test
    @Tag("getAllAirports")
    void testGetAllAirports_EmptyDatabase_ReturnsEmptyList() {
        // Given
        when(airportRepository.findAllActive()).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.getAllAirports();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findAllActive();
        verify(airportMapper).toDtoList(Collections.emptyList());
    }

    @Test
    @Tag("getAllAirports")
    void testGetAllAirports_RepositoryException_PropagatesException() {
        // Given
        when(airportRepository.findAllActive()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAllAirports());
        assertEquals("Database error", exception.getMessage());
        verify(airportRepository).findAllActive();
        verify(airportMapper, never()).toDtoList(any());
    }

    @Test
    @Tag("getAllAirports")
    void testGetAllAirports_MapperException_PropagatesException() {
        // Given
        List<Airport> mockAirports = Arrays.asList(testAirport);
        when(airportRepository.findAllActive()).thenReturn(mockAirports);
        when(airportMapper.toDtoList(mockAirports)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAllAirports());
        assertEquals("Mapping error", exception.getMessage());
        verify(airportRepository).findAllActive();
        verify(airportMapper).toDtoList(mockAirports);
    }

    // ================ GET AIRPORT BY ID TESTS ================

    @Test
    @Tag("getAirportById")
    void testGetAirportById_Success_ReturnsAirportDto() {
        // Given
        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportMapper.toDto(testAirport)).thenReturn(testAirportDto);

        // When
        AirportDto result = airportService.getAirportById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAirportId());
        assertEquals("Tan Son Nhat International Airport", result.getAirportName());
        assertEquals("Ho Chi Minh City", result.getCityName());
        verify(airportRepository).findActiveById(1);
        verify(airportMapper).toDto(testAirport);
    }

    @Test
    @Tag("getAirportById")
    void testGetAirportById_NotFound_ThrowsRuntimeException() {
        // Given
        when(airportRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAirportById(999));
        assertEquals("Airport not found with id: 999", exception.getMessage());
        verify(airportRepository).findActiveById(999);
        verify(airportMapper, never()).toDto(any());
    }

    @Test
    @Tag("getAirportById")
    void testGetAirportById_NullId_HandledByRepository() {
        // Given
        when(airportRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAirportById(null));
        assertTrue(exception.getMessage().contains("Airport not found with id: null"));
        verify(airportRepository).findActiveById(null);
    }

    @Test
    @Tag("getAirportById")
    void testGetAirportById_MapperException_PropagatesException() {
        // Given
        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportMapper.toDto(testAirport)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAirportById(1));
        assertEquals("Mapping failed", exception.getMessage());
        verify(airportRepository).findActiveById(1);
        verify(airportMapper).toDto(testAirport);
    }

    // ================ CREATE AIRPORT TESTS ================

    @Test
    @Tag("createAirport")
    void testCreateAirport_Success_ReturnsCreatedAirportDto() {
        // Given
        AirportDto inputDto = new AirportDto();
        inputDto.setAirportName("New Airport");
        inputDto.setCityName("New City");
        inputDto.setCountryName("New Country");

        Airport mappedAirport = new Airport();
        mappedAirport.setAirportName("New Airport");
        mappedAirport.setCityName("New City");
        mappedAirport.setCountryName("New Country");

        Airport savedAirport = new Airport();
        savedAirport.setAirportId(3);
        savedAirport.setAirportName("New Airport");
        savedAirport.setCityName("New City");
        savedAirport.setCountryName("New Country");
        savedAirport.setDeletedAt(null);

        AirportDto resultDto = new AirportDto();
        resultDto.setAirportId(3);
        resultDto.setAirportName("New Airport");
        resultDto.setCityName("New City");
        resultDto.setCountryName("New Country");

        when(airportMapper.toEntity(inputDto)).thenReturn(mappedAirport);
        when(airportRepository.save(any(Airport.class))).thenReturn(savedAirport);
        when(airportMapper.toDto(savedAirport)).thenReturn(resultDto);

        // When
        AirportDto result = airportService.createAirport(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getAirportId());
        assertEquals("New Airport", result.getAirportName());
        verify(airportMapper).toEntity(inputDto);
        verify(airportRepository).save(argThat(airport -> airport.getDeletedAt() == null));
        verify(airportMapper).toDto(savedAirport);
    }

    @Test
    @Tag("createAirport")
    void testCreateAirport_NullInput_HandledByMapper() {
        // Given
        when(airportMapper.toEntity(null)).thenThrow(new RuntimeException("Input cannot be null"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.createAirport(null));
        assertEquals("Input cannot be null", exception.getMessage());
        verify(airportMapper).toEntity(null);
        verify(airportRepository, never()).save(any());
    }

    @Test
    @Tag("createAirport")
    void testCreateAirport_RepositoryException_PropagatesException() {
        // Given
        when(airportMapper.toEntity(testAirportDto)).thenReturn(testAirport);
        when(airportRepository.save(any(Airport.class))).thenThrow(new RuntimeException("Database constraint violation"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.createAirport(testAirportDto));
        assertEquals("Database constraint violation", exception.getMessage());
        verify(airportMapper).toEntity(testAirportDto);
        verify(airportRepository).save(any(Airport.class));
        verify(airportMapper, never()).toDto(any());
    }

    @Test
    @Tag("createAirport")
    void testCreateAirport_MapperToEntityException_PropagatesException() {
        // Given
        when(airportMapper.toEntity(testAirportDto)).thenThrow(new RuntimeException("Entity mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.createAirport(testAirportDto));
        assertEquals("Entity mapping failed", exception.getMessage());
        verify(airportMapper).toEntity(testAirportDto);
        verify(airportRepository, never()).save(any());
    }

    @Test
    @Tag("createAirport")
    void testCreateAirport_MapperToDtoException_PropagatesException() {
        // Given
        when(airportMapper.toEntity(testAirportDto)).thenReturn(testAirport);
        when(airportRepository.save(any(Airport.class))).thenReturn(testAirport);
        when(airportMapper.toDto(testAirport)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.createAirport(testAirportDto));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(airportMapper).toEntity(testAirportDto);
        verify(airportRepository).save(any(Airport.class));
        verify(airportMapper).toDto(testAirport);
    }

    // ================ UPDATE AIRPORT TESTS ================

    @Test
    @Tag("updateAirport")
    void testUpdateAirport_Success_ReturnsUpdatedAirportDto() {
        // Given
        AirportDto updateDto = new AirportDto();
        updateDto.setAirportName("Updated Airport Name");
        updateDto.setCityName("Updated City");
        updateDto.setCountryName("Updated Country");

        Airport updatedAirport = new Airport();
        updatedAirport.setAirportId(1);
        updatedAirport.setAirportName("Updated Airport Name");
        updatedAirport.setCityName("Updated City");
        updatedAirport.setCountryName("Updated Country");

        AirportDto resultDto = new AirportDto();
        resultDto.setAirportId(1);
        resultDto.setAirportName("Updated Airport Name");
        resultDto.setCityName("Updated City");
        resultDto.setCountryName("Updated Country");

        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportRepository.save(testAirport)).thenReturn(updatedAirport);
        when(airportMapper.toDto(updatedAirport)).thenReturn(resultDto);

        // When
        AirportDto result = airportService.updateAirport(1, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Airport Name", result.getAirportName());
        assertEquals("Updated City", result.getCityName());
        assertEquals("Updated Country", result.getCountryName());
        verify(airportRepository).findActiveById(1);
        verify(airportRepository).save(testAirport);
        verify(airportMapper).toDto(updatedAirport);

        // Verify the original entity was modified
        assertEquals("Updated Airport Name", testAirport.getAirportName());
        assertEquals("Updated City", testAirport.getCityName());
        assertEquals("Updated Country", testAirport.getCountryName());
    }

    @Test
    @Tag("updateAirport")
    void testUpdateAirport_NotFound_ThrowsRuntimeException() {
        // Given
        when(airportRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.updateAirport(999, testAirportDto));
        assertEquals("Airport not found with id: 999", exception.getMessage());
        verify(airportRepository).findActiveById(999);
        verify(airportRepository, never()).save(any());
        verify(airportMapper, never()).toDto(any());
    }

    @Test
    @Tag("updateAirport")
    void testUpdateAirport_NullInput_UpdatesWithNullValues() {
        // Given
        AirportDto nullDto = new AirportDto();
        nullDto.setAirportName(null);
        nullDto.setCityName(null);
        nullDto.setCountryName(null);

        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportRepository.save(testAirport)).thenReturn(testAirport);
        when(airportMapper.toDto(testAirport)).thenReturn(testAirportDto);

        // When
        AirportDto result = airportService.updateAirport(1, nullDto);

        // Then
        assertNotNull(result);
        verify(airportRepository).findActiveById(1);
        verify(airportRepository).save(testAirport);
        
        // Verify null values were set
        assertNull(testAirport.getAirportName());
        assertNull(testAirport.getCityName());
        assertNull(testAirport.getCountryName());
    }

    @Test
    @Tag("updateAirport")
    void testUpdateAirport_RepositoryException_PropagatesException() {
        // Given
        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportRepository.save(testAirport)).thenThrow(new RuntimeException("Database save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.updateAirport(1, testAirportDto));
        assertEquals("Database save failed", exception.getMessage());
        verify(airportRepository).findActiveById(1);
        verify(airportRepository).save(testAirport);
    }

    @Test
    @Tag("updateAirport")
    void testUpdateAirport_MapperException_PropagatesException() {
        // Given
        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportRepository.save(testAirport)).thenReturn(testAirport);
        when(airportMapper.toDto(testAirport)).thenThrow(new RuntimeException("Mapping to DTO failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.updateAirport(1, testAirportDto));
        assertEquals("Mapping to DTO failed", exception.getMessage());
        verify(airportRepository).findActiveById(1);
        verify(airportRepository).save(testAirport);
        verify(airportMapper).toDto(testAirport);
    }

    // ================ DELETE AIRPORT TESTS ================

    @Test
    @Tag("deleteAirport")
    void testDeleteAirport_Success_SetsDeletedAt() {
        // Given
        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportRepository.save(testAirport)).thenReturn(testAirport);

        // When
        airportService.deleteAirport(1);

        // Then
        verify(airportRepository).findActiveById(1);
        verify(airportRepository).save(argThat(airport -> airport.getDeletedAt() != null));
        assertNotNull(testAirport.getDeletedAt());
        assertTrue(testAirport.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @Tag("deleteAirport")
    void testDeleteAirport_NotFound_ThrowsRuntimeException() {
        // Given
        when(airportRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.deleteAirport(999));
        assertEquals("Airport not found with id: 999", exception.getMessage());
        verify(airportRepository).findActiveById(999);
        verify(airportRepository, never()).save(any());
    }

    @Test
    @Tag("deleteAirport")
    void testDeleteAirport_RepositoryFindException_PropagatesException() {
        // Given
        when(airportRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.deleteAirport(1));
        assertEquals("Database connection lost", exception.getMessage());
        verify(airportRepository).findActiveById(1);
        verify(airportRepository, never()).save(any());
    }

    @Test
    @Tag("deleteAirport")
    void testDeleteAirport_RepositorySaveException_PropagatesException() {
        // Given
        when(airportRepository.findActiveById(1)).thenReturn(Optional.of(testAirport));
        when(airportRepository.save(testAirport)).thenThrow(new RuntimeException("Save operation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.deleteAirport(1));
        assertEquals("Save operation failed", exception.getMessage());
        verify(airportRepository).findActiveById(1);
        verify(airportRepository).save(testAirport);
    }

    @Test
    @Tag("deleteAirport")
    void testDeleteAirport_NullId_HandledByRepository() {
        // Given
        when(airportRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.deleteAirport(null));
        assertTrue(exception.getMessage().contains("Airport not found with id: null"));
        verify(airportRepository).findActiveById(null);
    }

    // ================ GET AIRPORTS BY CITY TESTS ================

    @Test
    @Tag("getAirportsByCity")
    void testGetAirportsByCity_Success_ReturnsMatchingAirports() {
        // Given
        List<Airport> cityAirports = Arrays.asList(testAirport);
        List<AirportDto> expectedDtos = Arrays.asList(testAirportDto);

        when(airportRepository.findByCityName("Ho Chi Minh City")).thenReturn(cityAirports);
        when(airportMapper.toDtoList(cityAirports)).thenReturn(expectedDtos);

        // When
        List<AirportDto> result = airportService.getAirportsByCity("Ho Chi Minh City");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ho Chi Minh City", result.get(0).getCityName());
        verify(airportRepository).findByCityName("Ho Chi Minh City");
        verify(airportMapper).toDtoList(cityAirports);
    }

    @Test
    @Tag("getAirportsByCity")
    void testGetAirportsByCity_NoResults_ReturnsEmptyList() {
        // Given
        when(airportRepository.findByCityName("Nonexistent City")).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.getAirportsByCity("Nonexistent City");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findByCityName("Nonexistent City");
        verify(airportMapper).toDtoList(Collections.emptyList());
    }

    @Test
    @Tag("getAirportsByCity")
    void testGetAirportsByCity_NullCityName_HandledByRepository() {
        // Given
        when(airportRepository.findByCityName(null)).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.getAirportsByCity(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findByCityName(null);
    }

    @Test
    @Tag("getAirportsByCity")
    void testGetAirportsByCity_EmptyString_HandledByRepository() {
        // Given
        when(airportRepository.findByCityName("")).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.getAirportsByCity("");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findByCityName("");
    }

    @Test
    @Tag("getAirportsByCity")
    void testGetAirportsByCity_CaseInsensitive_ReturnsResults() {
        // Given
        List<Airport> cityAirports = Arrays.asList(testAirport);
        List<AirportDto> expectedDtos = Arrays.asList(testAirportDto);

        when(airportRepository.findByCityName("ho chi minh city")).thenReturn(cityAirports);
        when(airportMapper.toDtoList(cityAirports)).thenReturn(expectedDtos);

        // When
        List<AirportDto> result = airportService.getAirportsByCity("ho chi minh city");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(airportRepository).findByCityName("ho chi minh city");
    }

    @Test
    @Tag("getAirportsByCity")
    void testGetAirportsByCity_RepositoryException_PropagatesException() {
        // Given
        when(airportRepository.findByCityName("Ho Chi Minh City")).thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAirportsByCity("Ho Chi Minh City"));
        assertEquals("Database query failed", exception.getMessage());
        verify(airportRepository).findByCityName("Ho Chi Minh City");
        verify(airportMapper, never()).toDtoList(any());
    }

    // ================ GET AIRPORTS BY COUNTRY TESTS ================

    @Test
    @Tag("getAirportsByCountry")
    void testGetAirportsByCountry_Success_ReturnsMatchingAirports() {
        // Given
        List<Airport> countryAirports = Arrays.asList(testAirport, testAirport2);
        List<AirportDto> expectedDtos = Arrays.asList(testAirportDto, testAirportDto2);

        when(airportRepository.findByCountryName("Vietnam")).thenReturn(countryAirports);
        when(airportMapper.toDtoList(countryAirports)).thenReturn(expectedDtos);

        // When
        List<AirportDto> result = airportService.getAirportsByCountry("Vietnam");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Vietnam", result.get(0).getCountryName());
        assertEquals("Vietnam", result.get(1).getCountryName());
        verify(airportRepository).findByCountryName("Vietnam");
        verify(airportMapper).toDtoList(countryAirports);
    }

    @Test
    @Tag("getAirportsByCountry")
    void testGetAirportsByCountry_NoResults_ReturnsEmptyList() {
        // Given
        when(airportRepository.findByCountryName("Mars")).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.getAirportsByCountry("Mars");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findByCountryName("Mars");
    }

    @Test
    @Tag("getAirportsByCountry")
    void testGetAirportsByCountry_NullCountryName_HandledByRepository() {
        // Given
        when(airportRepository.findByCountryName(null)).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.getAirportsByCountry(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findByCountryName(null);
    }

    @Test
    @Tag("getAirportsByCountry")
    void testGetAirportsByCountry_RepositoryException_PropagatesException() {
        // Given
        when(airportRepository.findByCountryName("Vietnam")).thenThrow(new RuntimeException("Country search failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAirportsByCountry("Vietnam"));
        assertEquals("Country search failed", exception.getMessage());
        verify(airportRepository).findByCountryName("Vietnam");
    }

    @Test
    @Tag("getAirportsByCountry")
    void testGetAirportsByCountry_MapperException_PropagatesException() {
        // Given
        List<Airport> countryAirports = Arrays.asList(testAirport);
        when(airportRepository.findByCountryName("Vietnam")).thenReturn(countryAirports);
        when(airportMapper.toDtoList(countryAirports)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.getAirportsByCountry("Vietnam"));
        assertEquals("Mapping failed", exception.getMessage());
        verify(airportRepository).findByCountryName("Vietnam");
        verify(airportMapper).toDtoList(countryAirports);
    }

    // ================ SEARCH AIRPORTS BY NAME TESTS ================

    @Test
    @Tag("searchAirportsByName")
    void testSearchAirportsByName_Success_ReturnsMatchingAirports() {
        // Given
        List<Airport> searchResults = Arrays.asList(testAirport);
        List<AirportDto> expectedDtos = Arrays.asList(testAirportDto);

        when(airportRepository.findByAirportNameContaining("Tan Son")).thenReturn(searchResults);
        when(airportMapper.toDtoList(searchResults)).thenReturn(expectedDtos);

        // When
        List<AirportDto> result = airportService.searchAirportsByName("Tan Son");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAirportName().contains("Tan Son"));
        verify(airportRepository).findByAirportNameContaining("Tan Son");
        verify(airportMapper).toDtoList(searchResults);
    }

    @Test
    @Tag("searchAirportsByName")
    void testSearchAirportsByName_NoMatches_ReturnsEmptyList() {
        // Given
        when(airportRepository.findByAirportNameContaining("Nonexistent")).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.searchAirportsByName("Nonexistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findByAirportNameContaining("Nonexistent");
    }

    @Test
    @Tag("searchAirportsByName")
    void testSearchAirportsByName_EmptyString_ReturnsAllResults() {
        // Given
        List<Airport> allAirports = Arrays.asList(testAirport, testAirport2);
        List<AirportDto> expectedDtos = Arrays.asList(testAirportDto, testAirportDto2);

        when(airportRepository.findByAirportNameContaining("")).thenReturn(allAirports);
        when(airportMapper.toDtoList(allAirports)).thenReturn(expectedDtos);

        // When
        List<AirportDto> result = airportService.searchAirportsByName("");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(airportRepository).findByAirportNameContaining("");
    }

    @Test
    @Tag("searchAirportsByName")
    void testSearchAirportsByName_NullSearchTerm_HandledByRepository() {
        // Given
        when(airportRepository.findByAirportNameContaining(null)).thenReturn(Collections.emptyList());
        when(airportMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<AirportDto> result = airportService.searchAirportsByName(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airportRepository).findByAirportNameContaining(null);
    }

    @Test
    @Tag("searchAirportsByName")
    void testSearchAirportsByName_PartialMatch_ReturnsResults() {
        // Given
        List<Airport> searchResults = Arrays.asList(testAirport, testAirport2);
        List<AirportDto> expectedDtos = Arrays.asList(testAirportDto, testAirportDto2);

        when(airportRepository.findByAirportNameContaining("International")).thenReturn(searchResults);
        when(airportMapper.toDtoList(searchResults)).thenReturn(expectedDtos);

        // When
        List<AirportDto> result = airportService.searchAirportsByName("International");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getAirportName().contains("International"));
        assertTrue(result.get(1).getAirportName().contains("International"));
        verify(airportRepository).findByAirportNameContaining("International");
    }

    @Test
    @Tag("searchAirportsByName")
    void testSearchAirportsByName_RepositoryException_PropagatesException() {
        // Given
        when(airportRepository.findByAirportNameContaining("Test")).thenThrow(new RuntimeException("Search operation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> airportService.searchAirportsByName("Test"));
        assertEquals("Search operation failed", exception.getMessage());
        verify(airportRepository).findByAirportNameContaining("Test");
        verify(airportMapper, never()).toDtoList(any());
    }
}