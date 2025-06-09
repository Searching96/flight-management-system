package com.flightmanagement.service.impl;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.dto.ChatboxTestDto;
import com.flightmanagement.entity.Chatbox;
import com.flightmanagement.mapper.ChatboxMapper;
import com.flightmanagement.repository.ChatboxRepository;
import com.flightmanagement.service.ChatboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatboxServiceImpl implements ChatboxService {
    
    @Autowired
    private ChatboxRepository chatboxRepository;
    
    @Autowired
    private ChatboxMapper chatboxMapper;

    @Override
    public List<ChatboxDto> getAllChatboxes() {
        List<Chatbox> chatboxes = chatboxRepository.findAllActive();
        return chatboxMapper.toDtoList(chatboxes);
    }
    
    @Override
    public ChatboxDto getChatboxById(Integer id) {
        Chatbox chatbox = chatboxRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Chatbox not found with id: " + id));
        return chatboxMapper.toDto(chatbox);
    }
    
    @Override
    public ChatboxDto createChatbox(ChatboxDto chatboxDto) {
        Chatbox chatbox = chatboxMapper.toEntity(chatboxDto);
        chatbox.setDeletedAt(null);
        Chatbox savedChatbox = chatboxRepository.save(chatbox);
        return chatboxMapper.toDto(savedChatbox);
    }
    
    @Override
    public void deleteChatbox(Integer id) {
        Chatbox chatbox = chatboxRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Chatbox not found with id: " + id));
        
        chatbox.setDeletedAt(LocalDateTime.now());
        chatboxRepository.save(chatbox);
    }
    
    @Override
    public ChatboxDto getChatboxByCustomerId(Integer customerId) {
        System.out.println("Getting chatbox for customer ID: " + customerId);
        
        // Get existing chatbox (should be only one per customer)
        List<Chatbox> chatboxes = chatboxRepository.findByCustomerId(customerId);
        if (!chatboxes.isEmpty()) {
            System.out.println("Found existing chatbox: " + chatboxes.get(0).getChatboxId());
            return chatboxMapper.toDto(chatboxes.get(0));
        }
        
        // If no chatbox found, create a new one
        System.out.println("No chatbox found, creating new one for customer: " + customerId);
        return createChatboxWithCustomerId(customerId);
    }
    
    private ChatboxDto createChatboxWithCustomerId(Integer customerId) {
        System.out.println("Creating new chatbox for customer ID: " + customerId);
        
        ChatboxDto chatboxDto = new ChatboxDto();
        chatboxDto.setCustomerId(customerId);
        return createChatbox(chatboxDto);
    }
    
    @Override
    public List<ChatboxDto> getChatboxesByCustomerId(Integer customerId) {
        List<Chatbox> chatboxes = chatboxRepository.findByCustomerId(customerId);
        return chatboxMapper.toDtoList(chatboxes);
    }
}
