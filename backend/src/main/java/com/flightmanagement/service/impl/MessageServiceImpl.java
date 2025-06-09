package com.flightmanagement.service.impl;

import com.flightmanagement.dto.MessageDto;
import com.flightmanagement.entity.Message;
import com.flightmanagement.mapper.MessageMapper;
import com.flightmanagement.repository.MessageRepository;
import com.flightmanagement.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Override
    public List<MessageDto> getAllMessages() {
        List<Message> messages = messageRepository.findAllActive();
        return messageMapper.toDtoList(messages);
    }
    
    @Override
    public MessageDto getMessageById(Integer id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        return messageMapper.toDto(message);
    }
    
    @Override
    public MessageDto createMessage(MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);
        message.setDeletedAt(null);
        message.setSendTime(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDto(savedMessage);
    }
    
    @Override
    public void deleteMessage(Integer id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        
        message.setDeletedAt(LocalDateTime.now());
        messageRepository.save(message);
    }
    
    @Override
    public List<MessageDto> getMessagesByChatboxId(Integer chatboxId) {
        List<Message> messages = messageRepository.findByChatboxIdOrderBySendTime(chatboxId);
        return messageMapper.toDtoList(messages);
    }
    
    @Override
    public List<MessageDto> getRecentMessagesByChatboxId(Integer chatboxId, int limit) {
        List<Message> messages = messageRepository.findByChatboxIdOrderBySendTimeDesc(chatboxId, PageRequest.of(0, limit));
        return messageMapper.toDtoList(messages);
    }
    
    @Override
    public MessageDto createCustomerMessage(Integer chatboxId, String content) {
        MessageDto messageDto = new MessageDto();
        messageDto.setChatboxId(chatboxId);
        messageDto.setContent(content);
        messageDto.setEmployeeId(null); // Customer message has null employeeId
        return createMessage(messageDto);
    }
    
    @Override
    public MessageDto createEmployeeMessage(Integer chatboxId, String content) {
        // You'll need to pass employeeId as parameter or get it from security context
        MessageDto messageDto = new MessageDto();
        messageDto.setChatboxId(chatboxId);
        messageDto.setContent(content);
        messageDto.setEmployeeId(1); // Set actual employee ID here
        return createMessage(messageDto);
    }
}
