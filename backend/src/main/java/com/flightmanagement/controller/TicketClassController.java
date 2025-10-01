package com.flightmanagement.controller;

import com.flightmanagement.dto.TicketClassDto;
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
    public ResponseEntity<List<TicketClassDto>> getAllTicketClasses() {
        List<TicketClassDto> ticketClasses = ticketClassService.getAllTicketClasses();
        return ResponseEntity.ok(ticketClasses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TicketClassDto> getTicketClassById(@PathVariable Integer id) {
        TicketClassDto ticketClass = ticketClassService.getTicketClassById(id);
        return ResponseEntity.ok(ticketClass);
    }
    
    @PostMapping
    public ResponseEntity<TicketClassDto> createTicketClass(@RequestBody TicketClassDto ticketClassDto) {
        TicketClassDto createdTicketClass = ticketClassService.createTicketClass(ticketClassDto);
        return new ResponseEntity<>(createdTicketClass, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TicketClassDto> updateTicketClass(@PathVariable Integer id, @RequestBody TicketClassDto ticketClassDto) {
        TicketClassDto updatedTicketClass = ticketClassService.updateTicketClass(id, ticketClassDto);
        return ResponseEntity.ok(updatedTicketClass);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketClass(@PathVariable Integer id) {
        ticketClassService.deleteTicketClass(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<TicketClassDto> getTicketClassByName(@PathVariable String name) {
        TicketClassDto ticketClass = ticketClassService.getTicketClassByName(name);
        return ResponseEntity.ok(ticketClass);
    }
}
