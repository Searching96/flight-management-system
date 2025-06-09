package com.flightmanagement.mapper;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.entity.Chatbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatboxMapper implements BaseMapper<Chatbox, ChatboxDto> {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    
    @Override
    public ChatboxDto toDto(Chatbox entity) {
        if (entity == null) return null;
        
        ChatboxDto dto = new ChatboxDto();
        dto.setChatboxId(entity.getChatboxId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setDeletedAt(entity.getDeletedAt());
        
        // Get customer name from account
        if (entity.getCustomer() != null && entity.getCustomer().getAccount() != null) {
            dto.setCustomerName(entity.getCustomer().getAccount().getAccountName());
        }
        
        // Initialize default values
        dto.setUnreadCount(0);
        
        return dto;
    }
    
    @Override
    public Chatbox toEntity(ChatboxDto dto) {
        if (dto == null) return null;
        
        Chatbox entity = new Chatbox();
        entity.setChatboxId(dto.getChatboxId());
        
        // Lấy customer từ repository
        var customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + dto.getCustomerId()));
        
        // Đặt cả customer và customerId để đảm bảo consistency
        entity.setCustomer(customer);
        entity.setCustomerId(dto.getCustomerId());
        
        System.out.println("ChatboxMapper.toEntity - Set customerId: " + dto.getCustomerId());
        System.out.println("ChatboxMapper.toEntity - Entity customerId after set: " + entity.getCustomerId());
        
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
