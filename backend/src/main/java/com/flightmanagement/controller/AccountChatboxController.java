package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountChatboxDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.AccountChatboxService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account-chatbox")
public class AccountChatboxController {
    
    private final AccountChatboxService accountChatboxService;

    public AccountChatboxController(AccountChatboxService accountChatboxService) {
        this.accountChatboxService = accountChatboxService;
    }
    
    @GetMapping
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