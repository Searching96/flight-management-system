package com.flightmanagement.controller;

import com.flightmanagement.dto.TicketClassDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.TicketClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-classes")
@Tag(name = "TicketClass", description = "Operations related to ticket classes")
public class TicketClassController {
    
    private final TicketClassService ticketClassService;

    public TicketClassController(TicketClassService ticketClassService) {
        this.ticketClassService = ticketClassService;
    }
    
    @Operation(summary = "Get all ticket classes")
    @GetMapping
    public ResponseEntity<?> getAllTicketClasses(
            @PageableDefault(page = 0, size = 10)
            Pageable pageable
    ) {
        Page<TicketClassDto> page = ticketClassService.getAllTicketClassesPaged(pageable);
        ApiResponse<?> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched all ticket classes",
                page,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Get ticket class by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketClassDto>> getTicketClassById(@PathVariable Integer id) {
        TicketClassDto ticketClass = ticketClassService.getTicketClassById(id);
        ApiResponse<TicketClassDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Ticket class retrieved successfully",
                ticketClass,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Create a new ticket class")
    @PostMapping
    public ResponseEntity<ApiResponse<TicketClassDto>> createTicketClass(@RequestBody TicketClassDto ticketClassDto) {
        TicketClassDto createdTicketClass = ticketClassService.createTicketClass(ticketClassDto);
        ApiResponse<TicketClassDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Ticket class created successfully",
                createdTicketClass,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @Operation(summary = "Update an existing ticket class")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketClassDto>> updateTicketClass(@PathVariable Integer id, @RequestBody TicketClassDto ticketClassDto) {
        TicketClassDto updatedTicketClass = ticketClassService.updateTicketClass(id, ticketClassDto);
        ApiResponse<TicketClassDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Ticket class updated successfully",
                updatedTicketClass,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Delete a ticket class")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicketClass(@PathVariable Integer id) {
        ticketClassService.deleteTicketClass(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Ticket class deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
    
    @Operation(summary = "Get ticket class by name")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<TicketClassDto>> getTicketClassByName(@PathVariable String name) {
        TicketClassDto ticketClass = ticketClassService.getTicketClassByName(name);
        ApiResponse<TicketClassDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Ticket class retrieved by name successfully",
                ticketClass,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
