package com.flightmanagement.service;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.ChatboxRepository;
import com.flightmanagement.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    
    @Autowired
    private ChatboxRepository chatboxRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    // ...existing methods...

    public List<ChatboxDto> getAllChatboxes() {
        return chatboxRepository.findAll().stream()
                .map(chatbox -> {
                    ChatboxDto dto = new ChatboxDto();
                    dto.setChatboxId(chatbox.getChatboxId());
                    dto.setCustomerId(chatbox.getCustomerId());
                    
                    // Get customer name
                    String customerName = accountRepository.findById(chatbox.getCustomerId())
                            .map(account -> account.getAccountName())
                            .orElse("Unknown");
                    dto.setCustomerName(customerName);
                    
                    // Get last message with detailed sender info
                    messageRepository.findTopByChatboxIdOrderBySendTimeDesc(chatbox.getChatboxId())
                            .ifPresent(msg -> {
                                dto.setLastMessageContent(msg.getContent());
                                dto.setLastMessageTime(msg.getSendTime());
                                dto.setIsLastMessageFromCustomer(msg.getEmployeeId() == null);
                                dto.setLastMessageEmployeeId(msg.getEmployeeId());
                                
                                // Set sender name for display
                                if (msg.getEmployeeId() != null) {
                                    // Message from employee - get employee name
                                    String employeeName = accountRepository.findById(msg.getEmployeeId())
                                            .map(account -> account.getAccountName())
                                            .orElse("Employee");
                                    dto.setLastMessageSenderName(employeeName);
                                } else {
                                    // Message from customer - use customer name
                                    dto.setLastMessageSenderName(customerName);
                                }
                            });
                    
                    dto.setDeletedAt(chatbox.getDeletedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}