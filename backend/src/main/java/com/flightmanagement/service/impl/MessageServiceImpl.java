package com.flightmanagement.service.impl;

import com.flightmanagement.dto.MessageDto;
import com.flightmanagement.entity.Message;
import com.flightmanagement.mapper.MessageMapper;
import com.flightmanagement.repository.MessageRepository;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public List<MessageDto> getMessagesByChatboxId(Integer chatboxId) {
        List<Message> messages = messageRepository.findByChatboxIdOrderBySendTimeAsc(chatboxId);
        return messages.stream()
                .map(this::enrichMessageWithEmployeeInfo)
                .collect(Collectors.toList());
    }
    
    private MessageDto enrichMessageWithEmployeeInfo(Message message) {
        MessageDto dto = messageMapper.toDto(message);
        
        // Set employee name if message is from employee
        if (message.getEmployeeId() != null) {
            String employeeName = accountRepository.findById(message.getEmployeeId())
                    .map(account -> account.getAccountName())
                    .orElse("Employee");
            dto.setEmployeeName(employeeName);
        }
        
        // Set isFromCustomer flag
        dto.setIsFromCustomer(message.getEmployeeId() == null);
        
        return dto;
    }
    
    @Override
    public MessageDto createEmployeeMessage(Integer chatboxId, Integer employeeId, String content) {
        Message message = new Message();
        message.setChatboxId(chatboxId);
        message.setEmployeeId(employeeId);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        return enrichMessageWithEmployeeInfo(savedMessage);
    }
    
    @Override
    public MessageDto createCustomerMessage(Integer chatboxId, String content) {
        Message message = new Message();
        message.setChatboxId(chatboxId);
        message.setEmployeeId(null); // null indicates customer message
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        return enrichMessageWithEmployeeInfo(savedMessage);
    }
}
