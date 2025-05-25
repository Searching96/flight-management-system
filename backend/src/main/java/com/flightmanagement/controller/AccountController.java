package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.dto.LoginRequestDto;
import com.flightmanagement.dto.LoginResponseDto;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Integer id) {
        AccountDto account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            LoginResponseDto response = accountService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        try {
            AccountDto createdAccount = accountService.createAccount(registerDto);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Registration failed", "message", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Integer id, @RequestBody AccountDto accountDto) {
        AccountDto updatedAccount = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updatedAccount);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<AccountDto> getAccountByEmail(@PathVariable String email) {
        AccountDto account = accountService.getAccountByEmail(email);
        return ResponseEntity.ok(account);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<AccountDto>> getAccountsByType(@PathVariable Integer type) {
        List<AccountDto> accounts = accountService.getAccountsByType(type);
        return ResponseEntity.ok(accounts);
    }
}
