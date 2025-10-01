package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountChatboxDto;
import com.flightmanagement.service.AccountChatboxService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<AccountChatboxDto>> getAllAccountChatboxes() {
        List<AccountChatboxDto> accountChatboxes = accountChatboxService.getAllAccountChatboxes();
        return ResponseEntity.ok(accountChatboxes);
    }
    
    @GetMapping("/account/{accountId}/chatbox/{chatboxId}")
    public ResponseEntity<AccountChatboxDto> getAccountChatboxById(
            @PathVariable Integer accountId, 
            @PathVariable Integer chatboxId) {
        AccountChatboxDto accountChatbox = accountChatboxService.getAccountChatboxById(accountId, chatboxId);
        return ResponseEntity.ok(accountChatbox);
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<AccountChatboxDto>> getAccountChatboxesByAccountId(@PathVariable Integer accountId) {
        List<AccountChatboxDto> accountChatboxes = accountChatboxService.getAccountChatboxesByAccountId(accountId);
        return ResponseEntity.ok(accountChatboxes);
    }
    
    @PostMapping
    public ResponseEntity<AccountChatboxDto> createAccountChatbox(@RequestBody AccountChatboxDto accountChatboxDto) {
        AccountChatboxDto createdAccountChatbox = accountChatboxService.createAccountChatbox(accountChatboxDto);
        return ResponseEntity.ok(createdAccountChatbox);
    }
    
    @PutMapping("/account/{accountId}/chatbox/{chatboxId}")
    public ResponseEntity<AccountChatboxDto> updateAccountChatbox(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId,
            @RequestBody AccountChatboxDto accountChatboxDto) {
        AccountChatboxDto updatedAccountChatbox = accountChatboxService.updateAccountChatbox(accountId, chatboxId, accountChatboxDto);
        return ResponseEntity.ok(updatedAccountChatbox);
    }
    
    @DeleteMapping("/account/{accountId}/chatbox/{chatboxId}")
    public ResponseEntity<Void> deleteAccountChatbox(@PathVariable Integer accountId, @PathVariable Integer chatboxId) {
        accountChatboxService.deleteAccountChatbox(accountId, chatboxId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/account/{accountId}/chatbox/{chatboxId}/visit")
    public ResponseEntity<Void> updateLastVisitTime(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId) {
        accountChatboxService.updateLastVisitTime(accountId, chatboxId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/account/{accountId}/chatbox/{chatboxId}/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId) {
        Long unreadCount = accountChatboxService.getUnreadMessageCount(accountId, chatboxId);
        return ResponseEntity.ok(unreadCount);
    }
    
    @GetMapping("/account/{accountId}/unread-counts")
    public ResponseEntity<Map<Integer, Long>> getUnreadCountsForAllChatboxes(@PathVariable Integer accountId) {
        Map<Integer, Long> unreadCounts = accountChatboxService.getUnreadCountsForAllChatboxes(accountId);
        return ResponseEntity.ok(unreadCounts);
    }
}