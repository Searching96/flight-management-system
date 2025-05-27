package com.flightmanagement.mapper;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.TicketClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FlightTicketClassMapperTest {

    @InjectMocks
    private FlightTicketClassMapper flightTicketClassMapper;

    private FlightTicketClass testEntity;
    private FlightTicketClassDto testDto;
    private Flight testFlight;
    private TicketClass testTicketClass;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setFlightId(1);
        testFlight.setFlightCode("AA123");

        testTicketClass = new TicketClass();
        testTicketClass.setTicketClassId(1);
        testTicketClass.setTicketClassName("Economy");
        testTicketClass.setColor("blue");

        testEntity = new FlightTicketClass();
        testEntity.setFlightId(1);
        testEntity.setTicketClassId(1);
        testEntity.setTicketQuantity(100);
        testEntity.setRemainingTicketQuantity(80);
        testEntity.setSpecifiedFare(new BigDecimal("299.99"));
        testEntity.setFlight(testFlight);
        testEntity.setTicketClass(testTicketClass);

        testDto = new FlightTicketClassDto();
        testDto.setFlightId(1);
        testDto.setTicketClassId(1);
        testDto.setTicketQuantity(100);
        testDto.setRemainingTicketQuantity(80);
        testDto.setSpecifiedFare(new BigDecimal("299.99"));
        testDto.setTicketClassName("Economy");
        testDto.setColor("blue");
        testDto.setFlightCode("AA123");
        testDto.setIsAvailable(true);
    }

    @Test
    void toDto_WithCompleteEntity_ShouldMapAllFields() {
        // Act
        FlightTicketClassDto result = flightTicketClassMapper.toDto(testEntity);

        // Assert
        assertNotNull(result);
        assertEquals(testEntity.getFlightId(), result.getFlightId());
        assertEquals(testEntity.getTicketClassId(), result.getTicketClassId());
        assertEquals(testEntity.getTicketQuantity(), result.getTicketQuantity());
        assertEquals(testEntity.getRemainingTicketQuantity(), result.getRemainingTicketQuantity());
        assertEquals(testEntity.getSpecifiedFare(), result.getSpecifiedFare());
        assertEquals(testTicketClass.getTicketClassName(), result.getTicketClassName());
        assertEquals(testTicketClass.getColor(), result.getColor());
        assertEquals(testFlight.getFlightCode(), result.getFlightCode());
        assertTrue(result.getIsAvailable());
    }

    @Test
    void toDto_WithNullEntity_ShouldReturnNull() {
        // Act
        FlightTicketClassDto result = flightTicketClassMapper.toDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDto_WithNullRelations_ShouldHandleGracefully() {
        // Arrange
        testEntity.setFlight(null);
        testEntity.setTicketClass(null);

        // Act
        FlightTicketClassDto result = flightTicketClassMapper.toDto(testEntity);

        // Assert
        assertNotNull(result);
        assertEquals(testEntity.getFlightId(), result.getFlightId());
        assertEquals(testEntity.getTicketClassId(), result.getTicketClassId());
        assertNull(result.getTicketClassName());
        assertNull(result.getColor());
        assertNull(result.getFlightCode());
    }

    @Test
    void toDto_WithZeroRemainingTickets_ShouldSetAvailableFalse() {
        // Arrange
        testEntity.setRemainingTicketQuantity(0);

        // Act
        FlightTicketClassDto result = flightTicketClassMapper.toDto(testEntity);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsAvailable());
    }

    @Test
    void toEntity_WithCompleteDto_ShouldMapBasicFields() {
        // Act
        FlightTicketClass result = flightTicketClassMapper.toEntity(testDto);

        // Assert
        assertNotNull(result);
        assertEquals(testDto.getFlightId(), result.getFlightId());
        assertEquals(testDto.getTicketClassId(), result.getTicketClassId());
        assertEquals(testDto.getTicketQuantity(), result.getTicketQuantity());
        assertEquals(testDto.getRemainingTicketQuantity(), result.getRemainingTicketQuantity());
        assertEquals(testDto.getSpecifiedFare(), result.getSpecifiedFare());
    }

    @Test
    void toEntity_WithNullDto_ShouldReturnNull() {
        // Act
        FlightTicketClass result = flightTicketClassMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDtoList_WithEntityList_ShouldMapAllEntities() {
        // Arrange
        FlightTicketClass secondEntity = new FlightTicketClass();
        secondEntity.setFlightId(2);
        secondEntity.setTicketClassId(2);
        secondEntity.setTicketQuantity(50);
        secondEntity.setRemainingTicketQuantity(25);
        secondEntity.setSpecifiedFare(new BigDecimal("599.99"));

        List<FlightTicketClass> entities = Arrays.asList(testEntity, secondEntity);

        // Act
        List<FlightTicketClassDto> result = flightTicketClassMapper.toDtoList(entities);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testEntity.getFlightId(), result.get(0).getFlightId());
        assertEquals(secondEntity.getFlightId(), result.get(1).getFlightId());
    }

    @Test
    void toDtoList_WithNullList_ShouldReturnNull() {
        // Act
        List<FlightTicketClassDto> result = flightTicketClassMapper.toDtoList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntityList_WithDtoList_ShouldMapAllDtos() {
        // Arrange
        FlightTicketClassDto secondDto = new FlightTicketClassDto();
        secondDto.setFlightId(2);
        secondDto.setTicketClassId(2);
        secondDto.setTicketQuantity(50);
        secondDto.setRemainingTicketQuantity(25);
        secondDto.setSpecifiedFare(new BigDecimal("599.99"));

        List<FlightTicketClassDto> dtos = Arrays.asList(testDto, secondDto);

        // Act
        List<FlightTicketClass> result = flightTicketClassMapper.toEntityList(dtos);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testDto.getFlightId(), result.get(0).getFlightId());
        assertEquals(secondDto.getFlightId(), result.get(1).getFlightId());
    }

    @Test
    void toEntityList_WithNullList_ShouldReturnNull() {
        // Act
        List<FlightTicketClass> result = flightTicketClassMapper.toEntityList(null);

        // Assert
        assertNull(result);
    }
}
