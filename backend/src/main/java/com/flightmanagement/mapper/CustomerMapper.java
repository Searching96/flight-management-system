package com.flightmanagement.mapper;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper implements BaseMapper<Customer, CustomerDto> {
    
    @Override
    public CustomerDto toDto(Customer entity) {
        if (entity == null) return null;
        
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(entity.getCustomerId());
        dto.setScore(entity.getScore());
        
        if (entity.getAccount() != null) {
            dto.setAccountName(entity.getAccount().getAccountName());
            dto.setEmail(entity.getAccount().getEmail());
            dto.setCitizenId(entity.getAccount().getCitizenId());
            dto.setPhoneNumber(entity.getAccount().getPhoneNumber());
        }
        
        return dto;
    }
    
    @Override
    public Customer toEntity(CustomerDto dto) {
        if (dto == null) return null;
        
        Customer entity = new Customer();
        entity.setCustomerId(dto.getCustomerId());
        entity.setScore(dto.getScore());
        return entity;
    }
    
    @Override
    public List<CustomerDto> toDtoList(List<Customer> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Customer> toEntityList(List<CustomerDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
