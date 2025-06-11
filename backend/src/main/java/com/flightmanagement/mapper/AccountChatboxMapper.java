package com.flightmanagement.mapper;

import com.flightmanagement.dto.AccountChatboxDto;
import com.flightmanagement.entity.AccountChatbox;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountChatboxMapper {
    
    public AccountChatboxDto toDto(AccountChatbox entity) {
        if (entity == null) {
            return null;
        }
        
        AccountChatboxDto dto = new AccountChatboxDto();
        dto.setAccountId(entity.getAccountId());
        dto.setChatboxId(entity.getChatboxId());
        dto.setLastVisitTime(entity.getLastVisitTime());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Include related entity information
        if (entity.getAccount() != null) {
            dto.setAccountName(entity.getAccount().getAccountName());
        }
        
        return dto;
    }
    
    public AccountChatbox toEntity(AccountChatboxDto dto) {
        if (dto == null) {
            return null;
        }
        
        AccountChatbox entity = new AccountChatbox();
        entity.setAccountId(dto.getAccountId());
        entity.setChatboxId(dto.getChatboxId());
        entity.setLastVisitTime(dto.getLastVisitTime());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        
        return entity;
    }
    
    public List<AccountChatboxDto> toDtoList(List<AccountChatbox> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<AccountChatbox> toEntityList(List<AccountChatboxDto> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
