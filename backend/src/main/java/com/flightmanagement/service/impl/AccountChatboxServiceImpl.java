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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        AccountChatbox accountChatbox = accountChatboxRepository.findByAccountIdAndChatboxId(accountId, chatboxId)
            .orElse(null);
        
        if (accountChatbox != null) {
            // Update existing record
            accountChatbox.setLastVisitTime(lastVisitTime);
            accountChatbox.setUpdatedAt(LocalDateTime.now());
            accountChatboxRepository.save(accountChatbox);
        } else {
            // Create new record
            AccountChatboxDto dto = new AccountChatboxDto();
            dto.setAccountId(accountId);
            dto.setChatboxId(chatboxId);
            dto.setLastVisitTime(lastVisitTime);
            createAccountChatbox(dto);
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
        List<AccountChatbox> accountChatboxes = accountChatboxRepository.findByAccountId(accountId);
        
        return accountChatboxes.stream()
            .collect(Collectors.toMap(
                AccountChatbox::getChatboxId,
                accountChatbox -> getUnreadMessageCount(accountId, accountChatbox.getChatboxId())
            ));
    }
}
