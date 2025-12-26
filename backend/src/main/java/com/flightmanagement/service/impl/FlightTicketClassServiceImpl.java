package com.flightmanagement.service.impl;

import com.flightmanagement.dto.FlightTicketClassDto;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.TicketClass;
import com.flightmanagement.mapper.FlightTicketClassMapper;
import com.flightmanagement.repository.FlightTicketClassRepository;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.repository.TicketClassRepository;
import com.flightmanagement.service.AuditLogService;
import com.flightmanagement.service.FlightTicketClassService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightTicketClassServiceImpl implements FlightTicketClassService {

    private final FlightTicketClassRepository flightTicketClassRepository;

    private final FlightTicketClassMapper flightTicketClassMapper;

    private final FlightRepository flightRepository;

    private final TicketClassRepository ticketClassRepository;

    private final AuditLogService auditLogService;

    public FlightTicketClassServiceImpl(FlightTicketClassRepository flightTicketClassRepository,
                                        FlightTicketClassMapper flightTicketClassMapper,
                                        FlightRepository flightRepository,
                                        TicketClassRepository ticketClassRepository,
                                        AuditLogService auditLogService) {
        this.flightTicketClassRepository = flightTicketClassRepository;
        this.flightTicketClassMapper = flightTicketClassMapper;
        this.flightRepository = flightRepository;
        this.ticketClassRepository = ticketClassRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public List<FlightTicketClassDto> getAllFlightTicketClasses() {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findAllActive();
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }

    @Override
    public Page<FlightTicketClassDto> getAllFlightTicketClassesPaged(Pageable pageable) {
        Page<FlightTicketClass> page = flightTicketClassRepository.findByDeletedAtIsNull(pageable);
        return page.map(flightTicketClassMapper::toDto);
    }

    @Override
    public FlightTicketClassDto getFlightTicketClassById(Integer flightId, Integer ticketClassId) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
                .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));
        return flightTicketClassMapper.toDto(flightTicketClass);
    }

    @Override
    public List<FlightTicketClassDto> getFlightTicketClassesByFlightId(Integer flightId) {
        List<FlightTicketClass> flightTicketClasses = flightTicketClassRepository.findByFlightId(flightId);
        return flightTicketClassMapper.toDtoList(flightTicketClasses);
    }

    @Override
    @Transactional // Đảm bảo tính toàn vẹn dữ liệu
    public FlightTicketClassDto createFlightTicketClass(FlightTicketClassDto dto) {
        // 1. Validate Null Safety (Fail-fast)
        if (dto.getFlightId() == null || dto.getTicketClassId() == null) {
            throw new IllegalArgumentException("FlightId and TicketClassId are required fields.");
        }

        // 2. Optimization: Check Duplicate (Business Validation)
        // Tránh lỗi duplicate key từ DB và báo lỗi thân thiện hơn
        if (flightTicketClassRepository.findByFlightIdAndTicketClassId(dto.getFlightId(), dto.getTicketClassId()).isPresent()) {
            throw new IllegalArgumentException("This Ticket Class has already been added to the Flight.");
        }

        // 3. Fetch Relationships
        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + dto.getFlightId()));

        TicketClass ticketClass = ticketClassRepository.findById(dto.getTicketClassId())
                .orElseThrow(() -> new RuntimeException("TicketClass not found with id: " + dto.getTicketClassId()));

        // 4. Map & Set Relationships (FIXED HERE)
        FlightTicketClass entity = flightTicketClassMapper.toEntity(dto);
        
        // --- QUAN TRỌNG: Phải set quan hệ thủ công sau khi fetch ---
        entity.setFlight(flight);
        entity.setTicketClass(ticketClass);
        // Nếu Entity có trường Id riêng lẻ, set luôn để đảm bảo đồng bộ (tùy cấu hình JPA)
        entity.setFlightId(flight.getFlightId()); 
        entity.setTicketClassId(ticketClass.getTicketClassId());
        
        entity.setDeletedAt(null);

        // 5. Save & Return
        FlightTicketClass savedEntity = flightTicketClassRepository.save(entity);
        
        // Audit log for CREATE
        String entityId = savedEntity.getFlightId() + "-" + savedEntity.getTicketClassId();
        auditLogService.saveAuditLog("FlightTicketClass", entityId, "CREATE", "flightTicketClass", null, "FlightTicketClass", "system");
        
        return flightTicketClassMapper.toDto(savedEntity);
    }

    @Override
    public FlightTicketClassDto updateFlightTicketClass(Integer flightId, Integer ticketClassId, FlightTicketClassDto flightTicketClassDto) {
        FlightTicketClass existingFlightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
                .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));

        // Store old values for audit logging
        String oldSpecifiedFare = existingFlightTicketClass.getSpecifiedFare() != null ? existingFlightTicketClass.getSpecifiedFare().toString() : null;
        String oldTicketQuantity = existingFlightTicketClass.getTicketQuantity() != null ? existingFlightTicketClass.getTicketQuantity().toString() : null;
        String oldRemainingTicketQuantity = existingFlightTicketClass.getRemainingTicketQuantity() != null ? existingFlightTicketClass.getRemainingTicketQuantity().toString() : null;
        
        existingFlightTicketClass.setSpecifiedFare(flightTicketClassDto.getSpecifiedFare());
        existingFlightTicketClass.setTicketQuantity(flightTicketClassDto.getTicketQuantity());
        existingFlightTicketClass.setRemainingTicketQuantity(flightTicketClassDto.getRemainingTicketQuantity());

        FlightTicketClass updatedFlightTicketClass = flightTicketClassRepository.save(existingFlightTicketClass);
        
        // Audit log for changed fields
        String entityId = flightId + "-" + ticketClassId;
        String newSpecifiedFare = updatedFlightTicketClass.getSpecifiedFare() != null ? updatedFlightTicketClass.getSpecifiedFare().toString() : null;
        if ((oldSpecifiedFare == null && newSpecifiedFare != null) || (oldSpecifiedFare != null && !oldSpecifiedFare.equals(newSpecifiedFare))) {
            auditLogService.saveAuditLog("FlightTicketClass", entityId, "UPDATE", "specifiedFare", oldSpecifiedFare, newSpecifiedFare, "system");
        }
        
        String newTicketQuantity = updatedFlightTicketClass.getTicketQuantity() != null ? updatedFlightTicketClass.getTicketQuantity().toString() : null;
        if ((oldTicketQuantity == null && newTicketQuantity != null) || (oldTicketQuantity != null && !oldTicketQuantity.equals(newTicketQuantity))) {
            auditLogService.saveAuditLog("FlightTicketClass", entityId, "UPDATE", "ticketQuantity", oldTicketQuantity, newTicketQuantity, "system");
        }
        
        String newRemainingTicketQuantity = updatedFlightTicketClass.getRemainingTicketQuantity() != null ? updatedFlightTicketClass.getRemainingTicketQuantity().toString() : null;
        if ((oldRemainingTicketQuantity == null && newRemainingTicketQuantity != null) || (oldRemainingTicketQuantity != null && !oldRemainingTicketQuantity.equals(newRemainingTicketQuantity))) {
            auditLogService.saveAuditLog("FlightTicketClass", entityId, "UPDATE", "remainingTicketQuantity", oldRemainingTicketQuantity, newRemainingTicketQuantity, "system");
        }
        
        return flightTicketClassMapper.toDto(updatedFlightTicketClass);
    }

    @Override
    public void deleteFlightTicketClass(Integer flightId, Integer ticketClassId) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
                .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));
        
        // Capture entity info before delete
        String entityId = flightId + "-" + ticketClassId;

        flightTicketClass.setDeletedAt(LocalDateTime.now());
        flightTicketClassRepository.save(flightTicketClass);
        
        // Audit log for DELETE
        auditLogService.saveAuditLog("FlightTicketClass", entityId, "DELETE", "flightTicketClass", "FlightTicketClass", null, "system");
    }

    @Override
    public void updateRemainingTickets(Integer flightId, Integer ticketClassId, Integer quantity) {
        FlightTicketClass flightTicketClass = flightTicketClassRepository.findByFlightIdAndTicketClassId(flightId, ticketClassId)
                .orElseThrow(() -> new RuntimeException("FlightTicketClass not found for flight: " + flightId + " and class: " + ticketClassId));

        int newRemainingQuantity = flightTicketClass.getRemainingTicketQuantity() - quantity;
        if (newRemainingQuantity < 0) {
            throw new RuntimeException("Not enough tickets available. Requested: " + quantity +
                    ", Available: " + flightTicketClass.getRemainingTicketQuantity());
        }

        flightTicketClass.setRemainingTicketQuantity(newRemainingQuantity);
        flightTicketClassRepository.save(flightTicketClass);
    }

    @Override
    public List<FlightTicketClassDto> getAvailableFlightTicketClasses() {
        List<FlightTicketClass> available = flightTicketClassRepository.findAvailable();
        return flightTicketClassMapper.toDtoList(available);
    }

    @Override
    public Integer calculateOccupiedSeatsByFlightIdAndTicketClassId(Integer flightId, Integer ticketClassId) {
        Integer occupiedSeats = flightTicketClassRepository.calculateOccupiedSeatsByFlightIdAndTicketClassId(flightId, ticketClassId);
        System.out.println("Occupied seats for flightId: " + flightId + ", ticketClassId: " + ticketClassId + " is " + occupiedSeats);
        if (occupiedSeats == null) {
            throw new RuntimeException(
                    "No occupied seats found for flightId: " + flightId + " and ticketClassId: " + ticketClassId);
        }
        return occupiedSeats;
    }
}