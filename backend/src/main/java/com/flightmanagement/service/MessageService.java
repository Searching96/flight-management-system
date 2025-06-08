package com.flightmanagement.service;

import com.flightmanagement.dto.MessageDto;

import java.util.List;

public interface MessageService {
    
    List<MessageDto> getAllMessages();
    
    MessageDto getMessageById(Integer id);
    
    MessageDto createMessage(MessageDto messageDto);
    
    void deleteMessage(Integer id);
    
    List<MessageDto> getMessagesByChatboxId(Integer chatboxId);
    
    List<MessageDto> getRecentMessagesByChatboxId(Integer chatboxId, int limit);
    
    List<MessageDto> getMessagesByType(Integer messageType);
    
    MessageDto sendMessage(Integer chatboxId, String content, Integer messageType);
    
    MessageDto createCustomerMessage(Integer chatboxId, String content);
    
    MessageDto createEmployeeMessage(Integer chatboxId, String content);
}
