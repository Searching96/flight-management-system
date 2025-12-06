package com.flightmanagement.controller;

import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.entity.Passenger;
import com.flightmanagement.repository.PassengerRepository;
import com.flightmanagement.service.AuthService;
import com.flightmanagement.service.EmailService;
import com.flightmanagement.service.FlightService;
import com.flightmanagement.service.PassengerService;
import com.flightmanagement.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@Tag(name = "Debug", description = "Debugging operations for development purposes")
public class DebugController {

    private final AuthService authService;
    private final TicketService ticketService;

    public DebugController(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;
    }

    @Operation(summary = "Debug login by account name")
    @GetMapping("/login-by-name/{accountName}")
    public ResponseEntity<ApiResponse<AuthResponse>> debugLoginByName(@PathVariable String accountName) {
        System.out.println("=== Debug Login by Name START ===");
        System.out.println("Account name: " + accountName);
        System.out.println("Request received at: " + java.time.LocalDateTime.now());

        try {
            AuthResponse response = authService.debugLoginByName(accountName);
            System.out.println("Debug login successful for: " + accountName);
            System.out.println("Account type: " + response.getUserDetails().getAccountTypeName());
            System.out.println("=== Debug Login by Name END ===");

            ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Debug login successful",
                    response,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (RuntimeException e) {
            System.err.println("Debug login failed for: " + accountName);
            System.err.println("Error: " + e.getMessage());
            System.err.println("=== Debug Login by Name END (Error) ===");

            ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                    HttpStatus.NOT_FOUND,
                    "Debug login failed: " + e.getMessage(),
                    null,
                    "ACCOUNT_NOT_FOUND"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        } catch (Exception e) {
            System.err.println("Unexpected error during debug login: " + e.getMessage());
            e.printStackTrace();

            ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error during debug login",
                    null,
                    "INTERNAL_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @Operation(summary = "Bypass MoMo payment - Mark booking as paid directly")
    @PostMapping("/bypass-payment/{confirmationCode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bypassPayment(@PathVariable String confirmationCode) {
        System.out.println("=== Bypass Payment START ===");
        System.out.println("Confirmation Code: " + confirmationCode);
        System.out.println("Request received at: " + java.time.LocalDateTime.now());

        try {
            // Get tickets by confirmation code
            List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(confirmationCode);

            if (tickets == null || tickets.isEmpty()) {
                System.err.println("No tickets found for confirmation code: " + confirmationCode);
                ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                        HttpStatus.NOT_FOUND,
                        "No tickets found for confirmation code: " + confirmationCode,
                        null,
                        "TICKETS_NOT_FOUND"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }

            // Generate mock transaction ID
            String mockTransactionId = "DEBUG_" + System.currentTimeMillis();

            // Pay all tickets
            int paidCount = 0;
            for (TicketDto ticket : tickets) {
                if (ticket.getTicketStatus() == 0) { // Only pay unpaid tickets
                    ticketService.payTicket(ticket.getTicketId(), mockTransactionId);
                    paidCount++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("confirmationCode", confirmationCode);
            result.put("totalTickets", tickets.size());
            result.put("paidTickets", paidCount);
            result.put("transactionId", mockTransactionId);
            result.put("message", "Payment bypassed successfully - all tickets marked as paid");

            System.out.println("Successfully bypassed payment for " + paidCount + " tickets");
            System.out.println("=== Bypass Payment END ===");

            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Payment bypassed successfully",
                    result,
                    null
            );
            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            System.err.println("Error bypassing payment: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== Bypass Payment END (Error) ===");

            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error bypassing payment: " + e.getMessage(),
                    null,
                    "BYPASS_PAYMENT_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}