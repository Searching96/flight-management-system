package com.flightmanagement.controller;

import com.flightmanagement.dto.*;
import com.flightmanagement.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Integer id) {
        TicketDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestBody TicketDto ticketDto) {
        TicketDto createdTicket = ticketService.createTicket(ticketDto);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @PostMapping("/book")
    public ResponseEntity<List<TicketDto>> bookTickets(@RequestBody BookingDto bookingDto) {
        List<TicketDto> bookedTickets = ticketService.bookTickets(bookingDto);
        return new ResponseEntity<>(bookedTickets, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable Integer id, @RequestBody TicketDto ticketDto) {
        TicketDto updatedTicket = ticketService.updateTicket(id, ticketDto);
        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelTicket(@PathVariable Integer id) {
        ticketService.cancelTicket(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Integer id, HttpServletRequest request) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<TicketDto>> getTicketsByFlightId(@PathVariable Integer flightId) {
        List<TicketDto> tickets = ticketService.getTicketsByFlightId(flightId);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<TicketDto> payTicket(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketService.payTicket(id, "PAID_WITH_CASH"));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TicketDto>> getTicketsByCustomerId(@PathVariable Integer customerId) {
        List<TicketDto> tickets = ticketService.getTicketsByCustomerId(customerId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<TicketDto>> getTicketsByPassengerId(@PathVariable Integer passengerId) {
        List<TicketDto> tickets = ticketService.getTicketsByPassengerId(passengerId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketDto>> getTicketsByStatus(@PathVariable Byte status) {
        List<TicketDto> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/seat-available")
    public ResponseEntity<Boolean> isSeatAvailable(@RequestParam Integer flightId, @RequestParam String seatNumber) {
        boolean available = ticketService.isSeatAvailable(flightId, seatNumber);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/confirmation-code")
    public ResponseEntity<String> generateConfirmationCode() {
        String code = ticketService.generateConfirmationCode();
        return ResponseEntity.ok(code);
    }

    @GetMapping("/booking-lookup/{code}")
    public ResponseEntity<List<TicketDto>> getTicketsOnConfirmationCode(@PathVariable String code) {
        List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(code);
        return ResponseEntity.ok(tickets);
    }
}