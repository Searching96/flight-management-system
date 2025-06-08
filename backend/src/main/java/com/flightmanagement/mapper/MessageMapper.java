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
        dto.setMessageType(entity.getMessageType());
        dto.setContent(entity.getContent());
        dto.setSendTime(entity.getSendTime());
        dto.setIsFromCustomer(entity.getMessageType() == 1);
        
        if (entity.getChatbox() != null) {
            dto.setChatboxId(entity.getChatbox().getChatboxId());
            
            if (entity.getMessageType() == 1 && entity.getChatbox().getCustomer() != null
                && entity.getChatbox().getCustomer().getAccount() != null) {
                dto.setSenderName(entity.getChatbox().getCustomer().getAccount().getAccountName());
            } else if (entity.getMessageType() == 2 && entity.getChatbox().getEmployee() != null
                && entity.getChatbox().getEmployee().getAccount() != null) {
                dto.setSenderName(entity.getChatbox().getEmployee().getAccount().getAccountName());
            }
        }
        
        return dto;
    }
    
    @Override
    public Message toEntity(MessageDto dto) {
        if (dto == null) return null;
        
        Message entity = new Message();
        entity.setMessageId(dto.getMessageId());
        entity.setMessageType(dto.getMessageType());
        entity.setContent(dto.getContent());
        entity.setSendTime(dto.getSendTime());
        
        // Set chatbox relationship
        if (dto.getChatboxId() != null) {
            entity.setChatbox(chatboxRepository.findById(dto.getChatboxId())
                .orElseThrow(() -> new RuntimeException("Chatbox not found with id: " + dto.getChatboxId())));
        }
        
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
