package com.flightmanagement.mapper;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.entity.Account;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper implements BaseMapper<Account, AccountDto> {
    
    @Override
    public AccountDto toDto(Account entity) {
        if (entity == null) return null;
        
        AccountDto dto = new AccountDto();
        dto.setAccountId(entity.getAccountId());
        dto.setAccountName(entity.getAccountName());
        dto.setEmail(entity.getEmail());
        dto.setCitizenId(entity.getCitizenId());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setAccountType(entity.getAccountType());
        dto.setAccountTypeName(getAccountTypeName(entity.getAccountType()));
        return dto;
    }
    
    @Override
    public Account toEntity(AccountDto dto) {
        if (dto == null) return null;
        
        Account entity = new Account();
        entity.setAccountId(dto.getAccountId());
        entity.setAccountName(dto.getAccountName());
        entity.setEmail(dto.getEmail());
        entity.setCitizenId(dto.getCitizenId());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAccountType(dto.getAccountType());
        return entity;
    }
    
    @Override
    public List<AccountDto> toDtoList(List<Account> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Account> toEntityList(List<AccountDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    private String getAccountTypeName(Integer accountType) {
        return accountType == 1 ? "Customer" : "Employee";
    }
}
