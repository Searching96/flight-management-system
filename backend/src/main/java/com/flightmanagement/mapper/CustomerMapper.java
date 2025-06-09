package com.flightmanagement.mapper;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer entity) {
        if (entity == null) return null;
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(entity.getCustomerId());
        dto.setAccountName(entity.getAccount() != null ? entity.getAccount().getAccountName() : null);
        dto.setEmail(entity.getAccount() != null ? entity.getAccount().getEmail() : null);
        dto.setCitizenId(entity.getAccount() != null ? entity.getAccount().getCitizenId() : null);
        dto.setPhoneNumber(entity.getAccount() != null ? entity.getAccount().getPhoneNumber() : null);
        dto.setScore(entity.getScore());
        return dto;
    }

    public Customer toEntity(CustomerDto dto) {
        if (dto == null) return null;
        Customer entity = new Customer();
        entity.setCustomerId(dto.getCustomerId());
        entity.setScore(dto.getScore());
        // Note: You must set the account relationship after saving the account
        return entity;
    }

    public List<CustomerDto> toDtoList(List<Customer> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<Customer> toEntityList(List<CustomerDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
