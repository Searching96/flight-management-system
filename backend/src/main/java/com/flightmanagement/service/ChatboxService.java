package com.flightmanagement.service;

import com.flightmanagement.dto.ChatboxDto;

import java.util.List;

public interface ChatboxService {
    
    List<ChatboxDto> getAllChatboxes();
    
    ChatboxDto getChatboxById(Integer id);
    
    ChatboxDto createChatbox(ChatboxDto chatboxDto);
    
    void deleteChatbox(Integer id);
    
    List<ChatboxDto> getChatboxesByCustomerId(Integer customerId);
    
    List<ChatboxDto> getChatboxesByEmployeeId(Integer employeeId);
    
    ChatboxDto getChatboxByCustomerAndEmployee(Integer customerId, Integer employeeId);
    
    ChatboxDto getOrCreateChatbox(Integer customerId, Integer employeeId);
}
