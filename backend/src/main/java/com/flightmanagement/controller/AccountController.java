package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// AccountController.java - Secured account management
@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasRole('EMPLOYEE_ADMIN')")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Integer id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
