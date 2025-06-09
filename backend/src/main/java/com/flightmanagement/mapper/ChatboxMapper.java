package com.flightmanagement.mapper;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.entity.Chatbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flightmanagement.repository.CustomerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatboxMapper implements BaseMapper<Chatbox, ChatboxDto> {
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Override
    public ChatboxDto toDto(Chatbox entity) {
        if (entity == null) return null;
        
        ChatboxDto dto = new ChatboxDto();
        dto.setChatboxId(entity.getChatboxId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setDeletedAt(entity.getDeletedAt());
        
        // Add customer name if customer relationship is loaded
        if (entity.getCustomer() != null && entity.getCustomer().getAccount() != null) {
            dto.setCustomerName(entity.getCustomer().getAccount().getAccountName());
        }
        
        return dto;
    }
    
    @Override
    public Chatbox toEntity(ChatboxDto dto) {
        if (dto == null) return null;
        
        Chatbox entity = new Chatbox();
        entity.setChatboxId(dto.getChatboxId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setDeletedAt(dto.getDeletedAt());
        
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
