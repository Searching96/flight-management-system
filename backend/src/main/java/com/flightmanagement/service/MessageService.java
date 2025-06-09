package com.flightmanagement.service;

import com.flightmanagement.dto.MessageDto;

import java.util.List;

public interface MessageService {
    
    List<MessageDto> getAllMessages();
    
    MessageDto getMessageById(Integer id);
    
    void deleteMessage(Integer id);
    
    List<MessageDto> getMessagesByChatboxId(Integer chatboxId);
    
    MessageDto createCustomerMessage(Integer chatboxId, String content);
    
    MessageDto createEmployeeMessage(Integer chatboxId, Integer employeeId, String content);
}
