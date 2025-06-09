package com.flightmanagement.service;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.dto.ChatboxTestDto;

import java.util.List;

public interface ChatboxService {
    
    List<ChatboxDto> getAllChatboxes();
    
    ChatboxDto getChatboxById(Integer id);
    
    ChatboxDto createChatbox(ChatboxDto chatboxDto);

    void deleteChatbox(Integer id);
    
    List<ChatboxDto> getChatboxesByCustomerId(Integer customerId);
    
    ChatboxDto getChatboxByCustomerId(Integer customerId);
}
