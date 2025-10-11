package com.flightmanagement.controller;

import com.flightmanagement.dto.*;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ApiResponse<List<TicketDto>>> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        ApiResponse<List<TicketDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Tickets retrieved successfully",
                tickets,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDto>> getTicketById(@PathVariable Integer id) {
        TicketDto ticket = ticketService.getTicketById(id);
        ApiResponse<TicketDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Ticket retrieved successfully",
                ticket,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketDto>> createTicket(@RequestBody TicketDto ticketDto) {
        TicketDto createdTicket = ticketService.createTicket(ticketDto);
        ApiResponse<TicketDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Ticket created successfully",
                createdTicket,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<List<TicketDto>>> bookTickets(@RequestBody BookingDto bookingDto) {
        List<TicketDto> bookedTickets = ticketService.bookTickets(bookingDto);
        ApiResponse<List<TicketDto>> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Tickets booked successfully",
                bookedTickets,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDto>> updateTicket(@PathVariable Integer id, @RequestBody TicketDto ticketDto) {
        TicketDto updatedTicket = ticketService.updateTicket(id, ticketDto);
        ApiResponse<TicketDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Ticket updated successfully",
                updatedTicket,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTicket(@PathVariable Integer id) {
        ticketService.cancelTicket(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Ticket canceled successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Integer id, HttpServletRequest request) {
        ticketService.deleteTicket(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Ticket deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<ApiResponse<List<TicketDto>>> getTicketsByFlightId(@PathVariable Integer flightId) {
        List<TicketDto> tickets = ticketService.getTicketsByFlightId(flightId);
        ApiResponse<List<TicketDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Tickets retrieved successfully",
                tickets,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<ApiResponse<TicketDto>> payTicket(@PathVariable Integer id) {
        TicketDto ticket = ticketService.payTicket(id, "PAID_WITH_CASH");
        ApiResponse<TicketDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Ticket paid successfully",
                ticket,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<TicketDto>>> getTicketsByCustomerId(@PathVariable Integer customerId) {
        List<TicketDto> tickets = ticketService.getTicketsByCustomerId(customerId);
        ApiResponse<List<TicketDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Tickets retrieved successfully",
                tickets,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<ApiResponse<List<TicketDto>>> getTicketsByPassengerId(@PathVariable Integer passengerId) {
        List<TicketDto> tickets = ticketService.getTicketsByPassengerId(passengerId);
        ApiResponse<List<TicketDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Tickets retrieved successfully",
                tickets,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TicketDto>>> getTicketsByStatus(@PathVariable Byte status) {
        List<TicketDto> tickets = ticketService.getTicketsByStatus(status);
        ApiResponse<List<TicketDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Tickets retrieved successfully",
                tickets,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/seat-available")
    public ResponseEntity<ApiResponse<Boolean>> isSeatAvailable(@RequestParam Integer flightId, @RequestParam String seatNumber) {
        boolean available = ticketService.isSeatAvailable(flightId, seatNumber);
        ApiResponse<Boolean> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Seat availability checked successfully",
                available,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/confirmation-code")
    public ResponseEntity<ApiResponse<String>> generateConfirmationCode() {
        String code = ticketService.generateConfirmationCode();
        ApiResponse<String> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Generated confirmation code",
                code,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/booking-lookup/{code}")
    public ResponseEntity<ApiResponse<List<TicketDto>>> getTicketsOnConfirmationCode(@PathVariable String code) {
        List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(code);
        ApiResponse<List<TicketDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Tickets retrieved successfully",
                tickets,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}