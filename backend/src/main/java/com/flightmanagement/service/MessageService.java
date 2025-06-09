package com.flightmanagement.service;

import com.flightmanagement.dto.MessageDto;

import java.util.List;

public interface MessageService {
    
    List<MessageDto> getMessagesByChatboxId(Integer chatboxId);
    
    MessageDto createEmployeeMessage(Integer chatboxId, Integer employeeId, String content);
    
    MessageDto createCustomerMessage(Integer chatboxId, String content);
}
