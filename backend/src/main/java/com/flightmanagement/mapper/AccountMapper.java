package com.flightmanagement.mapper;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.enums.AccountType;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper{
    /**
     * Converts Account and optional Employee details to DTO.
     * * @param account The base account entity (Required)
     * @param employee The employee detail entity (Nullable - pass null if Customer)
     * @return AccountDto
     */
    public AccountDto toDto(Account account, Employee employee) {
        if (account == null) return null;

        AccountDto dto = new AccountDto();

        // 1. Map Base Account Details
        dto.setAccountId(account.getAccountId());
        dto.setAccountName(account.getAccountName());
        dto.setEmail(account.getEmail());
        dto.setCitizenId(account.getCitizenId());
        dto.setPhoneNumber(account.getPhoneNumber());
        dto.setAccountType(account.getAccountType().getValue());

        // SECURITY: Never return the password to the frontend
        dto.setPassword(null);

        // 2. Handle Customer vs Employee Logic
        if (account.getAccountType() == AccountType.CUSTOMER) {
            dto.setAccountTypeName("Customer");
            dto.setRole("ROLE_CUSTOMER");
        }
        else if (account.getAccountType() == AccountType.EMPLOYEE) {
            // Logic for Employee
            dto.setAccountTypeName("Employee");

            if (employee != null) {
                // Use the Enum to get the string role
                dto.setRole(employee.getEmployeeType().name());
            } else {
                // Fallback if employee data is missing implies data inconsistency
                dto.setRole("ROLE_EMPLOYEE_UNKNOWN");
            }
        }

        return dto;
    }

    public Account toEntity(RegisterDto dto) {
        if (dto == null) return null;
        Account entity = new Account();
        entity.setAccountName(dto.getAccountName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword()); // Valid here (Client -> Server)
        entity.setCitizenId(dto.getCitizenId());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAccountType(AccountType.fromValue(dto.getAccountType()));
        return entity;
    }

    public Account toEntity(AccountDto dto) {
        if (dto == null)
            return null;
        Account entity = new Account();
        entity.setAccountId(dto.getAccountId());
        entity.setAccountName(dto.getAccountName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword()); // Password should be encoded later
        entity.setCitizenId(dto.getCitizenId());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAccountType(AccountType.fromValue(dto.getAccountType()));
        return entity;
    }
}
