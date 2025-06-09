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
        
        // Set employee name only if message is from employee
        if (entity.getEmployeeId() != null) {
            System.out.println("DEBUG - Message from employee ID: " + entity.getEmployeeId());
            // Message from employee - get employee name
            if (entity.getEmployee() != null) {
                System.out.println("DEBUG - Employee entity found: " + entity.getEmployee().getEmployeeId());
                if (entity.getEmployee().getAccount() != null) {
                    String employeeName = entity.getEmployee().getAccount().getAccountName();
                    System.out.println("DEBUG - Employee name found: " + employeeName);
                    dto.setEmployeeName(employeeName);
                } else {
                    System.out.println("DEBUG - Employee account is null");
                    dto.setEmployeeName("Support Agent"); // Fallback
                }
            } else {
                System.out.println("DEBUG - Employee entity is null for employee ID: " + entity.getEmployeeId());
                dto.setEmployeeName("Support Agent"); // Fallback
            }
        } else {
            System.out.println("DEBUG - Message from customer");
            // Message from customer - no employee name
            dto.setEmployeeName(null);
        }
        
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
