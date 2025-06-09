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
        dto.setDeletedAt(entity.getDeletedAt());
        
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
