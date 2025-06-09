package com.flightmanagement.mapper;

import com.flightmanagement.dto.MessageDto;
import com.flightmanagement.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper implements BaseMapper<Message, MessageDto> {
    
    @Override
    public MessageDto toDto(Message entity) {
        if (entity == null) return null;
        
        MessageDto dto = new MessageDto();
        dto.setMessageId(entity.getMessageId());
        dto.setChatboxId(entity.getChatboxId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setContent(entity.getContent());
        dto.setSendTime(entity.getSendTime());
        dto.setIsFromCustomer(entity.getEmployeeId() == null);
        
        // Set sender name based on who sent the message
        if (entity.getEmployeeId() == null) {
            // Message from customer - get customer name from chatbox
            if (entity.getChatbox() != null && entity.getChatbox().getCustomer() != null
                && entity.getChatbox().getCustomer().getAccount() != null) {
                dto.setSenderName(entity.getChatbox().getCustomer().getAccount().getAccountName());
            } else {
                dto.setSenderName("Customer"); // Fallback
            }
        } else {
            // Message from employee - get employee name
            if (entity.getEmployee() != null && entity.getEmployee().getAccount() != null) {
                dto.setSenderName(entity.getEmployee().getAccount().getAccountName());
            } else {
                dto.setSenderName("Support Agent"); // Fallback
            }
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
