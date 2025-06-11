package com.flightmanagement.service.impl;

import com.flightmanagement.dto.AccountChatboxDto;
import com.flightmanagement.entity.AccountChatbox;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Chatbox;
import com.flightmanagement.mapper.AccountChatboxMapper;
import com.flightmanagement.repository.AccountChatboxRepository;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.ChatboxRepository;
import com.flightmanagement.service.AccountChatboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AccountChatboxServiceImpl implements AccountChatboxService {
    
    @Autowired
    private AccountChatboxRepository accountChatboxRepository;
    
    @Autowired
    private AccountChatboxMapper accountChatboxMapper;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private ChatboxRepository chatboxRepository;
    
    @Override
    public List<AccountChatboxDto> getAllAccountChatboxes() {
        List<AccountChatbox> accountChatboxes = accountChatboxRepository.findAllActive();
        return accountChatboxMapper.toDtoList(accountChatboxes);
    }
    
    @Override
    public AccountChatboxDto getAccountChatboxById(Integer accountId, Integer chatboxId) {
        AccountChatbox accountChatbox = accountChatboxRepository.findByAccountIdAndChatboxId(accountId, chatboxId)
            .orElseThrow(() -> new RuntimeException("AccountChatbox not found for account: " + accountId + " and chatbox: " + chatboxId));
        return accountChatboxMapper.toDto(accountChatbox);
    }
    
    @Override
    public List<AccountChatboxDto> getAccountChatboxesByAccountId(Integer accountId) {
        List<AccountChatbox> accountChatboxes = accountChatboxRepository.findByAccountId(accountId);
        return accountChatboxMapper.toDtoList(accountChatboxes);
    }
    
    @Override
    public AccountChatboxDto createAccountChatbox(AccountChatboxDto accountChatboxDto) {
        AccountChatbox accountChatbox = new AccountChatbox();
        accountChatbox.setLastVisitTime(accountChatboxDto.getLastVisitTime() != null ? 
            accountChatboxDto.getLastVisitTime() : LocalDateTime.now());
        accountChatbox.setCreatedAt(LocalDateTime.now());
        accountChatbox.setUpdatedAt(LocalDateTime.now());
        accountChatbox.setDeletedAt(null);
        
        // Set entity relationships
        if (accountChatboxDto.getAccountId() != null) {
            Account account = accountRepository.findById(accountChatboxDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountChatboxDto.getAccountId()));
            accountChatbox.setAccount(account);
            accountChatbox.setAccountId(account.getAccountId());
        }
        
        if (accountChatboxDto.getChatboxId() != null) {
            Chatbox chatbox = chatboxRepository.findById(accountChatboxDto.getChatboxId())
                .orElseThrow(() -> new RuntimeException("Chatbox not found with id: " + accountChatboxDto.getChatboxId()));
            accountChatbox.setChatbox(chatbox);
            accountChatbox.setChatboxId(chatbox.getChatboxId());
        }
        
        AccountChatbox savedAccountChatbox = accountChatboxRepository.save(accountChatbox);
        return accountChatboxMapper.toDto(savedAccountChatbox);
    }
    
    @Override
    public AccountChatboxDto updateAccountChatbox(Integer accountId, Integer chatboxId, AccountChatboxDto accountChatboxDto) {
        AccountChatbox existingAccountChatbox = accountChatboxRepository.findByAccountIdAndChatboxId(accountId, chatboxId)
            .orElseThrow(() -> new RuntimeException("AccountChatbox not found for account: " + accountId + " and chatbox: " + chatboxId));
        
        if (accountChatboxDto.getLastVisitTime() != null) {
            existingAccountChatbox.setLastVisitTime(accountChatboxDto.getLastVisitTime());
        }
        existingAccountChatbox.setUpdatedAt(LocalDateTime.now());
        
        AccountChatbox updatedAccountChatbox = accountChatboxRepository.save(existingAccountChatbox);
        return accountChatboxMapper.toDto(updatedAccountChatbox);
    }
    
    @Override
    public void deleteAccountChatbox(Integer accountId, Integer chatboxId) {
        AccountChatbox accountChatbox = accountChatboxRepository.findByAccountIdAndChatboxId(accountId, chatboxId)
            .orElseThrow(() -> new RuntimeException("AccountChatbox not found for account: " + accountId + " and chatbox: " + chatboxId));
        
        accountChatbox.setDeletedAt(LocalDateTime.now());
        accountChatboxRepository.save(accountChatbox);
    }
    
    @Override
    public void updateLastVisitTime(Integer accountId, Integer chatboxId, LocalDateTime lastVisitTime) {
        System.out.println("=== updateLastVisitTime START ===");
        System.out.println("AccountId: " + accountId + ", ChatboxId: " + chatboxId + ", LastVisitTime: " + lastVisitTime);
        
        try {
            AccountChatbox accountChatbox = accountChatboxRepository.findByAccountIdAndChatboxId(accountId, chatboxId)
                .orElse(null);
            
            if (accountChatbox != null) {
                System.out.println("Found existing AccountChatbox record, updating...");
                // Update existing record
                accountChatbox.setLastVisitTime(lastVisitTime);
                accountChatbox.setUpdatedAt(LocalDateTime.now());
                accountChatboxRepository.save(accountChatbox);
                System.out.println("Updated existing record successfully");
            } else {
                System.out.println("No existing record found, creating new one...");
                // Create new record
                AccountChatbox newRecord = new AccountChatbox();
                newRecord.setAccountId(accountId);
                newRecord.setChatboxId(chatboxId);
                newRecord.setLastVisitTime(lastVisitTime);
                newRecord.setCreatedAt(LocalDateTime.now());
                newRecord.setUpdatedAt(LocalDateTime.now());
                newRecord.setDeletedAt(null);
                
                // Set entity relationships
                Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
                newRecord.setAccount(account);
                
                Chatbox chatbox = chatboxRepository.findById(chatboxId)
                    .orElseThrow(() -> new RuntimeException("Chatbox not found with id: " + chatboxId));
                newRecord.setChatbox(chatbox);
                
                AccountChatbox saved = accountChatboxRepository.save(newRecord);
                System.out.println("Created new record with accountId: " + saved.getAccountId() + ", chatboxId: " + saved.getChatboxId());
            }
            
            System.out.println("=== updateLastVisitTime END SUCCESS ===");
        } catch (Exception e) {
            System.err.println("=== ERROR in updateLastVisitTime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            throw new RuntimeException("Failed to update last visit time", e);
        }
    }
    
    @Override
    public void updateLastVisitTime(Integer accountId, Integer chatboxId) {
        updateLastVisitTime(accountId, chatboxId, LocalDateTime.now());
    }
    
    @Override
    public Long getUnreadMessageCount(Integer accountId, Integer chatboxId) {
        return accountChatboxRepository.countUnreadMessages(accountId, chatboxId);
    }
    
    @Override
    public Map<Integer, Long> getUnreadCountsForAllChatboxes(Integer accountId) {
        try {
            System.out.println("=== getUnreadCountsForAllChatboxes START ===");
            System.out.println("Getting unread counts for account: " + accountId);
            
            // Validate account exists
            if (!accountRepository.existsById(accountId)) {
                throw new RuntimeException("Account not found with id: " + accountId);
            }
            
            // Get all active chatboxes
            List<Chatbox> allChatboxes = chatboxRepository.findAllActive();
            Map<Integer, Long> unreadCounts = new HashMap<>();
            
            System.out.println("Found " + allChatboxes.size() + " active chatboxes");
            
            for (Chatbox chatbox : allChatboxes) {
                try {
                    Integer chatboxId = chatbox.getChatboxId();
                    System.out.println("Processing chatbox: " + chatboxId);
                    
                    // Check if account_chatbox record exists
                    Optional<AccountChatbox> accountChatboxOpt = accountChatboxRepository
                        .findByAccountIdAndChatboxId(accountId, chatboxId);
                    
                    if (accountChatboxOpt.isPresent()) {
                        // Record exists, count unread messages
                        Long unreadCount = accountChatboxRepository.countUnreadMessages(accountId, chatboxId);
                        unreadCounts.put(chatboxId, unreadCount != null ? unreadCount : 0L);
                        System.out.println("Chatbox " + chatboxId + " has existing record, unread count: " + unreadCount);
                    } else {
                        // No record exists, create one with current time (so all messages before now are not unread)
                        System.out.println("No record exists for chatbox " + chatboxId + ", creating with current time...");
                        
                        try {
                            updateLastVisitTime(accountId, chatboxId, LocalDateTime.now());
                            // Set unread count to 0 since we just created the record with current time
                            unreadCounts.put(chatboxId, 0L);
                            System.out.println("Created new record for chatbox " + chatboxId + " with unread count: 0");
                        } catch (Exception e) {
                            System.err.println("Failed to create record for chatbox " + chatboxId + ": " + e.getMessage());
                            unreadCounts.put(chatboxId, 0L);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing chatbox " + chatbox.getChatboxId() + ": " + e.getMessage());
                    // Default to 0 if there's an error
                    unreadCounts.put(chatbox.getChatboxId(), 0L);
                }
            }
            
            System.out.println("Final unread counts: " + unreadCounts);
            System.out.println("=== getUnreadCountsForAllChatboxes END SUCCESS ===");
            return unreadCounts;
            
        } catch (Exception e) {
            System.err.println("=== ERROR in getUnreadCountsForAllChatboxes ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            throw new RuntimeException("Failed to get unread counts: " + e.getMessage(), e);
        }
    }
}
