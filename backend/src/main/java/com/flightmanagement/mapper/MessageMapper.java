package com.flightmanagement.mapper;

import com.flightmanagement.dto.MessageDto;
import com.flightmanagement.entity.Message;
import com.flightmanagement.repository.ChatboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper implements BaseMapper<Message, MessageDto> {
    
    @Autowired
    private ChatboxRepository chatboxRepository;
    
    @Override
    public MessageDto toDto(Message entity) {
        if (entity == null) return null;
        
        MessageDto dto = new MessageDto();
        dto.setMessageId(entity.getMessageId());
        dto.setChatboxId(entity.getChatboxId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setContent(entity.getContent());
        dto.setSendTime(entity.getSendTime());
        dto.setDeletedAt(entity.getDeletedAt());
        
        // Determine sender name based on employeeId
        if (entity.getEmployeeId() != null) {
            // Message from employee
            // You may need to fetch employee name from repository if needed
            dto.setSenderName("Support Agent");
        } else {
            // Message from customer 
            // You may need to fetch customer name from repository if needed
            dto.setSenderName("Customer");
        }
        
        return dto;
    }
    
    @Override
    public Message toEntity(MessageDto dto) {
        if (dto == null) return null;
        
        Message entity = new Message();
        entity.setMessageId(dto.getMessageId());
        entity.setChatboxId(dto.getChatboxId());
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setContent(dto.getContent());
        entity.setSendTime(dto.getSendTime());
        entity.setDeletedAt(dto.getDeletedAt());
        
        return entity;
    }
    
    @Override
    public List<MessageDto> toDtoList(List<Message> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Message> toEntityList(List<MessageDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
