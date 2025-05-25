package com.flightmanagement.mapper;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.entity.Chatbox;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatboxMapper implements BaseMapper<Chatbox, ChatboxDto> {
    
    @Override
    public ChatboxDto toDto(Chatbox entity) {
        if (entity == null) return null;
        
        ChatboxDto dto = new ChatboxDto();
        dto.setChatboxId(entity.getChatboxId());
        
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getCustomerId());
            if (entity.getCustomer().getAccount() != null) {
                dto.setCustomerName(entity.getCustomer().getAccount().getAccountName());
            }
        }
        
        if (entity.getEmployee() != null) {
            dto.setEmployeeId(entity.getEmployee().getEmployeeId());
            if (entity.getEmployee().getAccount() != null) {
                dto.setEmployeeName(entity.getEmployee().getAccount().getAccountName());
            }
        }
        
        // Set default values for message metadata (can be populated by service layer)
        dto.setUnreadCount(0);
        
        return dto;
    }
    
    @Override
    public Chatbox toEntity(ChatboxDto dto) {
        if (dto == null) return null;
        
        Chatbox entity = new Chatbox();
        entity.setChatboxId(dto.getChatboxId());
        return entity;
    }
    
    @Override
    public List<ChatboxDto> toDtoList(List<Chatbox> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Chatbox> toEntityList(List<ChatboxDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
