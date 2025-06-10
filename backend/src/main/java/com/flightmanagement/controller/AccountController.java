package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import java.util.List;

// AccountController.java - Secured account management
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE_ADMIN')")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMIN') or (hasRole('CUSTOMER') and #id == principal.id)")

    public ResponseEntity<AccountDto> getAccountById(@PathVariable Integer id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMIN') or (hasRole('CUSTOMER') and #id == principal.id)")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable Integer id, @RequestBody AccountDto accountDto) {
        try {
            return ResponseEntity.ok(accountService.updateAccount(id, accountDto));
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error updating account " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let Spring handle it properly
        }
    }

    @PostMapping("/{id}/verify-password")
    @PreAuthorize("hasRole('EMPLOYEE_ADMIN') or (hasRole('CUSTOMER') and #id == principal.id)")
    public ResponseEntity<Boolean> verifyCurrentPassword(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            boolean isValid = accountService.verifyCurrentPassword(id, currentPassword);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            System.err.println("Error verifying password for account " + id + ": " + e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('EMPLOYEE_ADMIN') or (hasRole('CUSTOMER') and #id == principal.id)")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            accountService.resetPassword(id, currentPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error resetting password for account " + id + ": " + e.getMessage());
            throw e;
        }
    }
}
