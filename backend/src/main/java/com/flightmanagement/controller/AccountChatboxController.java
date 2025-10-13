package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountChatboxDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.AccountChatboxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account-chatbox")
@Tag(name = "AccountChatbox", description = "Operations related to account-chatbox associations")
public class AccountChatboxController {
    
    private final AccountChatboxService accountChatboxService;

    public AccountChatboxController(AccountChatboxService accountChatboxService) {
        this.accountChatboxService = accountChatboxService;
    }
    
    @GetMapping
    @Operation(summary = "Get all account-chatbox associations")
    public ResponseEntity<ApiResponse<List<AccountChatboxDto>>> getAllAccountChatboxes() {
        List<AccountChatboxDto> accountChatboxes = accountChatboxService.getAllAccountChatboxes();

        ApiResponse<List<AccountChatboxDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched all account-chatbox associations",
                accountChatboxes,
                null
        );

        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/account/{accountId}/chatbox/{chatboxId}")
    @Operation(summary = "Get a specific account-chatbox association by IDs")
    public ResponseEntity<ApiResponse<AccountChatboxDto>> getAccountChatboxById(
            @PathVariable Integer accountId, 
            @PathVariable Integer chatboxId) {
        AccountChatboxDto accountChatbox = accountChatboxService.getAccountChatboxById(accountId, chatboxId);

        ApiResponse<AccountChatboxDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched account-chatbox association",
                accountChatbox,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get all chatbox associations for a specific account")
    public ResponseEntity<ApiResponse<List<AccountChatboxDto>>> getAccountChatboxesByAccountId(@PathVariable Integer accountId) {
        List<AccountChatboxDto> accountChatboxes = accountChatboxService.getAccountChatboxesByAccountId(accountId);
        ApiResponse<List<AccountChatboxDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched account-chatbox associations for accountId: " + accountId,
                accountChatboxes,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Tag(name = "AccountChatbox")
    @Operation(summary = "Create a new account-chatbox association")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountChatboxDto>> createAccountChatbox(@RequestBody AccountChatboxDto accountChatboxDto) {
        AccountChatboxDto createdAccountChatbox = accountChatboxService.createAccountChatbox(accountChatboxDto);
        ApiResponse<AccountChatboxDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Created account-chatbox association",
                createdAccountChatbox,
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
    
    @Tag(name = "AccountChatbox")
    @Operation(summary = "Update an existing account-chatbox association")
    @PutMapping("/account/{accountId}/chatbox/{chatboxId}")
    public ResponseEntity<ApiResponse<AccountChatboxDto>> updateAccountChatbox(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId,
            @RequestBody AccountChatboxDto accountChatboxDto) {
        AccountChatboxDto updatedAccountChatbox = accountChatboxService.updateAccountChatbox(accountId, chatboxId, accountChatboxDto);
        ApiResponse<AccountChatboxDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Updated account-chatbox association",
                updatedAccountChatbox,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Tag(name = "AccountChatbox")
    @Operation(summary = "Delete an account-chatbox association")
    @DeleteMapping("/account/{accountId}/chatbox/{chatboxId}")
    public ResponseEntity<ApiResponse<Void>> deleteAccountChatbox(@PathVariable Integer accountId, @PathVariable Integer chatboxId) {
        accountChatboxService.deleteAccountChatbox(accountId, chatboxId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Deleted account-chatbox association",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
    
    @PutMapping("/account/{accountId}/chatbox/{chatboxId}/visit")
    @Operation(summary = "Update the last visit time for a chatbox")
    public ResponseEntity<ApiResponse<Void>> updateLastVisitTime(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId) {
        accountChatboxService.updateLastVisitTime(accountId, chatboxId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Updated last visit time",
                null,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/account/{accountId}/chatbox/{chatboxId}/unread-count")
    @Operation(summary = "Get the unread message count for a specific chatbox")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId) {
        Long unreadCount = accountChatboxService.getUnreadMessageCount(accountId, chatboxId);

        ApiResponse<Long> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched unread message count",
                unreadCount,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/account/{accountId}/unread-counts")
    @Operation(summary = "Get unread message counts for all chatboxes of an account")
    public ResponseEntity<ApiResponse<Map<Integer, Long>>> getUnreadCountsForAllChatboxes(@PathVariable Integer accountId) {
        Map<Integer, Long> unreadCounts = accountChatboxService.getUnreadCountsForAllChatboxes(accountId);

        ApiResponse<Map<Integer, Long>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched unread message counts for all chatboxes",
                unreadCounts,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}