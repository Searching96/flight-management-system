package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.AccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAllAccounts() {
        List<AccountDto> accountDtoList = accountService.getAllAccounts();

        ApiResponse<List<AccountDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Accounts retrieved successfully",
                accountDtoList,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or #id == principal.id")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountById(@PathVariable Integer id) {
        AccountDto accountDto = accountService.getAccountById(id);

        ApiResponse<AccountDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Account retrieved successfully",
                accountDto,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Account deleted successfully",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or #id == principal.id")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccount(
            @PathVariable Integer id, @RequestBody AccountDto accountDto) {
        try {
            AccountDto updatedAccountDto = accountService.updateAccount(id, accountDto);
            ApiResponse<AccountDto> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Account updated successfully",
                    updatedAccountDto,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error updating account " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let Spring handle it properly
        }
    }

    @PostMapping("/{id}/verify-password")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or #id == principal.id")
    public ResponseEntity<ApiResponse<Boolean>> verifyCurrentPassword(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            Boolean isValid = accountService.verifyCurrentPassword(id, currentPassword);
            ApiResponse<Boolean> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Password verification completed",
                    isValid,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            System.err.println("Error verifying password for account " + id + ": " + e.getMessage());
            ApiResponse<Boolean> apiResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error verifying password",
                    false,
                    "INTERNAL_SERVER_ERROR"
            );
            return ResponseEntity.ok(apiResponse);
        }
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or #id == principal.id")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            accountService.resetPassword(id, currentPassword, newPassword);
            ApiResponse<Void> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Password reset successfully",
                    null,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            System.err.println("Error resetting password for account " + id + ": " + e.getMessage());
            throw e;
        }
    }
}
