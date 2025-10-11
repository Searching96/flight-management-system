package com.flightmanagement.controller;

import com.flightmanagement.dto.TicketClassDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.TicketClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-classes")
public class TicketClassController {
    
    private final TicketClassService ticketClassService;

    public TicketClassController(TicketClassService ticketClassService) {
        this.ticketClassService = ticketClassService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketClassDto>>> getAllTicketClasses() {
        List<TicketClassDto> ticketClasses = ticketClassService.getAllTicketClasses();
        ApiResponse<List<TicketClassDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Ticket classes retrieved successfully",
                ticketClasses,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
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
