package com.flightmanagement.service.impl;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.entity.Chatbox;
import com.flightmanagement.mapper.ChatboxMapper;
import com.flightmanagement.repository.ChatboxRepository;
import com.flightmanagement.service.ChatboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<ChatboxDto> getChatboxesByCustomerId(Integer customerId) {
        List<Chatbox> chatboxes = chatboxRepository.findByCustomerId(customerId);
        return chatboxMapper.toDtoList(chatboxes);
    }
    
    @Override
    public List<ChatboxDto> getChatboxesByEmployeeId(Integer employeeId) {
        List<Chatbox> chatboxes = chatboxRepository.findByEmployeeId(employeeId);
        return chatboxMapper.toDtoList(chatboxes);
    }
    
    @Override
    public ChatboxDto getChatboxByCustomerAndEmployee(Integer customerId, Integer employeeId) {
        Chatbox chatbox = chatboxRepository.findByCustomerIdAndEmployeeId(customerId, employeeId)
            .orElseThrow(() -> new RuntimeException("Chatbox not found for customer and employee"));
        return chatboxMapper.toDto(chatbox);
    }
    
    @Override
    public ChatboxDto getOrCreateChatbox(Integer customerId, Integer employeeId) {
        return chatboxRepository.findByCustomerIdAndEmployeeId(customerId, employeeId)
            .map(chatboxMapper::toDto)
            .orElseGet(() -> {
                ChatboxDto newChatboxDto = new ChatboxDto();
                newChatboxDto.setCustomerId(customerId);
                newChatboxDto.setEmployeeId(employeeId);
                return createChatbox(newChatboxDto);
            });
    }
}
