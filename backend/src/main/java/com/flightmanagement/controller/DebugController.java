package com.flightmanagement.controller;

import com.flightmanagement.dto.LoginResponseDto;
import com.flightmanagement.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private AccountService accountService;
    
    @GetMapping("/login-by-name/{accountName}")
    public ResponseEntity<LoginResponseDto> debugLoginByName(@PathVariable String accountName) {
        System.out.println("=== Debug Login by Name START ===");
        System.out.println("Account name: " + accountName);
        System.out.println("Request received at: " + java.time.LocalDateTime.now());
        
        try {
            LoginResponseDto response = accountService.debugLoginByName(accountName);
            System.out.println("Debug login successful for: " + accountName);
            System.out.println("Account type: " + response.getAccountType());
            System.out.println("=== Debug Login by Name END ===");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Debug login failed for: " + accountName);
            System.err.println("Error: " + e.getMessage());
            System.err.println("=== Debug Login by Name END (Error) ===");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Unexpected error during debug login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
