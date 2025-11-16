package com.flightmanagement.service;

import com.flightmanagement.dto.TicketClassDto;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.mapper.TicketClassMapper;
import com.flightmanagement.repository.TicketClassRepository;
import com.flightmanagement.service.impl.TicketClassServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketClassServiceTest {

    @Mock
    private TicketClassRepository ticketClassRepository;

    @Mock
    private TicketClassMapper ticketClassMapper;

    @InjectMocks
    private TicketClassServiceImpl ticketClassService;

    private TicketClassDto validDto;
    private TicketClass validEntity;

    @BeforeEach
    void setUp() {
        validDto = new TicketClassDto();
        validDto.setTicketClassId(1);
        validDto.setTicketClassName("Economy");
        validDto.setColor("#3498db");

        validEntity = new TicketClass();
        validEntity.setTicketClassId(1);
        validEntity.setTicketClassName("Economy");
        validEntity.setColor("#3498db");
    }

    // ==================== createTicketClass Tests ====================

    @Tag("createTicketClass")
    @Test
    void createTicketClass_validDto_success() {
        when(ticketClassMapper.toEntity(validDto)).thenReturn(validEntity);
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(validDto);

        TicketClassDto result = ticketClassService.createTicketClass(validDto);

        assertNotNull(result);
        assertEquals(validDto.getTicketClassName(), result.getTicketClassName());
        assertEquals(validDto.getColor(), result.getColor());
        verify(ticketClassRepository).save(any(TicketClass.class));
    }

    @Tag("createTicketClass")
    @Test
    void createTicketClass_setsDeletedAtToNull() {
        TicketClass entityWithDeletedAt = new TicketClass();
        entityWithDeletedAt.setTicketClassName("Economy");
        entityWithDeletedAt.setDeletedAt(LocalDateTime.now());

        when(ticketClassMapper.toEntity(validDto)).thenReturn(entityWithDeletedAt);
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(validDto);

        ticketClassService.createTicketClass(validDto);

        assertNull(entityWithDeletedAt.getDeletedAt());
        verify(ticketClassRepository).save(entityWithDeletedAt);
    }

    @Tag("createTicketClass")
    @Test
    void createTicketClass_withMinimalData_success() {
        TicketClassDto minimalDto = new TicketClassDto();
        minimalDto.setTicketClassName("Business");

        TicketClass minimalEntity = new TicketClass();
        minimalEntity.setTicketClassName("Business");

        when(ticketClassMapper.toEntity(minimalDto)).thenReturn(minimalEntity);
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(minimalEntity);
        when(ticketClassMapper.toDto(minimalEntity)).thenReturn(minimalDto);

        TicketClassDto result = ticketClassService.createTicketClass(minimalDto);

        assertNotNull(result);
        assertEquals("Business", result.getTicketClassName());
        verify(ticketClassRepository).save(minimalEntity);
    }

    @Tag("createTicketClass")
    @Test
    void createTicketClass_withColor_success() {
        validDto.setColor("#e74c3c");
        validEntity.setColor("#e74c3c");

        when(ticketClassMapper.toEntity(validDto)).thenReturn(validEntity);
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(validDto);

        TicketClassDto result = ticketClassService.createTicketClass(validDto);

        assertNotNull(result);
        assertEquals("#e74c3c", result.getColor());
    }

    @Tag("createTicketClass")
    @Test
    void createTicketClass_mapperThrowsException_propagatesException() {
        when(ticketClassMapper.toEntity(validDto))
            .thenThrow(new RuntimeException("Mapping error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.createTicketClass(validDto)
        );

        assertTrue(exception.getMessage().contains("Mapping error"));
        verify(ticketClassRepository, never()).save(any());
    }

    @Tag("createTicketClass")
    @Test
    void createTicketClass_repositoryThrowsException_propagatesException() {
        when(ticketClassMapper.toEntity(validDto)).thenReturn(validEntity);
        when(ticketClassRepository.save(any(TicketClass.class)))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.createTicketClass(validDto)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Tag("createTicketClass")
    @Test
    void createTicketClass_nullColor_success() {
        validDto.setColor(null);
        validEntity.setColor(null);

        when(ticketClassMapper.toEntity(validDto)).thenReturn(validEntity);
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(validDto);

        TicketClassDto result = ticketClassService.createTicketClass(validDto);

        assertNotNull(result);
        assertNull(result.getColor());
    }

    // ==================== updateTicketClass Tests ====================

    @Tag("updateTicketClass")
    @Test
    void updateTicketClass_validUpdate_success() {
        TicketClassDto updateDto = new TicketClassDto();
        updateDto.setTicketClassName("Business Class");
        updateDto.setColor("#f39c12");

        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        TicketClassDto result = ticketClassService.updateTicketClass(1, updateDto);

        assertNotNull(result);
        assertEquals("Business Class", validEntity.getTicketClassName());
        assertEquals("#f39c12", validEntity.getColor());
        verify(ticketClassRepository).save(validEntity);
    }

    @Tag("updateTicketClass")
    @Test
    void updateTicketClass_ticketClassNotFound_throwsException() {
        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.updateTicketClass(1, validDto)
        );

        assertTrue(exception.getMessage().contains("TicketClass not found"));
        assertTrue(exception.getMessage().contains("id: 1"));
        verify(ticketClassRepository, never()).save(any());
    }

    @Tag("updateTicketClass")
    @Test
    void updateTicketClass_updatesOnlyName() {
        TicketClassDto updateDto = new TicketClassDto();
        updateDto.setTicketClassName("First Class");
        updateDto.setColor("#3498db");

        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        ticketClassService.updateTicketClass(1, updateDto);

        assertEquals("First Class", validEntity.getTicketClassName());
    }

    @Tag("updateTicketClass")
    @Test
    void updateTicketClass_updatesOnlyColor() {
        TicketClassDto updateDto = new TicketClassDto();
        updateDto.setTicketClassName("Economy");
        updateDto.setColor("#9b59b6");

        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        ticketClassService.updateTicketClass(1, updateDto);

        assertEquals("#9b59b6", validEntity.getColor());
    }

    @Tag("updateTicketClass")
    @Test
    void updateTicketClass_withNullColor_success() {
        TicketClassDto updateDto = new TicketClassDto();
        updateDto.setTicketClassName("Premium Economy");
        updateDto.setColor(null);

        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);
        when(ticketClassMapper.toDto(validEntity)).thenReturn(updateDto);

        ticketClassService.updateTicketClass(1, updateDto);

        assertEquals("Premium Economy", validEntity.getTicketClassName());
        assertNull(validEntity.getColor());
    }

    @Tag("updateTicketClass")
    @Test
    void updateTicketClass_differentId_throwsException() {
        when(ticketClassRepository.findActiveById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.updateTicketClass(999, validDto)
        );

        assertTrue(exception.getMessage().contains("TicketClass not found"));
        assertTrue(exception.getMessage().contains("id: 999"));
    }

    @Tag("updateTicketClass")
    @Test
    void updateTicketClass_repositoryThrowsException_propagatesException() {
        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class)))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.updateTicketClass(1, validDto)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    // ==================== deleteTicketClass Tests ====================

    @Tag("deleteTicketClass")
    @Test
    void deleteTicketClass_validId_success() {
        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);

        ticketClassService.deleteTicketClass(1);

        assertNotNull(validEntity.getDeletedAt());
        verify(ticketClassRepository).save(validEntity);
    }

    @Tag("deleteTicketClass")
    @Test
    void deleteTicketClass_ticketClassNotFound_throwsException() {
        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.deleteTicketClass(1)
        );

        assertTrue(exception.getMessage().contains("TicketClass not found"));
        assertTrue(exception.getMessage().contains("id: 1"));
        verify(ticketClassRepository, never()).save(any());
    }

    @Tag("deleteTicketClass")
    @Test
    void deleteTicketClass_setsDeletedAtTimestamp() {
        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);

        LocalDateTime beforeDelete = LocalDateTime.now();
        ticketClassService.deleteTicketClass(1);
        LocalDateTime afterDelete = LocalDateTime.now();

        assertNotNull(validEntity.getDeletedAt());
        assertTrue(validEntity.getDeletedAt().isAfter(beforeDelete.minusSeconds(1)));
        assertTrue(validEntity.getDeletedAt().isBefore(afterDelete.plusSeconds(1)));
    }

    @Tag("deleteTicketClass")
    @Test
    void deleteTicketClass_preservesOtherFields() {
        validEntity.setTicketClassName("Economy");
        validEntity.setColor("#3498db");

        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);

        ticketClassService.deleteTicketClass(1);

        assertNotNull(validEntity.getDeletedAt());
        assertEquals("Economy", validEntity.getTicketClassName());
        assertEquals("#3498db", validEntity.getColor());
    }

    @Tag("deleteTicketClass")
    @Test
    void deleteTicketClass_differentId_throwsException() {
        when(ticketClassRepository.findActiveById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.deleteTicketClass(999)
        );

        assertTrue(exception.getMessage().contains("TicketClass not found"));
        assertTrue(exception.getMessage().contains("id: 999"));
    }

    @Tag("deleteTicketClass")
    @Test
    void deleteTicketClass_repositoryThrowsException_propagatesException() {
        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class)))
            .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ticketClassService.deleteTicketClass(1)
        );

        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Tag("deleteTicketClass")
    @Test
    void deleteTicketClass_alreadyHasDeletedAt_updatesDeletedAt() {
        LocalDateTime oldDeletedAt = LocalDateTime.now().minusDays(1);
        validEntity.setDeletedAt(oldDeletedAt);

        when(ticketClassRepository.findActiveById(1)).thenReturn(Optional.of(validEntity));
        when(ticketClassRepository.save(any(TicketClass.class))).thenReturn(validEntity);

        ticketClassService.deleteTicketClass(1);

        assertNotNull(validEntity.getDeletedAt());
        assertTrue(validEntity.getDeletedAt().isAfter(oldDeletedAt));
    }
}
