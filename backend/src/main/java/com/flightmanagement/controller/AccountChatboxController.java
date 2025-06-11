package com.flightmanagement.controller;

import com.flightmanagement.dto.AccountChatboxDto;
import com.flightmanagement.service.AccountChatboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/account-chatboxes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AccountChatboxController {
    
    @Autowired
    private AccountChatboxService accountChatboxService;
    
    @GetMapping
    public ResponseEntity<List<AccountChatboxDto>> getAllAccountChatboxes() {
        List<AccountChatboxDto> accountChatboxes = accountChatboxService.getAllAccountChatboxes();
        return ResponseEntity.ok(accountChatboxes);
    }
    
    @GetMapping("/{accountId}/{chatboxId}")
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
    
    @PutMapping("/{accountId}/{chatboxId}")
    public ResponseEntity<AccountChatboxDto> updateAccountChatbox(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId,
            @RequestBody AccountChatboxDto accountChatboxDto) {
        AccountChatboxDto updatedAccountChatbox = accountChatboxService.updateAccountChatbox(accountId, chatboxId, accountChatboxDto);
        return ResponseEntity.ok(updatedAccountChatbox);
    }
    
    @DeleteMapping("/{accountId}/{chatboxId}")
    public ResponseEntity<Void> deleteAccountChatbox(@PathVariable Integer accountId, @PathVariable Integer chatboxId) {
        accountChatboxService.deleteAccountChatbox(accountId, chatboxId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{accountId}/{chatboxId}/visit")
    public ResponseEntity<Void> updateLastVisitTime(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId) {
        accountChatboxService.updateLastVisitTime(accountId, chatboxId, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{accountId}/{chatboxId}/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable Integer accountId,
            @PathVariable Integer chatboxId) {
        Long unreadCount = accountChatboxService.getUnreadMessageCount(accountId, chatboxId);
        return ResponseEntity.ok(unreadCount);
    }
}
